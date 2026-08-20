package se.sundsvall.educationdata.service;

import generated.se.sundsvall.plannededucation.ApiResponseListedAdultEducationEvents;
import generated.se.sundsvall.plannededucation.ListedAdultEducationEventsRM;
import generated.se.sundsvall.plannededucation.PageMetadataRM;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.EventCategoryRepository;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.EventCategoryEntity;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;
import se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegration;
import se.sundsvall.educationdata.service.mapper.PlannedEducationMapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlannedEducationServiceTest {

	@Mock
	private PlannedEducationIntegration integration;

	@Mock
	private ReferenceCategoryRepository referenceCategoryRepository;

	@Mock
	private EventCategoryRepository eventCategoryRepository;

	@Mock
	private PlannedEducationMapper mapper;

	@InjectMocks
	private PlannedEducationService service;

	@BeforeEach
	void setUp() {
		service = new PlannedEducationService(integration, referenceCategoryRepository,
			eventCategoryRepository, mapper, Set.of("2281"));
	}

	@Test
	void importReferenceCategories_updateRows() {
		final var rows = List.of(ReferenceCategoryEntity.builder().withCategoryId("1").build());
		when(integration.getAllReferenceCategories()).thenReturn(rows);

		service.importReferenceCategories();

		final var order = inOrder(referenceCategoryRepository);
		order.verify(referenceCategoryRepository).deleteAllInBatch();
		order.verify(referenceCategoryRepository).saveAll(rows);

		verify(integration).getAllReferenceCategories();
		verifyNoMoreInteractions(integration, referenceCategoryRepository);
	}

	@Test
	void importReferenceCategories_emptyRows() {
		final List<ReferenceCategoryEntity> rows = List.of();
		when(integration.getAllReferenceCategories()).thenReturn(rows);

		assertThatThrownBy(() -> service.importReferenceCategories())
			.hasMessageContaining("No content");

		verify(integration).getAllReferenceCategories();
		verifyNoMoreInteractions(integration);
	}

	@Test
	void refreshEventCategoryRelations_singlePage() {
		final var directionId = "1";
		final var response = new ApiResponseListedAdultEducationEvents()
			.body(new ListedAdultEducationEventsRM().page(new PageMetadataRM().totalPages(1L)));
		final var rel1 = EventCategoryEntity.builder().withEducationEventId("e.1").withDirectionId(directionId).build();
		final var rel2 = EventCategoryEntity.builder().withEducationEventId("e.2").withDirectionId(directionId).build();

		when(referenceCategoryRepository.findDistinctDirectionIds()).thenReturn(Set.of(directionId));
		when(integration.getByReferenceId(directionId, "2281", 0)).thenReturn(response);
		when(mapper.toEventIdList(response)).thenReturn(List.of("e.1", "e.2"));
		when(mapper.toEventCategory(directionId, "e.1")).thenReturn(rel1);
		when(mapper.toEventCategory(directionId, "e.2")).thenReturn(rel2);

		service.refreshEventCategoryRelations();

		final var order = inOrder(eventCategoryRepository);
		order.verify(eventCategoryRepository).deleteByEducationEventIdIn(Set.of("e.1", "e.2"));
		order.verify(eventCategoryRepository).saveAll(List.of(rel1, rel2));
	}

	@Test
	void refreshEventCategoryRelations_multiplePages() {
		final var directionId = "1";
		final var page0 = new ApiResponseListedAdultEducationEvents()
			.body(new ListedAdultEducationEventsRM().page(new PageMetadataRM().number(0L).totalPages(2L)));
		final var page1 = new ApiResponseListedAdultEducationEvents()
			.body(new ListedAdultEducationEventsRM().page(new PageMetadataRM().number(1L).totalPages(2L)));
		final var rel1 = EventCategoryEntity.builder().withEducationEventId("e.1").withDirectionId(directionId).build();
		final var rel2 = EventCategoryEntity.builder().withEducationEventId("e.2").withDirectionId(directionId).build();

		when(referenceCategoryRepository.findDistinctDirectionIds()).thenReturn(Set.of(directionId));
		when(integration.getByReferenceId(directionId, "2281", 0)).thenReturn(page0);
		when(integration.getByReferenceId(directionId, "2281", 1)).thenReturn(page1);
		when(mapper.toEventIdList(page0)).thenReturn(List.of("e.1"));
		when(mapper.toEventIdList(page1)).thenReturn(List.of("e.2"));
		when(mapper.toEventCategory(directionId, "e.1")).thenReturn(rel1);
		when(mapper.toEventCategory(directionId, "e.2")).thenReturn(rel2);

		service.refreshEventCategoryRelations();

		verify(integration).getByReferenceId(directionId, "2281", 0);
		verify(integration).getByReferenceId(directionId, "2281", 1);
		verify(eventCategoryRepository).deleteByEducationEventIdIn(Set.of("e.1", "e.2"));
		verify(eventCategoryRepository).saveAll(List.of(rel1, rel2));
	}

	@Test
	void refreshEventCategoryRelations_noDirections() {
		when(referenceCategoryRepository.findDistinctDirectionIds()).thenReturn(Set.of());

		service.refreshEventCategoryRelations();

		verify(eventCategoryRepository, never()).deleteByEducationEventIdIn(Set.of());
		verify(eventCategoryRepository).saveAll(List.of());
		verifyNoMoreInteractions(integration);
	}
}
