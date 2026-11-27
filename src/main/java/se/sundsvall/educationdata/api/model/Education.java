package se.sundsvall.educationdata.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Education", accessMode = Schema.AccessMode.READ_ONLY)
public class Education {

	@Schema(description = "Education event ID", examples = "e.myh.yh.27217")
	private String id;

	@Schema(description = "Education name", examples = "Vatten- & Miljötekniker")
	private String title;

	@Schema(description = "Education title, for some educations that have have a title instead of an education name", examples = "Naturprogrammet")
	private String eventTitle;

	@Schema(description = "Education provider name", examples = "Sundsvalls kommun")
	private String provider;

	@Schema(description = "Education provider web page", examples = "https://sundsvall.se/vuxenutbildning")
	private String providerUrl;

	@Schema(description = "Organisation running the education on behalf of the provider", examples = "Jensen Gy")
	private String contractor;

	@Schema(description = "School name, mostly for upper secondary schools", examples = "Sundsvalls gymnasium, Västermalms skola")
	private String schoolName;

	@Schema(description = "Study location", examples = "Sundsvall")
	private String studyLocation;

	@Schema(description = "Study site as described by the provider", examples = "Campus")
	private String studySite;

	@Schema(description = "Municipality ID", examples = "2281")
	private String municipalityId;

	@Schema(description = "URL to the education", examples = "https://sundsvall.alvis.se/hittakurser/kurs/38837")
	private String url;

	@Schema(description = "Number of seats", examples = "25")
	private Integer numberOfSeats;

	@Schema(description = "Cost", examples = "0")
	private BigDecimal cost;

	@Schema(description = "Currency", examples = "SEK")
	private String currency;

	@Schema(description = "Lecture type", examples = "Distans")
	private String lectureType;

	@Schema(description = "Study pace in percent", examples = "100")
	private String studyPace;

	@Schema(description = "Language of instruction", examples = "Svenska")
	private String languageOfInstructions;

	@Schema(description = "Start date", examples = "2026-09-07")
	private LocalDate start;

	@Schema(description = "End date", examples = "2028-06-01")
	private LocalDate end;

	@Schema(description = "Type of start from Susa: 0 = not decided, 1 = date set, 2 and 3 = continuous start", examples = "1")
	private String startType;

	@Schema(description = "Earliest application date", examples = "2026-01-15")
	private LocalDate earliestApplication;

	@Schema(description = "Latest application date", examples = "2026-04-15")
	private LocalDate latestApplication;

	@Schema(description = "Link to the application", examples = "https://vasternorrland.mittgymnasieval.se/")
	private String applicationUrl;

	@Schema(description = "Whether the education is cancelled", examples = "false")
	private Boolean cancelled;

	@Schema(description = "Education code", examples = "PRRPRR02")
	private String code;

	@Schema(description = "School type code", examples = "VUXGY")
	private String schoolType;

	@Schema(description = "Education type", examples = "Yrkeshögskoleexamen")
	private String educationType;

	@Schema(description = "Education information", examples = "Utbildningen ger dig kompetens inom vatten- och miljöteknik.")
	private String information;

	@Schema(description = "Credits", examples = "400")
	private Double credits;

	@Schema(description = "Credit type", examples = "yh")
	private String creditType;

	@Schema(description = "Duration", examples = "2 år")
	private String duration;

	@Schema(description = "Unit of duration", examples = "days")
	private String durationUnit;

	@Schema(description = "Eligibility requirements", examples = "Grundläggande behörighet")
	private String eligibility;

	@Schema(description = "Recommended prior knowledge", examples = "Matematik 2")
	private String recommendedPriorKnowledge;

	@Schema(description = "Degrees awarded", examples = "Yrkeshögskoleexamen")
	private List<String> degree;
}
