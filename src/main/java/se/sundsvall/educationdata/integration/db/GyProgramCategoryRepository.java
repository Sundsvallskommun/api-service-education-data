package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.GyProgramCategory;

@CircuitBreaker(name = "gyProgramCategoryRepository")
public interface GyProgramCategoryRepository extends JpaRepository<GyProgramCategory, String> {
}
