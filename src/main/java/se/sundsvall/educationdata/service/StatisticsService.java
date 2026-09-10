package se.sundsvall.educationdata.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.api.model.Statistics;
import se.sundsvall.educationdata.api.model.StatisticsParameters;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity_;
import se.sundsvall.educationdata.integration.db.model.projection.CityProjection;
import se.sundsvall.educationdata.integration.db.model.projection.StudyPaceProjection;
import se.sundsvall.educationdata.integration.db.specification.EducationSpecification;
import se.sundsvall.educationdata.integration.db.specification.StatisticsSpecification;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StatisticsService {

    private static final String CITY = "city";
    private static final String STUDY_PACE = "studyPace";

    private final EducationEventEntityRepository educationEventEntityRepository;

    public StatisticsService(EducationEventEntityRepository educationEventEntityRepository) {
        this.educationEventEntityRepository = educationEventEntityRepository;
    }

    public Statistics getStatisticsByParameters(final StatisticsParameters parameters, LocalDate date){
        date = defaultLatestDateIfNull(date);
        final var specification = StatisticsSpecification.createSpecification(parameters, date);
        final var educations = educationEventEntityRepository.findAll(specification);
        return calculateStatistics(parameters, educations);
    }


    public Statistics calculateStatistics(final StatisticsParameters parameters, final List<EducationEventEntity> educations){
        final var ongoingEducations = educations.stream()
                .filter(education -> education.getStartDate()
                        .isBefore(parameters.getStartDate()))
                .filter(education -> education.getEndDate()
                        .isBefore(parameters.getEndDate())).count();

        final var plannedEducations = educations.stream()
                .filter(education -> education.getStartDate()
                        .isAfter(parameters.getStartDate()))
                .filter(education -> education.getEndDate()
                        .isBefore(parameters.getEndDate())).count();


        final var finishedEducations = educations.stream()
                .filter(education -> education.getStartDate()
                        .isAfter(parameters.getStartDate()))
                .filter(education -> education.getEndDate()
                        .isAfter(parameters.getEndDate())).count();

        final var availableSeats = educations.stream()
                .filter(education -> education.getStartDate()
                        .isAfter(parameters.getStartDate()))
                .filter(education -> education.getSeats() != null)
                .mapToInt(EducationEventEntity::getSeats)
                .sum();

        final var totalCapacity = educations.stream()
                .filter(education -> education.getSeats() != null)
                .mapToInt(EducationEventEntity::getSeats)
                .sum();

        return Statistics.builder()
                .withOnGoingEducations(ongoingEducations)
                .withPlannedEducations(plannedEducations)
                .withFinishedEducations(finishedEducations)
                .withAvailableSeats(availableSeats)
                .withTotalCapacity(totalCapacity)
                .withStartDate(parameters.getStartDate())
                .withEndDate(parameters.getEndDate())
                .build();

    }

    public List<String> findStatisticsFilterValues(String attribute, LocalDate date){
        date = defaultLatestDateIfNull(date);
        return switch (attribute) {
            case CITY ->
                    educationEventEntityRepository.findDistinctByCreatedAt(CityProjection.class, date, Sort.by(EducationEventEntity_.CITY)).stream()
                            .filter(Objects::nonNull)
                            .map(CityProjection::getCity)
                            .filter(StringUtils::isNotEmpty)
                            .toList();
            case STUDY_PACE ->
                    educationEventEntityRepository.findDistinctByCreatedAt(StudyPaceProjection.class, date, Sort.by(EducationEventEntity_.STUDY_PACE)).stream()
                            .filter(Objects::nonNull)
                            .map(StudyPaceProjection::getStudyPace)
                            .filter(StringUtils::isNotEmpty)
                            .toList();
            default -> List.of("def", "ault");
        };
    }

    //exist in educationService too, should probably be done in a better way
    private LocalDate defaultLatestDateIfNull(LocalDate date) {
        return Optional.ofNullable(date).orElseGet(educationEventEntityRepository::findLatestImportDate);
    }

}
