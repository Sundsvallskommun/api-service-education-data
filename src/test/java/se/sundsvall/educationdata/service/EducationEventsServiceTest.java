package se.sundsvall.educationdata.service;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.SusaEducationEventRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEventEntity;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.SusaMapper;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
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

	@Mock
	private SusaMapper mapper;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private EducationEventsService service;

	@Test
	void savePageTest_successful() throws IOException {
		final int page = 0;
		final int size = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationEventEntity.builder().build();

		when(integration.getEducationEvents(page, size)).thenReturn(json);
		when(mapper.toZippedEvents(json, 0)).thenReturn(entity);

		service.savePageEventJsonTable(page, size);

		verify(integration).getEducationEvents(page, size);
		verify(repository).save(entity);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_succesful() throws IOException {
		final var size = 1;
		final byte[] json1 = "json1".getBytes();
		final byte[] json2 = "json2".getBytes();
		final byte[] json3 = "json3".getBytes();
		final var entity1 = SusaEducationEventEntity.builder().build();
		final var entity2 = SusaEducationEventEntity.builder().build();
		final var entity3 = SusaEducationEventEntity.builder().build();
		final var firstPage = JsonMapper.builder().build().readTree("{\"page\":{\"totalPages\":3}}");

		when(integration.getEducationEvents(0, size)).thenReturn(json1);
		when(integration.getEducationEvents(1, size)).thenReturn(json2);
		when(integration.getEducationEvents(2, size)).thenReturn(json3);
		when(objectMapper.readTree(json1)).thenReturn(firstPage);
		when(mapper.toZippedEvents(json1, 0)).thenReturn(entity1);
		when(mapper.toZippedEvents(json2, 1)).thenReturn(entity2);
		when(mapper.toZippedEvents(json3, 2)).thenReturn(entity3);

		service.saveAllPagesEventsJsonTable(size);

		final var captor = ArgumentCaptor.forClass(SusaEducationEventEntity.class);
		verify(repository, times(3)).save(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(entity1, entity2, entity3);

		verify(integration).getEducationEvents(0, size);
		verify(integration).getEducationEvents(1, size);
		verify(integration).getEducationEvents(2, size);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_withOnlyOnePage() throws IOException {
		final int size = 1;
		final int page = 0;
		final int nonExistingPage = 1;
		final byte[] json = "{\"page\":{\"totalPages\":1}}".getBytes();
		final var firstPage = JsonMapper.builder().build().readTree("{\"page\":{\"totalPages\":1}}");
		final var entity = SusaEducationEventEntity.builder().build();

		when(integration.getEducationEvents(page, size)).thenReturn(json);
		when(objectMapper.readTree(json)).thenReturn(firstPage);
		when(mapper.toZippedEvents(json, 0)).thenReturn(entity);

		service.saveAllPagesEventsJsonTable(size);

		verify(integration).getEducationEvents(page, size);
		verify(integration, never()).getEducationEvents(nonExistingPage, size);
		verify(repository).save(entity);
		verifyNoMoreInteractions(integration, repository);
	}
}
