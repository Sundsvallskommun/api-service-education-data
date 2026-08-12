package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.Address;
import generated.se.sundsvall.susanavet.EducationEvent;
import generated.se.sundsvall.susanavet.EducationEventListResponse;
import generated.se.sundsvall.susanavet.EducationEventResponse;
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
import se.sundsvall.educationdata.integration.db.SusaEducationEventPageRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEventPageEntity;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationEventsMapper;
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
class EducationEventsServiceTest {

	@Mock
	private SusaNavetIntegration susaNavetIntegration;

	@Mock
	private SusaEducationEventPageRepository susaEducationEventPageRepository;

	@Mock
	private EducationEventEntityRepository educationEventEntityRepository;

	@Mock
	private EducationEventsMapper educationEventsMapper;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private EducationEventsService educationEventsService;

	@Test
	void saveAllPagesEventsJsonTableTest_successful() {
        final byte[] json1 = "json1".getBytes();
		final byte[] json2 = "json2".getBytes();
		final byte[] json3 = "json3".getBytes();
		final var entity1 = SusaEducationEventPageEntity.builder().build();
		final var entity2 = SusaEducationEventPageEntity.builder().build();
		final var entity3 = SusaEducationEventPageEntity.builder().build();

		final var firstResponse = new EducationEventListResponse()
			.page(new PageMetadata().totalPages(3L))
			.educationEvents(List.of());
		final var otherResponse = new EducationEventListResponse()
			.educationEvents(List.of());

		when(susaNavetIntegration.getEducationEvents(0)).thenReturn(json1);
		when(susaNavetIntegration.getEducationEvents(1)).thenReturn(json2);
		when(susaNavetIntegration.getEducationEvents(2)).thenReturn(json3);
		when(objectMapper.readValue(any(byte[].class), eq(EducationEventListResponse.class)))
			.thenReturn(firstResponse, otherResponse, otherResponse);
		when(educationEventsMapper.toZippedEvents(json1, 0)).thenReturn(entity1);
		when(educationEventsMapper.toZippedEvents(json2, 1)).thenReturn(entity2);
		when(educationEventsMapper.toZippedEvents(json3, 2)).thenReturn(entity3);

		educationEventsService.saveAllPagesEventsJsonTable();

		final var captor = ArgumentCaptor.forClass(SusaEducationEventPageEntity.class);
		verify(susaEducationEventPageRepository, times(3)).save(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(entity1, entity2, entity3);

		verify(susaNavetIntegration).getEducationEvents(0);
		verify(susaNavetIntegration).getEducationEvents(1);
		verify(susaNavetIntegration).getEducationEvents(2);
		verifyNoMoreInteractions(susaNavetIntegration, susaEducationEventPageRepository);
	}

	@Test
	void saveAllPagesEventsJsonTableTest_withOnlyOnePage() {
        final int page = 0;
		final int nonExistingPage = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationEventPageEntity.builder().build();

		final var response = new EducationEventListResponse()
			.page(new PageMetadata().totalPages(1L))
			.educationEvents(List.of());

		when(susaNavetIntegration.getEducationEvents(page)).thenReturn(json);
		when(objectMapper.readValue(json, EducationEventListResponse.class)).thenReturn(response);
		when(educationEventsMapper.toZippedEvents(json, 0)).thenReturn(entity);

		educationEventsService.saveAllPagesEventsJsonTable();

		verify(susaNavetIntegration).getEducationEvents(page);
		verify(susaNavetIntegration, never()).getEducationEvents(nonExistingPage);
		verify(susaEducationEventPageRepository).save(entity);
		verifyNoMoreInteractions(susaNavetIntegration, susaEducationEventPageRepository);
	}

	@Test
	void saveAllPagesEventsJsonTableTest_pageNull() {
        final byte[] json = "json".getBytes();
		final var entity = SusaEducationEventPageEntity.builder().build();
		final var response = new EducationEventListResponse().educationEvents(List.of());

		when(susaNavetIntegration.getEducationEvents(0)).thenReturn(json);
		when(objectMapper.readValue(json, EducationEventListResponse.class)).thenReturn(response);
		when(educationEventsMapper.toZippedEvents(json, 0)).thenReturn(entity);
		educationEventsService.saveAllPagesEventsJsonTable();

		verify(susaNavetIntegration).getEducationEvents(0);
		verify(susaNavetIntegration, never()).getEducationEvents(1);
	}

	@Test
	void saveAllPagesEventsJsonTableTest_totalPagesNull() {
        final byte[] json = "json".getBytes();
		final var entity = SusaEducationEventPageEntity.builder().build();
		final var response = new EducationEventListResponse()
			.page(new PageMetadata())
			.educationEvents(List.of());

		when(susaNavetIntegration.getEducationEvents(0)).thenReturn(json);
		when(objectMapper.readValue(json, EducationEventListResponse.class)).thenReturn(response);
		when(educationEventsMapper.toZippedEvents(json, 0)).thenReturn(entity);

		educationEventsService.saveAllPagesEventsJsonTable();

		verify(susaNavetIntegration).getEducationEvents(0);
		verify(susaNavetIntegration, never()).getEducationEvents(1);
	}

	@Test
	void createEventEntitiesFromJsonTest() {
		final byte[] json = "json".getBytes();
		final var zipped = Util.zip(json);
		final var page = SusaEducationEventPageEntity.builder().withJsonBody(zipped).build();
		final var response = new EducationEventListResponse().educationEvents(List.of());

		when(susaEducationEventPageRepository.findAllByDateCollected(LocalDate.now())).thenReturn(List.of(page));
		when(objectMapper.readValue(any(byte[].class), eq(EducationEventListResponse.class))).thenReturn(response);
		when(educationEventsMapper.toEventEntities(any())).thenReturn(List.of());

		educationEventsService.createEventEntitiesFromJson();

		verify(susaEducationEventPageRepository).findAllByDateCollected(LocalDate.now());
		verify(educationEventEntityRepository).saveAll(List.of());
	}

	@Test
	void createEventEntitiesFromJsonTest_noPages() {
		when(susaEducationEventPageRepository.findAllByDateCollected(LocalDate.now())).thenReturn(List.of());

		educationEventsService.createEventEntitiesFromJson();

		verify(susaEducationEventPageRepository).findAllByDateCollected(LocalDate.now());
		verifyNoInteractions(educationEventEntityRepository, educationEventsMapper, objectMapper);
	}

	@Test
	void getMunicipalityFilteredEvents() {
		final var whitelist = Set.of("2281");

		final var matching = new EducationEventResponse().content(new EducationEvent().locations(List.of(
			new Address().areaCode("2281"))));
		final var wrongMunicipality = new EducationEventResponse().content(new EducationEvent().locations(List.of(
			new Address().areaCode("9999"))));
		final var nullMunicipality = new EducationEventResponse().content(new EducationEvent().locations(List.of(
			new Address().areaCode(null))));
		final var nullLocations = new EducationEventResponse().content(new EducationEvent().locations(null));

		final var result = educationEventsService.getMunicipalityFilteredEvents(List.of(
			matching, wrongMunicipality, nullMunicipality, nullLocations), whitelist);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getLocations().getFirst().getAreaCode()).isEqualTo("2281");
	}
}
