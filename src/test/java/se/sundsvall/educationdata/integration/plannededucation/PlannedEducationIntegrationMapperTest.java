package se.sundsvall.educationdata.integration.plannededucation;

import generated.se.sundsvall.plannededucation.ApiResponseAreasRM;
import generated.se.sundsvall.plannededucation.AreaRM;
import generated.se.sundsvall.plannededucation.AreasRM;
import generated.se.sundsvall.plannededucation.DirectionRM;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(MockitoExtension.class)
class PlannedEducationIntegrationMapperTest {
	private final PlannedEducationIntegrationMapper mapper = new PlannedEducationIntegrationMapper();

	@Test
	void toReferenceCategoryEntityTest() {
		final var response = new ApiResponseAreasRM().body(
			new AreasRM().areas(List.of(new AreaRM()
				.areaId(1L)
				.name("Bygg och anläggning")
				.directions(List.of(
					new DirectionRM().directionId(4L).name("Arkitektur"),
					new DirectionRM().directionId(9L).name("Bergteknik"))))));

		final var rows = mapper.toReferenceCategory(response);

		assertThat(rows).extracting(ReferenceCategoryEntity::getCategoryId, ReferenceCategoryEntity::getCategoryName,
			ReferenceCategoryEntity::getDirectionId, ReferenceCategoryEntity::getDirectionName)
			.containsExactly(
				tuple("1", "Bygg och anläggning", "4", "Arkitektur"),
				tuple("1", "Bygg och anläggning", "9", "Bergteknik"));
	}
}
