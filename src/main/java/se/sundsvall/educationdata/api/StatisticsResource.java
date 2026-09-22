package se.sundsvall.educationdata.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.educationdata.api.model.Statistics;
import se.sundsvall.educationdata.api.model.StatisticsParameters;
import se.sundsvall.educationdata.api.validation.FilterType;
import se.sundsvall.educationdata.api.validation.ValidFilter;
import se.sundsvall.educationdata.service.StatisticsService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping(path = "/{municipalityId}/statistics")
@Tag(name = "Statistics", description = "Education statistics")
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
public class StatisticsResource {

	private final StatisticsService statisticsService;

	public StatisticsResource(StatisticsService statisticsService) {
		this.statisticsService = statisticsService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get education statistics", responses = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Statistics> getStatistics(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@ParameterObject @Valid final StatisticsParameters parameters,
		@Parameter(name = "date", description = "date of instance yyyy-mm-dd", example = "2026-06-07") @RequestParam(required = false) LocalDate date) {
		return ok(statisticsService.getStatisticsByParameters(parameters, date));
	}

	@GetMapping(path = "filters/{filterAttribute}/values", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get available filter values", responses = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<List<String>> findFilterValue(
		@Parameter @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "filterAttribute", description = "Attribute name to get available values from") @ValidFilter(type = FilterType.STATISTICS) @PathVariable final String filterAttribute,
		@Parameter(name = "date", description = "date of instance yyyy-mm-dd", example = "2026-06-07") @RequestParam(required = false) LocalDate date) {
		return ok(statisticsService.findStatisticsFilterValues(filterAttribute, date));
	}
}
