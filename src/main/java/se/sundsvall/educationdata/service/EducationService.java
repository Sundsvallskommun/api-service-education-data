package se.sundsvall.educationdata.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.educationdata.api.model.Education;
import se.sundsvall.educationdata.api.model.EducationParameters;
import se.sundsvall.educationdata.api.model.PagedEducationResponse;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.EducationInfoEntityRepository;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity_;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.projection.CityProjection;
import se.sundsvall.educationdata.integration.db.model.projection.LanguageOfInstructionsProjection;
import se.sundsvall.educationdata.integration.db.model.projection.LectureTypeProjection;
import se.sundsvall.educationdata.integration.db.model.projection.StudyPaceProjection;
import se.sundsvall.educationdata.integration.db.specification.EducationSpecification;
import se.sundsvall.educationdata.service.mapper.EducationMapper;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.LANGUAGE_OF_INSTRUCTIONS;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.LECTURE_TYPE;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.STUDY_PACE;

@Service
public class EducationService {

	private final EducationEventEntityRepository educationEventEntityRepository;
	private final EducationInfoEntityRepository educationInfoEntityRepository;
	private final EducationMapper educationMapper;

	private static final String EDUCATION_NOT_FOUND = "Education with id '%s' not found for import date '%s'";
	private static final String STUDY_LOCATION = "studyLocation";

	public EducationService(EducationEventEntityRepository educationEventEntityRepository, EducationInfoEntityRepository educationInfoEntityRepository, EducationMapper educationMapper) {
		this.educationEventEntityRepository = educationEventEntityRepository;
		this.educationInfoEntityRepository = educationInfoEntityRepository;
		this.educationMapper = educationMapper;
	}

	public Education findEducationById(String educationEventId, LocalDate date) {
		final var importDate = defaultLatestDateIfNull(date);
		final var event = educationEventEntityRepository.findByEducationEventIdAndCreatedAt(educationEventId, importDate)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, EDUCATION_NOT_FOUND.formatted(educationEventId, importDate)));
		final var info = Optional.ofNullable(event.getEducationInfoId())
			.flatMap(infoId -> educationInfoEntityRepository.findByEducationInfoIdAndCreatedAt(infoId, event.getCreatedAt())).orElse(null);
		return educationMapper.toEducation(event, info);
	}

	public PagedEducationResponse find(EducationParameters parameters, LocalDate date) {
		date = defaultLatestDateIfNull(date);
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var specification = EducationSpecification.createSpecification(parameters, date);
		final var result = educationEventEntityRepository.findAll(specification, pageable);
		return educationMapper.toPagedEducationResponse(result, getInfos(result.getContent(), date));
	}

	private Map<String, EducationInfoEntity> getInfos(final List<EducationEventEntity> events, LocalDate date) {
		final var infoIds = events.stream().map(EducationEventEntity::getEducationInfoId).filter(Objects::nonNull).collect(Collectors.toSet());
		return educationInfoEntityRepository.findByEducationInfoIdInAndCreatedAt(infoIds, date).stream()
			.collect(toMap(EducationInfoEntity::getEducationInfoId, identity()));
	}

	public List<String> findFilterValues(final String attribute, LocalDate date) {
		date = defaultLatestDateIfNull(date);
		return switch (attribute) {

			case LECTURE_TYPE -> educationEventEntityRepository.findDistinctByCreatedAt(LectureTypeProjection.class, date, Sort.by(LECTURE_TYPE)).stream()
				.filter(Objects::nonNull)
				.map(LectureTypeProjection::getLectureType)
				.toList();
			case LANGUAGE_OF_INSTRUCTIONS -> educationEventEntityRepository.findDistinctByCreatedAt(LanguageOfInstructionsProjection.class, date, Sort.by(LANGUAGE_OF_INSTRUCTIONS)).stream()
				.filter(Objects::nonNull)
				.map(LanguageOfInstructionsProjection::getLanguageOfInstructions)
				.toList();
			case STUDY_LOCATION -> educationEventEntityRepository.findDistinctByCreatedAt(CityProjection.class, date, Sort.by(EducationEventEntity_.CITY)).stream()
				.filter(Objects::nonNull)
				.map(CityProjection::getCity)
				.toList();
			case STUDY_PACE -> educationEventEntityRepository.findDistinctByCreatedAt(StudyPaceProjection.class, date, Sort.by(STUDY_PACE)).stream()
				.filter(Objects::nonNull)
				.map(StudyPaceProjection::getStudyPace)
				.toList();
			default -> List.of();
		};
	}

	private LocalDate defaultLatestDateIfNull(LocalDate date) {
		return Optional.ofNullable(date).orElseGet(educationEventEntityRepository::findLatestImportDate);
	}
}
