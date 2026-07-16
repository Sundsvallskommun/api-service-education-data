package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.Address;
import generated.se.sundsvall.susanavet.Application;
import generated.se.sundsvall.susanavet.ApplicationDetailsType;
import generated.se.sundsvall.susanavet.CSchoolType;
import generated.se.sundsvall.susanavet.EducationEvent;
import generated.se.sundsvall.susanavet.EducationInfo;
import generated.se.sundsvall.susanavet.EducationInfoResponse;
import generated.se.sundsvall.susanavet.Execution;
import generated.se.sundsvall.susanavet.Fee;
import generated.se.sundsvall.susanavet.LangString;
import generated.se.sundsvall.susanavet.LangStringNode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import generated.se.sundsvall.susanavet.LangUrl;
import generated.se.sundsvall.susanavet.PaceOfStudy;
import generated.se.sundsvall.susanavet.SchoolTypeCode;
import generated.se.sundsvall.susanavet.UrlNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.util.Util;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SusaMapperTest {

	private final SusaMapper susaMapper = new SusaMapper();
	private final byte[] json = "{\"page\":{\"totalPages\":1}}".getBytes();

	@Test
	void toZippedEvents() {
		final var entity = susaMapper.toZippedEvents(json, 2);

		assertThat(entity.getPage()).isEqualTo(2);
		assertThat(entity.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(Util.unzip(entity.getJsonBody())).isEqualTo(json);
	}

	@Test
	void toZippedInfos() {
		final var entity = susaMapper.toZippedInfos(json, 5);

		assertThat(entity.getPage()).isEqualTo(5);
		assertThat(entity.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(Util.unzip(entity.getJsonBody())).isEqualTo(json);
	}

	@Test
	void toZippedProviders() {
		final var entity = susaMapper.toZippedProviders(json, 0);

		assertThat(entity.getPage()).isZero();
		assertThat(entity.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(Util.unzip(entity.getJsonBody())).isEqualTo(json);
	}

	@Test
	void toEventEntities() {
		final var startDate = LocalDate.of(2027, 1, 10);
		final var endDate = LocalDate.of(2027, 6, 10);
		final var applicationStart = LocalDate.of(2026, 10, 1);
		final var applicationEnd = LocalDate.of(2026, 12, 1);

		final var event = new EducationEvent()
				.identifier("event-123")
				.education("info-456")
				.providers(List.of("provider-789"))
				.title(new LangString()
						.strings(List.of(new LangStringNode()
								.lang("swe")
								.value("Mekaniker"))))
				.url(new LangUrl().urls(List.of(new UrlNode().value("https://example.com/course"))))
				.locations(List.of(new Address()
						.town("Sundsvall")
						.areaCode("2281")))
				.languageOfInstructions(List.of("swe"))
				.fees(List.of(new Fee()
						.currency("SEK")
						.totalAmount(1250.50)))
				.places(24)
				.execution(new Execution()
						.start(startDate)
						.end(endDate))
				.paceOfStudy(new PaceOfStudy()
						.percentage(100.0))
				.application(new Application()
						.first(applicationStart)
						.last(applicationEnd))
				.isCancelled(true);

		final var result = mapper.toEventEntities(List.of(event));

		assertThat(result).hasSize(1);

		final var row = result.getFirst();

		assertThat(row.getEducationEventId()).isEqualTo("event-123");
		assertThat(row.getEducationInfoId()).isEqualTo("info-456");
		assertThat(row.getEducationProviderId()).isEqualTo("provider-789");
		assertThat(row.getTitle()).isEqualTo("Mekaniker");
		assertThat(row.getCity()).isEqualTo("Sundsvall");
		assertThat(row.getMunicipalityId()).isEqualTo("2281");
		assertThat(row.getCoursePostUrl()).isEqualTo("https://example.com/course");
		assertThat(row.getSeats()).isEqualTo(24);
		assertThat(row.getCurrencyType()).isEqualTo("SEK");
		assertThat(row.getCost()).isEqualByComparingTo(BigDecimal.valueOf(1250.50));
		assertThat(row.getLanguageOfInstructions()).isEqualTo("swe");
		assertThat(row.getStartDate()).isEqualTo(startDate);
		assertThat(row.getEndDate()).isEqualTo(endDate);
		assertThat(row.getStudyPace()).isEqualTo("100.0");
		assertThat(row.getLectureType()).isEqualTo("Classroom");
		assertThat(row.getApplicationDateStart()).isEqualTo(applicationStart);
		assertThat(row.getApplicationDateEnd()).isEqualTo(applicationEnd);
		assertThat(row.getDeleted()).isTrue();
	}

	@Test
	void toInfoEntities() {
		final var content = new EducationInfo()
				.identifier("info-123")
				.type(new CSchoolType()
						.type("schoolType")
						.code(SchoolTypeCode.YH))
				.title(new LangString()
						.strings(List.of(new LangStringNode()
								.lang("swe")
								.value("Mekaniker"))))
				.description(new LangString()
				.strings(List.of(new LangStringNode()
						.lang("swe")
						.value("Laga bilar"))))
				.eligibility(new LangString()
				.strings(List.of(new LangStringNode()
						.lang("swe")
						.value("Grundläggande behörighet"))))
				.recommendedPriorKnowledge(new LangString()
				.strings(List.of(new LangStringNode()
						.lang("swe")
						.value("no knowledge needed"))))
				.resultIsDegree(false)
				.degrees(new LangString()
						.strings(List.of(new LangStringNode()
								.lang("swe")
								.value("Yrkeshögskoleexamen"))))
				.url(new LangUrl()
						.urls(List.of(new UrlNode()
						.lang("swe")
						.value("https://example.com/info"))))
				.expires("2027-06-07T13:37");

		final var response = new EducationInfoResponse()
				.id("info-123")
				.status("ACTIVE")
				.content(content);

		final var result = mapper.toInfoEntities(List.of(response));

		assertThat(result).hasSize(1);

		final var row = result.getFirst();

		assertThat(row.getEducationInfoId()).isEqualTo("info-123");
		assertThat(row.getCode()).isEqualTo("YH");
		assertThat(row.getSchoolType()).isEqualTo("YH");
		assertThat(row.getTitle()).isEqualTo("Mekaniker");
		assertThat(row.getDescription()).isEqualTo("Laga bilar");
		assertThat(row.getEducationEligibility()).isEqualTo("Grundläggande behörighet");
		assertThat(row.getRecommendedPriorKnowledge()).isEqualTo("no knowledge needed");
		assertThat(row.getResultIsDegree()).isFalse();
		assertThat(row.getDegree()).isEqualTo("Yrkeshögskoleexamen");
		assertThat(row.getContentUrl()).isEqualTo("https://example.com/info");
		assertThat(row.getExpires()).isEqualTo(LocalDateTime.parse("2027-06-07T13:37"));
		assertThat(row.getSubjects()).isEqualTo("[]");
		assertThat(row.getDeleted()).isFalse();
	}
}
