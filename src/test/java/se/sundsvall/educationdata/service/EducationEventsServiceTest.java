package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.Address;
import generated.se.sundsvall.susanavet.EducationEvent;
import generated.se.sundsvall.susanavet.EducationEventListResponse;
import generated.se.sundsvall.susanavet.EducationEventResponse;
import generated.se.sundsvall.susanavet.PageMetadata;
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
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
	private EducationEventEntityRepository entityRepository;

	@Mock
	private EducationEventsMapper eventsMapper;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private EducationEventsService service;

	@Test
	void savePageTest_successful() {
		final int page = 0;
		final int size = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationEventEntity.builder().build();
		final var response = new EducationEventListResponse().educationEvents(List.of());

		when(integration.getEducationEvents(page, size)).thenReturn(json);
		when(eventsMapper.toZippedEvents(json, page)).thenReturn(entity);
		when(objectMapper.readValue(json, EducationEventListResponse.class)).thenReturn(response);
		when(eventsMapper.toEventEntities(any())).thenReturn(List.of());

		service.savePageEventJsonTable(page, size);

		verify(integration).getEducationEvents(page, size);
		verify(repository).save(entity);
		verify(entityRepository).saveAll(List.of());
		verifyNoMoreInteractions(integration, repository, entityRepository);
	}

	@Test
	void saveAllPagesTest_successful() {
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
		when(eventsMapper.toEventEntities(any())).thenReturn(List.of());

		service.saveAllPagesEventsJsonTable(size);

		final var captor = ArgumentCaptor.forClass(SusaEducationEventEntity.class);
		verify(repository, times(3)).save(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(entity1, entity2, entity3);

		verify(entityRepository, times(3)).saveAll(any());
		verify(integration).getEducationEvents(0, size);
		verify(integration).getEducationEvents(1, size);
		verify(integration).getEducationEvents(2, size);
		verifyNoMoreInteractions(integration, repository, entityRepository);
	}

	@Test
	void saveAllPagesTest_withOnlyOnePage() {
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
		when(eventsMapper.toEventEntities(any())).thenReturn(List.of());

		service.saveAllPagesEventsJsonTable(size);

		verify(integration).getEducationEvents(page, size);
		verify(integration, never()).getEducationEvents(nonExistingPage, size);
		verify(repository).save(entity);
		verify(entityRepository).saveAll(List.of());
		verifyNoMoreInteractions(integration, repository, entityRepository);
	}

	@Test
	void saveAllPagesTest_pageNull() {
		final int size = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationEventEntity.builder().build();
		final var response = new EducationEventListResponse().educationEvents(List.of());

		when(integration.getEducationEvents(0, size)).thenReturn(json);
		when(objectMapper.readValue(json, EducationEventListResponse.class)).thenReturn(response);
		when(eventsMapper.toZippedEvents(json, 0)).thenReturn(entity);
		when(eventsMapper.toEventEntities(any())).thenReturn(List.of());

		service.saveAllPagesEventsJsonTable(size);

		verify(integration).getEducationEvents(0, size);
		verify(integration, never()).getEducationEvents(1, size);
	}

	@Test
	void saveAllPages_totalPagesNull() {
		final int size = 1;
		final byte[] json = "json".getBytes();
		final var entity = SusaEducationEventEntity.builder().build();
		final var response = new EducationEventListResponse()
			.page(new PageMetadata())
			.educationEvents(List.of());

		when(integration.getEducationEvents(0, size)).thenReturn(json);
		when(objectMapper.readValue(json, EducationEventListResponse.class)).thenReturn(response);
		when(eventsMapper.toZippedEvents(json, 0)).thenReturn(entity);
		when(eventsMapper.toEventEntities(any())).thenReturn(List.of());

		service.saveAllPagesEventsJsonTable(size);

		verify(integration).getEducationEvents(0, size);
		verify(integration, never()).getEducationEvents(1, size);
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
