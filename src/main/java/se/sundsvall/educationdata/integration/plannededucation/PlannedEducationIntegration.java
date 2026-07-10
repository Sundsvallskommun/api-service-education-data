package se.sundsvall.educationdata.integration.plannededucation;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategory;

@Component
@EnableConfigurationProperties(PlannedEducationIntegrationProperties.class)
public class PlannedEducationIntegration {

	private final PlannedEducationClient client;
	private final PlannedEducationIntegrationMapper mapper;

	public PlannedEducationIntegration(PlannedEducationClient client, PlannedEducationIntegrationMapper mapper) {
		this.client = client;
		this.mapper = mapper;
	}

	public List<ReferenceCategory> getAllAreas() throws JsonProcessingException {
		final var response = client.getAllAreas();
		return mapper.toReferenceCategory(response);
	}
}
