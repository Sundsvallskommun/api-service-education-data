package se.sundsvall.educationdata.integration.susanavet;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(SusaNavetIntegrationProperties.class)
public class SusaNavetIntegration {

	private final SusaNavetClient client;

	SusaNavetIntegration(SusaNavetClient client) {
		this.client = client;
	}

	public String getEducationEvents(int page, int size) {
		var body = client.getAllEducationEvents(page, size).getBody();
		if (body == null) {
			return null;
		}
		return new String(body);
	}

	public String getEducationInfos(int page, int size) {
		var body = client.getAllEducationInfos(page, size).getBody();
		if (body == null) {
			return null;
		}
		return new String(body);
	}

	public String getEducationProviders(int page, int size) {
		var body = client.getAllEducationProviders(page, size).getBody();
		if (body == null) {
			return null;
		}
		return new String(body);
	}
}
