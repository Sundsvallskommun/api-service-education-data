package se.sundsvall.educationdata.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfo;

public interface SusaEducationInfoRepository extends JpaRepository<SusaEducationInfo, Long> {
}
