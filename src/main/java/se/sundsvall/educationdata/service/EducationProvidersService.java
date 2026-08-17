package se.sundsvall.educationdata.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderPageRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationProvidersMapper;
import tools.jackson.databind.ObjectMapper;

@Service
public class EducationProvidersService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationProviderPageRepository susaEducationProviderPageRepository;
	private final ObjectMapper objectMapper;
	private final EducationProvidersMapper educationProvidersMapper;

	public EducationProvidersService(SusaNavetIntegration susaNavetIntegration, SusaEducationProviderPageRepository susaEducationProviderPageRepository, ObjectMapper objectMapper, EducationProvidersMapper educationProvidersMapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.susaEducationProviderPageRepository = susaEducationProviderPageRepository;
		this.objectMapper = objectMapper;
		this.educationProvidersMapper = educationProvidersMapper;
	}

	@Transactional
	public void saveAllPagesProviderJsonTable() {
		int page = 0;
		var json = susaNavetIntegration.getEducationProviders(page);
		int totalPages = objectMapper.readTree(json).path("page").path("totalPages").asInt();
		susaEducationProviderPageRepository.save(educationProvidersMapper.toZippedProviders(json, page));

		for (page = 1; page < totalPages; page++) {
			json = susaNavetIntegration.getEducationProviders(page);
			final var entity = educationProvidersMapper.toZippedProviders(json, page);
			susaEducationProviderPageRepository.save(entity);
		}
	}
}
