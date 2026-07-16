package se.sundsvall.educationdata.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.educationdata.scheduler.Scheduler;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@RestController
@RequestMapping("{municipalityId}/scheduler")
class SchedulerResource {

	private final Scheduler scheduler;

	public SchedulerResource(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@PostMapping("/trigger")
	@Operation(summary = "Triggers the scheduled imports", responses = {
		@ApiResponse(responseCode = "202", description = "Accepted"),
		@ApiResponse(responseCode = "400",
			description = "Bad Request",
			content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
				schema = @Schema(implementation = Problem.class)))
	})

	public ResponseEntity<Void> triggerScheduler(@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId) {
		scheduler.triggerAsyncImport();
		return ResponseEntity.accepted().build();
	}
}
