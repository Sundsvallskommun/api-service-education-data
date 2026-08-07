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

	public Scheduler(EducationEventsService eventsService, EducationInfosService infosService, EducationProvidersService providersService, PlannedEducationService educationService, @Value("${scheduler.import-susa-json.json-size}") int jsonSize) {
		this.eventsService = eventsService;
		this.infosService = infosService;
		this.providersService = providersService;
		this.educationService = educationService;
		this.jsonSize = jsonSize;
	}

	@Async
	public void triggerAsyncImport() {
		try {
			importSusaJson();
		} catch (Exception e) {
			log.error("Manually triggered Susa import job failed", e);
		}
		try {
			importPlannedEducationCategories();
		} catch (Exception e) {
			log.error("Manually triggered planned education job failed", e);
		}
		try {
			generateEntitiesFromJson();
		} catch (Exception e) {
			log.error("Manually triggered generation job failed", e);
		}
	}

	@Dept44Scheduled(
		cron = "${scheduler.import-susa-json.cron}",
		name = "${scheduler.import-susa-json.name}",
		lockAtMostFor = "${scheduler.import-susa-json.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.import-susa-json.maximum-execution-time}")
	public void importSusaJson() {

		eventsService.saveAllPagesEventsJsonTable(jsonSize);

		infosService.saveAllPagesInfoJsonTable(jsonSize);

		providersService.saveAllPagesProviderJsonTable(jsonSize);
	}

	@Dept44Scheduled(
		cron = "${scheduler.import-categories.cron}",
		name = "${scheduler.import-categories.name}",
		lockAtMostFor = "${scheduler.import-categories.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.import-categories.maximum-execution-time}")
	public void importPlannedEducationCategories() {

		educationService.getCategoryInfo();
	}

	@Dept44Scheduled(
		cron = "${scheduler.generate.cron}",
		name = "${scheduler.generate.name}",
		lockAtMostFor = "${scheduler.generate.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.generate.maximum-execution-time}")
	public void generateEntitiesFromJson() {

		eventsService.saveAllJsonDataEventsToEntities();

		infosService.saveAllJsonDataInfosToEntities();
	}
}
