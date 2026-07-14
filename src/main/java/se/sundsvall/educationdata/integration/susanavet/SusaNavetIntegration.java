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

	public byte[] getEducationEvents(int page, int size) {
		return client.getAllEducationEvents(page, size);
	}

	public byte[] getEducationInfos(int page, int size) {
		return client.getAllEducationInfos(page, size);
	}

	public byte[] getEducationProviders(int page, int size) {
		return client.getAllEducationProviders(page, size);
	}
}
