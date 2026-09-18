package se.sundsvall.educationdata.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.educationdata.Application;
import se.sundsvall.educationdata.service.EducationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@AutoConfigureWebTestClient
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class EducationResourceFailureTest {

	private static final String EVENT_ID = "e.2281.123";
	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private EducationService educationService;

	@Test
	void searchWithInvalidMunicipalityId() {
		webTestClient.get().uri("/9999/educations")
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}

	@Test
	void searchWithInvalidLimit() {
		webTestClient.get().uri("/2281/educations?limit=0")
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}

	@Test
	void searchWithInvalidPage() {
		webTestClient.get().uri("/2281/educations?page=0")
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}

	@Test
	void searchWithMalformedDate() {
		webTestClient.get().uri("/2281/educations?date=07-06-2026")
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}

	@Test
	void searchWithInvalidSortBy() {
		webTestClient.get().uri("/2281/educations?sortBy=invalidSortOption")
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}

	@Test
	void findEducationByIdNotFound() {
		when(educationService.findEducationById(MUNICIPALITY_ID, EVENT_ID, null))
			.thenThrow(Problem.valueOf(NOT_FOUND, "Education with id '%s' not found for date '%s'".formatted(EVENT_ID, "2026-06-07")));

		final var response = webTestClient.get().uri("/2281/educations/{educationEventId}", EVENT_ID)
			.exchange()
			.expectStatus().isNotFound()
			.expectBody(String.class)
			.returnResult().getResponseBody();

		assertThat(response).contains("not found for date");
	}

	@Test
	void findEducationByIdWithInvalidMunicipalityId() {
		webTestClient.get().uri("/9999/educations/{educationEventId}", EVENT_ID)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}

	@Test
	void findEducationByIdWithMalformedDate() {
		webTestClient.get().uri("/2281/educations/{educationEventId}?date=07-06-2026", EVENT_ID)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}

	@Test
	void findFilterValuesInvalidAttribute() {
		final var response = webTestClient.get().uri("/2281/educations/filters/{filterAttribute}/values", "invalid")
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(String.class)
			.returnResult().getResponseBody();

		assertThat(response).contains("given value invalid is not valid, valid values are [lectureType, languageOfInstructions, studyPace, studyLocation]");
		verifyNoInteractions(educationService);
	}

	@Test
	void findFilterValuesInvalidMunicipalityId() {
		webTestClient.get().uri("/9999/educations/filters/{filterAttribute}/values", "lectureType")
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}

	@Test
	void findFilerValuesWithMalformedDate() {
		webTestClient.get().uri("/2281/educations/filters/{filterAttribute}/values?date=07-06-2026", "lectureType")
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}
}
