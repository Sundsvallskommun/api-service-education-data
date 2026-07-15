package se.sundsvall.educationdata.scheduler;

import org.springframework.stereotype.Component;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.educationdata.service.EducationEventsService;
import se.sundsvall.educationdata.service.EducationInfosService;
import se.sundsvall.educationdata.service.EducationProvidersService;
import se.sundsvall.educationdata.service.PlannedEducationService;

@Component
public class Scheduler {

	private final EducationEventsService eventsService;
	private final EducationInfosService infosService;
	private final EducationProvidersService providersService;
	private final PlannedEducationService educationService;

	public Scheduler(EducationEventsService eventsService, EducationInfosService infosService, EducationProvidersService providersService, PlannedEducationService educationService) {
		this.eventsService = eventsService;
		this.infosService = infosService;
		this.providersService = providersService;
		this.educationService = educationService;
	}

	@Dept44Scheduled(
		cron = "${scheduler.import.cron}",
		name = "${scheduler.import.name}",
		lockAtMostFor = "${scheduler.import.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.import.maximum-execution-time}")
	public void importData() {
		final int jsonSize = 100;

		// for use under development
		// swap to saveAllPagesEventsJsonTable(jsonSize) after development
		eventsService.savePageEventJsonTable(0, jsonSize);

		// for use under development
		// swap to saveAllPagesInfoJsonTable(jsonSize) after development
		infosService.savePageInfoJsonTable(0, jsonSize);

		// for use under development
		// swap to saveAllPagesProviderJsonTable(jsonSize) after development
		providersService.savePageProviderJsonTable(0, jsonSize);

		educationService.getCategoryInfo();
	}
}
