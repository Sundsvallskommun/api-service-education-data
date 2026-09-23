package se.sundsvall.educationdata.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.educationdata.api.model.StatisticsParameters;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.EducationInfoEntityRepository;
import se.sundsvall.educationdata.integration.db.GyProgramCategoryRepository;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.GyProgramCategoryEntity;
import se.sundsvall.educationdata.integration.db.model.projection.CategoryProjection;
import se.sundsvall.educationdata.integration.db.model.projection.DirectionProjection;
import se.sundsvall.educationdata.integration.db.model.projection.LanguageOfInstructionsProjection;
import se.sundsvall.educationdata.integration.db.model.projection.SchoolTypeProjection;
import se.sundsvall.educationdata.integration.db.model.projection.StudyLocationProjection;
import se.sundsvall.educationdata.integration.db.model.projection.StudyPaceProjection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {
	@Mock
	private EducationEventEntityRepository educationEventEntityRepository;

	@Mock
	private EducationInfoEntityRepository educationInfoEntityRepository;
	@Mock
	private ReferenceCategoryRepository referenceCategoryRepository;
	@Mock
	private GyProgramCategoryRepository gyProgramCategoryRepository;

	@InjectMocks
	StatisticsService statisticsService;

	private static final LocalDate FROM = LocalDate.of(2026, Month.MAY, 1);
	private static final LocalDate TO = LocalDate.of(2026, Month.NOVEMBER, 1);
	private static final LocalDate LATEST_IMPORT_DATE = LocalDate.of(2026, Month.JUNE, 7);

	@Test
	void calculateStatisticsClassifiesEducationsByPeriod() {
		final var parameters = StatisticsParameters.builder().withStartDate(FROM).withEndDate(TO).build();

		final var spanning = EducationEventEntity.builder().withEducationEventId("e.1")
			.withStartDate(LocalDate.of(2026, Month.APRIL, 1)).withEndDate(LocalDate.of(2026, Month.DECEMBER, 1)).build();
		final var within = EducationEventEntity.builder().withEducationEventId("e.2")
			.withStartDate(LocalDate.of(2026, Month.JUNE, 1)).withEndDate(LocalDate.of(2026, Month.SEPTEMBER, 1)).build();
		final var noEndDate = EducationEventEntity.builder().withEducationEventId("e.3")
			.withStartDate(LocalDate.of(2026, Month.JUNE, 1)).build();
		final var noStartDate = EducationEventEntity.builder().withEducationEventId("e.4")
			.withEndDate(LocalDate.of(2026, Month.OCTOBER, 1)).build();
		final var noDates = EducationEventEntity.builder().withEducationEventId("e.5").build();

		final var result = statisticsService.calculateStatistics(
			parameters, List.of(spanning, within, noEndDate, noStartDate, noDates));

		assertThat(result.getOnGoingEducations()).isEqualTo(1);
		assertThat(result.getPlannedEducations()).isEqualTo(1);
		assertThat(result.getFinishedEducations()).isEqualTo(1);
		assertThat(result.getMissingEndDateEducations()).isEqualTo(1);
		assertThat(result.getMissingStartDateEducations()).isEqualTo(1);
		assertThat(result.getMissingStartAndEndDateEducations()).isEqualTo(1);
		assertThat(result.getTotalEducations()).isEqualTo(5);
	}

	@Test
	void calculateStatisticsSumsSeatsAndCountsSchoolTypes() {
		final var parameters = StatisticsParameters.builder()
			.withStartDate(FROM).withEndDate(TO).withStudyLocations(List.of("Sundsvall")).build();

		final var beforePeriod = EducationEventEntity.builder().withEducationEventId("e.1")
			.withStartDate(LocalDate.of(2026, Month.APRIL, 1)).withEndDate(LocalDate.of(2026, Month.DECEMBER, 1))
			.withSeats(20).withEducationInfo(EducationInfoEntity.builder().withSchoolType("VUXGY").build()).build();
		final var withinPeriod = EducationEventEntity.builder().withEducationEventId("e.2")
			.withStartDate(LocalDate.of(2026, Month.JUNE, 1)).withEndDate(LocalDate.of(2026, Month.SEPTEMBER, 1))
			.withSeats(10).withEducationInfo(EducationInfoEntity.builder().withSchoolType("VUXGY").build()).build();
		final var withoutInfo = EducationEventEntity.builder().withEducationEventId("e.3")
			.withStartDate(LocalDate.of(2026, Month.JUNE, 1)).withSeats(5)
			.withEducationInfo(EducationInfoEntity.builder().withSchoolType("HS").build()).build();

		final var result = statisticsService.calculateStatistics(
			parameters, List.of(beforePeriod, withinPeriod, withoutInfo));

		assertThat(result.getTotalCapacity()).isEqualTo(35);
		assertThat(result.getAvailableSeats()).isEqualTo(15);
		assertThat(result.getSchoolType()).containsExactlyInAnyOrderEntriesOf(Map.of("VUXGY", 2L, "HS", 1L));
		assertThat(result.getStudyLocations()).containsExactly("Sundsvall");
		assertThat(result.getStartDate()).isEqualTo(FROM);
		assertThat(result.getEndDate()).isEqualTo(TO);
	}

	@Test
	void getStatisticsByParametersLatestImportWhenNullDate() {
		final var parameters = StatisticsParameters.builder().withStartDate(FROM).withEndDate(TO).build();
		final var event = EducationEventEntity.builder().withEducationEventId("e.1").build();

		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findAll(any(Specification.class))).thenReturn(List.of(event));

		final var result = statisticsService.getStatisticsByParameters(parameters, null);

		assertThat(result.getTotalEducations()).isEqualTo(1);
		verify(educationEventEntityRepository).findLatestImportDate();
	}

	@Test
	void getStatisticsByParametersUsesProvidedDate() {
		final var parameters = StatisticsParameters.builder().withStartDate(FROM).withEndDate(TO).build();

		when(educationEventEntityRepository.findAll(any(Specification.class))).thenReturn(List.of());

		statisticsService.getStatisticsByParameters(parameters, LATEST_IMPORT_DATE);

		verify(educationEventEntityRepository, never()).findLatestImportDate();
	}

	@Test
	void findStatisticsFilterValuesForStudyLocations() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findDistinctByCreatedAt(
			eq(StudyLocationProjection.class), eq(LATEST_IMPORT_DATE), any(Sort.class)))
			.thenReturn(List.of(() -> "Sundsvall"));

		assertThat(statisticsService.findStatisticsFilterValues("studyLocations", null))
			.containsExactly("Sundsvall");
	}

	@Test
	void findStatisticsFilterValuesForStudyPace() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findDistinctByCreatedAt(
			eq(StudyPaceProjection.class), eq(LATEST_IMPORT_DATE), any(Sort.class)))
			.thenReturn(List.of(() -> "100.0"));

		assertThat(statisticsService.findStatisticsFilterValues("studyPace", null))
			.containsExactly("100.0");
	}

	@Test
	void findStatisticsFilterValuesForLanguageOfInstructions() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findDistinctByCreatedAt(
			eq(LanguageOfInstructionsProjection.class), eq(LATEST_IMPORT_DATE), any(Sort.class)))
			.thenReturn(List.of(() -> "swe"));

		assertThat(statisticsService.findStatisticsFilterValues("languageOfInstructions", null))
			.containsExactly("swe");
	}

	@Test
	void findStatisticsFilterValuesForDirections() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(referenceCategoryRepository.findDistinctBy(eq(DirectionProjection.class), any(Sort.class)))
			.thenReturn(List.of(() -> "Redovisning"));

		assertThat(statisticsService.findStatisticsFilterValues("directions", null))
			.containsExactly("Redovisning");
	}

	@Test
	void findStatisticsFilterValuesForCategoriesMergesSortsAndDeduplicates() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(referenceCategoryRepository.findDistinctBy(eq(CategoryProjection.class), any(Sort.class)))
			.thenReturn(List.of(() -> "Teknik", () -> "Ekonomi"));
		when(gyProgramCategoryRepository.findAll()).thenReturn(List.of(
			GyProgramCategoryEntity.builder().withCategory("Naturbruk").build(),
			GyProgramCategoryEntity.builder().withCategory("Ekonomi").build()));

		assertThat(statisticsService.findStatisticsFilterValues("categories", null))
			.containsExactly("Ekonomi", "Naturbruk", "Teknik");
	}

	@Test
	void findStatisticsFilterValuesForSchoolType() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationInfoEntityRepository.findDistinctByCreatedAt(
			eq(SchoolTypeProjection.class), eq(LATEST_IMPORT_DATE), any(Sort.class)))
			.thenReturn(List.of(() -> "VUXGY"));

		assertThat(statisticsService.findStatisticsFilterValues("schoolType", null))
			.containsExactly("VUXGY");
	}

	@Test
	void findStatisticsFilterValuesNonExistentValue() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		assertThat(statisticsService.findStatisticsFilterValues("nonsense", null)).isEmpty();
	}

	@Test
	void calculateStatisticsExcludesEducationsOutsideThePeriod() {
		final var parameters = StatisticsParameters.builder().withStartDate(FROM).withEndDate(TO).build();

		final var afterPeriod = EducationEventEntity.builder().withEducationEventId("e.1")
			.withStartDate(LocalDate.of(2026, Month.DECEMBER, 1))
			.withEndDate(LocalDate.of(2027, Month.JANUARY, 1))
			.withSeats(24).build();

		final var beforePeriod = EducationEventEntity.builder().withEducationEventId("e.2")
			.withStartDate(LocalDate.of(2026, Month.JANUARY, 1))
			.withEndDate(LocalDate.of(2026, Month.FEBRUARY, 1))
			.withSeats(22).build();

		final var result = statisticsService.calculateStatistics(parameters, List.of(afterPeriod, beforePeriod));

		assertThat(result.getOnGoingEducations()).isZero();
		assertThat(result.getPlannedEducations()).isZero();
		assertThat(result.getFinishedEducations()).isZero();
		assertThat(result.getAvailableSeats()).isZero();
		assertThat(result.getTotalCapacity()).isEqualTo(46);
		assertThat(result.getTotalEducations()).isEqualTo(2);
	}
}
