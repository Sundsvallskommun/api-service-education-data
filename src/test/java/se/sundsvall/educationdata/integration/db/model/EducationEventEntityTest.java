package se.sundsvall.educationdata.integration.db.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Random;
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
		var eduId = "eduId";
		var identifier = "identifier";
		var name = "name";
		var description = "description";
		var city = "city";
		var visitingAddress = "visitingAddress";
		var municipalityId = "municipalityId";
		var schoolUnitName = "schoolUnitName";
		var providerId = "providerId";
		var providerUrl = "providerUrl";
		var coursePostUrl = "coursePostUrl";
		var seats = 10;
		var currencyType = "SEK";
		var cost = BigDecimal.valueOf(1000);
		var studentAidEligible = true;
		var lectureType = "distance";
		var studyPace = "100";
		var creditType = "creditType";
		var credits = "150";
		var typeOfEducation = "vocational";
		var languageOfInstructions = "Swedish";
		var recommendedKnowledge = "recommendedKnowledge";
		var requirements = "requirements";
		var startDate = LocalDate.of(2026, Month.JUNE, 1);
		var endDate = LocalDate.of(2025, Month.JUNE, 1);
		var applicationDateStart = LocalDate.of(2025, Month.JUNE, 1);
		var applicationDateEnd = LocalDate.of(2025, Month.JUNE, 1);
		var subjectCode = "subjectCode";
		var category = "category";
		var subcategory = "subcategory";
		var outdatedAt = LocalDate.of(2025, Month.JUNE, 10);
		var deleted = false;

		// Act
		var education = EducationEventEntity.builder()
			.withEduId(eduId)
			.withIdentifier(identifier)
			.withName(name)
			.withDescription(description)
			.withCity(city)
			.withVisitingAddress(visitingAddress)
			.withMunicipalityId(municipalityId)
			.withSchoolUnitName(schoolUnitName)
			.withProviderId(providerId)
			.withProviderUrl(providerUrl)
			.withCoursePostUrl(coursePostUrl)
			.withSeats(seats)
			.withCurrencyType(currencyType)
			.withCost(cost)
			.withStudentAidEligible(studentAidEligible)
			.withLectureType(lectureType)
			.withStudyPace(studyPace)
			.withCreditType(creditType)
			.withCredits(credits)
			.withTypeOfEducation(typeOfEducation)
			.withLanguageOfInstructions(languageOfInstructions)
			.withRecommendedKnowledge(recommendedKnowledge)
			.withRequirements(requirements)
			.withStartDate(startDate)
			.withEndDate(endDate)
			.withApplicationDateStart(applicationDateStart)
			.withApplicationDateEnd(applicationDateEnd)
			.withSubjectCode(subjectCode)
			.withCategory(category)
			.withSubcategory(subcategory)
			.withOutdatedAt(outdatedAt)
			.withDeleted(deleted).build();

		// Assert
		assertThat(education.getEduId()).isEqualTo(eduId);
		assertThat(education.getIdentifier()).isEqualTo(identifier);
		assertThat(education.getName()).isEqualTo(name);
		assertThat(education.getDescription()).isEqualTo(description);
		assertThat(education.getCity()).isEqualTo(city);
		assertThat(education.getVisitingAddress()).isEqualTo(visitingAddress);
		assertThat(education.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(education.getSchoolUnitName()).isEqualTo(schoolUnitName);
		assertThat(education.getProviderId()).isEqualTo(providerId);
		assertThat(education.getProviderUrl()).isEqualTo(providerUrl);
		assertThat(education.getCoursePostUrl()).isEqualTo(coursePostUrl);
		assertThat(education.getSeats()).isEqualTo(seats);
		assertThat(education.getCurrencyType()).isEqualTo(currencyType);
		assertThat(education.getCost()).isEqualTo(cost);
		assertThat(education.getStudentAidEligible()).isEqualTo(studentAidEligible);
		assertThat(education.getLectureType()).isEqualTo(lectureType);
		assertThat(education.getStudyPace()).isEqualTo(studyPace);
		assertThat(education.getCreditType()).isEqualTo(creditType);
		assertThat(education.getCredits()).isEqualTo(credits);
		assertThat(education.getTypeOfEducation()).isEqualTo(typeOfEducation);
		assertThat(education.getLanguageOfInstructions()).isEqualTo(languageOfInstructions);
		assertThat(education.getRecommendedKnowledge()).isEqualTo(recommendedKnowledge);
		assertThat(education.getRequirements()).isEqualTo(requirements);
		assertThat(education.getStartDate()).isEqualTo(startDate);
		assertThat(education.getEndDate()).isEqualTo(endDate);
		assertThat(education.getApplicationDateStart()).isEqualTo(applicationDateStart);
		assertThat(education.getApplicationDateEnd()).isEqualTo(applicationDateEnd);
		assertThat(education.getSubjectCode()).isEqualTo(subjectCode);
		assertThat(education.getCategory()).isEqualTo(category);
		assertThat(education.getSubcategory()).isEqualTo(subcategory);
		assertThat(education.getOutdatedAt()).isEqualTo(outdatedAt);
		assertThat(education.getDeleted()).isEqualTo(deleted);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(EducationEventEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new EducationEventEntity()).hasAllNullFieldsOrProperties();
	}
}
