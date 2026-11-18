package se.sundsvall.educationdata.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.educationdata.integration.db.model.EducationProviderEntity;

@CircuitBreaker(name = "educationProviderEntityRepository")
public interface EducationProviderEntityRepository extends JpaRepository<EducationProviderEntity, String> {

	Optional<EducationProviderEntity> findByEducationProviderIdAndCreatedAt(String educationProviderId, LocalDate createdAt);

	List<EducationProviderEntity> findByEducationProviderIdInAndCreatedAt(Set<String> educationProviderIds, LocalDate createdAt);

	void deleteByCreatedAt(LocalDate createdAt);
}
