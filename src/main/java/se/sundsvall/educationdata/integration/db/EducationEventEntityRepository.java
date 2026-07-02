package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;

@Repository
@CircuitBreaker(name = "educationEventEntityRepository")
public interface EducationEventEntityRepository extends JpaRepository<EducationEventEntity, String> {
}
