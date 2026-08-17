package se.sundsvall.educationdata.integration.db.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "education_info")
public class EducationInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "education_info_id", length = 64)
	private String educationInfoId;

	@Column(name = "title", length = 255)
	private String title;

	@Column(name = "school_type", length = 32)
	private String schoolType;

	@Column(name = "education_type", columnDefinition = "TEXT")
	private String educationType;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "education_eligibility", columnDefinition = "TEXT")
	private String educationEligibility;

	@Column(name = "recommended_prior_knowledge", columnDefinition = "TEXT")
	private String recommendedPriorKnowledge;

	@Column(name = "credit_type", length = 10)
	private String creditType;

	@Column(name = "credits", length = 10)
	private Double credits;

	@Column(name = "duration", length = 10)
	private String duration;

	@Column(name = "result_is_degree")
	private Boolean resultIsDegree;

	@ElementCollection
	@CollectionTable(name = "education_info_degree",
		joinColumns = @JoinColumn(name = "education_info_id"))
	@Column(name = "degree", length = 255)
	private List<String> degree;

	@Column(name = "expires")
	private LocalDateTime expires;

	@ElementCollection
	@CollectionTable(name = "education_info_subject",
		joinColumns = @JoinColumn(name = "education_info_id"))
	private List<EducationInfoSubject> subjects;

	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDate createdAt;
}
