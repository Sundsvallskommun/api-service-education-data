package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.EventCategoryStagingEntity;

@CircuitBreaker(name = "eventCategoryStagingRepository")
public interface EventCategoryStagingRepository extends JpaRepository<EventCategoryStagingEntity, String> {
}
