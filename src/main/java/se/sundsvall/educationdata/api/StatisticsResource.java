package se.sundsvall.educationdata.api;

import io.swagger.v3.oas.annotations.Parameter;
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
import se.sundsvall.educationdata.api.model.Statistics;
import se.sundsvall.educationdata.api.model.StatisticsParameters;
import se.sundsvall.educationdata.service.StatisticsService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RequestMapping(path = "/{municipalityId}/statistics")
@RestController
public class StatisticsResource {

	private final StatisticsService statisticsService;

	public StatisticsResource(StatisticsService statisticsService) {
		this.statisticsService = statisticsService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	ResponseEntity<Statistics> getStatistics(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@ParameterObject @Valid final StatisticsParameters parameters,
		@Parameter @RequestParam(required = false) LocalDate date) {
		return ok(statisticsService.getStatisticsByParameters(municipalityId, parameters, date));
	}

	@GetMapping(path = "filters/{filterAttribute}/values", produces = APPLICATION_JSON_VALUE)
	ResponseEntity<List<String>> findFilerValue(
		@Parameter @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter @Validated @PathVariable final String filterAttribute,
		@Parameter @RequestParam(required = false) LocalDate date) {
		return ok(statisticsService.findStatisticsFilterValues(filterAttribute, date));
	}
}
