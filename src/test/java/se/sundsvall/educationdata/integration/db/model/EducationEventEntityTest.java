package se.sundsvall.educationdata.integration.db.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Random;
import java.util.UUID;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class EducationEventEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(EducationEventEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		var id = UUID.randomUUID().toString();
		var educationEventId = "educationEventId";
		var educationInfoId = "educationInfoId";
		var title = "name";
		var city = "city";
		var municipalityId = "municipalityId";
		var providerId = "providerId";
		var coursePostUrl = "coursePostUrl";
		var seats = 10;
		var currencyType = "SEK";
		var cost = BigDecimal.valueOf(1000);
		var lectureType = "distance";
		var studyPace = "100";
		var languageOfInstructions = "Swedish";
		var startDate = LocalDate.of(2026, Month.JUNE, 1);
		var endDate = LocalDate.of(2025, Month.JUNE, 1);
		var applicationDateStart = LocalDate.of(2025, Month.JUNE, 1);
		var applicationDateEnd = LocalDate.of(2025, Month.JUNE, 1);
		var outdatedAt = LocalDate.of(2025, Month.JUNE, 10);
		var deleted = false;

		// Act
		var education = EducationEventEntity.builder()
			.withId(id)
			.withEducationEventId(educationEventId)
			.withEducationInfoId(educationInfoId)
			.withTitle(title)
			.withCity(city)
			.withMunicipalityId(municipalityId)
			.withEducationProviderId(providerId)
			.withCoursePostUrl(coursePostUrl)
			.withSeats(seats)
			.withCurrencyType(currencyType)
			.withCost(cost)
			.withLectureType(lectureType)
			.withStudyPace(studyPace)
			.withLanguageOfInstructions(languageOfInstructions)
			.withStartDate(startDate)
			.withEndDate(endDate)
			.withApplicationDateStart(applicationDateStart)
			.withApplicationDateEnd(applicationDateEnd)
			.withOutdatedAt(outdatedAt)
			.withDeleted(deleted).build();

		// Assert
		assertThat(education.getId()).isEqualTo(id);
		assertThat(education.getEducationEventId()).isEqualTo(educationEventId);
		assertThat(education.getEducationInfoId()).isEqualTo(educationInfoId);
		assertThat(education.getTitle()).isEqualTo(title);
		assertThat(education.getCity()).isEqualTo(city);
		assertThat(education.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(education.getEducationProviderId()).isEqualTo(providerId);
		assertThat(education.getCoursePostUrl()).isEqualTo(coursePostUrl);
		assertThat(education.getSeats()).isEqualTo(seats);
		assertThat(education.getCurrencyType()).isEqualTo(currencyType);
		assertThat(education.getCost()).isEqualTo(cost);
		assertThat(education.getLectureType()).isEqualTo(lectureType);
		assertThat(education.getStudyPace()).isEqualTo(studyPace);
		assertThat(education.getLanguageOfInstructions()).isEqualTo(languageOfInstructions);
		assertThat(education.getStartDate()).isEqualTo(startDate);
		assertThat(education.getEndDate()).isEqualTo(endDate);
		assertThat(education.getApplicationDateStart()).isEqualTo(applicationDateStart);
		assertThat(education.getApplicationDateEnd()).isEqualTo(applicationDateEnd);
		assertThat(education.getOutdatedAt()).isEqualTo(outdatedAt);
		assertThat(education.getDeleted()).isEqualTo(deleted);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(EducationEventEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new EducationEventEntity()).hasAllNullFieldsOrProperties();
	}
}
