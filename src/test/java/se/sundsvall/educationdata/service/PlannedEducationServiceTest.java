package se.sundsvall.educationdata.service;

import generated.se.sundsvall.plannededucation.ApiResponseListedAdultEducationEvents;
import generated.se.sundsvall.plannededucation.ListedAdultEducationEventsRM;
import generated.se.sundsvall.plannededucation.PageMetadataRM;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.EventCategoryStagingRepository;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;
import se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegration;
import se.sundsvall.educationdata.service.mapper.PlannedEducationMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlannedEducationServiceTest {

	@Mock
	private PlannedEducationIntegration plannedEducationIntegration;

	@Mock
	private ReferenceCategoryRepository referenceCategoryRepository;

	@Mock
	private PlannedEducationMapper plannedEducationMapper;

	@Mock
	private EventCategoryService eventCategoryService;

	@Mock
	private EventCategoryStagingRepository eventCategoryStagingRepository;

	private PlannedEducationService plannedEducationService;

	@BeforeEach
	void setUp() {
		plannedEducationService = new PlannedEducationService(plannedEducationIntegration, referenceCategoryRepository,
			plannedEducationMapper, Set.of("2281"), eventCategoryService, eventCategoryStagingRepository);
	}

	@Test
	void importReferenceCategories_updateRows() {
		final var existing = ReferenceCategoryEntity.builder()
			.withId("existing").withCategoryId("1").withDirectionId("4").build();
		final var imported = ReferenceCategoryEntity.builder()
			.withCategoryId("1").withDirectionId("4").build();
		final var rows = List.of(imported);

		when(plannedEducationIntegration.getAllReferenceCategories()).thenReturn(rows);
		when(referenceCategoryRepository.findByCategoryIdAndDirectionId("1", "4"))
			.thenReturn(Optional.of(existing));

		plannedEducationService.importReferenceCategories();

		assertThat(imported.getId()).isEqualTo("existing");
		verify(referenceCategoryRepository).saveAll(rows);
	}

	@Test
	void importReferenceCategories_emptyRows() {
		final List<ReferenceCategoryEntity> rows = List.of();
		when(plannedEducationIntegration.getAllReferenceCategories()).thenReturn(rows);

		assertThatThrownBy(() -> plannedEducationService.importReferenceCategories())
			.hasMessageContaining("No content");

		verify(plannedEducationIntegration).getAllReferenceCategories();
		verifyNoMoreInteractions(plannedEducationIntegration);
	}

	@Test
	void refreshEventCategoryRelations_singlePage() {
		final var directionId = "1";
		final var response = new ApiResponseListedAdultEducationEvents()
			.body(new ListedAdultEducationEventsRM().page(new PageMetadataRM().totalPages(1L)));

		when(referenceCategoryRepository.findDistinctDirectionIds()).thenReturn(Set.of(directionId));
		when(plannedEducationIntegration.getEducationEventsByReferenceId(directionId, Set.of("2281"), 0)).thenReturn(response);
		when(plannedEducationMapper.toEventIdList(response)).thenReturn(List.of("e.1", "e.2"));

		plannedEducationService.refreshEventCategoryRelations();

		final var order = inOrder(eventCategoryService);
		order.verify(eventCategoryService).addToEventCategoryRelationsStagingTable(directionId, List.of("e.1", "e.2"));
		order.verify(eventCategoryService).replaceEventCategoryWithStagedData();
		verify(eventCategoryStagingRepository).deleteAllInBatch();
	}

	@Test
	void refreshEventCategoryRelations_multiplePages() {
		final var directionId = "1";
		final var page0 = new ApiResponseListedAdultEducationEvents()
			.body(new ListedAdultEducationEventsRM().page(new PageMetadataRM().number(0L).totalPages(2L)));
		final var page1 = new ApiResponseListedAdultEducationEvents()
			.body(new ListedAdultEducationEventsRM().page(new PageMetadataRM().number(1L).totalPages(2L)));

		when(referenceCategoryRepository.findDistinctDirectionIds()).thenReturn(Set.of(directionId));
		when(plannedEducationIntegration.getEducationEventsByReferenceId(directionId, Set.of("2281"), 0)).thenReturn(page0);
		when(plannedEducationIntegration.getEducationEventsByReferenceId(directionId, Set.of("2281"), 1)).thenReturn(page1);
		when(plannedEducationMapper.toEventIdList(page0)).thenReturn(List.of("e.1"));
		when(plannedEducationMapper.toEventIdList(page1)).thenReturn(List.of("e.2"));

		plannedEducationService.refreshEventCategoryRelations();

		verify(plannedEducationIntegration, times(2)).getEducationEventsByReferenceId(directionId, Set.of("2281"), 0);
		verify(plannedEducationIntegration).getEducationEventsByReferenceId(directionId, Set.of("2281"), 1);
		verify(eventCategoryService).addToEventCategoryRelationsStagingTable(directionId, List.of("e.1", "e.2"));
		verify(eventCategoryService).replaceEventCategoryWithStagedData();
		verify(eventCategoryStagingRepository).deleteAllInBatch();

	}

	@Test
	void refreshEventCategoryRelations_noDirections() {
		when(referenceCategoryRepository.findDistinctDirectionIds()).thenReturn(Set.of());

		plannedEducationService.refreshEventCategoryRelations();

		verify(eventCategoryService).replaceEventCategoryWithStagedData();
		verify(eventCategoryService, never()).addToEventCategoryRelationsStagingTable(any(), any());
		verify(eventCategoryStagingRepository).deleteAllInBatch();
		verifyNoMoreInteractions(plannedEducationIntegration);
	}

	@Test
	void refreshEventCategoryRelations_directionFails() {
		final var directionId = "id-1";
		final var whitelist = Set.of("2281");
		when(referenceCategoryRepository.findDistinctDirectionIds()).thenReturn(Set.of(directionId));
		when(plannedEducationIntegration.getEducationEventsByReferenceId(directionId, whitelist, 0))
			.thenThrow(new RuntimeException("boom"));

		assertThatThrownBy(() -> plannedEducationService.refreshEventCategoryRelations())
			.hasMessageContaining("Failed to add relations for directions");

		verify(eventCategoryService, never()).replaceEventCategoryWithStagedData();
		verify(eventCategoryStagingRepository).deleteAllInBatch();
	}

	@Test
	void refreshEventCategoryRelations_directionWithNoEvents() {
		final var directionId = "id-1";
		final var whitelist = Set.of("2281");

		final var response = new ApiResponseListedAdultEducationEvents()
			.body(new ListedAdultEducationEventsRM().page(new PageMetadataRM().totalPages(1L)));

		when(referenceCategoryRepository.findDistinctDirectionIds()).thenReturn(Set.of(directionId));
		when(plannedEducationIntegration.getEducationEventsByReferenceId(directionId, whitelist, 0))
			.thenReturn(response);
		when(plannedEducationMapper.toEventIdList(response)).thenReturn(List.of());

		plannedEducationService.refreshEventCategoryRelations();

		verify(eventCategoryService).replaceEventCategoryWithStagedData();
		verify(eventCategoryService, never()).addToEventCategoryRelationsStagingTable(any(), any());
		verify(eventCategoryStagingRepository).deleteAllInBatch();
		verifyNoMoreInteractions(plannedEducationIntegration);
	}
}
