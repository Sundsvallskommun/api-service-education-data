package se.sundsvall.educationdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategory;
import se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlannedEducationServiceTest {

	@Mock
	private PlannedEducationIntegration integration;

	@Mock
	private ReferenceCategoryRepository repository;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	private PlannedEducationService service;

	private static final String json = """
		{
		  "body": {
		    "areas": [
		      { "areaId": "1", "name": "Bygg och anläggning",
		        "directions": [
		          { "directionId": "4", "name": "Arkitektur" },
		          { "directionId": "9", "name": "Bergteknik" }
		        ]
		      }
		    ]
		  }
		}
		""";

	@Test
	void getCategoryInfo_updateRows() throws JsonProcessingException {
		when(integration.getAllAreas()).thenReturn(json);

		final var response = service.getEducationInfo();

		final ArgumentCaptor<List<ReferenceCategory>> captor = ArgumentCaptor.captor();
		final var order = inOrder(repository);
		order.verify(repository).deleteAllInBatch();
		order.verify(repository).saveAll(captor.capture());

		final var rows = captor.getValue();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(rows).hasSize(2);
		assertThat(rows).extracting(
			ReferenceCategory::getCategoryId,
			ReferenceCategory::getDirectionId,
			ReferenceCategory::getDirectionName).containsExactly(
				tuple("1", "4", "Arkitektur"),
				tuple("1", "9", "Bergteknik"));

		verify(integration).getAllAreas();
		verifyNoMoreInteractions(integration, repository);

	}

	@Test
	void getEducationInfo_whenBodyIsNull() {
		when(integration.getAllAreas()).thenReturn(null);

		assertThatThrownBy(() -> service.getEducationInfo())
			.isInstanceOf(IllegalArgumentException.class);
		verify(integration).getAllAreas();
		verifyNoInteractions(repository);
	}
}
