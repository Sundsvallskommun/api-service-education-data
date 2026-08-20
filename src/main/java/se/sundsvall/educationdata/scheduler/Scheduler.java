package se.sundsvall.educationdata.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	/**
	 * Sets the relations between an education and the category of that education.
	 * <p>
	 * Needs both the Educations and Categories to have been imported before running.
	 */
	@Dept44Scheduled(
		cron = "${scheduler.refresh-category-relations.cron}",
		name = "${scheduler.refresh-category-relations.name}",
		lockAtMostFor = "${scheduler.refresh-category-relations.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.refresh-category-relations.maximum-execution-time}")
	public void refreshCategoryRelations() {

		plannedEducationService.refreshEventCategoryRelations();
	}
}
