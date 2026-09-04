package se.sundsvall.educationdata.integration.susanavet;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(SusaNavetIntegrationProperties.class)
public class SusaNavetIntegration {

	private final SusaNavetClient client;

	private static final int PAGE_SIZE = 2000;

	SusaNavetIntegration(SusaNavetClient client) {
		this.client = client;
	}

	public byte[] getEducationEvents(int page) {
		return client.getAllEducationEvents(page, PAGE_SIZE);
	}

	public byte[] getEducationInfos(int page) {
		return client.getAllEducationInfos(page, PAGE_SIZE);
	}

	public byte[] getEducationProviders(int page) {
		return client.getAllEducationProviders(page, PAGE_SIZE);
	}
}
