package se.sundsvall.educationdata.service;

import java.io.IOException;
import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.SusaMapper;
import tools.jackson.databind.ObjectMapper;

@Service
public class EducationProvidersService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationProviderRepository providerRepository;
	private final ObjectMapper objectMapper;
	private final SusaMapper mapper;

	public EducationProvidersService(SusaNavetIntegration susaNavetIntegration, SusaEducationProviderRepository providerRepository, ObjectMapper objectMapper, SusaMapper mapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.providerRepository = providerRepository;
		this.objectMapper = objectMapper;
		this.mapper = mapper;
	}

	public void savePageProviderJsonTable(int page, int size) throws IOException {
		var json = susaNavetIntegration.getEducationProviders(page, size);
		providerRepository.save(mapper.toZippedProviders(json, page));
	}

	public void saveAllPagesProviderJsonTable(int size) throws IOException {
		int page = 0;
		var json = susaNavetIntegration.getEducationProviders(page, size);
		int totalPages = objectMapper.readTree(json).path("page").path("totalPages").asInt();
		providerRepository.save(mapper.toZippedProviders(json, page));

		for (page++; page < totalPages; page++) {
			json = susaNavetIntegration.getEducationProviders(page, size);
			final var entity = mapper.toZippedProviders(json, page);
			providerRepository.save(entity);
		}
	}
}
