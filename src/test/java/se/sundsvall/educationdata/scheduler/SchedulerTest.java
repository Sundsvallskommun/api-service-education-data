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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class SchedulerTest {

	@Mock
	private PlannedEducationService plannedEducationService;
	@Mock
	private EducationEventsService educationEventsService;
	@Mock
	private EducationInfosService educationInfosService;
	@Mock
	private EducationProvidersService educationProvidersService;

	private Scheduler scheduler;

	@BeforeEach
	void setUp() {
		scheduler = new Scheduler(educationEventsService, educationInfosService, educationProvidersService, plannedEducationService);
	}

	@Test
	void importSusaJsonTest() {
		scheduler.importSusaJson();

		verify(educationEventsService).saveAllPagesEventsJsonTable();
		verify(educationInfosService).saveAllPagesInfoJsonTable();
		verify(educationProvidersService).saveAllPagesProviderJsonTable();
		verifyNoMoreInteractions(educationEventsService, educationInfosService, educationProvidersService);
	}

	@Test
	void importPlannedEducationCategoriesTest() {
		scheduler.importPlannedEducationCategories();
		verify(plannedEducationService).importReferenceCategories();
		verifyNoInteractions(educationEventsService, educationInfosService, educationProvidersService);
	}

	@Test
	void createEntitiesFromJsonTest() {
		scheduler.createEntitiesFromJson();
		verify(educationEventsService).createEventEntitiesFromJson();
		verify(educationInfosService).createInfoEntitiesFromJson();
		verifyNoMoreInteractions(educationEventsService, educationInfosService);
		verifyNoInteractions(educationProvidersService, plannedEducationService);
	}
}
