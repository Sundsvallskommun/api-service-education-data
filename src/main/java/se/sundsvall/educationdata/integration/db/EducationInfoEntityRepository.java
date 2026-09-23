package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;

@CircuitBreaker(name = "educationInfoEntityRepository")
public interface EducationInfoEntityRepository extends JpaRepository<EducationInfoEntity, String> {
	Optional<EducationInfoEntity> findByEducationInfoIdAndCreatedAt(String educationInfoId, LocalDate createdAt);

	List<EducationInfoEntity> findByEducationInfoIdInAndCreatedAt(Set<String> educationInfoIds, LocalDate createdAt);

	void deleteByCreatedAt(LocalDate createdAt);

}
