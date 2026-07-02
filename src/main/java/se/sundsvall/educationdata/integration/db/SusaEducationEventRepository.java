package se.sundsvall.educationdata.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEvent;

public interface SusaEducationEventRepository extends JpaRepository<SusaEducationEvent, Long> {
}
