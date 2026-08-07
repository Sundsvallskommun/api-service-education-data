package se.sundsvall.educationdata.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationProvidersMapper;
import tools.jackson.databind.ObjectMapper;

@Service
public class EducationProvidersService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationProviderRepository providerRepository;
	private final ObjectMapper objectMapper;
	private final EducationProvidersMapper providersMapper;

	public EducationProvidersService(SusaNavetIntegration susaNavetIntegration, SusaEducationProviderRepository providerRepository, ObjectMapper objectMapper, EducationProvidersMapper providersMapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.providerRepository = providerRepository;
		this.objectMapper = objectMapper;
		this.providersMapper = providersMapper;
	}

	@Transactional
	public void saveAllPagesProviderJsonTable(int size) {
		int page = 0;
		var json = susaNavetIntegration.getEducationProviders(page, size);
		int totalPages = objectMapper.readTree(json).path("page").path("totalPages").asInt();
		providerRepository.save(providersMapper.toZippedProviders(json, page));

		for (page = 1; page < totalPages; page++) {
			json = susaNavetIntegration.getEducationProviders(page, size);
			final var entity = providersMapper.toZippedProviders(json, page);
			providerRepository.save(entity);
		}
	}
}
