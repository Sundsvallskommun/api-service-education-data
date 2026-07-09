package se.sundsvall.educationdata.integration.plannededucation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategory;

@Component
public class PlannedEducationIntegrationMapper {

	private final ObjectMapper objectMapper;

	public PlannedEducationIntegrationMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	List<ReferenceCategory> toReferenceCategory(String json) throws JsonProcessingException {
		final var areas = objectMapper.readTree(json).path("body").path("areas");
		List<ReferenceCategory> rows = new ArrayList<>();
		for (var area : areas) {
			String categoryId = area.path("areaId").asText();
			String categoryName = area.path("name").asText();

			for (JsonNode direction : area.path("directions")) {
				rows.add(ReferenceCategory.builder()
					.withCategoryId(categoryId)
					.withCategoryName(categoryName)
					.withDirectionId(direction.path("directionId").asText())
					.withDirectionName(direction.path("name").asText())
					.build());
			}
		}
		return rows;
	}
}
