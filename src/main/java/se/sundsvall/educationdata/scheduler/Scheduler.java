package se.sundsvall.educationdata.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.educationdata.service.EducationEventsService;
import se.sundsvall.educationdata.service.EducationInfosService;
import se.sundsvall.educationdata.service.EducationProvidersService;
import se.sundsvall.educationdata.service.PlannedEducationService;

@Component
public class Scheduler {

	private final EducationEventsService educationEventsService;
	private final EducationInfosService educationInfosService;
	private final EducationProvidersService educationProvidersService;
	private final PlannedEducationService plannedEducationService;

	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	public Scheduler(EducationEventsService educationEventsService, EducationInfosService educationInfosService, EducationProvidersService educationProvidersService, PlannedEducationService plannedEducationService) {
		this.educationEventsService = educationEventsService;
		this.educationInfosService = educationInfosService;
		this.educationProvidersService = educationProvidersService;
		this.plannedEducationService = plannedEducationService;
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
			createEntitiesFromJson();
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

		educationEventsService.saveAllPagesEventsJsonTable();

		educationInfosService.saveAllPagesInfoJsonTable();

		educationProvidersService.saveAllPagesProviderJsonTable();
	}

	@Dept44Scheduled(
		cron = "${scheduler.import-categories.cron}",
		name = "${scheduler.import-categories.name}",
		lockAtMostFor = "${scheduler.import-categories.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.import-categories.maximum-execution-time}")
	public void importPlannedEducationCategories() {

		plannedEducationService.importReferenceCategories();
	}

	/**
	 * Creates and stores event and info entities found in SUSA JSON
	 * snapshots imported that day. Only events matching the configured
	 * municipality whitelist and information related to stored events
	 * are included.
	 * <p>
	 * The SUSA snapshot import should complete before this method runs.
	 */
	@Dept44Scheduled(
		cron = "${scheduler.create-entities.cron}",
		name = "${scheduler.create-entities.name}",
		lockAtMostFor = "${scheduler.create-entities.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.create-entities.maximum-execution-time}")
	public void createEntitiesFromJson() {

		educationEventsService.createEventEntitiesFromJson();

		educationInfosService.createInfoEntitiesFromJson();
	}
}
