package se.sundsvall.educationdata.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.educationdata.scheduler.Scheduler;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@RestController
@RequestMapping("/scheduler")
public class SchedulerResource {

	private final Scheduler scheduler;

	public SchedulerResource(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@PostMapping("/trigger")
	@Operation(summary = "Triggers the scheduled imports", responses = {
		@ApiResponse(responseCode = "200", description = "Successful Operation"),
		@ApiResponse(responseCode = "500",
			description = "Internal Server Error",
			content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
				schema = @Schema(implementation = Problem.class))),
		@ApiResponse(responseCode = "502",
			description = "Bad Gateway",
			content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
				schema = @Schema(implementation = Problem.class)))
	})
	public ResponseEntity<Void> triggerScheduler() throws IOException {
		scheduler.importData();
		return ResponseEntity.ok().build();
	}
}
