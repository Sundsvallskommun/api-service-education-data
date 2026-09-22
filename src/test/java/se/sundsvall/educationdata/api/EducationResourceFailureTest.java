package se.sundsvall.educationdata.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private EducationService educationService;

	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = {
		"/9999/educations",
		"/2281/educations?limit=0",
		"/2281/educations?page=0",
		"/2281/educations?date=07-06-2026",
		"/2281/educations?sortBy=invalidSortOption"
	})
	void searchWithInvalidParameters(final String url) {
		webTestClient.get().uri(url)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}

	@Test
	void findEducationByIdNotFound() {
		when(educationService.findEducationById(EVENT_ID, null))
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
	void findFilterValuesWithMalformedDate() {
		webTestClient.get().uri("/2281/educations/filters/{filterAttribute}/values?date=07-06-2026", "lectureType")
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(educationService);
	}
}
