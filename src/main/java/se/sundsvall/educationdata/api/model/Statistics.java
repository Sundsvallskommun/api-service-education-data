package se.sundsvall.educationdata.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class Statistics {
	@Schema(description = "Number of on-going educations", examples = "10", accessMode = READ_ONLY)
	private long onGoingEducations;

	@Schema(description = "Number of educations planned to start within span", examples = "10", accessMode = READ_ONLY)
	private long plannedEducations;

	@Schema(description = "Number of finished educations", examples = "10", accessMode = READ_ONLY)
	private long finishedEducations;

	@Schema(description = "Number of educations with missing start date", examples = "10", accessMode = READ_ONLY)
	private long missingStartDateEducations;

	@Schema(description = "Number of educations with missing end date", examples = "10", accessMode = READ_ONLY)
	private long missingEndDateEducations;

	@Schema(description = "Number of educations with missing start and end date", examples = "10", accessMode = READ_ONLY)
	private long missingStartAndEndDateEducations;

	@Schema(description = "Number of educations during date span", examples = "10", accessMode = READ_ONLY)
	private long totalEducations;

	@Schema(description = "Number of available seats", examples = "10", accessMode = READ_ONLY)
	private int availableSeats;

	@Schema(description = "Total capacity", examples = "10", accessMode = READ_ONLY)
	private int totalCapacity;

	@Schema(description = "Study locations used for filtering", accessMode = READ_ONLY)
	private List<String> studyLocations = new ArrayList<>();

	@Schema(description = "SchoolTypes", accessMode = READ_ONLY)
	private Map<String, Long> schoolType;

	@Schema(description = "Categories used for filtering", accessMode = READ_ONLY)
	private List<String> categories = new ArrayList<>();

	@Schema(description = "directions used for filtering", accessMode = READ_ONLY)
	private List<String> directions = new ArrayList<>();

	@Schema(description = "Start date used for filtering", accessMode = READ_ONLY)
	private LocalDate startDate;

	@Schema(description = "End date used for filtering", accessMode = READ_ONLY)
	private LocalDate endDate;
}
