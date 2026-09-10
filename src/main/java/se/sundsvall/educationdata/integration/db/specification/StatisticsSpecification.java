package se.sundsvall.educationdata.integration.db.specification;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.educationdata.api.model.StatisticsParameters;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;

import java.time.LocalDate;

import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.CREATED_AT;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.END_DATE;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.MUNICIPALITY_ID;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.START_DATE;


public interface StatisticsSpecification {

    static Specification<EducationEventEntity> createSpecification(final StatisticsParameters parameters, final LocalDate date) {
        return Specification.allOf(
            withCreated(date),
            withPeriod(parameters.getStartDate(), parameters.getEndDate()));
    }

    static Specification<EducationEventEntity> withCreated(final LocalDate date){
        return SpecificationBuilder.buildEqualFilter(CREATED_AT, date);
    }

    static Specification<EducationEventEntity> withPeriod(final LocalDate from, final LocalDate to){
        return SpecificationBuilder.withinPeriod(from, to);
}

}
