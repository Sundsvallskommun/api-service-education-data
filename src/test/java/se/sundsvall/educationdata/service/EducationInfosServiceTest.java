package se.sundsvall.educationdata.service;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.SusaEducationInfoRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfo;
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
class EducationInfosServiceTest {
	@Mock
	private SusaNavetIntegration integration;

	@Mock
	private SusaEducationInfoRepository repository;

	@InjectMocks
	private EducationInfosService service;

	@Test
	void savePageTest_successful() throws IOException {
		final int page = 0;
		final int size = 1;

		final var entity = SusaEducationInfo.builder().build();
		when(integration.getEducationInfosWithPage(page, size)).thenReturn(new SusaPage<>(entity, page));

		service.savePageInfoJsonTable(page, size);

		verify(integration).getEducationInfosWithPage(page, size);
		verify(repository).save(entity);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_succesful() throws IOException {
		final var size = 1;
		final var entity1 = SusaEducationInfo.builder().build();
		final var entity2 = SusaEducationInfo.builder().build();
		final var entity3 = SusaEducationInfo.builder().build();

		when(integration.getEducationInfosWithPage(0, size)).thenReturn(new SusaPage<>(entity1, 3));
		when(integration.getEducationInfosWithPage(1, size)).thenReturn(new SusaPage<>(entity2, 3));
		when(integration.getEducationInfosWithPage(2, size)).thenReturn(new SusaPage<>(entity3, 3));

		service.saveAllPagesInfoJsonTable(1);

		final var captor = ArgumentCaptor.forClass(SusaEducationInfo.class);
		verify(repository, times(3)).save(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(entity1, entity2, entity3);

		verify(integration).getEducationInfosWithPage(0, size);
		verify(integration).getEducationInfosWithPage(1, size);
		verify(integration).getEducationInfosWithPage(2, size);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_withOnlyOnePage() throws IOException {
		final int page = 0;
		final int nonExistentPage = 1;
		final int size = 1;

		final var entity = SusaEducationInfo.builder().build();

		when(integration.getEducationInfosWithPage(page, size)).thenReturn(new SusaPage<>(entity, 1));

		service.saveAllPagesInfoJsonTable(size);

		verify(integration).getEducationInfosWithPage(page, size);
		verify(integration, never()).getEducationInfosWithPage(nonExistentPage, size);
		verify(repository).save(any());
		verifyNoMoreInteractions(integration, repository);
	}
}
