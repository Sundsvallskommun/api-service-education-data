package se.sundsvall.educationdata.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Statistics parameters model")
public class StatisticsParameters {

	@Schema(description = "List of categories", examples = "Teknik")
	private List<String> categories;

	@Schema(description = "List of direction(subcategories)", examples = "Datateknik")
	private List<String> directions;

	@Schema(description = "School type", examples = "Distance")
	private String schoolType;

	@Schema(description = "List of study locations", examples = "Sundsvall")
	private List<String> studyLocations;

	@Schema(description = "language of instruction", examples = "swe")
	private String languageOfInstructions;

	@Schema(description = "List of studyPace", examples = "25.0, 50.0, 75.0, 100.0")
	private List<String> studyPace;

	@NotNull
	@Schema(description = "Start date", examples = "2026-05-01", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDate startDate;

	@NotNull
	@Schema(description = "End date", examples = "2026-11-01", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDate endDate;
}
