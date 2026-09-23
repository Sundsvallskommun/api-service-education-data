package se.sundsvall.educationdata.api;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.educationdata.Application;
import se.sundsvall.educationdata.api.model.Statistics;
import se.sundsvall.educationdata.api.model.StatisticsParameters;
import se.sundsvall.educationdata.service.StatisticsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@AutoConfigureWebTestClient
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class StatisticsResourceTest {
	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private StatisticsService statisticsService;

	@Test
	void getStatisticsBindsParametersAndImportDate() {
		final var importDate = LocalDate.of(2026, Month.JUNE, 7);

		final var parameters = StatisticsParameters.builder()
			.withStartDate(LocalDate.of(2026, Month.MAY, 1))
			.withEndDate(LocalDate.of(2026, Month.NOVEMBER, 1))
			.withSchoolType("VUXGY")
			.withStudyLocations(List.of("Sundsvall"))
			.build();

		when(statisticsService.getStatisticsByParameters(parameters, importDate))
			.thenReturn(Statistics.builder()
				.withTotalEducations(2)
				.withAvailableSeats(5)
				.build());

		final var response = webTestClient.get()
			.uri("/2281/statistics"
				+ "?startDate=2026-05-01&endDate=2026-11-01"
				+ "&date=2026-06-07"
				+ "&schoolType=VUXGY&studyLocations=Sundsvall")
			.exchange()
			.expectStatus().isOk()
			.expectBody(Statistics.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTotalEducations()).isEqualTo(2);
		assertThat(response.getAvailableSeats()).isEqualTo(5);

		verify(statisticsService).getStatisticsByParameters(parameters, importDate);
	}

	@Test
	void getStatisticsWithMissingEndDateReturnsBadRequest() {
		webTestClient.get()
			.uri("/2281/statistics?startDate=2026-05-01")
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(statisticsService);
	}

	@Test
	void getStatisticsWithInvalidMunicipalityReturnsBadRequest() {
		webTestClient.get()
			.uri("/9999/statistics"
				+ "?startDate=2026-05-01&endDate=2026-11-01")
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(statisticsService);
	}

	@Test
	void findStudyLocationValues() {
		when(statisticsService.findStatisticsFilterValues(
			"studyLocations", null))
			.thenReturn(List.of("Härnösand", "Sundsvall"));

		final var response = webTestClient.get()
			.uri("/2281/statistics/filters/studyLocations/values")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull()
			.contains("Härnösand", "Sundsvall");

		verify(statisticsService)
			.findStatisticsFilterValues("studyLocations", null);
	}
}
