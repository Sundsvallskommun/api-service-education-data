package se.sundsvall.educationdata.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderPageRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProviderPageEntity;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationProvidersMapper;
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
	private SusaNavetIntegration susaNavetIntegration;

	@Mock
	private SusaEducationProviderPageRepository susaEducationProviderPageRepository;

	@Mock
	private EducationProvidersMapper educationProvidersMapper;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private EducationProvidersService educationProvidersService;

	@Test
	void saveAllPagesProviderJsonTableTest_successful() {
		final byte[] json1 = "json1".getBytes();
		final byte[] json2 = "json2".getBytes();
		final byte[] json3 = "json3".getBytes();
		final var entity1 = SusaEducationProviderPageEntity.builder().build();
		final var entity2 = SusaEducationProviderPageEntity.builder().build();
		final var entity3 = SusaEducationProviderPageEntity.builder().build();
		final var firstPage = JsonMapper.builder().build().readTree("{\"page\":{\"totalPages\":3}}");

		when(susaNavetIntegration.getEducationProviders(0)).thenReturn(json1);
		when(susaNavetIntegration.getEducationProviders(1)).thenReturn(json2);
		when(susaNavetIntegration.getEducationProviders(2)).thenReturn(json3);
		when(objectMapper.readTree(json1)).thenReturn(firstPage);
		when(educationProvidersMapper.toZippedProviders(json1, 0)).thenReturn(entity1);
		when(educationProvidersMapper.toZippedProviders(json2, 1)).thenReturn(entity2);
		when(educationProvidersMapper.toZippedProviders(json3, 2)).thenReturn(entity3);

		educationProvidersService.saveAllPagesProviderJsonTable();

		final var captor = ArgumentCaptor.forClass(SusaEducationProviderPageEntity.class);
		verify(susaEducationProviderPageRepository, times(3)).save(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(entity1, entity2, entity3);

		verify(susaNavetIntegration).getEducationProviders(0);
		verify(susaNavetIntegration).getEducationProviders(1);
		verify(susaNavetIntegration).getEducationProviders(2);
		verifyNoMoreInteractions(susaNavetIntegration, susaEducationProviderPageRepository);
	}

	@Test
	void saveAllPagesProviderJsonTableTest_withOnlyOnePage() {
		final int page = 0;
		final int nonExistingPage = 1;
		final byte[] json = "{page:{totalPages:1}}".getBytes();
		final var firstPage = JsonMapper.builder().build().readTree("{\"page\":{\"totalPages\":1}}");
		final var entity = SusaEducationProviderPageEntity.builder().build();

		when(susaNavetIntegration.getEducationProviders(page)).thenReturn(json);
		when(objectMapper.readTree(json)).thenReturn(firstPage);
		when(educationProvidersMapper.toZippedProviders(json, 0)).thenReturn(entity);

		educationProvidersService.saveAllPagesProviderJsonTable();

		verify(susaNavetIntegration).getEducationProviders(page);
		verify(susaNavetIntegration, never()).getEducationProviders(nonExistingPage);
		verify(susaEducationProviderPageRepository).save(entity);
		verifyNoMoreInteractions(susaNavetIntegration, susaEducationProviderPageRepository);
	}
}
