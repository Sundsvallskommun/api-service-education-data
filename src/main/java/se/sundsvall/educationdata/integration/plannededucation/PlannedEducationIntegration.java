package se.sundsvall.educationdata.integration.plannededucation;

import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;

import static se.sundsvall.dept44.problem.Problem.badGateway;

@Component
@EnableConfigurationProperties(PlannedEducationIntegrationProperties.class)
public class PlannedEducationIntegration {

	private final PlannedEducationClient client;
	private final PlannedEducationIntegrationMapper mapper;

	public PlannedEducationIntegration(PlannedEducationClient client, PlannedEducationIntegrationMapper mapper) {
		this.client = client;
		this.mapper = mapper;
	}

	public List<ReferenceCategoryEntity> getAllReferenceCategories() {
		final var response = client.getAllReferenceCategories();
		if (response == null || response.getBody() == null || response.getBody().getAreas() == null
			|| response.getBody().getAreas().isEmpty()) {
			throw badGateway("Empty body");
		}
		return mapper.toReferenceCategory(response);
	}
}
