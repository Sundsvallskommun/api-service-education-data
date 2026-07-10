package se.sundsvall.educationdata.service.mapper;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEvent;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfo;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProvider;
import se.sundsvall.educationdata.util.Util;

@Component
public class SusaMapper {

	public SusaEducationEvent toZippedEvents(byte[] json, int page) {
		return SusaEducationEvent.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}

	public SusaEducationInfo toZippedInfos(byte[] json, int page) {
		return SusaEducationInfo.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}

	public SusaEducationProvider toZippedProviders(byte[] json, int page) {
		return SusaEducationProvider.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}
}
