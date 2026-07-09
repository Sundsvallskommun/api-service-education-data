package se.sundsvall.educationdata.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
import se.sundsvall.educationdata.integration.susanavet.SusaPage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EducationProvidersServiceTest {
	@Mock
	private SusaNavetIntegration integration;

	@Mock
	private SusaEducationProviderRepository repository;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	private EducationProvidersService service;

	@Test
	void savePageTest_successful() throws IOException {
		final int page = 0;
		final int size = 1;

		final var entity = SusaEducationProvider.builder().build();
		when(integration.getEducationProvidersWithPage(page, size)).thenReturn(new SusaPage<>(entity, page));

		service.savePageProviderJsonTable(page, size);

		verify(integration).getEducationProvidersWithPage(page, size);
		verify(repository).save(entity);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_succesful() throws IOException {
		final var size = 1;
		final var entity1 = SusaEducationProvider.builder().build();
		final var entity2 = SusaEducationProvider.builder().build();
		final var entity3 = SusaEducationProvider.builder().build();

		when(integration.getEducationProvidersWithPage(0, size)).thenReturn(new SusaPage<>(entity1, 3));
		when(integration.getEducationProvidersWithPage(1, size)).thenReturn(new SusaPage<>(entity2, 3));
		when(integration.getEducationProvidersWithPage(2, size)).thenReturn(new SusaPage<>(entity3, 3));

		service.saveAllPagesProviderJsonTable(1);

		final var captor = ArgumentCaptor.forClass(SusaEducationProvider.class);
		verify(repository, times(3)).save(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(entity1, entity2, entity3);

		verify(integration).getEducationProvidersWithPage(0, size);
		verify(integration).getEducationProvidersWithPage(1, size);
		verify(integration).getEducationProvidersWithPage(2, size);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_withOnlyOnePage() throws IOException {
		final int page = 0;
		final int nonExistentPage = 1;
		final int size = 1;

		final var entity = SusaEducationProvider.builder().build();

		when(integration.getEducationProvidersWithPage(page, size)).thenReturn(new SusaPage<>(entity, 1));

		service.saveAllPagesProviderJsonTable(size);

		verify(integration).getEducationProvidersWithPage(page, size);
		verify(integration, never()).getEducationProvidersWithPage(nonExistentPage, size);
		verify(repository).save(any());
		verifyNoMoreInteractions(integration, repository);
	}
}
