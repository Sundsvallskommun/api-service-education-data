package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;

@CircuitBreaker(name = "referenceCategoryRepository")
public interface ReferenceCategoryRepository extends JpaRepository<ReferenceCategoryEntity, String> {

	@Query("SELECT DISTINCT r.directionId FROM ReferenceCategoryEntity r")
	Set<String> findDistinctDirectionIds();

	Optional<ReferenceCategoryEntity> findByCategoryIdAndDirectionId(String categoryId, String directionId);
}
