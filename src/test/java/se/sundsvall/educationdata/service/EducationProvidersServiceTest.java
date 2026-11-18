package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.EducationProvider;
import generated.se.sundsvall.susanavet.EducationProviderListResponse;
import generated.se.sundsvall.susanavet.EducationProviderResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.EducationProviderEntityRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderPageRepository;
import se.sundsvall.educationdata.integration.db.model.EducationProviderEntity;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProviderPageEntity;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationProvidersMapper;
import se.sundsvall.educationdata.util.Util;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EducationProvidersServiceTest {
	@Mock
	private SusaNavetIntegration susaNavetIntegration;

	@Mock
	private SusaEducationProviderPageRepository susaEducationProviderPageRepository;

	@Mock
	private EducationProviderEntityRepository educationProviderEntityRepository;

	@Mock
	private EducationEventEntityRepository educationEventEntityRepository;

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

		verify(susaEducationProviderPageRepository).deleteByDateCollected(LocalDate.now());
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

		verify(susaEducationProviderPageRepository).deleteByDateCollected(LocalDate.now());
		verify(susaNavetIntegration).getEducationProviders(page);
		verify(susaNavetIntegration, never()).getEducationProviders(nonExistingPage);
		verify(susaEducationProviderPageRepository).save(entity);
		verifyNoMoreInteractions(susaNavetIntegration, susaEducationProviderPageRepository);
	}

	@Test
	void createProviderEntitiesFromJsonTest() {
		final var today = LocalDate.now();
		final var page = SusaEducationProviderPageEntity.builder().withJsonBody(Util.zip("json".getBytes())).build();
		final var used = new EducationProviderResponse().content(new EducationProvider().identifier("p.used"));
		final var unused = new EducationProviderResponse().content(new EducationProvider().identifier("p.unused"));
		final var response = new EducationProviderListResponse().educationProviders(List.of(used, unused));
		final var entities = List.of(new EducationProviderEntity());

		when(educationEventEntityRepository.findDistinctEducationProviderIdByCreatedAt(today)).thenReturn(Set.of("p.used"));
		when(susaEducationProviderPageRepository.findAllByDateCollected(today)).thenReturn(List.of(page));
		when(objectMapper.readValue(any(byte[].class), eq(EducationProviderListResponse.class))).thenReturn(response);
		when(educationProvidersMapper.toProviderEntities(List.of(used))).thenReturn(entities);

		educationProvidersService.createProviderEntitiesFromJson();

		final var inOrder = Mockito.inOrder(educationProviderEntityRepository);
		inOrder.verify(educationProviderEntityRepository).deleteByCreatedAt(today);
		inOrder.verify(educationProviderEntityRepository).flush();
		inOrder.verify(educationProviderEntityRepository).saveAll(entities);
	}

	@Test
	void createProviderEntitiesFromJson_noPages() {
		final var today = LocalDate.now();
		when(susaEducationProviderPageRepository.findAllByDateCollected(today)).thenReturn(List.of());

		educationProvidersService.createProviderEntitiesFromJson();

		verify(educationProviderEntityRepository).deleteByCreatedAt(today);
		verify(educationProviderEntityRepository).flush();
		verify(educationProviderEntityRepository, never()).saveAll(any());
		verifyNoInteractions(educationProvidersMapper, objectMapper);
	}

	@Test
	void saveFilteredProviders_keepsOnlyUsedProviders() {
		final var used = new EducationProviderResponse().content(new EducationProvider().identifier("p.used"));
		final var unused = new EducationProviderResponse().content(new EducationProvider().identifier("p.unused"));
		final var nullContent = new EducationProviderResponse().content(null);
		final var entities = List.of(new EducationProviderEntity());

		when(educationProvidersMapper.toProviderEntities(List.of(used))).thenReturn(entities);

		educationProvidersService.saveFilteredProviders(List.of(used, unused, nullContent), Set.of("p.used"));

		verify(educationProviderEntityRepository).saveAll(entities);
	}

	@Test
	void saveFilteredProviders_nullProviders() {
		when(educationProvidersMapper.toProviderEntities(List.of())).thenReturn(List.of());

		educationProvidersService.saveFilteredProviders(null, Set.of());

		verify(educationProviderEntityRepository).saveAll(List.of());
	}
}
