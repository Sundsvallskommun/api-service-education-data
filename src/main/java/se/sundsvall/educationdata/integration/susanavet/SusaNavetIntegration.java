package se.sundsvall.educationdata.integration.susanavet;

import java.io.IOException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEvent;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfo;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProvider;

@Component
@EnableConfigurationProperties(SusaNavetIntegrationProperties.class)
public class SusaNavetIntegration {

	private final SusaNavetClient client;
	private final SusaNavetIntegrationMapper mapper;

	private static final String EMPTY_BODY = "Empty body for page %s";

	SusaNavetIntegration(SusaNavetClient client, SusaNavetIntegrationMapper mapper) {
		this.client = client;
		this.mapper = mapper;
	}

	public SusaPage<SusaEducationEvent> getEducationEventsWithPage(int page, int size) throws IOException {
		final var response = client.getAllEducationEvents(page, size);
		if (response == null || response.length == 0) {
			throw Problem.valueOf(HttpStatus.BAD_GATEWAY, EMPTY_BODY.formatted(page));
		}
		return mapper.toEducationEventWithPages(response);

	}

	public SusaPage<SusaEducationInfo> getEducationInfosWithPage(int page, int size) throws IOException {
		final var response = client.getAllEducationInfos(page, size);
		if (response == null || response.length == 0) {
			throw Problem.valueOf(HttpStatus.BAD_GATEWAY, EMPTY_BODY.formatted(page));
		}
		return mapper.toEducationInfosWithPages(response);
	}

	public SusaPage<SusaEducationProvider> getEducationProvidersWithPage(int page, int size) throws IOException {
		final var response = client.getAllEducationProviders(page, size);
		if (response == null || response.length == 0) {
			throw Problem.valueOf(HttpStatus.BAD_GATEWAY, EMPTY_BODY.formatted(page));
		}
		return mapper.toEducationProviderWithPages(response);
	}
}
