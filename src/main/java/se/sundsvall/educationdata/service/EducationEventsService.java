package se.sundsvall.educationdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.SusaEducationEventRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEvent;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;

@Service
public class EducationEventsService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationEventRepository eventRepository;
	private final ObjectMapper objectMapper;

	public EducationEventsService(SusaNavetIntegration susaNavetIntegration, SusaEducationEventRepository eventRepository, ObjectMapper objectMapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.eventRepository = eventRepository;
		this.objectMapper = objectMapper;
	}

	public void savePageEventJsonTable(int page, int size) {
		String json = susaNavetIntegration.getEducationEvents(page, size);
		if (json == null) {
			throw new IllegalStateException(
				"Empty body for page %d".formatted(page));
		}
		var raw = SusaEducationEvent.builder()
			.withJsonBody(json)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
		eventRepository.save(raw);
	}

	public void saveAllPagesEventsJsonTable(int size) throws JsonProcessingException {
		int page = 0;
		int totalPages;

		do {
			String json = susaNavetIntegration.getEducationEvents(page, size);
			if (json == null) {
				throw new IllegalStateException(
					"Empty body for page %d".formatted(page));
			}

			totalPages = objectMapper.readTree(json)
				.path("page")
				.path("totalPages").asInt();

			var raw = SusaEducationEvent.builder()
				.withJsonBody(json)
				.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
				.build();
			eventRepository.save(raw);

			page++;
		} while (page < totalPages);
	}
}
