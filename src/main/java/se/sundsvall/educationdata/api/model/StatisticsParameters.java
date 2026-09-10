package se.sundsvall.educationdata.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Statistics parameters model")
public class StatisticsParameters {

    @Schema(description = "List of education types", examples = "gymnasial vuxenutbildning")
    private List<String> educationTypes;

    @Schema(description = "List of study locations", examples = "Sundsvall")
    private List<String> studyLocations;

    @Schema(description = "List of studyPace", examples = "25, 50, 75")
    private List<String> studyPace;

    @NotNull
    @Schema(description = "Start date", examples = "2026-05-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @NotNull
    @Schema(description = "End date", examples = "2026-16-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;
}