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
import se.sundsvall.educationdata.integration.db.SusaEducationEventRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEventEntity;
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
	private SusaNavetIntegration integration;

	@Mock
	private SusaEducationEventRepository repository;

	@Mock
	private EducationEventEntityRepository entityRepository;

	@Mock
	private EducationEventsMapper eventsMapper;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private EducationEventsService service;

	@Test
	void saveAllPagesEventsJsonTableTest_successful() {
		final int size = 1;
		final byte[] json1 = "json1".getBytes();
		final byte[] json2 = "json2".getBytes();
		final byte[] json3 = "json3".getBytes();
		final var entity1 = SusaEducationEventEntity.builder().build();
		final var entity2 = SusaEducationEventEntity.builder().build();
		final var entity3 = SusaEducationEventEntity.builder().build();

		final var firstResponse = new EducationEventListResponse()
			.page(new PageMetadata().totalPages(3L))
			.educationEvents(List.of());
		final var otherResponse = new EducationEventListResponse()
			.educationEvents(List.of());

		when(integration.getEducationEvents(0, size)).thenReturn(json1);
		when(integration.getEducationEvents(1, size)).thenReturn(json2);
		when(integration.getEducationEvents(2, size)).thenReturn(json3);
		when(objectMapper.readValue(any(byte[].class), eq(EducationEventListResponse.class)))
			.thenReturn(firstResponse, otherResponse, otherResponse);
		when(eventsMapper.toZippedEvents(json1, 0)).thenReturn(entity1);
		when(eventsMapper.toZippedEvents(json2, 1)).thenReturn(entity2);
		when(eventsMapper.toZippedEvents(json3, 2)).thenReturn(entity3);

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
	void saveAllPagesEventsJsonTableTest_withOnlyOnePage() {
		final int size = 1;
		final int page = 0;
		final int nonExistingPage = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationEventEntity.builder().build();

		final var response = new EducationEventListResponse()
			.page(new PageMetadata().totalPages(1L))
			.educationEvents(List.of());

		when(integration.getEducationEvents(page, size)).thenReturn(json);
		when(objectMapper.readValue(json, EducationEventListResponse.class)).thenReturn(response);
		when(eventsMapper.toZippedEvents(json, 0)).thenReturn(entity);

		service.saveAllPagesEventsJsonTable(size);

		verify(integration).getEducationEvents(page, size);
		verify(integration, never()).getEducationEvents(nonExistingPage, size);
		verify(repository).save(entity);
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void saveAllPagesEventsJsonTableTest_pageNull() {
		final int size = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationEventEntity.builder().build();
		final var response = new EducationEventListResponse().educationEvents(List.of());

		when(integration.getEducationEvents(0, size)).thenReturn(json);
		when(objectMapper.readValue(json, EducationEventListResponse.class)).thenReturn(response);
		when(eventsMapper.toZippedEvents(json, 0)).thenReturn(entity);
		service.saveAllPagesEventsJsonTable(size);

		verify(integration).getEducationEvents(0, size);
		verify(integration, never()).getEducationEvents(1, size);
	}

	@Test
	void saveAllPagesEventsJsonTableTest_totalPagesNull() {
		final int size = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationEventEntity.builder().build();
		final var response = new EducationEventListResponse()
			.page(new PageMetadata())
			.educationEvents(List.of());

		when(integration.getEducationEvents(0, size)).thenReturn(json);
		when(objectMapper.readValue(json, EducationEventListResponse.class)).thenReturn(response);
		when(eventsMapper.toZippedEvents(json, 0)).thenReturn(entity);

		service.saveAllPagesEventsJsonTable(size);

		verify(integration).getEducationEvents(0, size);
		verify(integration, never()).getEducationEvents(1, size);
	}

	@Test
	void saveAllJsonDataEventsToEntitiesTest() {
		final byte[] json = "json".getBytes();
		final var zipped = Util.zip(json);
		final var page = SusaEducationEventEntity.builder().withJsonBody(zipped).build();
		final var response = new EducationEventListResponse().educationEvents(List.of());

		when(repository.findAllByDateCollected(LocalDate.now())).thenReturn(List.of(page));
		when(objectMapper.readValue(any(byte[].class), eq(EducationEventListResponse.class))).thenReturn(response);
		when(eventsMapper.toEventEntities(any())).thenReturn(List.of());

		service.saveAllJsonDataEventsToEntities();

		verify(repository).findAllByDateCollected(LocalDate.now());
		verify(entityRepository).saveAll(List.of());
	}

	@Test
	void saveAllJsonDataEventsToEntities_noPages() {
		when(repository.findAllByDateCollected(LocalDate.now())).thenReturn(List.of());

		service.saveAllJsonDataEventsToEntities();

		verify(repository).findAllByDateCollected(LocalDate.now());
		verifyNoInteractions(entityRepository, eventsMapper, objectMapper);
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

		final var result = service.getMunicipalityFilteredEvents(List.of(
			matching, wrongMunicipality, nullMunicipality, nullLocations), whitelist);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getLocations().getFirst().getAreaCode()).isEqualTo("2281");
	}
}
