package se.sundsvall.educationdata.service.mapper;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProviderEntity;
import se.sundsvall.educationdata.util.Util;

@Component
public class SusaMapper {

	public SusaEducationEventEntity toZippedEvents(byte[] json, int page) {
		return SusaEducationEventEntity.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}

	public SusaEducationInfoEntity toZippedInfos(byte[] json, int page) {
		return SusaEducationInfoEntity.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}

	public SusaEducationProviderEntity toZippedProviders(byte[] json, int page) {
		return SusaEducationProviderEntity.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}
}
