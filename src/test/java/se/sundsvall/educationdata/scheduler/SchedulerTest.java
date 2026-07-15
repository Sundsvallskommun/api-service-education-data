package se.sundsvall.educationdata.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.service.EducationEventsService;
import se.sundsvall.educationdata.service.EducationInfosService;
import se.sundsvall.educationdata.service.EducationProvidersService;
import se.sundsvall.educationdata.service.PlannedEducationService;

import static org.mockito.Mockito.verify;
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
	@InjectMocks
	private Scheduler scheduler;

	private static final int PAGE = 0;
	private static final int SIZE = 100;

	@Test
	void importData() {
		scheduler.importData();

		verify(plannedEducationService).getCategoryInfo();
		verify(eventsService).savePageEventJsonTable(PAGE, SIZE);
		verify(infosService).savePageInfoJsonTable(PAGE, SIZE);
		verify(providersService).savePageProviderJsonTable(PAGE, SIZE);
		verifyNoMoreInteractions(plannedEducationService, eventsService, infosService, providersService);

	}

}
