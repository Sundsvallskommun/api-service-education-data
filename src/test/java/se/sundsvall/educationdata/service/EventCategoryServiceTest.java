package se.sundsvall.educationdata.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.EventCategoryRepository;
import se.sundsvall.educationdata.integration.db.EventCategoryStagingRepository;
import se.sundsvall.educationdata.integration.db.model.EventCategoryStagingEntity;
import se.sundsvall.educationdata.service.mapper.PlannedEducationMapper;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventCategoryServiceTest {

	@Mock
	private EventCategoryRepository eventCategoryRepository;
	@Mock
	private EventCategoryStagingRepository eventCategoryStagingRepository;
	@Mock
	private PlannedEducationMapper plannedEducationMapper;

	@InjectMocks
	private EventCategoryService eventCategoryService;

	@Test
	void addToEventCategoryRelationsStagingTable() {
		final var directionId = "id-1";
		final var stagedEntity1 = EventCategoryStagingEntity.builder()
			.withDirectionId(directionId).withEducationEventId("e.1").build();
		final var stagedEntity2 = EventCategoryStagingEntity.builder()
			.withDirectionId(directionId).withEducationEventId("e.2").build();

		when(plannedEducationMapper.toEventCategoryStaging(directionId, "e.1"))
			.thenReturn(stagedEntity1);
		when(plannedEducationMapper.toEventCategoryStaging(directionId, "e.2"))
			.thenReturn(stagedEntity2);

		eventCategoryService.addToEventCategoryRelationsStagingTable(directionId, List.of("e.1", "e.2"));

		verify(eventCategoryStagingRepository).saveAll(List.of(stagedEntity1, stagedEntity2));
	}

	@Test
	void replaceEventCategoryWithStagedData() {
		eventCategoryService.replaceEventCategoryWithStagedData();

		final var inOrder = Mockito.inOrder(eventCategoryRepository);
		inOrder.verify(eventCategoryRepository).deleteEventRelationsForStagedEventIds();
		inOrder.verify(eventCategoryRepository).saveStagedToEventCategory();
	}
}
