package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEventPageEntity;

@CircuitBreaker(name = "susaEducationEventPageRepository")
public interface SusaEducationEventPageRepository extends JpaRepository<SusaEducationEventPageEntity, String> {
	List<SusaEducationEventPageEntity> findAllByDateCollected(LocalDate dateCollected);

}
