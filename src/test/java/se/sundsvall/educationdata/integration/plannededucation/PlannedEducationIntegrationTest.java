package se.sundsvall.educationdata.integration.plannededucation;

import generated.se.sundsvall.plannededucation.ApiResponseAreasRM;
import generated.se.sundsvall.plannededucation.ApiResponseListedAdultEducationEvents;
import generated.se.sundsvall.plannededucation.AreaRM;
import generated.se.sundsvall.plannededucation.AreasRM;
import generated.se.sundsvall.plannededucation.ListedAdultEducationEventsRM;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlannedEducationIntegrationTest {

	@Mock
	private PlannedEducationClient clientMock;
	@Mock
	private PlannedEducationIntegrationMapper mapper;
	@InjectMocks
	private PlannedEducationIntegration integration;

	@Test
	void getAreas() {
		final var response = new ApiResponseAreasRM().body(new AreasRM().areas(List.of(new AreaRM().areaId(1L).name("Bygg"))));
		final var rows = List.of(ReferenceCategoryEntity.builder().withCategoryId("1").build());
		when(clientMock.getAllReferenceCategories()).thenReturn(response);
		when(mapper.toReferenceCategory(response)).thenReturn(rows);

		final var result = integration.getAllReferenceCategories();

		assertThat(result).isSameAs(rows);
		verify(clientMock).getAllReferenceCategories();
		verify(mapper).toReferenceCategory(response);
		verifyNoMoreInteractions(clientMock, mapper);
	}

	@Test
	void getEducationEventsByReferenceId() {
		final var response = new ApiResponseListedAdultEducationEvents().body(new ListedAdultEducationEventsRM());
		when(clientMock.getEventsByDirection("e.1", Set.of("2281"), 0, 200)).thenReturn(response);

		final var result = integration.getEducationEventsByReferenceId("e.1", Set.of("2281"), 0);

		assertThat(result).isSameAs(response);
		verify(clientMock).getEventsByDirection("e.1", Set.of("2281"), 0, 200);
	}
}
