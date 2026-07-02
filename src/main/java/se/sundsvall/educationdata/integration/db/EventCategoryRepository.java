package se.sundsvall.educationdata.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.EventCategory;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
}
