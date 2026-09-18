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
import se.sundsvall.educationdata.api.model.Education;
import se.sundsvall.educationdata.api.model.EducationParameters;
import se.sundsvall.educationdata.api.model.PagedEducationResponse;
import se.sundsvall.educationdata.api.validation.FilterType;
import se.sundsvall.educationdata.api.validation.ValidFilter;
import se.sundsvall.educationdata.service.EducationService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping(path = "/{municipalityId}/educations")
@Tag(name = "Educations", description = "find educations")
@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class EducationResource {

	private final EducationService educationService;

	public EducationResource(EducationService educationService) {
		this.educationService = educationService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Search for educations", responses = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<PagedEducationResponse> search(
		@Parameter(name = "municipalityId", description = "MunicipalityId", example = "2281") @PathVariable @ValidMunicipalityId String municipalityId,
		@ParameterObject @Valid final EducationParameters parameters,
		@Parameter(name = "date", description = "Date of instance yyyy-mm-dd") @RequestParam(required = false) LocalDate date) {
		return ok(educationService.find(municipalityId, parameters, date));
	}

	@GetMapping(path = "/{educationEventId}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Find educations by id", responses = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Education> findEducationsEventById(
		@Parameter(name = "municipalityId", description = "MunicipalityId", example = "2281") @PathVariable @ValidMunicipalityId String municipalityId,
		@Parameter(name = "educationEventId", description = "Id of Education event", example = "e.2281.12345678") @PathVariable String educationEventId,
		@Parameter(name = "date", description = "Date of instance", example = "2026-06-07") @RequestParam(required = false) LocalDate date) {
		return ok(educationService.findEducationById(municipalityId, educationEventId, date));
	}

	@GetMapping(path = "/filters/{filterAttribute}/values", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Find available filter values", description = "Find available filter values to use in the find resource", responses = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<List<String>> findFilterValues(
		@Parameter(name = "municipalityId", description = "MunicipalityId", example = "2281") @PathVariable @ValidMunicipalityId String municipalityId,
		@Parameter(name = "filterAttribute",
			description = "The attribute name to get available values from") @ValidFilter(type = FilterType.EDUCATION) @PathVariable String filterAttribute,
		@Parameter(name = "date", description = "Date of instance yyyy-mm-dd") @RequestParam(required = false) LocalDate date) {
		return ok(educationService.findFilterValues(municipalityId, filterAttribute, date));
	}
}
