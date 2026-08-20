package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import se.sundsvall.educationdata.integration.db.model.EventCategoryEntity;

@CircuitBreaker(name = "eventCategoryRepository")
public interface EventCategoryRepository extends JpaRepository<EventCategoryEntity, String> {
	@Modifying
	@Query("DELETE FROM EventCategoryEntity e WHERE e.educationEventId IN :eventIds")
	void deleteByEducationEventIdIn(Set<String> eventIds);
}
