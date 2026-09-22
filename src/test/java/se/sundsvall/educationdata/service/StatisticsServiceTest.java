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
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.educationdata.api.model.StatisticsParameters;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {
	@Mock
	private EducationEventEntityRepository educationEventEntityRepository;

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
}
