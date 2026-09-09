package se.sundsvall.educationdata.api;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.educationdata.Application;
import se.sundsvall.educationdata.api.model.Education;
import se.sundsvall.educationdata.api.model.EducationParameters;
import se.sundsvall.educationdata.api.model.PagedEducationResponse;
import se.sundsvall.educationdata.service.EducationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static se.sundsvall.educationdata.api.model.ApiConstants.LANGUAGE_OF_INSTRUCTIONS;
import static se.sundsvall.educationdata.api.model.ApiConstants.LECTURE_TYPE;
import static se.sundsvall.educationdata.api.model.ApiConstants.STUDY_LOCATION;
import static se.sundsvall.educationdata.api.model.ApiConstants.STUDY_PACE;

@AutoConfigureWebTestClient
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class EducationResourceTest {

	private static final String EVENT_ID = "e.2281.123";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private EducationService educationService;

	@Test
	void search() {
		when(educationService.find(any(EducationParameters.class), isNull()))
			.thenReturn(PagedEducationResponse.builder()
				.withEducations(List.of(Education.builder().withId(EVENT_ID).build()))
				.build());

		final var response = webTestClient.get().uri("/2281/educations")
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedEducationResponse.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getEducations())
			.extracting(Education::getId)
			.containsExactly(EVENT_ID);
	}

	@Test
	void searchWithNoResult() {
		when(educationService.find(any(EducationParameters.class), isNull()))
			.thenReturn(PagedEducationResponse.builder()
				.withEducations(List.of())
				.build());

		final var response = webTestClient.get().uri("/2281/educations")
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedEducationResponse.PagedEducationResponseBuilder.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull();
	}

	@Test
	void searchWithInvalidMunicipalityId() {
		when(educationService.find(any(EducationParameters.class), isNull()))
			.thenReturn(PagedEducationResponse.builder()
				.withEducations(List.of(Education.builder().withId(EVENT_ID).build()))
				.build());

		final var response = webTestClient.get().uri("/9999/educations")
			.exchange()
			.expectStatus().isBadRequest();

		assertThat(response).isNotNull();
		verifyNoInteractions(educationService);
	}

	@Test
	void findEducationById() {

		final var education = Education.builder().withId(EVENT_ID).build();
		when(educationService.findEducationById(EVENT_ID, null)).thenReturn(education);

		final var response = webTestClient.get().uri("/2281/educations/{educationEventId}", EVENT_ID)
			.exchange()
			.expectStatus().isOk()
			.expectBody(Education.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(EVENT_ID);
	}

	@Test
	void findFilterValuesStudyLocation() {
		when(educationService.findFilterValues(STUDY_LOCATION, null)).thenReturn(List.of("Sundsvall", "Örnsköldsvik"));

		final var response = webTestClient.get().uri("/2281/educations/filters/{filterAttribute}/values", "studyLocation")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.returnResult().getResponseBody();

		assertThat(response)
			.isNotNull()
			.contains("Sundsvall", "Örnsköldsvik");
	}

	@Test
	void findFilterValuesLectureType() {
		when(educationService.findFilterValues(LECTURE_TYPE, null)).thenReturn(List.of("Classroom", "Distance"));

		final var response = webTestClient.get().uri("/2281/educations/filters/{filterAttribute}/values", "lectureType")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.returnResult().getResponseBody();

		assertThat(response)
			.isNotNull()
			.contains("Classroom", "Distance");
	}

	@Test
	void findFilterValuesStudyPace() {
		when(educationService.findFilterValues(STUDY_PACE, null)).thenReturn(List.of("100.0", "75.0", "50.0", "25.0"));

		final var response = webTestClient.get().uri("/2281/educations/filters/{filterAttribute}/values", "studyPace")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.returnResult().getResponseBody();

		assertThat(response)
			.isNotNull()
			.contains("100.0", "75.0", "50.0", "25.0");
	}

	@Test
	void findFilterValuesLanguageOfInstructions() {
		when(educationService.findFilterValues(LANGUAGE_OF_INSTRUCTIONS, null)).thenReturn(List.of("swe", "eng"));

		final var response = webTestClient.get().uri("/2281/educations/filters/{filterAttribute}/values", "languageOfInstructions")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull()
			.contains("swe", "eng");
	}
}
