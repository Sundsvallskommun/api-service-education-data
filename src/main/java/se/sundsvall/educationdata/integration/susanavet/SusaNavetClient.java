package se.sundsvall.educationdata.integration.susanavet;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegrationConfiguration.CLIENT_ID;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.susa-navet.base-url}",
	configuration = SusaNavetIntegrationConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface SusaNavetClient {

	@GetMapping(value = "/educationEvents", produces = MediaType.APPLICATION_JSON_VALUE)
	byte[] getAllEducationEvents(
		@RequestParam("page") int page,
		@RequestParam("size") int size);

	@GetMapping(value = "/educationInfos", produces = MediaType.APPLICATION_JSON_VALUE)
	byte[] getAllEducationInfos(
		@RequestParam("page") int page,
		@RequestParam("size") int size);

	@GetMapping(value = "/educationProviders", produces = MediaType.APPLICATION_JSON_VALUE)
	byte[] getAllEducationProviders(
		@RequestParam("page") int page,
		@RequestParam("size") int size);
}
