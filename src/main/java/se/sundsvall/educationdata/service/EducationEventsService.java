package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.EducationEvent;
import generated.se.sundsvall.susanavet.EducationEventListResponse;
import generated.se.sundsvall.susanavet.EducationEventResponse;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationEventPageRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationEventsMapper;
import se.sundsvall.educationdata.util.Util;
import tools.jackson.databind.ObjectMapper;

@Service
public class EducationEventsService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationEventPageRepository susaEducationEventPageRepository;
	private final EducationEventEntityRepository educationEventEntityRepository;
	private final ObjectMapper objectMapper;
	private final EducationEventsMapper educationEventsMapper;
	private final Set<String> municipalityIdWhitelist;

	public EducationEventsService(SusaNavetIntegration susaNavetIntegration, SusaEducationEventPageRepository susaEducationEventPageRepository,
		EducationEventEntityRepository educationEventEntityRepository, ObjectMapper objectMapper, EducationEventsMapper educationEventsMapper,
		@Value("${scheduler.create-entities.municipality-whitelist}") Set<String> municipalityIdWhitelist) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.susaEducationEventPageRepository = susaEducationEventPageRepository;
		this.educationEventEntityRepository = educationEventEntityRepository;
		this.objectMapper = objectMapper;
		this.educationEventsMapper = educationEventsMapper;
		this.municipalityIdWhitelist = municipalityIdWhitelist;
	}

	@Transactional
	public void saveAllPagesEventsJsonTable() {
		int page = 0;
		var json = susaNavetIntegration.getEducationEvents(page);
		var response = objectMapper.readValue(json, EducationEventListResponse.class);

		var pageInfo = response.getPage();
		var totalPages = (pageInfo == null || pageInfo.getTotalPages() == null) ? 0 : pageInfo.getTotalPages();

		susaEducationEventPageRepository.save(educationEventsMapper.toZippedEvents(json, page));

		for (page = 1; page < totalPages; page++) {
			json = susaNavetIntegration.getEducationEvents(page);
			susaEducationEventPageRepository.save(educationEventsMapper.toZippedEvents(json, page));
		}
	}

	@Transactional
	public void createEventEntitiesFromJson() {
		var jsonPageList = susaEducationEventPageRepository.findAllByDateCollected(LocalDate.now(ZoneId.systemDefault()));

		for (var page : jsonPageList) {

			var json = Util.unzip(page.getJsonBody());
			var response = objectMapper.readValue(json, EducationEventListResponse.class);
			var events = getMunicipalityFilteredEvents(response.getEducationEvents(), municipalityIdWhitelist);
			educationEventEntityRepository.saveAll(educationEventsMapper.toEventEntities(events));
		}
	}

	public List<EducationEvent> getMunicipalityFilteredEvents(List<EducationEventResponse> events, Set<String> municipalityIdWhitelist) {
		return events.stream()
			.map(EducationEventResponse::getContent)
			.filter(Objects::nonNull)
			.filter(event -> event.getLocations() != null)
			.filter(event -> event.getLocations().stream()
				.anyMatch(location -> location.getAreaCode() != null
					&& municipalityIdWhitelist.contains(location.getAreaCode())))
			.toList();
	}
}
