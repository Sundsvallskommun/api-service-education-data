package se.sundsvall.educationdata.integration.db.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

public class EducationInfoEntityTest {
	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDateTime.now().plusDays(new Random().nextInt()), LocalDateTime.class);
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(EducationInfoEntity.class, allOf(
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
		var educationInfoId = "education_info_id";
		var title = "title";
		var schoolType = "schoolType";
		var educationType = "educationType";
		var description = "description";
		var educationEligibility = "educationEligibility";
		var recommendedPriorKnowledge = "recommendedPriorKnowledge";
		var creditType = "creditType";
		var credits = "credits";
		var duration = "6 weeks";
		var degree = "degree";
		var url = "url";
		var studentAidEligibility = "studentAidEligibility";
		var subjects = "subjects";
		var code = "subjects";
		boolean deleted = true;
		boolean resultIsDegree = true;
		var localDateTime = LocalDateTime.of(2025, Month.JUNE, 10, 0, 0, 0);
		var localDate = LocalDate.of(2025, Month.JULY, 10);

		// Act
		var education = EducationInfoEntity.builder()
			.withId(id)
			.withEducationInfoId(educationInfoId)
			.withTitle(title)
			.withSchoolType(schoolType)
			.withCode(code)
			.withEducationType(educationType)
			.withDescription(description)
			.withEducationEligibility(educationEligibility)
			.withRecommendedPriorKnowledge(recommendedPriorKnowledge)
			.withCredits(credits)
			.withCreditType(creditType)
			.withDegree(degree)
			.withResultIsDegree(resultIsDegree)
			.withContentUrl(url)
			.withExpires(localDateTime)
			.withStudentAidEligibility(studentAidEligibility)
			.withSubjects(subjects)
			.withDuration(duration)
			.withOutdatedAt(localDate)
			.withCreatedAt(localDate)
			.withDeleted(deleted)
			.build();

		// Assert
		assertThat(education.getId()).isEqualTo(id);
		assertThat(education.getEducationEligibility()).isEqualTo(educationEligibility);
		assertThat(education.getEducationInfoId()).isEqualTo(educationInfoId);
		assertThat(education.getDegree()).isEqualTo(degree);
		assertThat(education.getCode()).isEqualTo(code);
		assertThat(education.getExpires()).isEqualTo(localDateTime);
		assertThat(education.getTitle()).isEqualTo(title);
		assertThat(education.getDescription()).isEqualTo(description);
		assertThat(education.getSubjects()).isEqualTo(subjects);
		assertThat(education.getSchoolType()).isEqualTo(schoolType);
		assertThat(education.getCredits()).isEqualTo(credits);
		assertThat(education.getStudentAidEligibility()).isEqualTo(studentAidEligibility);
		assertThat(education.getEducationType()).isEqualTo(educationType);
		assertThat(education.getRecommendedPriorKnowledge()).isEqualTo(recommendedPriorKnowledge);
		assertThat(education.getCreditType()).isEqualTo(creditType);
		assertThat(education.getDuration()).isEqualTo(duration);
		assertThat(education.getResultIsDegree()).isEqualTo(resultIsDegree);
		assertThat(education.getContentUrl()).isEqualTo(url);
		assertThat(education.getCreatedAt()).isEqualTo(localDate);
		assertThat(education.getOutdatedAt()).isEqualTo(localDate);
		assertThat(education.getDeleted()).isEqualTo(deleted);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(EducationInfoEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new EducationInfoEntity()).hasAllNullFieldsOrProperties();
	}
}
