package se.sundsvall.educationdata.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;

@Service
public class EducationProvidersService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationProviderRepository providerRepository;

	public EducationProvidersService(SusaNavetIntegration susaNavetIntegration, SusaEducationProviderRepository providerRepository) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.providerRepository = providerRepository;
	}

	public void savePageProviderJsonTable(int page, int size) throws IOException {
		final var entity = susaNavetIntegration.getEducationProvidersWithPage(page, size).entity();
		providerRepository.save(entity);
	}

	public void saveAllPagesProviderJsonTable(int size) throws IOException {
		int page = 0;
		int totalPages;

		do {
			final var providerWithPage = susaNavetIntegration.getEducationProvidersWithPage(page, size);
			final var entity = providerWithPage.entity();
			providerRepository.save(entity);

			totalPages = providerWithPage.totalPages();
			page++;
		} while (page < totalPages);
	}
}
