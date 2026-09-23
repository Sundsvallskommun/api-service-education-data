package se.sundsvall.educationdata.integration.db.specification;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.EducationInfoEntityRepository;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.educationdata.integration.db.specification.EducationSpecification.withCreated;
import static se.sundsvall.educationdata.integration.db.specification.EducationSpecification.withEducationType;
import static se.sundsvall.educationdata.integration.db.specification.EducationSpecification.withEligibility;
import static se.sundsvall.educationdata.integration.db.specification.EducationSpecification.withExpires;
import static se.sundsvall.educationdata.integration.db.specification.EducationSpecification.withMunicipalityIds;
import static se.sundsvall.educationdata.integration.db.specification.EducationSpecification.withSchoolType;
import static se.sundsvall.educationdata.integration.db.specification.EducationSpecification.withStartDate;
import static se.sundsvall.educationdata.integration.db.specification.EducationSpecification.withTitle;

@DataJpaTest
@Sql(scripts = "/db/scripts/testdata.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("junit")
class EducationSpecificationTest {

	private static final LocalDate YESTERDAY = LocalDate.of(2026, Month.JUNE, 6);
	private static final LocalDate TODAY = LocalDate.of(2026, Month.JUNE, 7);

	@Autowired
	private EducationEventEntityRepository educationEventEntityRepository;

	@Autowired
	private EducationInfoEntityRepository educationInfoEntityRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void nullFiltersIncludeAllEvents() {
		final var specification = Specification.allOf(
			withCreated(null),
			withTitle(null),
			withSchoolType(null),
			withEligibility(null),
			withStartDate(null),
			withExpires(null));

		final var result = educationEventEntityRepository.findAll(specification)
			.stream().map(EducationEventEntity::getEducationEventId)
			.toList();
		assertThat(result).containsExactlyInAnyOrder("e.1", "e.2", "e.3", "e.4", "e.5", "e.6", "e.7", "e.8", "e.9", "e.10");

	}

	@Test
	void titleMatchesPartiallyIgnoringCase() {
		final var result = educationEventEntityRepository.findAll(withCreated(TODAY).and(withTitle("ENERGI"))).stream()
			.map(EducationEventEntity::getEducationEventId)
			.toList();
		assertThat(result).containsExactlyInAnyOrder("e.2");
	}

	@Test
	void eligibilityMatchesPartiallyIgnoringCaseThroughJoin() {

		final var result = educationEventEntityRepository.findAll(withCreated(TODAY).and(withEligibility("GRUNDLÄGGANDE")))
			.stream().map(EducationEventEntity::getEducationEventId)
			.toList();
		assertThat(result).containsExactlyInAnyOrder("e.1", "e.5", "e.6", "e.8");

	}

	@Test
	void municipalityIdsMatchesGivenIds() {

		final var result = educationEventEntityRepository.findAll(withCreated(TODAY).and(withMunicipalityIds(List.of("2281"))))
			.stream().map(EducationEventEntity::getEducationEventId)
			.toList();
		assertThat(result).containsExactly("e.1", "e.10", "e.2", "e.3", "e.5", "e.7");
	}

	@Test
	void emptyMunicipalityIdsDoesNotFilter() {

		final var result = educationEventEntityRepository.findAll(withCreated(TODAY).and(withMunicipalityIds(List.of())))
			.stream().map(EducationEventEntity::getEducationEventId)
			.toList();
		assertThat(result).hasSize(8);
	}

	@Test
	void joinIncludesEarlierEqualAndNullDates() {
		entityManager.find(EducationEventEntity.class, "event-1")
			.setStartDate(TODAY);
		entityManager.find(EducationEventEntity.class, "event-2")
			.setStartDate(TODAY.plusDays(1));
		entityManager.find(EducationEventEntity.class, "event-4")
			.setStartDate(YESTERDAY);
		entityManager.flush();
		entityManager.clear();

		final var result = educationEventEntityRepository.findAll(withStartDate(TODAY))
			.stream().map(EducationEventEntity::getEducationEventId)
			.toList();

		assertThat(result).containsExactlyInAnyOrder("e.1", "e.2", "e.10");
	}

	@Test
	void expiresIncludesEarlierAndEqualDates() {
		final var cutoff = TODAY.atStartOfDay();

		entityManager.find(EducationInfoEntity.class, "info-1")
			.setExpires(cutoff.minusDays(1));
		entityManager.find(EducationInfoEntity.class, "info-2")
			.setExpires(cutoff);
		entityManager.find(EducationInfoEntity.class, "info-3")
			.setExpires(cutoff.plusDays(1));
		entityManager.flush();
		entityManager.clear();

		final var result = educationEventEntityRepository.findAll(withExpires(cutoff.toLocalDate()))
			.stream().map(EducationEventEntity::getEducationEventId)
			.toList();

		assertThat(result).containsExactlyInAnyOrder("e.1", "e.2", "e.5", "e.6", "e.8");
	}

	@Test
	void expiresIncludesEntireGivenDay() {
		entityManager.find(EducationInfoEntity.class, "info-1")
			.setExpires(TODAY.atTime(0, 0, 0));
		entityManager.find(EducationInfoEntity.class, "info-2")
			.setExpires(TODAY.atTime(23, 0, 0));
		entityManager.flush();
		entityManager.clear();

		final var result = educationEventEntityRepository.findAll(withExpires(TODAY))
			.stream().map(EducationEventEntity::getEducationEventId)
			.toList();

		assertThat(result).containsExactlyInAnyOrder("e.1", "e.2", "e.5", "e.6", "e.8");
	}

	@Test
	void combinesFiltersOnTheSameJoin() {
		var specification = withCreated(TODAY)
			.and(withSchoolType("VUXGY"))
			.and(withEducationType("kurs"));

		final var result = educationEventEntityRepository.findAll(specification)
			.stream().map(EducationEventEntity::getEducationEventId)
			.toList();
		assertThat(result).containsExactlyInAnyOrder("e.1", "e.5", "e.6", "e.8");
	}
}
