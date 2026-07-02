package se.sundsvall.educationdata.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;

public interface EducationInfoEntityRepository extends JpaRepository<EducationInfoEntity, Long> {
}
