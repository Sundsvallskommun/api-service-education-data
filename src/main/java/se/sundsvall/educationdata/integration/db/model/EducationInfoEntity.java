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
	@Column(name = "id")
	private String id;

	@Column(name = "education_info_id")
	private String educationInfoId;

	@Column(name = "title")
	private String title;

	@Column(name = "school_type")
	private String schoolType;

	// Ex. program or course
	@Column(name = "education_type")
	private String educationType;

	@Column(name = "code")
	private String code;

	@Column(name = "description")
	private String description;

	@Column(name = "education_eligibility")
	private String educationEligibility;

	@Column(name = "recommended_prior_knowledge")
	private String recommendedPriorKnowledge;

	@Column(name = "credit_type")
	private String creditType;

	@Column(name = "credits")
	private String credits;

	@Column(name = "duration")
	private String duration;

	@Column(name = "result_is_degree")
	private Boolean resultIsDegree;

	@Column(name = "degree")
	private String degree;

	@Column(name = "content_url")
	private String contentUrl;

	@Column(name = "expires")
	private LocalDateTime expires;

	@Column(name = "student_aid_eligibility")
	private String studentAidEligibility;

	@Column(name = "subjects")
	private String subjects;

	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDate createdAt;

	@Column(name = "outdated_at")
	private LocalDate outdatedAt;

	@Column(name = "deleted")
	private Boolean deleted;
}
