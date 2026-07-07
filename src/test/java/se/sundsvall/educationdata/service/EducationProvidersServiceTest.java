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
import se.sundsvall.educationdata.integration.db.SusaEducationProviderRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProvider;
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
public class EducationProvidersServiceTest {
	@Mock
	private SusaNavetIntegration integration;

	@Mock
	private SusaEducationProviderRepository repository;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	private EducationProvidersService service;

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

		when(integration.getEducationProviders(page, size)).thenReturn(json);

		service.savePageProviderJsonTable(page, size);

		final var captor = ArgumentCaptor.forClass(SusaEducationProvider.class);
		verify(repository).save(captor.capture());
		final var saved = captor.getValue();

		assertThat(saved.getJsonBody()).isEqualTo(json);
		assertThat(saved.getDateCollected()).isEqualTo(LocalDate.now());

		verify(integration).getEducationProviders(page, size);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_succesful() throws JsonProcessingException {
		final int size = 1;

		when(integration.getEducationProviders(0, size)).thenReturn(json);
		when(integration.getEducationProviders(1, size)).thenReturn(json);
		when(integration.getEducationProviders(2, size)).thenReturn(json);

		service.saveAllPagesProviderJsonTable(1);

		final var captor = ArgumentCaptor.forClass(SusaEducationProvider.class);
		verify(repository, times(3)).save(captor.capture());
		final var saved = captor.getAllValues();

		assertThat(saved).hasSize(3);
		assertThat(saved).extracting(SusaEducationProvider::getJsonBody).containsExactly(json, json, json);
		assertThat(saved).allSatisfy(e -> assertThat(e.getDateCollected()).isEqualTo(LocalDate.now()));

		verify(integration).getEducationProviders(0, size);
		verify(integration).getEducationProviders(1, size);
		verify(integration).getEducationProviders(2, size);
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

		when(integration.getEducationProviders(page, size)).thenReturn(oneTotalPages);

		service.saveAllPagesProviderJsonTable(size);

		verify(integration).getEducationProviders(page, size);
		verify(integration, never()).getEducationProviders(nonExistentPage, size);
		verify(repository).save(any());
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void savePage_whenBodyIsNull_doesNotSave() {
		final int page = 0;
		final int size = 1;

		when(integration.getEducationProviders(page, size)).thenReturn(null);

		assertThatThrownBy(() -> service.savePageProviderJsonTable(page, size))
			.isInstanceOf(IllegalStateException.class).hasMessage("Empty body for page %d".formatted(page));

		verifyNoInteractions(repository);
	}

	@Test
	void saveAllPage_whenBodyIsNull_doesNotSave() {
		final int page = 0;
		final int size = 1;

		when(integration.getEducationProviders(page, size)).thenReturn(null);

		assertThatThrownBy(() -> service.saveAllPagesProviderJsonTable(size))
			.isInstanceOf(IllegalStateException.class).hasMessage("Empty body for page %d".formatted(page));

		verifyNoInteractions(repository);
	}
}
