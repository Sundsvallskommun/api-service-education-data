package se.sundsvall.educationdata.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.educationdata.integration.db.EventCategoryRepository;
import se.sundsvall.educationdata.integration.db.EventCategoryStagingRepository;
import se.sundsvall.educationdata.service.mapper.PlannedEducationMapper;

@Service
public class EventCategoryService {

	private final EventCategoryRepository eventCategoryRepository;
	private final EventCategoryStagingRepository eventCategoryStagingRepository;
	private final PlannedEducationMapper plannedEducationMapper;

	public EventCategoryService(EventCategoryRepository eventCategoryRepository, EventCategoryStagingRepository eventCategoryStagingRepository, PlannedEducationMapper plannedEducationMapper) {
		this.eventCategoryRepository = eventCategoryRepository;
		this.eventCategoryStagingRepository = eventCategoryStagingRepository;
		this.plannedEducationMapper = plannedEducationMapper;
	}

	@Transactional
	public void addToEventCategoryRelationsStagingTable(String directionId, List<String> eventIds) {
		var eventRelations = eventIds.stream()
			.map(eventId -> plannedEducationMapper.toEventCategoryStaging(directionId, eventId))
			.toList();
		eventCategoryStagingRepository.saveAll(eventRelations);
	}

	@Transactional
	public void replaceEventCategoryWithStagedData() {
		eventCategoryRepository.deleteEventRelationsForStagedEventIds();
		eventCategoryRepository.saveStagedToEventCategory();
	}
}
