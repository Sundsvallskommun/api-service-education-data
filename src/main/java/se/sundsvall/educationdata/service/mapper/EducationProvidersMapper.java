package se.sundsvall.educationdata.service.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProviderEntity;
import se.sundsvall.educationdata.util.Util;

@Component
public class EducationProvidersMapper {
	public SusaEducationProviderEntity toZippedProviders(byte[] json, int page) {
		return SusaEducationProviderEntity.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}
}
