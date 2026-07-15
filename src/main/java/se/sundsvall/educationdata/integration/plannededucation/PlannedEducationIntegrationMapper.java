package se.sundsvall.educationdata.integration.plannededucation;

import generated.se.sundsvall.plannededucation.ApiResponseAreasRM;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;

@Component
public class PlannedEducationIntegrationMapper {

	List<ReferenceCategoryEntity> toReferenceCategory(ApiResponseAreasRM json) {
		List<ReferenceCategoryEntity> rows = new ArrayList<>();
		for (var area : json.getBody().getAreas()) {
			for (var direction : area.getDirections()) {
				rows.add(ReferenceCategoryEntity.builder()
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
