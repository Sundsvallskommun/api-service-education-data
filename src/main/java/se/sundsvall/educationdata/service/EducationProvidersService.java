package se.sundsvall.educationdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProvider;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;

@Service
public class EducationProvidersService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationProviderRepository providerRepository;
	private final ObjectMapper objectMapper;

	public EducationProvidersService(SusaNavetIntegration susaNavetIntegration, SusaEducationProviderRepository providerRepository, ObjectMapper objectMapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.providerRepository = providerRepository;
		this.objectMapper = objectMapper;
	}

	public void savePageProviderJsonTable(int page, int size) {
		String json = susaNavetIntegration.getEducationProviders(page, size);
		if (json == null) {
			throw new IllegalStateException(
				"Empty body for page %d".formatted(page));
		}

		var raw = SusaEducationProvider.builder()
			.withJsonBody(json)
			.withDateCollected(LocalDate.now())
			.build();
		providerRepository.save(raw);
	}

	public void saveAllPagesProviderJsonTable(int size) throws JsonProcessingException {
		int page = 0;
		int totalPages;

		do {
			String json = susaNavetIntegration.getEducationProviders(page, size);
			if (json == null) {
				throw new IllegalStateException(
					"Empty body for page %d".formatted(page));
			}

			totalPages = objectMapper.readTree(json)
				.path("page")
				.path("totalPages").asInt();

			var raw = SusaEducationProvider.builder()
				.withJsonBody(json)
				.withDateCollected(LocalDate.now())
				.build();
			providerRepository.save(raw);

			page++;
		} while (page < totalPages);
	}
}
