package se.sundsvall.educationdata.service;

import generated.se.sundsvall.plannededucation.ListedAdultEducationEventsRM;
import generated.se.sundsvall.plannededucation.PageMetadataRM;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.educationdata.integration.db.EventCategoryRepository;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.EventCategoryEntity;
import se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegration;
import se.sundsvall.educationdata.service.mapper.PlannedEducationMapper;

import static java.util.stream.Collectors.toSet;
import static se.sundsvall.dept44.problem.Problem.badGateway;

@Service
public class PlannedEducationService {

	private final PlannedEducationIntegration plannedEducationIntegration;
	private final ReferenceCategoryRepository referenceCategoryRepository;
	private final EventCategoryRepository eventCategoryRepository;
	private final PlannedEducationMapper plannedEducationMapper;
	private final Set<String> municipalityIdWhitelist;

	public PlannedEducationService(PlannedEducationIntegration plannedEducationIntegration, ReferenceCategoryRepository referenceCategoryRepository,
		EventCategoryRepository eventCategoryRepository, PlannedEducationMapper plannedEducationMapper,
		@Value("${municipality-whitelist: 2281}") Set<String> municipalityIdWhitelist) {
		this.plannedEducationIntegration = plannedEducationIntegration;
		this.referenceCategoryRepository = referenceCategoryRepository;
		this.eventCategoryRepository = eventCategoryRepository;
		this.plannedEducationMapper = plannedEducationMapper;
		this.municipalityIdWhitelist = municipalityIdWhitelist;
	}

	@Transactional
	public void importReferenceCategories() {
		var categories = plannedEducationIntegration.getAllReferenceCategories();

		if (categories.isEmpty()) {
			throw badGateway(
				"No content");
		}

		referenceCategoryRepository.deleteAllInBatch();
		referenceCategoryRepository.saveAll(categories);
	}

	@Transactional
	public void refreshEventCategoryRelations() {
		final var eventCategoryRelations = new ArrayList<EventCategoryEntity>();

		final var distinctDirectionIds = referenceCategoryRepository.findDistinctDirectionIds();
		for (final var directionId : distinctDirectionIds) {
			getFilteredEventIdsByDirection(directionId).stream()
				.map(eventId -> plannedEducationMapper.toEventCategory(directionId, eventId))
				.forEach(eventCategoryRelations::add);
		}

		var fetchedEventIds = eventCategoryRelations.stream()
			.map(EventCategoryEntity::getEducationEventId)
			.collect(toSet());

		if (!fetchedEventIds.isEmpty()) {
			eventCategoryRepository.deleteByEducationEventIdIn(fetchedEventIds);
		}

		eventCategoryRepository.saveAll(eventCategoryRelations);
	}

	private List<String> getFilteredEventIdsByDirection(String directionId) {
		final var eventIds = new ArrayList<String>();
		for (var municipalityId : municipalityIdWhitelist) {
			eventIds.addAll(getEventIdsByDirectionAndMunicipality(directionId, municipalityId));
		}
		return eventIds;
	}

	private List<String> getEventIdsByDirectionAndMunicipality(String directionId, String municipality) {
		int page = 0;
		var result = plannedEducationIntegration.getByReferenceId(directionId, municipality, page);

		long totalPages = Optional.ofNullable(result.getBody())
			.map(ListedAdultEducationEventsRM::getPage)
			.map(PageMetadataRM::getTotalPages).orElse(1L).intValue();

		var eventIds = new ArrayList<>(plannedEducationMapper.toEventIdList(result));

		for (page = 1; page < totalPages; page++) {
			eventIds.addAll(plannedEducationMapper.toEventIdList(plannedEducationIntegration.getByReferenceId(directionId, municipality, page)));
		}
		return eventIds;
	}
}
