package se.sundsvall.educationdata.integration.db.specification;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.educationdata.api.model.StatisticsParameters;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;

import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.CITY;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.CREATED_AT;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.EDUCATION_INFO;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.LANGUAGE_OF_INSTRUCTIONS;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.MUNICIPALITY_ID;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.STUDY_PACE;
import static se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_.SCHOOL_TYPE;

public interface StatisticsSpecification {

	static Specification<EducationEventEntity> createSpecification(final String municipalityId, final StatisticsParameters parameters, final LocalDate date) {
		return Specification.allOf(
			withMunicipality(municipalityId),
			withCreated(date),
			withPeriod(parameters.getStartDate(), parameters.getEndDate()),
			withSchoolType(parameters.getSchoolType()),
			withStudyPace(parameters.getStudyPace()),
			withStudyLocations(parameters.getStudyLocations()),
			withLanguageOfInstructions(parameters.getLanguageOfInstructions()),
			withCategories(parameters.getCategories()),
			withDirections(parameters.getDirections()));
	}

	static Specification<EducationEventEntity> withMunicipality(final String municipalityId) {
		return SpecificationBuilder.buildEqualFilter(MUNICIPALITY_ID, municipalityId);
	}

	static Specification<EducationEventEntity> withCreated(final LocalDate date) {
		return SpecificationBuilder.buildEqualFilter(CREATED_AT, date);
	}

	static Specification<EducationEventEntity> withPeriod(final LocalDate from, final LocalDate to) {
		return SpecificationBuilder.buildWithinPeriodFilter(from, to);
	}

	static Specification<EducationEventEntity> withStudyPace(final List<String> studyPace) {
		return SpecificationBuilder.buildIgnoreCaseFilterWithList(STUDY_PACE, studyPace);
	}

	static Specification<EducationEventEntity> withSchoolType(final String schoolType) {
		return SpecificationBuilder.buildJoinedEqualFilter(EDUCATION_INFO, SCHOOL_TYPE, schoolType);
	}

	static Specification<EducationEventEntity> withStudyLocations(final List<String> studyLocations) {
		return SpecificationBuilder.buildIgnoreCaseFilterWithList(CITY, studyLocations);
	}

	static Specification<EducationEventEntity> withLanguageOfInstructions(final String languageOfInstructions) {
		return SpecificationBuilder.buildEqualFilter(LANGUAGE_OF_INSTRUCTIONS, languageOfInstructions);
	}

	static Specification<EducationEventEntity> withCategories(final List<String> categoryIds) {
		return SpecificationBuilder.buildCategoryFilter(categoryIds)
			.or(SpecificationBuilder.buildGyCategoryFilter(categoryIds));
	}

	static Specification<EducationEventEntity> withDirections(final List<String> directionIds) {
		return SpecificationBuilder.buildDirectionFilter(directionIds);
	}
}
