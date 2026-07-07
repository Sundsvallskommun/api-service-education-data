package se.sundsvall.educationdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.SusaEducationEventRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEvent;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EducationEventsServiceTest {

	@Mock
	private SusaNavetIntegration integration;

	@Mock
	private SusaEducationEventRepository repository;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	private EducationEventsService service;

	private static final String json = """
		{
		  "data": [],
		  "page": {
		    "totalPages": 3
		  }
		}
		""";

	@Test
	void savePageTest_successful() {
		final int page = 0;
		final int size = 1;

		when(integration.getEducationEvents(page, size)).thenReturn(json);

		service.savePageEventJsonTable(page, size);

		final var captor = ArgumentCaptor.forClass(SusaEducationEvent.class);
		verify(repository).save(captor.capture());
		final var saved = captor.getValue();

		assertThat(saved.getJsonBody()).isEqualTo(json);
		assertThat(saved.getDateCollected()).isEqualTo(LocalDate.now());

		verify(integration).getEducationEvents(page, size);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_succesful() throws JsonProcessingException {
		final int size = 1;

		when(integration.getEducationEvents(0, size)).thenReturn(json);
		when(integration.getEducationEvents(1, size)).thenReturn(json);
		when(integration.getEducationEvents(2, size)).thenReturn(json);

		service.saveAllPagesEventsJsonTable(1);

		final var captor = ArgumentCaptor.forClass(SusaEducationEvent.class);
		verify(repository, times(3)).save(captor.capture());
		final var saved = captor.getAllValues();

		assertThat(saved).hasSize(3);
		assertThat(saved).extracting(SusaEducationEvent::getJsonBody).containsExactly(json, json, json);
		assertThat(saved).allSatisfy(e -> assertThat(e.getDateCollected()).isEqualTo(LocalDate.now()));

		verify(integration).getEducationEvents(0, size);
		verify(integration).getEducationEvents(1, size);
		verify(integration).getEducationEvents(2, size);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_withOnlyOnePage() throws JsonProcessingException {
		final int page = 0;
		final int nonExistentPage = 1;
		final int size = 1;

		final var oneTotalPages = """
			{
			  "data": [],
			  "page": {
			     "totalPages": 1
			  }
			}
			""";

		when(integration.getEducationEvents(page, size)).thenReturn(oneTotalPages);

		service.saveAllPagesEventsJsonTable(size);

		verify(integration).getEducationEvents(page, size);
		verify(integration, never()).getEducationEvents(nonExistentPage, size);
		verify(repository).save(any());
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void savePage_whenBodyIsNull_doesNotSave() {
		final int page = 0;
		final int size = 1;

		when(integration.getEducationEvents(page, size)).thenReturn(null);

		assertThatThrownBy(() -> service.savePageEventJsonTable(page, size))
			.isInstanceOf(IllegalStateException.class).hasMessage("Empty body for page %d".formatted(page));

		verifyNoInteractions(repository);
	}

	@Test
	void saveAllPage_whenBodyIsNull_doesNotSave() {
		final int page = 0;
		final int size = 1;

		when(integration.getEducationEvents(page, size)).thenReturn(null);

		assertThatThrownBy(() -> service.saveAllPagesEventsJsonTable(size))
			.isInstanceOf(IllegalStateException.class).hasMessage("Empty body for page %d".formatted(page));

		verifyNoInteractions(repository);
	}
}
