package se.sundsvall.educationdata.integration.susanavet;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEvent;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfo;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProvider;
import se.sundsvall.educationdata.util.Util;

@Component
public class SusaNavetIntegrationMapper {

	private final ObjectMapper objectMapper;

	public SusaNavetIntegrationMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public SusaPage<SusaEducationEvent> toEducationEventWithPages(byte[] json) throws IOException {
		final var pages = objectMapper.readTree(json)
			.path("page").path("totalPages").asInt();

		final var entity = SusaEducationEvent.builder()
			.withJsonBody(Util.zip(json))
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
		return new SusaPage<>(entity, pages);
	}

	public SusaPage<SusaEducationInfo> toEducationInfosWithPages(byte[] json) throws IOException {
		final var pages = objectMapper.readTree(json)
			.path("page").path("totalPages").asInt();

		final var entity = SusaEducationInfo.builder()
			.withJsonBody(Util.zip(json))
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();

		return new SusaPage<>(entity, pages);
	}

	public SusaPage<SusaEducationProvider> toEducationProviderWithPages(byte[] json) throws IOException {
		final var entity = SusaEducationProvider.builder()
			.withJsonBody(Util.zip(json))
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
		final var pages = objectMapper.readTree(json)
			.path("page").path("totalPages").asInt();

		return new SusaPage<>(entity, pages);
	}
}
