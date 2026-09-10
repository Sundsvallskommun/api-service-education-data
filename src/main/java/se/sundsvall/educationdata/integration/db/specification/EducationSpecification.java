package se.sundsvall.educationdata.integration.db.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.educationdata.api.model.EducationParameters;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;

import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.APPLICATION_DATE_END;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.APPLICATION_DATE_START;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.CANCELLED;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.CITY;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.CREATED_AT;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.EDUCATION_EVENT_ID;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.EDUCATION_INFO;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.END_DATE;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.LANGUAGE_OF_INSTRUCTIONS;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.LECTURE_TYPE;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.MUNICIPALITY_ID;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.SEATS;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.START_DATE;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.STUDY_PACE;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.TITLE;
import static se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_.CREDITS;
import static se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_.CREDIT_TYPE;
import static se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_.DURATION;
import static se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_.EDUCATION_ELIGIBILITY;
import static se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_.EDUCATION_TYPE;
import static se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_.EXPIRES;
import static se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_.RECOMMENDED_PRIOR_KNOWLEDGE;
import static se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_.SCHOOL_TYPE;

public interface EducationSpecification {

	static Specification<EducationEventEntity> createSpecification(final EducationParameters parameters, final LocalDate date) {
		return Specification.allOf(
			withCreated(date),
			withEducationEventId(parameters.getEducationEventId()),
			withTitle(parameters.getName()),
			withInfoTitle(parameters.getTitle()),
			withMunicipalityId(parameters.getMunicipalityId()),
			withCity(parameters.getStudyLocation()),
			withLectureType(parameters.getLectureType()),
			withLanguageOfInstructions(parameters.getLanguageOfInstructions()),
			withCancelledStatus(parameters.getCancelled()),
			withStartDate(parameters.getStartDate()),
			withEndDate(parameters.getEndDate()),
			withStudyPace(parameters.getStudyPace()),
			withApplicationDateStart(parameters.getApplicationDateStart()),
			withApplicationDateEnd(parameters.getApplicationDateEnd()),
			withSeats(parameters.getSeats()),
			withRecommendedPriorKnowledge(parameters.getRecommendedPriorKnowledge()),
			withSchoolType(parameters.getSchoolType()),
			withEducationType(parameters.getEducationType()),
			withEligibility(parameters.getEligibility()),
			withDuration(parameters.getDuration()),
			withExpires(parameters.getExpires()),
			withCredits(parameters.getCredits()),
			withCreditType(parameters.getCreditType()));
	}

	static Specification<EducationEventEntity> withInfoTitle(final String infoTitle) {
		return SpecificationBuilder.buildJoinedLikeIgnoreCaseFilter(EDUCATION_INFO, TITLE, infoTitle);
	}

	static Specification<EducationEventEntity> withCredits(final Double credits) {
		return SpecificationBuilder.buildJoinedEqualFilter(EDUCATION_INFO, CREDITS, credits);
	}

	static Specification<EducationEventEntity> withCreditType(final String creditType) {
		return SpecificationBuilder.buildJoinedEqualFilter(EDUCATION_INFO, CREDIT_TYPE, creditType);
	}

	static Specification<EducationEventEntity> withSchoolType(final String schoolType) {
		return SpecificationBuilder.buildJoinedEqualFilter(EDUCATION_INFO, SCHOOL_TYPE, schoolType);
	}

	static Specification<EducationEventEntity> withEducationType(final String educationType) {
		return SpecificationBuilder.buildJoinedEqualFilter(EDUCATION_INFO, EDUCATION_TYPE, educationType);
	}

	static Specification<EducationEventEntity> withEligibility(final String eligibility) {
		return SpecificationBuilder.buildJoinedLikeIgnoreCaseFilter(EDUCATION_INFO, EDUCATION_ELIGIBILITY, eligibility);
	}

	static Specification<EducationEventEntity> withDuration(final String duration) {
		return SpecificationBuilder.buildJoinedEqualFilter(EDUCATION_INFO, DURATION, duration);
	}

	static Specification<EducationEventEntity> withExpires(final LocalDateTime expires) {
		return SpecificationBuilder.buildJoinedDateIsEqualOrBeforeOrNullFilter(EDUCATION_INFO, EXPIRES, expires);
	}

	static Specification<EducationEventEntity> withRecommendedPriorKnowledge(final String recommendedPriorKnowledge) {
		return SpecificationBuilder.buildJoinedLikeIgnoreCaseFilter(EDUCATION_INFO, RECOMMENDED_PRIOR_KNOWLEDGE, recommendedPriorKnowledge);
	}

	static Specification<EducationEventEntity> withCreated(LocalDate created) {
		return SpecificationBuilder.buildEqualFilter(CREATED_AT, created);
	}

	static Specification<EducationEventEntity> withEducationEventId(String educationEventId) {
		return SpecificationBuilder.buildEqualFilter(EDUCATION_EVENT_ID, educationEventId);
	}

	static Specification<EducationEventEntity> withTitle(String title) {
		return SpecificationBuilder.buildLikeIgnoreCaseFilter(TITLE, title);
	}

	static Specification<EducationEventEntity> withCity(String city) {
		return SpecificationBuilder.buildLikeIgnoreCaseFilter(CITY, city);
	}

	static Specification<EducationEventEntity> withMunicipalityId(String municipalityId) {
		return SpecificationBuilder.buildEqualFilter(MUNICIPALITY_ID, municipalityId);
	}

	static Specification<EducationEventEntity> withSeats(Integer seats) {
		return SpecificationBuilder.buildEqualFilter(SEATS, seats);
	}

	static Specification<EducationEventEntity> withLectureType(String lectureType) {
		return SpecificationBuilder.buildLikeIgnoreCaseFilter(LECTURE_TYPE, lectureType);
	}

	static Specification<EducationEventEntity> withStudyPace(String studyPace) {
		return SpecificationBuilder.buildEqualFilter(STUDY_PACE, studyPace);
	}

	static Specification<EducationEventEntity> withLanguageOfInstructions(String languageOfInstructions) {
		return SpecificationBuilder.buildLikeIgnoreCaseFilter(LANGUAGE_OF_INSTRUCTIONS, languageOfInstructions);
	}

	static Specification<EducationEventEntity> withStartDate(LocalDate startDate) {
		return SpecificationBuilder.buildDateIsEqualOrAfterFilter(START_DATE, startDate);
	}

	static Specification<EducationEventEntity> withEndDate(LocalDate endDate) {
		return SpecificationBuilder.buildDateIsEqualOrAfterFilter(END_DATE, endDate);
	}

	static Specification<EducationEventEntity> withApplicationDateStart(LocalDate applicationDateStart) {
		return SpecificationBuilder.buildDateIsEqualOrAfterFilter(APPLICATION_DATE_START, applicationDateStart);
	}

	static Specification<EducationEventEntity> withApplicationDateEnd(LocalDate applicationDateEnd) {
		return SpecificationBuilder.buildDateIsEqualOrAfterFilter(APPLICATION_DATE_END, applicationDateEnd);
	}

	static Specification<EducationEventEntity> withCancelledStatus(Boolean cancelledStatus) {
		return SpecificationBuilder.buildEqualFilter(CANCELLED, cancelledStatus);
	}

	static Specification<EducationEventEntity> withinPeriod(final LocalDate startDate, final LocalDate endDate) {
		return SpecificationBuilder.withinPeriod(startDate, endDate);
	}
}
