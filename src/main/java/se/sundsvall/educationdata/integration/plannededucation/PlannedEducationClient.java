package se.sundsvall.educationdata.integration.plannededucation;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
	name = "planned-education",
	url = "https://api.skolverket.se/planned-educations",
	configuration = PlannedEducationIntegrationConfiguration.class)
public interface PlannedEducationClient {

	@GetMapping("/v4/adult-education-events/areas")
	ResponseEntity<byte[]> getAllAreas();
}
