package se.sundsvall.educationdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.SusaEducationInfoRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfo;
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
class EducationInfosServiceTest {
	@Mock
	private SusaNavetIntegration integration;

	@Mock
	private SusaEducationInfoRepository repository;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	private EducationInfosService service;

	private static final String JSON = """
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

		when(integration.getEducationInfos(page, size)).thenReturn(JSON);

		service.savePageInfoJsonTable(page, size);

		final var captor = ArgumentCaptor.forClass(SusaEducationInfo.class);
		verify(repository).save(captor.capture());
		final var saved = captor.getValue();

		assertThat(saved.getJsonBody()).isEqualTo(JSON);
		assertThat(saved.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));

		verify(integration).getEducationInfos(page, size);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_successful() throws JsonProcessingException {
		final int size = 1;

		when(integration.getEducationInfos(0, size)).thenReturn(JSON);
		when(integration.getEducationInfos(1, size)).thenReturn(JSON);
		when(integration.getEducationInfos(2, size)).thenReturn(JSON);

		service.saveAllPagesInfoJsonTable(1);

		final var captor = ArgumentCaptor.forClass(SusaEducationInfo.class);
		verify(repository, times(3)).save(captor.capture());
		final var saved = captor.getAllValues();

		assertThat(saved).hasSize(3);
		assertThat(saved).extracting(SusaEducationInfo::getJsonBody).containsExactly(JSON, JSON, JSON);
		assertThat(saved).allSatisfy(e -> assertThat(e.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault())));

		verify(integration).getEducationInfos(0, size);
		verify(integration).getEducationInfos(1, size);
		verify(integration).getEducationInfos(2, size);
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

		when(integration.getEducationInfos(page, size)).thenReturn(oneTotalPages);

		service.saveAllPagesInfoJsonTable(size);

		verify(integration).getEducationInfos(page, size);
		verify(integration, never()).getEducationInfos(nonExistentPage, size);
		verify(repository).save(any());
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void savePage_whenBodyIsNull_doesNotSave() {
		final int page = 0;
		final int size = 1;

		when(integration.getEducationInfos(page, size)).thenReturn(null);

		assertThatThrownBy(() -> service.savePageInfoJsonTable(page, size))
			.isInstanceOf(IllegalStateException.class).hasMessage("Empty body for page %d".formatted(page));

		verifyNoInteractions(repository);
	}

	@Test
	void saveAllPage_whenBodyIsNull_doesNotSave() {
		final int page = 0;
		final int size = 1;

		when(integration.getEducationInfos(page, size)).thenReturn(null);

		assertThatThrownBy(() -> service.saveAllPagesInfoJsonTable(size))
			.isInstanceOf(IllegalStateException.class).hasMessage("Empty body for page %d".formatted(page));

		verifyNoInteractions(repository);
	}
}
