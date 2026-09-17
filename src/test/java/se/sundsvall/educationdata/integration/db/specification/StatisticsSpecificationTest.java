package se.sundsvall.educationdata.integration.db.specification;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.educationdata.api.model.StatisticsParameters;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.educationdata.integration.db.specification.StatisticsSpecification.createSpecification;
import static se.sundsvall.educationdata.integration.db.specification.StatisticsSpecification.withCategories;
import static se.sundsvall.educationdata.integration.db.specification.StatisticsSpecification.withCreated;
import static se.sundsvall.educationdata.integration.db.specification.StatisticsSpecification.withMunicipality;
import static se.sundsvall.educationdata.integration.db.specification.StatisticsSpecification.withPeriod;

@DataJpaTest
@Sql(scripts = "/db/scripts/testdata.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("junit")
class StatisticsSpecificationTest {

	private static final LocalDate YESTERDAY = LocalDate.of(2026, Month.JUNE, 6);
	private static final LocalDate TODAY = LocalDate.of(2026, Month.JUNE, 7);

	@Autowired
	private EducationEventEntityRepository educationEventEntityRepository;

	private List<String> eventIds(final Specification<EducationEventEntity> specification) {
		return educationEventEntityRepository.findAll(specification).stream()
			.map(EducationEventEntity::getEducationEventId)
			.toList();
	}

	@Test
	void createdSelectsASingleSnapshot() {
		assertThat(eventIds(withCreated(YESTERDAY)))
			.containsExactlyInAnyOrder("e.4", "e.9");
	}

	@Test
	void municipalityExcludesOtherMunicipalities() {
		assertThat(eventIds(withCreated(TODAY).and(withMunicipality("2281"))))
			.containsExactlyInAnyOrder("e.1", "e.2", "e.3", "e.5", "e.6", "e.7");
	}

	@Test
	void periodExcludesEventsThatEndedBeforeIt() {
		final var specification = withCreated(TODAY)
			.and(withMunicipality("2281"))
			.and(withPeriod(LocalDate.of(2026, Month.NOVEMBER, 15), LocalDate.of(2026, Month.DECEMBER, 31)));

		assertThat(eventIds(specification))
			.containsExactlyInAnyOrder("e.1", "e.2", "e.5", "e.6", "e.7");
	}

	@Test
	void periodKeepsEventsWithMissingDates() {
		final var specification = withCreated(TODAY)
			.and(withPeriod(LocalDate.of(2026, Month.JANUARY, 1), LocalDate.of(2026, Month.MARCH, 1)));

		assertThat(eventIds(specification))
			.containsExactlyInAnyOrder("e.5", "e.6", "e.7");
	}

	@Test
	void nullCategoriesAppliesNoFilter() {
		assertThat(eventIds(withCategories(null)))
			.containsExactlyInAnyOrder("e.1", "e.2", "e.3", "e.4", "e.5", "e.6", "e.7", "e.8", "e.9");
	}

	@Test
	void createSpecificationCombinesSnapshotMunicipalityAndPeriod() {
		final var parameters = StatisticsParameters.builder()
			.withStartDate(LocalDate.of(2026, Month.MAY, 1))
			.withEndDate(LocalDate.of(2026, Month.NOVEMBER, 1))
			.build();

		assertThat(eventIds(createSpecification("2281", parameters, TODAY)))
			.containsExactlyInAnyOrder("e.1", "e.2", "e.3", "e.5", "e.6", "e.7");
	}
}
