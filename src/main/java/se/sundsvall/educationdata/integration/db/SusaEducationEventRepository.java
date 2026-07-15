package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEventEntity;

@CircuitBreaker(name = "susaEducationEventRepository")
public interface SusaEducationEventRepository extends JpaRepository<SusaEducationEventEntity, String> {
}
