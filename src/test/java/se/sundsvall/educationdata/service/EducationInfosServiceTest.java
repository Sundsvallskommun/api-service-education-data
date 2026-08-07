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
import se.sundsvall.educationdata.integration.db.SusaEducationInfoRepository;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfoEntity;
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
	private SusaNavetIntegration integration;

	@Mock
	private SusaEducationInfoRepository jsonRepository;

	@Mock
	private EducationInfoEntityRepository entityRepository;

	@Mock
	private EducationEventEntityRepository eventEntityRepository;

	@Mock
	private EducationInfosMapper infosMapper;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private EducationInfosService service;

	@Test
	void saveAllPagesInfoJsonTableTest_successful() {
		final var size = 1;
		final byte[] json1 = "json1".getBytes();
		final byte[] json2 = "json2".getBytes();
		final byte[] json3 = "json3".getBytes();
		final var entity1 = SusaEducationInfoEntity.builder().build();
		final var entity2 = SusaEducationInfoEntity.builder().build();
		final var entity3 = SusaEducationInfoEntity.builder().build();

		final var firstResponse = new EducationInfoListResponse()
			.page(new PageMetadata().totalPages(3L))
			.educationInfos(List.of());
		final var otherResponse = new EducationInfoListResponse()
			.educationInfos(List.of());

		when(integration.getEducationInfos(0, size)).thenReturn(json1);
		when(integration.getEducationInfos(1, size)).thenReturn(json2);
		when(integration.getEducationInfos(2, size)).thenReturn(json3);
		when(objectMapper.readValue(any(byte[].class), eq(EducationInfoListResponse.class))).thenReturn(firstResponse, otherResponse, otherResponse);
		when(infosMapper.toZippedInfos(json1, 0)).thenReturn(entity1);
		when(infosMapper.toZippedInfos(json2, 1)).thenReturn(entity2);
		when(infosMapper.toZippedInfos(json3, 2)).thenReturn(entity3);

		service.saveAllPagesInfoJsonTable(size);

		final var captor = ArgumentCaptor.forClass(SusaEducationInfoEntity.class);
		verify(jsonRepository, times(3)).save(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(entity1, entity2, entity3);

		verify(integration).getEducationInfos(0, size);
		verify(integration).getEducationInfos(1, size);
		verify(integration).getEducationInfos(2, size);
		verifyNoMoreInteractions(integration, jsonRepository);
	}

	@Test
	void saveAllPagesInfoJsonTableTest_withOnlyOnePage() {
		final int size = 1;
		final int page = 0;
		final int nonExistingPage = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationInfoEntity.builder().build();

		final var response = new EducationInfoListResponse()
			.page(new PageMetadata().totalPages(1L))
			.educationInfos(List.of());

		when(integration.getEducationInfos(page, size)).thenReturn(json);
		when(objectMapper.readValue(json, EducationInfoListResponse.class)).thenReturn(response);
		when(infosMapper.toZippedInfos(json, 0)).thenReturn(entity);
		service.saveAllPagesInfoJsonTable(size);

		verify(integration).getEducationInfos(page, size);
		verify(integration, never()).getEducationInfos(nonExistingPage, size);
		verify(jsonRepository).save(entity);
		verifyNoMoreInteractions(integration, jsonRepository);
	}

	@Test
	void saveAllPagesInfoJsonTableTest_NullPage() {
		final int size = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationInfoEntity.builder().build();
		final var response = new EducationInfoListResponse().educationInfos(List.of());

		when(integration.getEducationInfos(0, size)).thenReturn(json);
		when(objectMapper.readValue(json, EducationInfoListResponse.class)).thenReturn(response);
		when(infosMapper.toZippedInfos(json, 0)).thenReturn(entity);

		service.saveAllPagesInfoJsonTable(size);

		verify(integration).getEducationInfos(0, size);
		verify(integration, never()).getEducationInfos(1, size);
	}

	@Test
	void saveAllPagesInfoJsonTableTest_totalPagesNull() {
		final int size = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationInfoEntity.builder().build();
		final var response = new EducationInfoListResponse()
			.page(new PageMetadata())
			.educationInfos(List.of());

		when(integration.getEducationInfos(0, size)).thenReturn(json);
		when(objectMapper.readValue(json, EducationInfoListResponse.class)).thenReturn(response);
		when(infosMapper.toZippedInfos(json, 0)).thenReturn(entity);

		service.saveAllPagesInfoJsonTable(size);

		verify(integration).getEducationInfos(0, size);
		verify(integration, never()).getEducationInfos(1, size);
	}

	@Test
	void saveAllJsonDataInfosToEntitiesTest() {
		final byte[] json = "json".getBytes();
		final var zipped = Util.zip(json);
		final var page = SusaEducationInfoEntity.builder().withJsonBody(zipped).build();
		final var response = new EducationInfoListResponse().educationInfos(List.of());

		when(jsonRepository.findAllByDateCollected(LocalDate.now())).thenReturn(List.of(page));
		when(eventEntityRepository.getDistinctEducationInfoId()).thenReturn(Set.of());
		when(objectMapper.readValue(any(byte[].class), eq(EducationInfoListResponse.class))).thenReturn(response);
		when(infosMapper.toInfoEntities(any())).thenReturn(List.of());

		service.saveAllJsonDataInfosToEntities();

		verify(jsonRepository).findAllByDateCollected(LocalDate.now());
		verify(entityRepository).saveAll(List.of());
	}

	@Test
	void saveAllJsonDataEventsToEntities_noPages() {
		when(jsonRepository.findAllByDateCollected(LocalDate.now())).thenReturn(List.of());

		service.saveAllJsonDataInfosToEntities();

		verify(jsonRepository).findAllByDateCollected(LocalDate.now());
		verifyNoInteractions(entityRepository, infosMapper, objectMapper);
	}

	@Test
	void saveFilteredInfos_keepWhitelisted() {
		final var whitelistedId = new EducationInfoResponse().content(new EducationInfo().identifier("i.id.whitelisted"));
		final var wrongId = new EducationInfoResponse().content(new EducationInfo().identifier("i.id.wrong"));
		final var nullContent = new EducationInfoResponse().content(null);
		final var entities = List.of(new EducationInfoEntity());

		when(infosMapper.toInfoEntities(List.of(whitelistedId))).thenReturn(entities);

		service.saveFilteredInfos(List.of(whitelistedId, wrongId, nullContent), Set.of("i.id.whitelisted"));

		verify(infosMapper).toInfoEntities(List.of(whitelistedId));
		verify(entityRepository).saveAll(entities);
	}

	@Test
	void saveFilteredInfos_empty() {
		final var info = new EducationInfoResponse().content(new EducationInfo().identifier("i.sv.wrong"));

		when(infosMapper.toInfoEntities(List.of())).thenReturn(List.of());

		service.saveFilteredInfos(List.of(info), Set.of());

		verify(entityRepository).saveAll(List.of());
	}
}
