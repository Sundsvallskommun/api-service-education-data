package se.sundsvall.educationdata.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Entity
@Table(name = "education_provider")
public class EducationProviderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "education_provider_id", length = 64)
	private String educationProviderId;

	@Column(name = "name", length = 255)
	private String name;

	@Column(name = "school_name", length = 255)
	private String schoolName;

	@Column(name = "url", length = 255)
	private String url;

	@Column(name = "city", length = 100)
	private String city;

	@Column(name = "municipality_id", length = 6)
	private String municipalityId;

	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDate createdAt;
}
