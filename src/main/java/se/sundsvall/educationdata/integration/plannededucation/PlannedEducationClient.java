package se.sundsvall.educationdata.integration.plannededucation;

import generated.se.sundsvall.plannededucation.ApiResponseAreasRM;
import generated.se.sundsvall.plannededucation.ApiResponseListedAdultEducationEvents;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegrationConfiguration.CLIENT_ID;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.planned-education.base-url}",
	configuration = PlannedEducationIntegrationConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface PlannedEducationClient {

	@GetMapping(value = "/v4/adult-education-events/areas", produces = "application/vnd.skolverket.plannededucations.api.v4.hal+json")
	ApiResponseAreasRM getAllReferenceCategories();

	@GetMapping(value = "/v4/adult-education-events", produces = "application/vnd.skolverket.plannededucations.api.v4.hal+json")
	ApiResponseListedAdultEducationEvents getEventsByDirection(
		@RequestParam("directionIds") String directionId,
		@RequestParam("geographicalAreaCode") String geographicalAreaCode,
		@RequestParam("page") int page,
		@RequestParam("size") int size);
}
