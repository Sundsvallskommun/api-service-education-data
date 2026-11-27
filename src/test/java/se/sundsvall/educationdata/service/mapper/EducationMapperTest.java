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
import se.sundsvall.educationdata.api.model.Education;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.EducationProviderEntity;

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
			.withStudySite("Distans")
			.withMunicipalityId("2281")
			.withContractor("Test Gy")
			.withCoursePostUrl("https://sundsvall.alvis.se/hittakurser")
			.withSeats(25)
			.withCost(BigDecimal.ZERO)
			.withCurrencyType("SEK")
			.withLectureType("Distance")
			.withStudyPace("100.0")
			.withLanguageOfInstructions("swe")
			.withStartDate(LocalDate.of(2026, Month.OCTOBER, 12))
			.withEndDate(LocalDate.of(2026, Month.DECEMBER, 18))
			.withStartType("1")
			.withApplicationDateStart(LocalDate.of(2026, Month.MARCH, 15))
			.withApplicationDateEnd(LocalDate.of(2026, Month.APRIL, 15))
			.withApplicationUrl("https://example.com/apply")
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
			.withDuration("65")
			.withDurationUnit("days")
			.withEducationEligibility("Grundläggande behörighet")
			.withRecommendedPriorKnowledge("Matematik 2")
			.build();
		final var educationProvider = EducationProviderEntity.builder()
			.withName("Sundsvalls kommun")
			.withSchoolName("Västermalms skola")
			.withUrl("https://sundsvall.se/vuxenutbildning").build();

		final var result = educationMapper.toEducation(educationEvent, educationInfo, educationProvider);

		assertThat(result)
			.extracting(
				Education::getId,
				Education::getEventTitle,
				Education::getStudyLocation,
				Education::getStudySite,
				Education::getMunicipalityId,
				Education::getContractor,
				Education::getLectureType,
				Education::getStudyPace,
				Education::getLanguageOfInstructions,
				Education::getStart,
				Education::getEnd,
				Education::getStartType,
				Education::getApplicationUrl,
				Education::getCancelled)
			.containsExactly(
				"e.2281.123",
				"Matematik nivå 1a",
				"Sundsvall",
				"Distans",
				"2281",
				"Test Gy",
				"Distance",
				"100.0",
				"swe",
				LocalDate.of(2026, Month.OCTOBER, 12),
				LocalDate.of(2026, Month.DECEMBER, 18),
				"1",
				"https://example.com/apply",
				false);

		assertThat(result)
			.extracting(
				Education::getTitle,
				Education::getCode,
				Education::getSchoolType,
				Education::getEducationType,
				Education::getCredits,
				Education::getCreditType,
				Education::getDuration,
				Education::getDurationUnit,
				Education::getEligibility,
				Education::getRecommendedPriorKnowledge)
			.containsExactly(
				"Matematik",
				"MATE1A00X",
				"VUXGY",
				"kurs",
				100.0,
				"vp",
				"65",
				"days",
				"Grundläggande behörighet",
				"Matematik 2");

		assertThat(result)
			.extracting(Education::getProvider, Education::getSchoolName, Education::getProviderUrl)
			.containsExactly("Sundsvalls kommun", "Västermalms skola", "https://sundsvall.se/vuxenutbildning");
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

		final var result = educationMapper.toEducation(educationEvent, null, null);

		assertThat(result.getId()).isEqualTo("e.2281.123");
		assertThat(result.getLectureType()).isEqualTo("Distance");

		assertThat(result.getCode()).isNull();
		assertThat(result.getSchoolType()).isNull();
		assertThat(result.getEducationType()).isNull();
		assertThat(result.getCredits()).isNull();
		assertThat(result.getDegree()).isNull();
		assertThat(result.getProvider()).isNull();
		assertThat(result.getProviderUrl()).isNull();
	}

	@Test
	void toPagedEducationResponseMapsContentAndMetadata() {
		final var educationEvent = EducationEventEntity.builder()
			.withEducationEventId("e.2281.123")
			.withEducationInfoId("i.123")
			.withEducationProviderId("p.123")
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
		final var educationProvider = EducationProviderEntity.builder()
			.withEducationProviderId("p.123")
			.withName("Sundsvalls kommun")
			.build();

		final var page = new PageImpl<>(List.of(educationEvent), PageRequest.of(0, 20), 1);

		final var result = educationMapper.toPagedEducationResponse(page, Map.of("i.123", educationInfo), Map.of("p.123", educationProvider));

		assertThat(result.getEducations()).hasSize(1);
		assertThat(result.getEducations().getFirst().getCode()).isEqualTo("MATE1A00X");
		assertThat(result.getEducations().getFirst().getProvider()).isEqualTo("Sundsvalls kommun");
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

		final var result = educationMapper.toPagedEducationResponse(page, Map.of(), Map.of());

		assertThat(result.getEducations()).hasSize(1);
		assertThat(result.getEducations().getFirst().getId()).isEqualTo("e.2281.123");
		assertThat(result.getEducations().getFirst().getCode()).isNull();
		assertThat(result.getEducations().getFirst().getProvider()).isNull();
	}
}
