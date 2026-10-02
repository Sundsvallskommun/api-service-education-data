package se.sundsvall.educationdata.apptest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.educationdata.Application;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Sql(scripts = "/db/scripts/testdata.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@WireMockAppTestSuite(files = "classpath:/StatisticsIT/", classes = Application.class)
class StatisticsIT extends AbstractAppTest {

	private static final String RESPONSE_FILE = "response.json";
	private static final String PATH = "/2281/statistics";
	private static final String PERIOD = "?startDate=2026-05-01&endDate=2026-11-01";

	@Test
	void test01_getStatistics() {
		setupCall()
			.withServicePath(PATH + PERIOD)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getStatisticsWithMunicipalityIdAndImportDate() {
		setupCall()
			.withServicePath(PATH + PERIOD + "&municipalityIds=2281&date=2026-06-06")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_getStatisticsWithCombinedFilters() {
		setupCall()
			.withServicePath(PATH + PERIOD + "&schoolType=VUXGY&studyLocations=Sundsvall")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_findStatisticsFilterValues() {
		setupCall()
			.withServicePath(PATH + "/filters/studyLocations/values")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_getStatisticsWithInvalidRequest() {
		setupCall()
			.withServicePath(PATH + "?startDate=2026-05-01")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
