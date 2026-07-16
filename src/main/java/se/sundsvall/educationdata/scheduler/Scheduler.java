package se.sundsvall.educationdata.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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

	private final int jsonSize;

	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	public Scheduler(EducationEventsService eventsService, EducationInfosService infosService, EducationProvidersService providersService, PlannedEducationService educationService, @Value("${scheduler.import.json-size}") int jsonSize) {
		this.eventsService = eventsService;
		this.infosService = infosService;
		this.providersService = providersService;
		this.educationService = educationService;
		this.jsonSize = jsonSize;
	}

	@Async
	public void triggerAsyncImport() {
		try {
			importData();
		} catch (Exception e) {
			log.error("Manually triggered import failed", e);
		}
	}

	@Dept44Scheduled(
		cron = "${scheduler.import.cron}",
		name = "${scheduler.import.name}",
		lockAtMostFor = "${scheduler.import.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.import.maximum-execution-time}")
	public void importData() {

		eventsService.saveAllPagesEventsJsonTable(jsonSize);

		infosService.saveAllPagesInfoJsonTable(jsonSize);

		providersService.saveAllPagesProviderJsonTable(jsonSize);

		educationService.getCategoryInfo();
	}
}
