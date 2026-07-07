package se.sundsvall.educationdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategory;
import se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegration;

@Service
public class PlannedEducationService {

	private final PlannedEducationIntegration integration;
	private final ReferenceCategoryRepository referenceCategoryRepository;
	private final ObjectMapper objectMapper;

	public PlannedEducationService(PlannedEducationIntegration integration, ReferenceCategoryRepository referenceCategoryRepository, ObjectMapper objectMapper) {
		this.integration = integration;
		this.referenceCategoryRepository = referenceCategoryRepository;
		this.objectMapper = objectMapper;
	}

	public ResponseEntity<Void> getEducationInfo() throws JsonProcessingException {
		String json = integration.getAllAreas();

		JsonNode areas = objectMapper.readTree(json)
			.path("body")
			.path("areas");
		List<ReferenceCategory> rows = new ArrayList<>();
		for (JsonNode area : areas) {
			String categoryId = area.path("areaId").asText();
			String categoryName = area.path("name").asText();

			for (JsonNode direction : area.path("directions")) {
				rows.add(ReferenceCategory.builder()
					.withCategoryId(categoryId)
					.withCategoryName(categoryName)
					.withDirectionId(direction.path("directionId").asText())
					.withDirectionName(direction.path("name").asText()).build());
			}
		}

		referenceCategoryRepository.deleteAllInBatch();
		referenceCategoryRepository.saveAll(rows);
		return ResponseEntity.ok().build();
	}
}
