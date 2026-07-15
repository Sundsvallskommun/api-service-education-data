package se.sundsvall.educationdata.integration.plannededucation;

import generated.se.sundsvall.plannededucation.ApiResponseAreasRM;
import generated.se.sundsvall.plannededucation.AreaRM;
import generated.se.sundsvall.plannededucation.AreasRM;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
		when(clientMock.getAllAreas()).thenReturn(response);
		when(mapper.toReferenceCategory(response)).thenReturn(rows);

		final var result = integration.getAllAreas();

		assertThat(result).isSameAs(rows);
		verify(clientMock).getAllAreas();
		verify(mapper).toReferenceCategory(response);
		verifyNoMoreInteractions(clientMock, mapper);
	}

	@Test
	void getAreas_nullResponse() {
		when(clientMock.getAllAreas()).thenReturn(null);

		assertThatThrownBy(() -> integration.getAllAreas())
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Empty body");

		verify(clientMock).getAllAreas();
		verifyNoInteractions(mapper);
	}

	@Test
	void getAllAreas_emptyAreas() {
		final var response = new ApiResponseAreasRM().body(new AreasRM().areas(List.of()));
		when(clientMock.getAllAreas()).thenReturn(response);

		assertThatThrownBy(() -> integration.getAllAreas())
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Empty body");

		verify(clientMock).getAllAreas();
		verifyNoInteractions(mapper);
	}

	@Test
	void getAllAreas_bodyIsNull() {
		when(clientMock.getAllAreas()).thenReturn(new ApiResponseAreasRM());
		assertThatThrownBy(() -> integration.getAllAreas())
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Empty body");
		verify(clientMock).getAllAreas();
		verifyNoInteractions(mapper);
	}

	@Test
	void getAllAreas_areasIsNull() {
		final var response = new ApiResponseAreasRM().body(new AreasRM().areas(null));
		when(clientMock.getAllAreas()).thenReturn(response);
		assertThatThrownBy(() -> integration.getAllAreas())
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Empty body");
		verify(clientMock).getAllAreas();
		verifyNoInteractions(mapper);
	}
}
