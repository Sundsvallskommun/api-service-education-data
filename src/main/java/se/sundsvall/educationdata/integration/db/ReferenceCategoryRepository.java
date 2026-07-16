package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;

@CircuitBreaker(name = "referenceCategoryRepository")
public interface ReferenceCategoryRepository extends JpaRepository<ReferenceCategoryEntity, String> {
}
