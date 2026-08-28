package se.sundsvall.educationdata.integration.plannededucation;

import generated.se.sundsvall.plannededucation.ApiResponseListedAdultEducationEvents;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;

import static se.sundsvall.dept44.problem.Problem.badGateway;

@Component
@EnableConfigurationProperties(PlannedEducationIntegrationProperties.class)
public class PlannedEducationIntegration {

	private final PlannedEducationClient client;
	private final PlannedEducationIntegrationMapper mapper;

	private static final String EMPTY_BODY = "Empty body";
	private static final int PAGE_SIZE = 200;

	public PlannedEducationIntegration(PlannedEducationClient client, PlannedEducationIntegrationMapper mapper) {
		this.client = client;
		this.mapper = mapper;
	}

	public List<ReferenceCategoryEntity> getAllReferenceCategories() {
		final var response = client.getAllReferenceCategories();
		if (response == null || response.getBody() == null || response.getBody().getAreas() == null
			|| response.getBody().getAreas().isEmpty()) {
			throw badGateway(EMPTY_BODY);
		}
		return mapper.toReferenceCategory(response);
	}

	public ApiResponseListedAdultEducationEvents getEducationEventsByReferenceId(String directionId, Set<String> municipalityId, int page) {
		final var response = client.getEventsByDirection(directionId, municipalityId, page, PAGE_SIZE);
		if (response == null || response.getBody() == null) {
			throw badGateway(EMPTY_BODY);
		}
		return response;
	}
}
