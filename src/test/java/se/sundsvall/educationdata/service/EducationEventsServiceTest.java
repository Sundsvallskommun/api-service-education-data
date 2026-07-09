package se.sundsvall.educationdata.service;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.SusaEducationEventRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEvent;
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
class EducationEventsServiceTest {

	@Mock
	private SusaNavetIntegration integration;

	@Mock
	private SusaEducationEventRepository repository;

	@InjectMocks
	private EducationEventsService service;

	@Test
	void savePageTest_successful() throws IOException {
		final int page = 0;
		final int size = 1;

		final var entity = SusaEducationEvent.builder().build();
		when(integration.getEducationEventsWithPage(page, size)).thenReturn(new SusaPage<>(entity, page));

		service.savePageEventJsonTable(page, size);

		verify(integration).getEducationEventsWithPage(page, size);
		verify(repository).save(entity);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_succesful() throws IOException {
		final var size = 1;
		final var entity1 = SusaEducationEvent.builder().build();
		final var entity2 = SusaEducationEvent.builder().build();
		final var entity3 = SusaEducationEvent.builder().build();

		when(integration.getEducationEventsWithPage(0, size)).thenReturn(new SusaPage<>(entity1, 3));
		when(integration.getEducationEventsWithPage(1, size)).thenReturn(new SusaPage<>(entity2, 3));
		when(integration.getEducationEventsWithPage(2, size)).thenReturn(new SusaPage<>(entity3, 3));

		service.saveAllPagesEventsJsonTable(1);

		final var captor = ArgumentCaptor.forClass(SusaEducationEvent.class);
		verify(repository, times(3)).save(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(entity1, entity2, entity3);

		verify(integration).getEducationEventsWithPage(0, size);
		verify(integration).getEducationEventsWithPage(1, size);
		verify(integration).getEducationEventsWithPage(2, size);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_withOnlyOnePage() throws IOException {
		final int page = 0;
		final int nonExistentPage = 1;
		final int size = 1;

		final var entity = SusaEducationEvent.builder().build();

		when(integration.getEducationEventsWithPage(page, size)).thenReturn(new SusaPage<>(entity, 1));

		service.saveAllPagesEventsJsonTable(size);

		verify(integration).getEducationEventsWithPage(page, size);
		verify(integration, never()).getEducationEventsWithPage(nonExistentPage, size);
		verify(repository).save(any());
		verifyNoMoreInteractions(integration, repository);
	}
}
