package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.Address;
import generated.se.sundsvall.susanavet.EducationProvider;
import generated.se.sundsvall.susanavet.EducationProviderResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.EducationProviderEntity;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProviderPageEntity;
import se.sundsvall.educationdata.util.Util;

import static java.util.Optional.ofNullable;
import static se.sundsvall.educationdata.service.mapper.MapperUtil.firstOrNull;
import static se.sundsvall.educationdata.service.mapper.SusaNavetMapper.firstStringValue;
import static se.sundsvall.educationdata.service.mapper.SusaNavetMapper.firstUrlValue;

@Component
public class EducationProvidersMapper {
	public SusaEducationProviderPageEntity toZippedProviders(byte[] json, int page) {
		return SusaEducationProviderPageEntity.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}

	public List<EducationProviderEntity> toProviderEntities(final List<EducationProviderResponse> providers) {
		return ofNullable(providers).orElse(Collections.emptyList()).stream()
			.filter(Objects::nonNull)
			.map(EducationProviderResponse::getContent)
			.filter(Objects::nonNull)
			.map(this::toProviderEntity)
			.toList();
	}

	private EducationProviderEntity toProviderEntity(final EducationProvider provider) {
		final var visitingAddress = firstOrNull(provider.getVisitAddresses());

		return EducationProviderEntity.builder()
			.withEducationProviderId(provider.getIdentifier())
			.withName(firstStringValue(provider.getName()))
			.withUrl(firstUrlValue(provider.getUrl()))
			.withCity(ofNullable(visitingAddress).map(Address::getTown).orElse(null))
			.withMunicipalityId(ofNullable(visitingAddress).map(Address::getAreaCode).orElse(null))
			.build();
	}
}
