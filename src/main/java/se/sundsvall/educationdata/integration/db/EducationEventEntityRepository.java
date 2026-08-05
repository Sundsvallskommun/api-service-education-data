package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;

@CircuitBreaker(name = "educationEventEntityRepository")
public interface EducationEventEntityRepository extends JpaRepository<EducationEventEntity, String> {
	@Query("SELECT DISTINCT event.educationInfoId FROM EducationEventEntity event")
	Set<String> findAllByDistinctId();
}
