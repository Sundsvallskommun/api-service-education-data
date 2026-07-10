package se.sundsvall.educationdata.integration.plannededucation;

import com.fasterxml.jackson.core.JsonProcessingException;
import generated.se.sundsvall.plannededucation.ApiResponseAreasRM;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategory;

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
	void getAreas() throws JsonProcessingException {
		final var response = new ApiResponseAreasRM();
		final var rows = List.of(ReferenceCategory.builder().withCategoryId("1").build());
		when(clientMock.getAllAreas()).thenReturn(response);
		when(mapper.toReferenceCategory(response)).thenReturn(rows);

		final var result = integration.getAllAreas();

		assertThat(result).isSameAs(rows);
		verify(clientMock).getAllAreas();
		verify(mapper).toReferenceCategory(response);
		verifyNoMoreInteractions(clientMock, mapper);
	}
}
