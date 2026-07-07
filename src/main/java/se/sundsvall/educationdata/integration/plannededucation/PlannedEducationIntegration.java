package se.sundsvall.educationdata.integration.plannededucation;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(PlannedEducationIntegrationProperties.class)
public class PlannedEducationIntegration {

	private final PlannedEducationClient client;

	public PlannedEducationIntegration(PlannedEducationClient client) {
		this.client = client;
	}

	public String getAllAreas() {
		var body = client.getAllAreas().getBody();
		if (body == null) {
			return null;
		}
		return new String(body);
	}
}
