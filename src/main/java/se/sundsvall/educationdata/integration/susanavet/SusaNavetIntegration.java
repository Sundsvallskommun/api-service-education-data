package se.sundsvall.educationdata.integration.susanavet;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import static se.sundsvall.dept44.problem.Problem.badGateway;

@Component
@EnableConfigurationProperties(SusaNavetIntegrationProperties.class)
public class SusaNavetIntegration {

	private final SusaNavetClient client;

	private static final String EMPTY_BODY = "Empty body";
	private static final int PAGE_SIZE = 2000;

	SusaNavetIntegration(SusaNavetClient client) {
		this.client = client;
	}

	public byte[] getEducationEvents(int page) {
		var response = client.getAllEducationEvents(page, PAGE_SIZE);
		if (response == null || response.length == 0) {
			throw badGateway(EMPTY_BODY);
		}
		return response;
	}

	public byte[] getEducationInfos(int page) {
		var response = client.getAllEducationInfos(page, PAGE_SIZE);
		if (response == null || response.length == 0) {
			throw badGateway(EMPTY_BODY);
		}
		return response;
	}

	public byte[] getEducationProviders(int page) {
		var response = client.getAllEducationProviders(page, PAGE_SIZE);
		if (response == null || response.length == 0) {
			throw badGateway(EMPTY_BODY);
		}
		return response;
	}
}
