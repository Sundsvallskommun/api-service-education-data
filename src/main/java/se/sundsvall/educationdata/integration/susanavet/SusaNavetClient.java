package se.sundsvall.educationdata.integration.susanavet;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
	name = "susa-navet",
	url = "https://api.skolverket.se/susa-navet/emil3",
	configuration = SusaNavetIntegrationConfiguration.class)
public interface SusaNavetClient {

	@GetMapping("/educationEvents")
	ResponseEntity<byte[]> getAllEducationEvents(
		@RequestParam("page") int page,
		@RequestParam("size") int size);

	@GetMapping("/educationInfos")
	ResponseEntity<byte[]> getAllEducationInfos(
		@RequestParam("page") int page,
		@RequestParam("size") int size);

	@GetMapping("/educationProviders")
	ResponseEntity<byte[]> getAllEducationProviders(
		@RequestParam("page") int page,
		@RequestParam("size") int size);
}
