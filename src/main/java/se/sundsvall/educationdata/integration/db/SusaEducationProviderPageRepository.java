package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProviderPageEntity;

@CircuitBreaker(name = "susaEducationProviderPageRepository")
public interface SusaEducationProviderPageRepository extends JpaRepository<SusaEducationProviderPageEntity, String> {
	void deleteByDateCollected(LocalDate date);
}
