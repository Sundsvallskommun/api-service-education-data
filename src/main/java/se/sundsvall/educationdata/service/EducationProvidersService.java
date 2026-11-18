package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.EducationProviderListResponse;
import generated.se.sundsvall.susanavet.EducationProviderResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.EducationProviderEntityRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderPageRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationProvidersMapper;
import se.sundsvall.educationdata.util.Util;
import tools.jackson.databind.ObjectMapper;

import static java.util.Objects.nonNull;

@Service
public class EducationProvidersService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationProviderPageRepository susaEducationProviderPageRepository;
	private final EducationProviderEntityRepository educationProviderEntityRepository;
	private final EducationEventEntityRepository educationEventEntityRepository;
	private final ObjectMapper objectMapper;
	private final EducationProvidersMapper educationProvidersMapper;

	public EducationProvidersService(SusaNavetIntegration susaNavetIntegration, SusaEducationProviderPageRepository susaEducationProviderPageRepository, EducationProviderEntityRepository educationProviderEntityRepository,
		EducationEventEntityRepository educationEventEntityRepository, ObjectMapper objectMapper, EducationProvidersMapper educationProvidersMapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.susaEducationProviderPageRepository = susaEducationProviderPageRepository;
		this.educationProviderEntityRepository = educationProviderEntityRepository;
		this.educationEventEntityRepository = educationEventEntityRepository;
		this.objectMapper = objectMapper;
		this.educationProvidersMapper = educationProvidersMapper;
	}

	@Transactional
	public void saveAllPagesProviderJsonTable() {
		susaEducationProviderPageRepository.deleteByDateCollected(LocalDate.now(ZoneId.systemDefault()));
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

	@Transactional
	public void createProviderEntitiesFromJson() {
		final var today = LocalDate.now(ZoneId.systemDefault());
		educationProviderEntityRepository.deleteByCreatedAt(today);
		educationProviderEntityRepository.flush();

		final var usedProviderIds = educationEventEntityRepository.findDistinctEducationProviderIdByCreatedAt(today);

		for (final var page : susaEducationProviderPageRepository.findAllByDateCollected(today)) {
			final var json = Util.unzip(page.getJsonBody());
			final var response = objectMapper.readValue(json, EducationProviderListResponse.class);
			saveFilteredProviders(response.getEducationProviders(), usedProviderIds);
		}
	}

	void saveFilteredProviders(final List<EducationProviderResponse> providers, final Set<String> usedProviderIds) {
		final var filtered = Optional.ofNullable(providers).orElse(List.of()).stream()
			.filter(provider -> nonNull(provider.getContent()))
			.filter(provider -> usedProviderIds.contains(provider.getContent().getIdentifier()))
			.toList();

		educationProviderEntityRepository.saveAll(educationProvidersMapper.toProviderEntities(filtered));
	}
}
