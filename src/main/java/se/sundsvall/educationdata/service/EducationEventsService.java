package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.EducationEvent;
import generated.se.sundsvall.susanavet.EducationEventListResponse;
import generated.se.sundsvall.susanavet.EducationEventResponse;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationEventRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.SusaMapper;
import tools.jackson.databind.ObjectMapper;

@Service
public class EducationEventsService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationEventRepository eventRepository;
	private final EducationEventEntityRepository eventEntityRepository;
	private final ObjectMapper objectMapper;
	private final SusaMapper mapper;

	public EducationEventsService(SusaNavetIntegration susaNavetIntegration, SusaEducationEventRepository eventRepository, EducationEventEntityRepository eventEntityRepository, ObjectMapper objectMapper, SusaMapper mapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.eventRepository = eventRepository;
		this.eventEntityRepository = eventEntityRepository;
		this.objectMapper = objectMapper;

		this.mapper = mapper;
	}

	// tillåtna kommuner
	private static final Set<String> municipalityIdWhitelist = Set.of("2281", "9999");

	public void savePageEventJsonTable(int page, int size) {

		var json = susaNavetIntegration.getEducationEvents(page, size);
		eventRepository.save(mapper.toZippedEvents(json, page));

		var response = objectMapper.readValue(json, EducationEventListResponse.class);
		var susaEvents = getMunicipalityFilteredEvents(response.getEducationEvents(), municipalityIdWhitelist);

		eventEntityRepository.saveAll(mapper.toEventEntities(susaEvents));
	}

	public void saveAllPagesEventsJsonTable(int size) {
		int page = 0;
		var json = susaNavetIntegration.getEducationEvents(page, size);
		var response = objectMapper.readValue(json, EducationEventListResponse.class);

		var pageInfo = response.getPage();
		var totalPages = (pageInfo == null || pageInfo.getTotalPages() == null) ? 0 : pageInfo.getTotalPages();

		eventRepository.save(mapper.toZippedEvents(json, page));
		var susaEvents = getMunicipalityFilteredEvents(response.getEducationEvents(), municipalityIdWhitelist);
		eventEntityRepository.saveAll(mapper.toEventEntities(susaEvents));

		for (page = 1; page < totalPages; page++) {
			json = susaNavetIntegration.getEducationEvents(page, size);
			eventRepository.save(mapper.toZippedEvents(json, page));

			susaEvents = getMunicipalityFilteredEvents(response.getEducationEvents(), municipalityIdWhitelist);
			eventEntityRepository.saveAll(mapper.toEventEntities(susaEvents));
		}
	}

	public List<EducationEvent> getMunicipalityFilteredEvents(List<EducationEventResponse> events, Set<String> municipalityIdWhitelist) {
		return events.stream()
			.map(EducationEventResponse::getContent)
			.filter(Objects::nonNull)
			.filter(event -> event.getLocations() != null)
			.filter(event -> event.getLocations().stream()
				.anyMatch(location -> municipalityIdWhitelist.contains(location.getAreaCode())))
			.toList();
	}
}
