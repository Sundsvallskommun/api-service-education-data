package se.sundsvall.educationdata.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.service.EducationEventsService;
import se.sundsvall.educationdata.service.EducationInfosService;
import se.sundsvall.educationdata.service.EducationProvidersService;
import se.sundsvall.educationdata.service.PlannedEducationService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class SchedulerTest {

	@Mock
	private PlannedEducationService plannedEducationService;
	@Mock
	private EducationEventsService eventsService;
	@Mock
	private EducationInfosService infosService;
	@Mock
	private EducationProvidersService providersService;

	private Scheduler scheduler;

	private static final int SIZE = 100;

	@BeforeEach
	void setUp() {
		scheduler = new Scheduler(eventsService, infosService, providersService, plannedEducationService, SIZE);
	}

	@Test
	void importDataTest() {
		scheduler.importData();

		verify(plannedEducationService).getCategoryInfo();
		verify(eventsService).saveAllPagesEventsJsonTable(SIZE);
		verify(infosService).saveAllPagesInfoJsonTable(SIZE);
		verify(providersService).saveAllPagesProviderJsonTable(SIZE);
		verifyNoMoreInteractions(plannedEducationService, eventsService, infosService, providersService);
	}

	@Test
	void triggerAsyncSchedulerTest() {
		scheduler.triggerAsyncImport();

		verify(eventsService).saveAllPagesEventsJsonTable(SIZE);
		verify(infosService).saveAllPagesInfoJsonTable(SIZE);
		verify(providersService).saveAllPagesProviderJsonTable(SIZE);
		verify(plannedEducationService).getCategoryInfo();
		verifyNoMoreInteractions(eventsService, infosService, providersService, plannedEducationService);
	}

	@Test
	void triggerAsyncSchedulerTest_catchException() {
		doThrow(new RuntimeException("boom")).when(eventsService).saveAllPagesEventsJsonTable(SIZE);

		assertDoesNotThrow(() -> scheduler.triggerAsyncImport());

		verify(eventsService).saveAllPagesEventsJsonTable(SIZE);
		verifyNoInteractions(infosService, providersService, plannedEducationService);
		verifyNoMoreInteractions(eventsService);
	}

}
