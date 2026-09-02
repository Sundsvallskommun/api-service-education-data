package se.sundsvall.educationdata.integration.plannededucation;

import generated.se.sundsvall.plannededucation.ApiResponseListedAdultEducationEvents;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;

@Component
@EnableConfigurationProperties(PlannedEducationIntegrationProperties.class)
public class PlannedEducationIntegration {

	private final PlannedEducationClient client;
	private final PlannedEducationIntegrationMapper mapper;

	private static final int PAGE_SIZE = 200;

	public PlannedEducationIntegration(PlannedEducationClient client, PlannedEducationIntegrationMapper mapper) {
		this.client = client;
		this.mapper = mapper;
	}

	public List<ReferenceCategoryEntity> getAllReferenceCategories() {
		final var response = client.getAllReferenceCategories();
		return mapper.toReferenceCategory(response);
	}

	public ApiResponseListedAdultEducationEvents getEducationEventsByReferenceId(String directionId, Set<String> municipalityId, int page) {
		return client.getEventsByDirection(directionId, municipalityId, page, PAGE_SIZE);
	}
}
