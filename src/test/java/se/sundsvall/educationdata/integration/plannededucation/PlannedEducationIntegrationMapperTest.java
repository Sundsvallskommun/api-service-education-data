package se.sundsvall.educationdata.integration.plannededucation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(MockitoExtension.class)
public class PlannedEducationIntegrationMapperTest {
	private final PlannedEducationIntegrationMapper mapper = new PlannedEducationIntegrationMapper(new ObjectMapper());

	@Test
	void toReferenceCategoryTest() throws JsonProcessingException {
		final var json = """
			{
			"body": {
			  "areas": [{
			    "areaId": 1, "name": "Bygg och anläggning", "directions": [{
			      "directionId": 4, "name": "Arkitektur" },{
			      "directionId": 9, "name": "Bergteknik" }]}
			  ]}
			}
			""";

		final var rows = mapper.toReferenceCategory(json);

		assertThat(rows).extracting(ReferenceCategory::getCategoryId, ReferenceCategory::getCategoryName,
			ReferenceCategory::getDirectionId, ReferenceCategory::getDirectionName)
			.containsExactly(
				tuple("1", "Bygg och anläggning", "4", "Arkitektur"),
				tuple("1", "Bygg och anläggning", "9", "Bergteknik"));
	}
}
