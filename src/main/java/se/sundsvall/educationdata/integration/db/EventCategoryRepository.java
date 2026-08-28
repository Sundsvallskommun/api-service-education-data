package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import se.sundsvall.educationdata.integration.db.model.EventCategoryEntity;

@CircuitBreaker(name = "eventCategoryRepository")
public interface EventCategoryRepository extends JpaRepository<EventCategoryEntity, String> {
	@Modifying
	@Query(value = """
		DELETE ec
		FROM event_category ec
		INNER JOIN (
		    SELECT DISTINCT education_event_id
		    FROM event_category_staging
		) staged
		    ON staged.education_event_id = ec.education_event_id
		""", nativeQuery = true)
	void deleteEventRelationsForStagedEventIds();

	@Modifying
	@Query(value = """
		INSERT INTO event_category (id, education_event_id, direction_id)
		SELECT UUID(), s.education_event_id, s.direction_id
		FROM event_category_staging s
		GROUP BY s.education_event_id, s.direction_id
		""", nativeQuery = true)
	void saveStagedToEventCategory();
}
