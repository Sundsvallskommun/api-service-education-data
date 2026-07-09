package se.sundsvall.educationdata.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.SusaEducationInfoRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;

@Service
public class EducationInfosService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationInfoRepository infoRepository;

	public EducationInfosService(SusaNavetIntegration susaNavetIntegration, SusaEducationInfoRepository infoRepository) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.infoRepository = infoRepository;
	}

	public void savePageInfoJsonTable(int page, int size) throws IOException {
		final var entity = susaNavetIntegration.getEducationInfosWithPage(page, size).entity();
		infoRepository.save(entity);
	}

	public void saveAllPagesInfoJsonTable(int size) throws IOException {
		int page = 0;
		int totalPages;

		do {
			final var infosWithPage = susaNavetIntegration.getEducationInfosWithPage(page, size);
			final var entity = infosWithPage.entity();
			infoRepository.save(entity);

			totalPages = infosWithPage.totalPages();
			page++;
		} while (page < totalPages);
	}
}
