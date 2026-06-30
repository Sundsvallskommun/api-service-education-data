package se.sundsvall.educationdata.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "education_event")
public class EducationEventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "education_event_id")
	private String educationEventId;

	@Column(name = "identifier")
	private String educationInfoId;

	@Column(name = "name")
	private String name;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "city")
	private String city;

	@Column(name = "visiting_address")
	private String visitingAddress;

	@Column(name = "municipality_id")
	private String municipalityId;

	@Column(name = "school_unit_name")
	private String schoolUnitName;

	@Column(name = "provider_id")
	private String providerId;

	@Column(name = "provider_url")
	private String providerUrl;

	@Column(name = "course_post_url")
	private String coursePostUrl;

	@Column(name = "seats")
	private Integer seats;

	@Column(name = "currency_type")
	private String currencyType;

	@Column(name = "cost")
	private BigDecimal cost;

	@Column(name = "student_aid_eligible")
	private Boolean studentAidEligible;

	@Column(name = "lecture_type")
	private String lectureType; // distance/atLocation/hybrid

	@Column(name = "study_pace")
	private String studyPace;

	@Column(name = "credit_type")
	private String creditType;

	@Column(name = "credits")
	private String credits;

	@Column(name = "language")
	private String languageOfInstructions;

	@Column(name = "recommended_knowledge")
	private String recommendedKnowledge;

	@Column(name = "requirements")
	private String requirements;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "application_start_date")
	private LocalDate applicationDateStart;

	@Column(name = "application_end_date")
	private LocalDate applicationDateEnd;

	@Column(name = "subject_code")
	private String subjectCode;

	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDate createdAt;

	@Column(name = "outdated_at")
	private LocalDate outdatedAt;

	@Column(name = "deleted")
	private Boolean deleted;

}
