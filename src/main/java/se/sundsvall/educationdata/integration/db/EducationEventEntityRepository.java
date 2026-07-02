package se.sundsvall.educationdata.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;

public interface EducationEventEntityRepository extends JpaRepository<EducationEventEntity, Long> {

}
