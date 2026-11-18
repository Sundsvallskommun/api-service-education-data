package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.Address;
import generated.se.sundsvall.susanavet.EducationProvider;
import generated.se.sundsvall.susanavet.EducationProviderResponse;
import generated.se.sundsvall.susanavet.LangString;
import generated.se.sundsvall.susanavet.LangStringNode;
import generated.se.sundsvall.susanavet.LangUrl;
import generated.se.sundsvall.susanavet.UrlNode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.educationdata.integration.db.model.EducationProviderEntity;
import se.sundsvall.educationdata.util.Util;

import static org.assertj.core.api.Assertions.assertThat;

class EducationProvidersMapperTest {

	private final EducationProvidersMapper providersMapper = new EducationProvidersMapper();
	private final byte[] json = "{\"page\":{\"totalPages\":1}}".getBytes();

	@Test
	void toZippedProviders_test() {
		final var entity = providersMapper.toZippedProviders(json, 0);

		assertThat(entity.getPage()).isZero();
		assertThat(entity.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(Util.unzip(entity.getJsonBody())).isEqualTo(json);
	}

	@Test
	void toProviderEntities_test() {
		final var content = new EducationProvider()
			.identifier("p.sv.16931486")
			.name(new LangString()
				.strings(List.of(new LangStringNode()
					.lang("swe")
					.value("Sundsvalls gymnasium"))))
			.url(new LangUrl()
				.urls(List.of(new UrlNode()
					.lang("swe")
					.value("https://sundsvall.se/gymnasium"))))
			.visitAddresses(List.of(
				new Address().town("Sundsvall").areaCode("2281"),
				new Address().town("Timrå").areaCode("2262")));

		final var result = providersMapper.toProviderEntities(List.of(new EducationProviderResponse().content(content)));

		assertThat(result).singleElement()
			.extracting(
				EducationProviderEntity::getEducationProviderId,
				EducationProviderEntity::getName,
				EducationProviderEntity::getUrl,
				EducationProviderEntity::getCity,
				EducationProviderEntity::getMunicipalityId,
				EducationProviderEntity::getCreatedAt)
			.containsExactly("p.sv.16931486", "Sundsvalls gymnasium", "https://sundsvall.se/gymnasium", "Sundsvall", "2281", null);
	}

	@Test
	void toProviderEntitiesWithoutVisitAddress() {
		final var content = new EducationProvider()
			.identifier("p.sv.16931486");

		final var result = providersMapper.toProviderEntities(List.of(new EducationProviderResponse().content(content)));

		assertThat(result).singleElement()
			.extracting(EducationProviderEntity::getCity, EducationProviderEntity::getMunicipalityId)
			.containsOnlyNulls();
	}

	@Test
	void toProviderEntitiesSkipsNullResponsesAndContent() {
		final var providers = new ArrayList<EducationProviderResponse>();
		providers.add(null);
		providers.add(new EducationProviderResponse());

		assertThat(providersMapper.toProviderEntities(providers)).isEmpty();
		assertThat(providersMapper.toProviderEntities(null)).isEmpty();
	}
}
