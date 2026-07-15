package se.sundsvall.educationdata.api;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import se.sundsvall.educationdata.scheduler.Scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class SchedulerResourceTest {

	@Mock
	private Scheduler scheduler;

	@InjectMocks
	private SchedulerResource schedulerResource;

	@Test
	void triggerScheduler_returnsOk() throws IOException {
		final var response = schedulerResource.triggerScheduler();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(scheduler).importData();
		verifyNoMoreInteractions(scheduler);
	}
}
