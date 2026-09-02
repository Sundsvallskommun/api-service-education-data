package se.sundsvall.educationdata.integration.plannededucation;

import generated.se.sundsvall.plannededucation.ApiResponseAreasRM;
import generated.se.sundsvall.plannededucation.AreaRM;
import generated.se.sundsvall.plannededucation.AreasRM;
import generated.se.sundsvall.plannededucation.DirectionRM;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;

@Component
public class PlannedEducationIntegrationMapper {

	List<ReferenceCategoryEntity> toReferenceCategory(ApiResponseAreasRM json) {
		return Optional.ofNullable(json.getBody())
			.map(AreasRM::getAreas)
			.orElseGet(List::of)
			.stream()
			.flatMap(area -> Optional.ofNullable(area.getDirections())
				.orElseGet(List::of)
				.stream()
				.map(direction -> toEntity(area, direction)))
			.toList();
	}

	private static ReferenceCategoryEntity toEntity(AreaRM area, DirectionRM direction) {
		return ReferenceCategoryEntity.builder()
			.withCategoryId(String.valueOf(area.getAreaId()))
			.withDirectionId(String.valueOf(direction.getDirectionId()))
			.withCategoryName(area.getName())
			.withDirectionName(direction.getName())
			.build();
	}
}
