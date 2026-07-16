package se.sundsvall.educationdata.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.educationdata.Application;
import se.sundsvall.educationdata.scheduler.Scheduler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
public class SchedulerResourceTest {

	@MockitoBean
	private Scheduler scheduler;

	@Autowired
	private WebTestClient webTestClient;

	private static final String PATH = "/{municipalityId}/scheduler/trigger";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String INVALID_MUNICIPALITY_ID = "9999";

	@Test
	void triggerScheduler_Accepted() {
		webTestClient.post()
			.uri(PATH, MUNICIPALITY_ID)
			.exchange()
			.expectStatus().isAccepted();

		verify(scheduler).triggerAsyncImport();
		verifyNoMoreInteractions(scheduler);
	}

	@Test
	void triggerScheduler_BadRequest() {
		webTestClient.post()
			.uri(PATH, INVALID_MUNICIPALITY_ID)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST);

		verifyNoInteractions(scheduler);
	}

}
