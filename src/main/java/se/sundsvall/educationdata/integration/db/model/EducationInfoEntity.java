package se.sundsvall.educationdata.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

	@Column(name = "title", length = 100)
	private String title;

	@Column(name = "school_type", length = 32)
	private String schoolType;

	// Ex. program or course
	@Column(name = "education_type", length = 32)
	private String educationType;

	@Column(name = "code", length = 50)
	private String code;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "education_eligibility", columnDefinition = "TEXT")
	private String educationEligibility;

	@Column(name = "recommended_prior_knowledge", columnDefinition = "TEXT")
	private String recommendedPriorKnowledge;

	@Column(name = "credit_type", length = 10)
	private String creditType;

	@Column(name = "credits", length = 10)
	private String credits;

	@Column(name = "duration", length = 10)
	private String duration;

	@Column(name = "result_is_degree")
	private Boolean resultIsDegree;

	@Column(name = "degree", length = 50)
	private String degree;

	@Column(name = "content_url")
	private String contentUrl;

	@Column(name = "expires")
	private LocalDateTime expires;

	@Column(name = "student_aid_eligibility", length = 10)
	private String studentAidEligibility;

	@Column(name = "subjects", length = 32)
	private String subjects;

	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDate createdAt;

	@Column(name = "outdated_at")
	private LocalDate outdatedAt;

	@Column(name = "deleted")
	private Boolean deleted;
}
