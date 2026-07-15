package se.sundsvall.educationdata.service;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProviderEntity;
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
class EducationProvidersServiceTest {
	@Mock
	private SusaNavetIntegration integration;

	@Mock
	private SusaEducationProviderRepository repository;

	@Mock
	private SusaMapper mapper;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private EducationProvidersService service;

	@Test
	void savePageTest_successful() throws IOException {
		final int page = 0;
		final int size = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationProviderEntity.builder().build();

		when(integration.getEducationProviders(page, size)).thenReturn(json);
		when(mapper.toZippedProviders(json, 0)).thenReturn(entity);

		service.savePageProviderJsonTable(page, size);

		verify(integration).getEducationProviders(page, size);
		verify(repository).save(entity);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_succesful() throws IOException {
		final var size = 1;
		final byte[] json1 = "json1".getBytes();
		final byte[] json2 = "json2".getBytes();
		final byte[] json3 = "json3".getBytes();
		final var entity1 = SusaEducationProviderEntity.builder().build();
		final var entity2 = SusaEducationProviderEntity.builder().build();
		final var entity3 = SusaEducationProviderEntity.builder().build();
		final var firstPage = JsonMapper.builder().build().readTree("{\"page\":{\"totalPages\":3}}");

		when(integration.getEducationProviders(0, size)).thenReturn(json1);
		when(integration.getEducationProviders(1, size)).thenReturn(json2);
		when(integration.getEducationProviders(2, size)).thenReturn(json3);
		when(objectMapper.readTree(json1)).thenReturn(firstPage);
		when(mapper.toZippedProviders(json1, 0)).thenReturn(entity1);
		when(mapper.toZippedProviders(json2, 1)).thenReturn(entity2);
		when(mapper.toZippedProviders(json3, 2)).thenReturn(entity3);

		service.saveAllPagesProviderJsonTable(size);

		final var captor = ArgumentCaptor.forClass(SusaEducationProviderEntity.class);
		verify(repository, times(3)).save(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(entity1, entity2, entity3);

		verify(integration).getEducationProviders(0, size);
		verify(integration).getEducationProviders(1, size);
		verify(integration).getEducationProviders(2, size);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesTest_withOnlyOnePage() throws IOException {
		final int size = 1;
		final int page = 0;
		final int nonExistingPage = 1;
		final byte[] json = "{page:{totalPages:1}}".getBytes();
		final var firstPage = JsonMapper.builder().build().readTree("{\"page\":{\"totalPages\":1}}");
		final var entity = SusaEducationProviderEntity.builder().build();

		when(integration.getEducationProviders(page, size)).thenReturn(json);
		when(objectMapper.readTree(json)).thenReturn(firstPage);
		when(mapper.toZippedProviders(json, 0)).thenReturn(entity);

		service.saveAllPagesProviderJsonTable(size);

		verify(integration).getEducationProviders(page, size);
		verify(integration, never()).getEducationProviders(nonExistingPage, size);
		verify(repository).save(entity);
		verifyNoMoreInteractions(integration, repository);
	}
}
