package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfoPageEntity;

@CircuitBreaker(name = "susaEducationInfoPageRepository")
public interface SusaEducationInfoPageRepository extends JpaRepository<SusaEducationInfoPageEntity, String> {
	List<SusaEducationInfoPageEntity> findAllByDateCollected(LocalDate dateCollected);

}
