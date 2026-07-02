package se.sundsvall.educationdata.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProvider;

public interface SusaEducationProviderRepository extends JpaRepository<SusaEducationProvider, Long> {
}
