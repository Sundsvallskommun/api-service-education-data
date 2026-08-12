package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.EducationInfo;
import generated.se.sundsvall.susanavet.EducationInfoListResponse;
import generated.se.sundsvall.susanavet.EducationInfoResponse;
import generated.se.sundsvall.susanavet.PageMetadata;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.EducationInfoEntityRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationInfoPageRepository;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfoPageEntity;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationInfosMapper;
import se.sundsvall.educationdata.util.Util;
import tools.jackson.databind.ObjectMapper;

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
class EducationInfosServiceTest {
	@Mock
	private SusaNavetIntegration susaNavetIntegration;

	@Mock
	private SusaEducationInfoPageRepository susaEducationInfoPageRepository;

	@Mock
	private EducationInfoEntityRepository educationInfoEntityRepository;

	@Mock
	private EducationEventEntityRepository educationEventEntityRepository;

	@Mock
	private EducationInfosMapper educationInfosMapper;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private EducationInfosService educationInfosService;

	@Test
	void saveAllPagesInfoJsonTableTest_successful() {
		final byte[] json1 = "json1".getBytes();
		final byte[] json2 = "json2".getBytes();
		final byte[] json3 = "json3".getBytes();
		final var entity1 = SusaEducationInfoPageEntity.builder().build();
		final var entity2 = SusaEducationInfoPageEntity.builder().build();
		final var entity3 = SusaEducationInfoPageEntity.builder().build();

		final var firstResponse = new EducationInfoListResponse()
			.page(new PageMetadata().totalPages(3L))
			.educationInfos(List.of());
		final var otherResponse = new EducationInfoListResponse()
			.educationInfos(List.of());

		when(susaNavetIntegration.getEducationInfos(0)).thenReturn(json1);
		when(susaNavetIntegration.getEducationInfos(1)).thenReturn(json2);
		when(susaNavetIntegration.getEducationInfos(2)).thenReturn(json3);
		when(objectMapper.readValue(any(byte[].class), eq(EducationInfoListResponse.class))).thenReturn(firstResponse, otherResponse, otherResponse);
		when(educationInfosMapper.toZippedInfos(json1, 0)).thenReturn(entity1);
		when(educationInfosMapper.toZippedInfos(json2, 1)).thenReturn(entity2);
		when(educationInfosMapper.toZippedInfos(json3, 2)).thenReturn(entity3);

		educationInfosService.saveAllPagesInfoJsonTable();

		final var captor = ArgumentCaptor.forClass(SusaEducationInfoPageEntity.class);
		verify(susaEducationInfoPageRepository, times(3)).save(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(entity1, entity2, entity3);

		verify(susaNavetIntegration).getEducationInfos(0);
		verify(susaNavetIntegration).getEducationInfos(1);
		verify(susaNavetIntegration).getEducationInfos(2);
		verifyNoMoreInteractions(susaNavetIntegration, susaEducationInfoPageRepository);
	}

	@Test
	void saveAllPagesInfoJsonTableTest_withOnlyOnePage() {
		final int page = 0;
		final int nonExistingPage = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationInfoPageEntity.builder().build();

		final var response = new EducationInfoListResponse()
			.page(new PageMetadata().totalPages(1L))
			.educationInfos(List.of());

		when(susaNavetIntegration.getEducationInfos(page)).thenReturn(json);
		when(objectMapper.readValue(json, EducationInfoListResponse.class)).thenReturn(response);
		when(educationInfosMapper.toZippedInfos(json, 0)).thenReturn(entity);
		educationInfosService.saveAllPagesInfoJsonTable();

		verify(susaNavetIntegration).getEducationInfos(page);
		verify(susaNavetIntegration, never()).getEducationInfos(nonExistingPage);
		verify(susaEducationInfoPageRepository).save(entity);
		verifyNoMoreInteractions(susaNavetIntegration, susaEducationInfoPageRepository);
	}

	@Test
	void saveAllPagesInfoJsonTableTest_pageNull() {
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationInfoPageEntity.builder().build();
		final var response = new EducationInfoListResponse().educationInfos(List.of());

		when(susaNavetIntegration.getEducationInfos(0)).thenReturn(json);
		when(objectMapper.readValue(json, EducationInfoListResponse.class)).thenReturn(response);
		when(educationInfosMapper.toZippedInfos(json, 0)).thenReturn(entity);

		educationInfosService.saveAllPagesInfoJsonTable();

		verify(susaNavetIntegration).getEducationInfos(0);
		verify(susaNavetIntegration, never()).getEducationInfos(1);
	}

	@Test
	void saveAllPagesInfoJsonTableTest_totalPagesNull() {
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationInfoPageEntity.builder().build();
		final var response = new EducationInfoListResponse()
			.page(new PageMetadata())
			.educationInfos(List.of());

		when(susaNavetIntegration.getEducationInfos(0)).thenReturn(json);
		when(objectMapper.readValue(json, EducationInfoListResponse.class)).thenReturn(response);
		when(educationInfosMapper.toZippedInfos(json, 0)).thenReturn(entity);

		educationInfosService.saveAllPagesInfoJsonTable();

		verify(susaNavetIntegration).getEducationInfos(0);
		verify(susaNavetIntegration, never()).getEducationInfos(1);
	}

	@Test
	void createInfoEntitiesFromJsonTest() {
		final byte[] json = "json".getBytes();
		final var zipped = Util.zip(json);
		final var page = SusaEducationInfoPageEntity.builder().withJsonBody(zipped).build();
		final var response = new EducationInfoListResponse().educationInfos(List.of());

		when(susaEducationInfoPageRepository.findAllByDateCollected(LocalDate.now())).thenReturn(List.of(page));
		when(educationEventEntityRepository.getDistinctEducationInfoId()).thenReturn(Set.of());
		when(objectMapper.readValue(any(byte[].class), eq(EducationInfoListResponse.class))).thenReturn(response);
		when(educationInfosMapper.toInfoEntities(any())).thenReturn(List.of());

		educationInfosService.createInfoEntitiesFromJson();

		verify(susaEducationInfoPageRepository).findAllByDateCollected(LocalDate.now());
		verify(educationInfoEntityRepository).saveAll(List.of());
	}

	@Test
	void createInfoEntitiesFromJson_noPages() {
		when(susaEducationInfoPageRepository.findAllByDateCollected(LocalDate.now())).thenReturn(List.of());

		educationInfosService.createInfoEntitiesFromJson();

		verify(susaEducationInfoPageRepository).findAllByDateCollected(LocalDate.now());
		verifyNoInteractions(educationInfoEntityRepository, educationInfosMapper, objectMapper);
	}

	@Test
	void saveFilteredInfos_keepWhitelisted() {
		final var whitelistedId = new EducationInfoResponse().content(new EducationInfo().identifier("i.id.whitelisted"));
		final var wrongId = new EducationInfoResponse().content(new EducationInfo().identifier("i.id.wrong"));
		final var nullContent = new EducationInfoResponse().content(null);
		final var entities = List.of(new EducationInfoEntity());

		when(educationInfosMapper.toInfoEntities(List.of(whitelistedId))).thenReturn(entities);

		educationInfosService.saveFilteredInfos(List.of(whitelistedId, wrongId, nullContent), Set.of("i.id.whitelisted"));

		verify(educationInfosMapper).toInfoEntities(List.of(whitelistedId));
		verify(educationInfoEntityRepository).saveAll(entities);
	}

	@Test
	void saveFilteredInfos_empty() {
		final var info = new EducationInfoResponse().content(new EducationInfo().identifier("i.sv.wrong"));

		when(educationInfosMapper.toInfoEntities(List.of())).thenReturn(List.of());

		educationInfosService.saveFilteredInfos(List.of(info), Set.of());

		verify(educationInfoEntityRepository).saveAll(List.of());
	}
}
