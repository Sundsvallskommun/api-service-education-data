package se.sundsvall.educationdata.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.api.model.Statistics;
import se.sundsvall.educationdata.api.model.StatisticsParameters;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.EducationInfoEntityRepository;
import se.sundsvall.educationdata.integration.db.GyProgramCategoryRepository;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity_;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_;
import se.sundsvall.educationdata.integration.db.model.GyProgramCategoryEntity;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity_;
import se.sundsvall.educationdata.integration.db.model.projection.CategoryProjection;
import se.sundsvall.educationdata.integration.db.model.projection.DirectionProjection;
import se.sundsvall.educationdata.integration.db.model.projection.LanguageOfInstructionsProjection;
import se.sundsvall.educationdata.integration.db.model.projection.SchoolTypeProjection;
import se.sundsvall.educationdata.integration.db.model.projection.StudyLocationProjection;
import se.sundsvall.educationdata.integration.db.model.projection.StudyPaceProjection;
import se.sundsvall.educationdata.integration.db.specification.StatisticsSpecification;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Service
public class StatisticsService {

	private final EducationEventEntityRepository educationEventEntityRepository;
	private final EducationInfoEntityRepository educationInfoEntityRepository;
	private final ReferenceCategoryRepository referenceCategoryRepository;
	private final GyProgramCategoryRepository gyProgramCategoryRepository;

	private static final String STUDY_LOCATIONS = "studyLocations";
	private static final String STUDY_PACE = "studyPace";
	private static final String LANGUAGE_OF_INSTRUCTIONS = "languageOfInstructions";
	private static final String CATEGORIES = "categories";
	private static final String DIRECTIONS = "directions";
	private static final String SCHOOL_TYPE = "schoolType";
	private static final String MUNICIPALITY_IDS = "municipalityId";

	public StatisticsService(EducationEventEntityRepository educationEventEntityRepository, EducationInfoEntityRepository educationInfoEntityRepository, ReferenceCategoryRepository referenceCategoryRepository,
		GyProgramCategoryRepository gyProgramCategoryRepository) {
		this.educationEventEntityRepository = educationEventEntityRepository;
		this.educationInfoEntityRepository = educationInfoEntityRepository;
		this.referenceCategoryRepository = referenceCategoryRepository;
		this.gyProgramCategoryRepository = gyProgramCategoryRepository;
	}

	public Statistics getStatisticsByParameters(final String municipalityId, final StatisticsParameters parameters, LocalDate date) {
		date = defaultLatestDateIfNull(date);
		final var specification = StatisticsSpecification.createSpecification(municipalityId, parameters, date);
		final var educations = educationEventEntityRepository.findAll(specification);
		return calculateStatistics(parameters, educations);
	}

	public Statistics calculateStatistics(final StatisticsParameters parameters, final List<EducationEventEntity> educations) {

		final var datedEducations = educations.stream()
			.filter(education -> education.getStartDate() != null)
			.filter(education -> education.getEndDate() != null)
			.toList();

		final var ongoingEducations = datedEducations.stream()
			.filter(education -> education.getStartDate()
				.isBefore(parameters.getStartDate()))
			.filter(education -> education.getEndDate()
				.isAfter(parameters.getEndDate()))
			.count();

		final var plannedEducations = datedEducations.stream()
			.filter(education -> !education.getStartDate()
				.isBefore(parameters.getStartDate()))
			.filter(education -> !education.getStartDate()
				.isAfter(parameters.getEndDate()))
			.count();

		final var finishedEducations = datedEducations.stream()
			.filter(education -> !education.getEndDate()
				.isBefore(parameters.getStartDate()))
			.filter(education -> !education.getEndDate()
				.isAfter(parameters.getEndDate()))
			.count();

		final var missingEndDate = educations.stream()
			.filter(education -> education.getStartDate() != null)
			.filter(education -> education.getEndDate() == null)
			.count();

		final var missingStartDate = educations.stream()
			.filter(education -> education.getStartDate() == null)
			.filter(education -> education.getEndDate() != null)
			.count();

		final var missingStartAndEndDate = educations.stream()
			.filter(education -> education.getStartDate() == null)
			.filter(education -> education.getEndDate() == null)
			.count();

		final var availableSeats = educations.stream()
			.filter(education -> education.getStartDate() != null)
			.filter(education -> !education.getStartDate()
				.isBefore(parameters.getStartDate()))
			.filter(education -> !education.getStartDate()
				.isAfter(parameters.getEndDate()))
			.filter(education -> education.getSeats() != null)
			.mapToInt(EducationEventEntity::getSeats)
			.sum();

		final var totalCapacity = educations.stream()
			.filter(education -> education.getSeats() != null)
			.mapToInt(EducationEventEntity::getSeats)
			.sum();

		final var schoolTypes = educations.stream()
			.map(EducationEventEntity::getEducationInfo)
			.filter(Objects::nonNull)
			.map(EducationInfoEntity::getSchoolType)
			.filter(Objects::nonNull)
			.collect(groupingBy(identity(), counting()));

		return Statistics.builder()
			.withOnGoingEducations(ongoingEducations)
			.withPlannedEducations(plannedEducations)
			.withFinishedEducations(finishedEducations)
			.withMissingEndDateEducations(missingEndDate)
			.withMissingStartDateEducations(missingStartDate)
			.withMissingStartAndEndDateEducations(missingStartAndEndDate)
			.withTotalEducations(educations.size())
			.withAvailableSeats(availableSeats)
			.withTotalCapacity(totalCapacity)
			.withCategories(parameters.getCategories())
			.withDirections(parameters.getDirections())
			.withStudyLocations(parameters.getStudyLocations())
			.withStartDate(parameters.getStartDate())
			.withEndDate(parameters.getEndDate())
			.withSchoolType(schoolTypes)
			.build();

	}

	public List<String> findStatisticsFilterValues(String attribute, LocalDate date) {
		date = defaultLatestDateIfNull(date);
		return switch (attribute) {
			case STUDY_LOCATIONS -> educationEventEntityRepository.findDistinctByCreatedAt(StudyLocationProjection.class, date, Sort.by(EducationEventEntity_.CITY)).stream()
				.filter(Objects::nonNull)
				.map(StudyLocationProjection::getCity)
				.filter(StringUtils::isNotEmpty)
				.toList();
			case STUDY_PACE -> educationEventEntityRepository.findDistinctByCreatedAt(StudyPaceProjection.class, date, Sort.by(EducationEventEntity_.STUDY_PACE)).stream()
				.filter(Objects::nonNull)
				.map(StudyPaceProjection::getStudyPace)
				.filter(StringUtils::isNotEmpty)
				.toList();
			case LANGUAGE_OF_INSTRUCTIONS -> educationEventEntityRepository.findDistinctByCreatedAt(LanguageOfInstructionsProjection.class, date, Sort.by(EducationEventEntity_.LANGUAGE_OF_INSTRUCTIONS)).stream()
				.filter(Objects::nonNull)
				.map(LanguageOfInstructionsProjection::getLanguageOfInstructions)
				.filter(StringUtils::isNotEmpty)
				.toList();
			case CATEGORIES -> Stream.concat(referenceCategoryRepository.findDistinctBy(CategoryProjection.class, Sort.by(ReferenceCategoryEntity_.CATEGORY_NAME)).stream()
				.filter(Objects::nonNull)
				.map(CategoryProjection::getCategoryName)
				.filter(StringUtils::isNotEmpty),
				gyProgramCategoryRepository.findAll().stream()
					.map(GyProgramCategoryEntity::getCategory))
				.distinct()
				.sorted()
				.toList();
			case DIRECTIONS -> referenceCategoryRepository.findDistinctBy(DirectionProjection.class, Sort.by(ReferenceCategoryEntity_.DIRECTION_NAME)).stream()
				.filter(Objects::nonNull)
				.map(DirectionProjection::getDirectionName)
				.filter(StringUtils::isNotEmpty)
				.toList();
			case SCHOOL_TYPE -> educationInfoEntityRepository.findDistinctByCreatedAt(SchoolTypeProjection.class, date, Sort.by(EducationInfoEntity_.SCHOOL_TYPE)).stream()
				.filter(Objects::nonNull)
				.map(SchoolTypeProjection::getSchoolType)
				.filter(StringUtils::isNotEmpty)
				.toList();

			default -> List.of();
		};
	}

	// exist in educationService too, should probably be done in a better way
	private LocalDate defaultLatestDateIfNull(LocalDate date) {
		return Optional.ofNullable(date).orElseGet(educationEventEntityRepository::findLatestImportDate);
	}

}
