package se.sundsvall.educationdata.service;

import java.io.IOException;
import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.SusaEducationEventRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;

@Service
public class EducationEventsService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationEventRepository eventRepository;

	public EducationEventsService(SusaNavetIntegration susaNavetIntegration, SusaEducationEventRepository eventRepository) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.eventRepository = eventRepository;
	}

	public void savePageEventJsonTable(int page, int size) throws IOException {
		final var entity = susaNavetIntegration.getEducationEventsWithPage(page, size).entity();
		eventRepository.save(entity);
	}

	public void saveAllPagesEventsJsonTable(int size) throws IOException {
		int page = 0;
		int totalPages;

		do {
			final var eventsWithPage = susaNavetIntegration.getEducationEventsWithPage(page, size);
			final var entity = eventsWithPage.entity();
			eventRepository.save(entity);

			totalPages = eventsWithPage.totalPages();
			page++;
		} while (page < totalPages);
	}
}
