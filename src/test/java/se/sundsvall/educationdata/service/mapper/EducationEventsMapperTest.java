package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.Address;
import generated.se.sundsvall.susanavet.Application;
import generated.se.sundsvall.susanavet.CExecutionCondition;
import generated.se.sundsvall.susanavet.DistanceEducation;
import generated.se.sundsvall.susanavet.EducationEvent;
import generated.se.sundsvall.susanavet.Execution;
import generated.se.sundsvall.susanavet.ExecutionConditionCode;
import generated.se.sundsvall.susanavet.Fee;
import generated.se.sundsvall.susanavet.KVEventExtension;
import generated.se.sundsvall.susanavet.LangString;
import generated.se.sundsvall.susanavet.LangStringNode;
import generated.se.sundsvall.susanavet.LangUrl;
import generated.se.sundsvall.susanavet.PaceOfStudy;
import generated.se.sundsvall.susanavet.UHEventExtension;
import generated.se.sundsvall.susanavet.UrlNode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.util.Util;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EducationEventsMapperTest {

	private final EducationEventsMapper educationEventsMapper = new EducationEventsMapper();
	private final byte[] json = "{\"page\":{\"totalPages\":1}}".getBytes();
	private static final Set<String> MUNICIPALITY_IDS = Set.of("2281");

	@Test
	void toZippedEvents_test() {

		final var entity = educationEventsMapper.toZippedEvents(json, 2);

		assertThat(entity.getPage()).isEqualTo(2);
		assertThat(entity.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(Util.unzip(entity.getJsonBody())).isEqualTo(json);
	}

	@Test
	void toEventEntities_test() {
		final var startDate = LocalDate.of(2027, Month.JANUARY, 10);
		final var endDate = LocalDate.of(2027, Month.JUNE, 10);
		final var applicationStart = LocalDate.of(2026, Month.OCTOBER, 1);
		final var applicationEnd = LocalDate.of(2026, Month.DECEMBER, 1);

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
				.areaCode("2281")
				.studyLocation(new LangString()
					.strings(List.of(new LangStringNode()
						.lang("swe")
						.value("Distans"))))))
			.languageOfInstructions(List.of("swe"))
			.fees(List.of(new Fee()
				.currency("SEK")
				.totalAmount(1250.50)))
			.places(24)
			.execution(new Execution()
				.start(startDate)
				.end(endDate)
				.condition(new CExecutionCondition().code(ExecutionConditionCode._1)))
			.paceOfStudy(new PaceOfStudy()
				.percentage(100.0))
			.application(new Application()
				.first(applicationStart)
				.last(applicationEnd)
				.url(new LangUrl().urls(List.of(new UrlNode().value("https://example.com/apply")))))
			.distance(new DistanceEducation())
			.isCancelled(true)
			.extensions(List.of(
				new UHEventExtension().type("UHEventExtension").id("1"),
				new KVEventExtension().type("KVEventExtension").id("2").contractor("Test Gy")));

		final var result = educationEventsMapper.toEventEntities(List.of(event), MUNICIPALITY_IDS);

		assertThat(result).hasSize(1);

		final var row = result.getFirst();

		assertThat(row.getEducationEventId()).isEqualTo("event-123");
		assertThat(row.getEducationInfoId()).isEqualTo("info-456");
		assertThat(row.getEducationProviderId()).isEqualTo("provider-789");
		assertThat(row.getContractor()).isEqualTo("Test Gy");
		assertThat(row.getTitle()).isEqualTo("Mekaniker");
		assertThat(row.getCity()).isEqualTo("Sundsvall");
		assertThat(row.getStudySite()).isEqualTo("Distans");
		assertThat(row.getMunicipalityId()).isEqualTo("2281");
		assertThat(row.getCoursePostUrl()).isEqualTo("https://example.com/course");
		assertThat(row.getSeats()).isEqualTo(24);
		assertThat(row.getCurrencyType()).isEqualTo("SEK");
		assertThat(row.getCost()).isEqualByComparingTo(BigDecimal.valueOf(1250.50));
		assertThat(row.getLanguageOfInstructions()).isEqualTo("swe");
		assertThat(row.getStartDate()).isEqualTo(startDate);
		assertThat(row.getEndDate()).isEqualTo(endDate);
		assertThat(row.getStartType()).isEqualTo("1");
		assertThat(row.getStudyPace()).isEqualTo("100.0");
		assertThat(row.getLectureType()).isEqualTo("Distance");
		assertThat(row.getApplicationDateStart()).isEqualTo(applicationStart);
		assertThat(row.getApplicationDateEnd()).isEqualTo(applicationEnd);
		assertThat(row.getApplicationUrl()).isEqualTo("https://example.com/apply");
		assertThat(row.getCancelled()).isTrue();
	}

	@Test
	void toEventEntity_nullDistance_defaultClassroom() {
		final var event = new EducationEvent().distance(null);
		final var row = educationEventsMapper.toEventEntities(List.of(event), MUNICIPALITY_IDS).getFirst();
		assertThat(row.getLectureType()).isEqualTo("Classroom");
		assertThat(row.getContractor()).isNull();
	}

	@Test
	void toEventEntities_nullTest() {
		final List<EducationEvent> events = null;
		final var result = educationEventsMapper.toEventEntities(events, MUNICIPALITY_IDS);
		assertThat(result).isEmpty();
	}

	@Test
	void toEventEntities_nullElement() {
		final var events = new ArrayList<EducationEvent>();
		events.add(null);
		final var result = educationEventsMapper.toEventEntities(events, MUNICIPALITY_IDS);
		assertThat(result).isEmpty();
	}

	@Test
	void toEventEntity_unrecognisedConditionCode_startTypeIsNull() {
		final var event = new EducationEvent()
			.execution(new Execution()
				.condition(new CExecutionCondition().code(ExecutionConditionCode.UNKNOWN_DEFAULT_OPEN_API)));

		final var row = educationEventsMapper.toEventEntities(List.of(event), MUNICIPALITY_IDS).getFirst();

		assertThat(row.getStartType()).isNull();
	}

	@Test
	void toEventEntity_multipleLocations_picksWhitelistedLocation() {
		final var event = new EducationEvent()
			.locations(List.of(
				new Address().town("Okänd"),
				new Address().town("Malmö").areaCode("1280"),
				new Address().town("Sundsvall").areaCode("2281"),
				new Address().town("Umeå").areaCode("2480")));

		final var row = educationEventsMapper.toEventEntities(List.of(event), MUNICIPALITY_IDS).getFirst();

		assertThat(row.getCity()).isEqualTo("Sundsvall");
		assertThat(row.getMunicipalityId()).isEqualTo("2281");
	}

	@Test
	void toEventEntity_noWhitelistedLocation_fallsBackToFirstLocation() {
		final var event = new EducationEvent()
			.locations(List.of(
				new Address().town("Malmö").areaCode("1280"),
				new Address().town("Umeå").areaCode("2480")));

		final var row = educationEventsMapper.toEventEntities(List.of(event), MUNICIPALITY_IDS).getFirst();

		assertThat(row.getCity()).isEqualTo("Malmö");
		assertThat(row.getMunicipalityId()).isEqualTo("1280");
	}
}
