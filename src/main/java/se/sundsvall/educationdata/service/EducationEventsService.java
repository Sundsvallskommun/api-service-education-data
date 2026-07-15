package se.sundsvall.educationdata.service;

import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.SusaEducationEventRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.SusaMapper;
import tools.jackson.databind.ObjectMapper;

@Service
public class EducationEventsService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationEventRepository eventRepository;
	private final ObjectMapper objectMapper;
	private final SusaMapper mapper;

	public EducationEventsService(SusaNavetIntegration susaNavetIntegration, SusaEducationEventRepository eventRepository, ObjectMapper objectMapper, SusaMapper mapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.eventRepository = eventRepository;
		this.objectMapper = objectMapper;
		this.mapper = mapper;
	}

	public void savePageEventJsonTable(int page, int size) {
		var json = susaNavetIntegration.getEducationEvents(page, size);
		eventRepository.save(mapper.toZippedEvents(json, page));
	}

	public void saveAllPagesEventsJsonTable(int size) {
		int page = 0;
		var json = susaNavetIntegration.getEducationEvents(page, size);
		int totalPages = objectMapper.readTree(json).path("page").path("totalPages").asInt();
		eventRepository.save(mapper.toZippedEvents(json, page));

		for (page = 1; page < totalPages; page++) {
			json = susaNavetIntegration.getEducationEvents(page, size);
			final var entity = mapper.toZippedEvents(json, page);
			eventRepository.save(entity);
		}
	}
}
