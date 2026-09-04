package se.sundsvall.educationdata.service;

import generated.se.sundsvall.plannededucation.ListedAdultEducationEventsRM;
import generated.se.sundsvall.plannededucation.PageMetadataRM;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.educationdata.integration.db.EventCategoryStagingRepository;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegration;
import se.sundsvall.educationdata.service.mapper.PlannedEducationMapper;

import static se.sundsvall.dept44.problem.Problem.badGateway;

@Service
public class PlannedEducationService {

	private final PlannedEducationIntegration plannedEducationIntegration;
	private final ReferenceCategoryRepository referenceCategoryRepository;
	private final PlannedEducationMapper plannedEducationMapper;
	private final Set<String> municipalityIdWhitelist;
	private final EventCategoryService eventCategoryService;
	private final EventCategoryStagingRepository eventCategoryStagingRepository;
	private static final Logger log = LoggerFactory.getLogger(PlannedEducationService.class);

	public PlannedEducationService(PlannedEducationIntegration plannedEducationIntegration, ReferenceCategoryRepository referenceCategoryRepository,
		PlannedEducationMapper plannedEducationMapper,
		@Value("${municipality-whitelist}") Set<String> municipalityIdWhitelist, EventCategoryService eventCategoryService, EventCategoryStagingRepository eventCategoryStagingRepository) {
		this.plannedEducationIntegration = plannedEducationIntegration;
		this.referenceCategoryRepository = referenceCategoryRepository;
		this.plannedEducationMapper = plannedEducationMapper;
		this.municipalityIdWhitelist = municipalityIdWhitelist;
		this.eventCategoryService = eventCategoryService;
		this.eventCategoryStagingRepository = eventCategoryStagingRepository;
	}

	@Transactional
	public void importReferenceCategories() {
		var importedCategories = plannedEducationIntegration.getAllReferenceCategories();

		if (importedCategories.isEmpty()) {
			throw badGateway(
				"No content");
		}

		importedCategories.forEach(imported -> referenceCategoryRepository
			.findByCategoryIdAndDirectionId(imported.getCategoryId(), imported.getDirectionId())
			.ifPresent(existing -> imported.setId(existing.getId())));

		referenceCategoryRepository.saveAll(importedCategories);
	}

	public void refreshEventCategoryRelations() {
		try {
			final var distinctDirectionIds = referenceCategoryRepository.findDistinctDirectionIds();
			final var failedDirections = new ArrayList<String>();
			for (final var directionId : distinctDirectionIds) {
				try {
					final var eventIds = getEventIdsByDirectionAndMunicipalityIds(directionId, municipalityIdWhitelist);
					if (!eventIds.isEmpty()) {
						eventCategoryService.addToEventCategoryRelationsStagingTable(directionId, eventIds);
					}
				} catch (Exception e) {
					log.warn("failed to add relations for direction: {} to staging", directionId, e);
					failedDirections.add(directionId);
				}
			}
			if (!failedDirections.isEmpty()) {
				throw Problem.badGateway("Failed to add relations for directions: " + failedDirections);
			}
			eventCategoryService.replaceEventCategoryWithStagedData();
		} finally {
			eventCategoryStagingRepository.deleteAllInBatch();
		}
	}

	private List<String> getEventIdsByDirectionAndMunicipalityIds(String directionId, Set<String> municipalityIds) {
		var totalPages = getTotalPages(directionId, municipalityIds);

		return IntStream.range(0, totalPages)
			.mapToObj(page -> plannedEducationIntegration.getEducationEventsByReferenceId(directionId, municipalityIds, page))
			.map(plannedEducationMapper::toEventIdList)
			.flatMap(List::stream)
			.toList();
	}

	private int getTotalPages(String directionId, Set<String> municipalityIds) {
		return Optional.ofNullable(plannedEducationIntegration.getEducationEventsByReferenceId(directionId, municipalityIds, 0).getBody())
			.map(ListedAdultEducationEventsRM::getPage)
			.map(PageMetadataRM::getTotalPages)
			.orElse(1L).intValue();
	}
}
