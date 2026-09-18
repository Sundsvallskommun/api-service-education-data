package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;

@CircuitBreaker(name = "educationEventEntityRepository")
public interface EducationEventEntityRepository extends JpaRepository<EducationEventEntity, String>, JpaSpecificationExecutor<EducationEventEntity> {
	@Query("SELECT DISTINCT event.educationInfoId FROM EducationEventEntity event")
	Set<String> getDistinctEducationInfoId();

	Optional<EducationEventEntity> findByMunicipalityIdAndEducationEventIdAndCreatedAt(String municipalityId, String educationEventId, LocalDate date);

	@Query("SELECT MAX(e.createdAt) FROM EducationEventEntity e")
	LocalDate findLatestImportDate();

	<P> List<P> findDistinctByMunicipalityIdAndCreatedAt(Class<P> type, String municipalityId, LocalDate createdAt, Sort sort);
}
