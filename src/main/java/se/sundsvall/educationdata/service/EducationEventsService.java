package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.Address;
import generated.se.sundsvall.susanavet.EducationEvent;
import generated.se.sundsvall.susanavet.EducationEventListResponse;
import generated.se.sundsvall.susanavet.EducationEventResponse;
import generated.se.sundsvall.susanavet.EducationProvider;
import generated.se.sundsvall.susanavet.EducationProviderListResponse;
import generated.se.sundsvall.susanavet.EducationProviderResponse;
import generated.se.sundsvall.susanavet.PageMetadata;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationEventPageRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderPageRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationEventsMapper;
import se.sundsvall.educationdata.util.Util;
import tools.jackson.databind.ObjectMapper;

@Service
public class EducationEventsService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationEventPageRepository susaEducationEventPageRepository;
	private final SusaEducationProviderPageRepository susaEducationProviderPageRepository;
	private final EducationEventEntityRepository educationEventEntityRepository;
	private final ObjectMapper objectMapper;
	private final EducationEventsMapper educationEventsMapper;
	private final Set<String> municipalityIdWhitelist;

	public EducationEventsService(SusaNavetIntegration susaNavetIntegration, SusaEducationEventPageRepository susaEducationEventPageRepository,
		SusaEducationProviderPageRepository susaEducationProviderPageRepository, EducationEventEntityRepository educationEventEntityRepository,
		ObjectMapper objectMapper, EducationEventsMapper educationEventsMapper,
		@Value("${municipality-whitelist}") Set<String> municipalityIdWhitelist) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.susaEducationEventPageRepository = susaEducationEventPageRepository;
		this.susaEducationProviderPageRepository = susaEducationProviderPageRepository;
		this.educationEventEntityRepository = educationEventEntityRepository;
		this.objectMapper = objectMapper;
		this.educationEventsMapper = educationEventsMapper;
		this.municipalityIdWhitelist = municipalityIdWhitelist;
	}

	@Transactional
	public void saveAllPagesEventsJsonTable() {
		susaEducationEventPageRepository.deleteByDateCollected(LocalDate.now(ZoneId.systemDefault()));
		int page = 0;
		var json = susaNavetIntegration.getEducationEvents(page);
		var response = objectMapper.readValue(json, EducationEventListResponse.class);

		final long totalPages = Optional.ofNullable(response.getPage())
			.map(PageMetadata::getTotalPages)
			.orElse(0L);

		susaEducationEventPageRepository.save(educationEventsMapper.toZippedEvents(json, page));

		for (page = 1; page < totalPages; page++) {
			json = susaNavetIntegration.getEducationEvents(page);
			susaEducationEventPageRepository.save(educationEventsMapper.toZippedEvents(json, page));
		}
	}

	@Transactional
	public void createEventEntitiesFromJson() {
		final var today = LocalDate.now(ZoneId.systemDefault());
		educationEventEntityRepository.deleteByCreatedAt(today);
		educationEventEntityRepository.flush();

		final var providerAddresses = getProviderVisitAddresses(today, municipalityIdWhitelist);
		var jsonPageList = susaEducationEventPageRepository.findAllByDateCollected(today);

		for (var page : jsonPageList) {

			var json = Util.unzip(page.getJsonBody());
			var response = objectMapper.readValue(json, EducationEventListResponse.class);
			var events = getMunicipalityFilteredEvents(response.getEducationEvents(), municipalityIdWhitelist, providerAddresses);
			educationEventEntityRepository.saveAll(educationEventsMapper.toEventEntities(events, municipalityIdWhitelist));
		}
	}

	public List<EducationEvent> getMunicipalityFilteredEvents(final List<EducationEventResponse> events, final Set<String> municipalityIdWhitelist, final Map<String, Address> providerAddresses) {
		return events.stream()
			.map(EducationEventResponse::getContent)
			.filter(Objects::nonNull)
			.map(event -> withProviderLocationIfMissing(event, providerAddresses))
			.filter(event -> event.getLocations() != null)
			.filter(event -> event.getLocations().stream()
				.anyMatch(location -> location.getAreaCode() != null
					&& municipalityIdWhitelist.contains(location.getAreaCode())))
			.toList();
	}

	public Map<String, Address> getProviderVisitAddresses(final LocalDate date, final Set<String> municipalityIds) {
		final var addresses = new HashMap<String, Address>();
		for (var page : susaEducationProviderPageRepository.findAllByDateCollected(date)) {
			var json = Util.unzip(page.getJsonBody());
			var response = objectMapper.readValue(json, EducationProviderListResponse.class);
			Optional.ofNullable(response.getEducationProviders()).orElse(List.of()).stream()
				.map(EducationProviderResponse::getContent)
				.filter(Objects::nonNull)
				.forEach(provider -> filterWhitelistedAddress(provider, municipalityIds)
					.ifPresent(address -> addresses.put(provider.getIdentifier(), address)));
		}
		return addresses;
	}

	private static Optional<Address> filterWhitelistedAddress(final EducationProvider provider, final Set<String> municipalityIds) {
		return Optional.ofNullable(provider.getVisitAddresses()).orElse(List.of()).stream()
			.filter(Objects::nonNull)
			.filter(address -> address.getAreaCode() != null && municipalityIds.contains(address.getAreaCode()))
			.findFirst();
	}

	private static EducationEvent withProviderLocationIfMissing(final EducationEvent event, final Map<String, Address> providerAddresses) {
		if (event.getLocations() != null && !event.getLocations().isEmpty()) {
			return event;
		}
		return Optional.ofNullable(event.getProviders()).orElse(List.of()).stream()
			.findFirst()
			.map(providerAddresses::get)
			.map(address -> event.locations(List.of(address)))
			.orElse(event);
	}
}
