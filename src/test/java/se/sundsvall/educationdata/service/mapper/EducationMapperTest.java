package se.sundsvall.educationdata.service.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EducationMapperTest {

	private final EducationMapper educationMapper = new EducationMapper();

	@Test
	void toEducationMapsEventAndInfo() {
		final var educationEvent = EducationEventEntity.builder()
			.withEducationEventId("e.2281.123")
			.withEducationInfoId("i.123")
			.withTitle("Matematik nivå 1a")
			.withCity("Sundsvall")
			.withMunicipalityId("2281")
			.withCoursePostUrl("https://sundsvall.alvis.se/hittakurser")
			.withSeats(25)
			.withCost(BigDecimal.ZERO)
			.withCurrencyType("SEK")
			.withLectureType("Distance")
			.withStudyPace("100.0")
			.withLanguageOfInstructions("swe")
			.withStartDate(LocalDate.of(2026, Month.OCTOBER, 12))
			.withEndDate(LocalDate.of(2026, Month.DECEMBER, 18))
			.withApplicationDateStart(LocalDate.of(2026, Month.MARCH, 15))
			.withApplicationDateEnd(LocalDate.of(2026, Month.APRIL, 15))
			.withCancelled(false)
			.build();
		final var educationInfo = EducationInfoEntity.builder()
			.withEducationInfoId("i.123")
			.withTitle("Matematik")
			.withCode("MATE1A00X")
			.withSchoolType("VUXGY")
			.withEducationType("kurs")
			.withDescription("Matematik nivå 1a")
			.withCredits(100.0)
			.withCreditType("vp")
			.withEducationEligibility("Grundläggande behörighet")
			.withRecommendedPriorKnowledge("Matematik 2")
			.build();

		final var result = educationMapper.toEducation(educationEvent, educationInfo);

		assertThat(result.getId()).isEqualTo("e.2281.123");
		assertThat(result.getName()).isEqualTo("Matematik nivå 1a");
		assertThat(result.getStudyLocation()).isEqualTo("Sundsvall");
		assertThat(result.getMunicipalityId()).isEqualTo("2281");
		assertThat(result.getLectureType()).isEqualTo("Distance");
		assertThat(result.getStudyPace()).isEqualTo("100.0");
		assertThat(result.getLanguageOfInstructions()).isEqualTo("swe");
		assertThat(result.getStart()).isEqualTo(LocalDate.of(2026, Month.OCTOBER, 12));
		assertThat(result.getEnd()).isEqualTo(LocalDate.of(2026, Month.DECEMBER, 18));
		assertThat(result.getCancelled()).isFalse();

		assertThat(result.getCode()).isEqualTo("MATE1A00X");
		assertThat(result.getSchoolType()).isEqualTo("VUXGY");
		assertThat(result.getEducationType()).isEqualTo("kurs");
		assertThat(result.getCredits()).isEqualTo(100.0);
		assertThat(result.getCreditType()).isEqualTo("vp");
		assertThat(result.getEligibility()).isEqualTo("Grundläggande behörighet");
		assertThat(result.getRecommendedPriorKnowledge()).isEqualTo("Matematik 2");
	}

	@Test
	void toEducationWithoutInfoLeavesInfoFieldsNull() {
		final var educationEvent = EducationEventEntity.builder()
			.withEducationEventId("e.2281.123")
			.withEducationInfoId("i.123")
			.withTitle("Matematik nivå 1a")
			.withCity("Sundsvall")
			.withMunicipalityId("2281")
			.withCoursePostUrl("https://sundsvall.alvis.se/hittakurser")
			.withSeats(25)
			.withCost(BigDecimal.valueOf(10))
			.withCurrencyType("SEK")
			.withLectureType("Distance")
			.withStudyPace("100.0")
			.withLanguageOfInstructions("swe")
			.withStartDate(LocalDate.of(2026, Month.OCTOBER, 12))
			.withEndDate(LocalDate.of(2026, Month.DECEMBER, 18))
			.withApplicationDateStart(LocalDate.of(2026, Month.MARCH, 15))
			.withApplicationDateEnd(LocalDate.of(2026, Month.APRIL, 15))
			.withCancelled(false)
			.build();

		final var result = educationMapper.toEducation(educationEvent, null);

		assertThat(result.getId()).isEqualTo("e.2281.123");
		assertThat(result.getLectureType()).isEqualTo("Distance");

		assertThat(result.getCode()).isNull();
		assertThat(result.getSchoolType()).isNull();
		assertThat(result.getEducationType()).isNull();
		assertThat(result.getCredits()).isNull();
		assertThat(result.getDegree()).isNull();
	}

	@Test
	void toPagedEducationResponseMapsContentAndMetadata() {
		final var educationEvent = EducationEventEntity.builder()
			.withEducationEventId("e.2281.123")
			.withEducationInfoId("i.123")
			.withTitle("Matematik nivå 1a")
			.withCity("Sundsvall")
			.withMunicipalityId("2281")
			.withCoursePostUrl("https://sundsvall.alvis.se/hittakurser")
			.withSeats(25)
			.withCost(BigDecimal.valueOf(10))
			.withCurrencyType("SEK")
			.withLectureType("Distance")
			.withStudyPace("100.0")
			.withLanguageOfInstructions("swe")
			.withStartDate(LocalDate.of(2026, Month.OCTOBER, 12))
			.withEndDate(LocalDate.of(2026, Month.DECEMBER, 18))
			.withApplicationDateStart(LocalDate.of(2026, Month.MARCH, 15))
			.withApplicationDateEnd(LocalDate.of(2026, Month.APRIL, 15))
			.withCancelled(false)
			.build();
		final var educationInfo = EducationInfoEntity.builder()
			.withEducationInfoId("i.123")
			.withTitle("Matematik")
			.withCode("MATE1A00X")
			.withSchoolType("VUXGY")
			.withEducationType("kurs")
			.withDescription("Matematik nivå 1a")
			.withCredits(100.0)
			.withCreditType("vp")
			.withEducationEligibility("Grundläggande behörighet")
			.withRecommendedPriorKnowledge("Matematik 2")
			.build();

		final var page = new PageImpl<>(List.of(educationEvent), PageRequest.of(0, 20), 1);

		final var result = educationMapper.toPagedEducationResponse(page, Map.of("i.123", educationInfo));

		assertThat(result.getEducations()).hasSize(1);
		assertThat(result.getEducations().getFirst().getCode()).isEqualTo("MATE1A00X");
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getLimit()).isEqualTo(20);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
	}

	@Test
	void toPagedEducationResponseWhenInfoIsMissing() {
		final var educationEvent = EducationEventEntity.builder()
			.withEducationEventId("e.2281.123")
			.withEducationInfoId("i.123")
			.withTitle("Matematik nivå 1a")
			.withCity("Sundsvall")
			.withMunicipalityId("2281")
			.withCoursePostUrl("https://sundsvall.alvis.se/hittakurser")
			.withSeats(25)
			.withCost(BigDecimal.valueOf(10))
			.withCurrencyType("SEK")
			.withLectureType("Distance")
			.withStudyPace("100.0")
			.withLanguageOfInstructions("swe")
			.withStartDate(LocalDate.of(2026, Month.OCTOBER, 12))
			.withEndDate(LocalDate.of(2026, Month.DECEMBER, 18))
			.withApplicationDateStart(LocalDate.of(2026, Month.MARCH, 15))
			.withApplicationDateEnd(LocalDate.of(2026, Month.APRIL, 15))
			.withCancelled(false)
			.build();

		final var page = new PageImpl<>(List.of(educationEvent), PageRequest.of(0, 20), 1);

		final var result = educationMapper.toPagedEducationResponse(page, Map.of());

		assertThat(result.getEducations()).hasSize(1);
		assertThat(result.getEducations().getFirst().getId()).isEqualTo("e.2281.123");
		assertThat(result.getEducations().getFirst().getCode()).isNull();
	}
}
