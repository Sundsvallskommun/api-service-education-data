package se.sundsvall.educationdata.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.GyProgramCategory;

public interface GyProgramCategoryRepository extends JpaRepository<GyProgramCategory, Long> {
}
