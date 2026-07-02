package se.sundsvall.educationdata.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategory;

public interface ReferenceCategoryRepository extends JpaRepository<ReferenceCategory, Long> {
}
