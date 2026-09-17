package se.sundsvall.educationdata.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class EducationParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Education event ID", examples = "e.2281.12345678")
	private String educationEventId;

	@Schema(description = "Education name", examples = "El och energiprogrammet")
	private String name;

	@Schema(description = "Education title", examples = "El-tekniker")
	private String title;

	@Schema(description = "Study location", examples = "Sundsvall")
	private String studyLocation;

	@Schema(description = "Number of seats", examples = "20")
	private Integer seats;

	@Schema(description = "Lecture type", examples = "Distance")
	private String lectureType;

	@Schema(description = "Study pace in percent", examples = "100.0")
	private String studyPace;

	@Schema(description = "Language of instruction", examples = "swe")
	private String languageOfInstructions;

	@Schema(description = "Whether the education is cancelled", examples = "false")
	private Boolean cancelled;

	@Schema(description = "Education start date", examples = "2026-09-07")
	private LocalDate startDate;

	@Schema(description = "Education end date", examples = "2026-12-31")
	private LocalDate endDate;

	@Schema(description = "Application start date", examples = "2026-03-15")
	private LocalDate applicationDateStart;

	@Schema(description = "Application end date", examples = "2026-04-15")
	private LocalDate applicationDateEnd;

	@Schema(description = "School type", allowableValues = {
		"HS", "VUXGY", "GY", "VUXGR", "AUB", "VUXGRAN", "VUXGYAN", "YH", "FHS", "GYAN"
	})
	private String schoolType;

	@Schema(description = "Type of education", allowableValues = {
		"kurs", "program", "kurspaket"
	})
	private String educationType;

	@Schema(description = "Recommended level of prior knowledge", examples = "Grundläggande")
	private String recommendedPriorKnowledge;

	@Schema(description = "Requirements for eligibility", examples = "Du behöver en godkänd grundskoleutbildning")
	private String eligibility;

	@Schema(description = "duration of education", examples = "30")
	private String duration;

	@Schema(description = "Date of expiration", examples = "2027-12-31T23:59:59")
	private LocalDateTime expires;

	@Schema(description = "Amount of credits", examples = "30")
	private Double credits;

	@Schema(description = "Type of credits", examples = "hp")
	private String creditType;
}
