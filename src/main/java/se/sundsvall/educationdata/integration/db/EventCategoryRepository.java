package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import se.sundsvall.educationdata.integration.db.model.EventCategoryEntity;

@CircuitBreaker(name = "eventCategoryRepository")
public interface EventCategoryRepository extends JpaRepository<EventCategoryEntity, String> {
	@Modifying
	@Query("""
		DELETE FROM EventCategoryEntity e
				WHERE e.educationEventId IN (SELECT s.educationEventId FROM EventCategoryStagingEntity s)
		""")
	void deleteEventRelationsForStagedEventIds();

	@Modifying
	@Query("""
		INSERT INTO EventCategoryEntity (id, educationEventId, directionId)
		SELECT s.id, s.educationEventId, s.directionId
		FROM EventCategoryStagingEntity s
		""")
	void saveStagedToEventCategory();
}
