package se.sundsvall.educationdata.integration.plannededucation;

import generated.se.sundsvall.plannededucation.ApiResponseAreasRM;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import static se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegrationConfiguration.CLIENT_ID;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.planned-education.base-url}",
	configuration = PlannedEducationIntegrationConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface PlannedEducationClient {

	@GetMapping(value = "/v4/adult-education-events/areas", produces = "application/vnd.skolverket.plannededucations.api.v4.hal+json")
	ApiResponseAreasRM getAllAreas();
}
