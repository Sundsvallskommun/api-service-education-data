package se.sundsvall.educationdata.integration.plannededucation;

import com.fasterxml.jackson.core.JsonProcessingException;
import generated.se.sundsvall.plannededucation.ApiResponseAreasRM;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategory;

@Component
public class PlannedEducationIntegrationMapper {

	List<ReferenceCategory> toReferenceCategory(ApiResponseAreasRM json) throws JsonProcessingException {
		List<ReferenceCategory> rows = new ArrayList<>();
		for (var area : json.getBody().getAreas()) {
			for (var direction : area.getDirections()) {
				rows.add(ReferenceCategory.builder()
					.withCategoryId(String.valueOf(area.getAreaId()))
					.withCategoryName(area.getName())
					.withDirectionId(String.valueOf(direction.getDirectionId()))
					.withDirectionName(direction.getName())
					.build());
			}
		}
		return rows;
	}
}
