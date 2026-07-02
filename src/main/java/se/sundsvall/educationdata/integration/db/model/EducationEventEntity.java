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
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private String id;

	@Column(name = "title")
	private String title;

	@Column(name = "education_event_id")
	private String educationEventId;

	@Column(name = "education_info_id")
	private String educationInfoId;

	@Column(name = "education_provider_id")
	private String educationProviderId;

	@Column(name = "city")
	private String city;

	@Column(name = "municipality_id")
	private String municipalityId;

	@Column(name = "course_post_url")
	private String coursePostUrl;

	@Column(name = "seats")
	private Integer seats;

	@Column(name = "currency_type")
	private String currencyType;

	@Column(name = "cost")
	private BigDecimal cost;

	@Column(name = "lecture_type")
	private String lectureType;

	@Column(name = "study_pace")
	private String studyPace;

	@Column(name = "language")
	private String languageOfInstructions;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "application_start_date")
	private LocalDate applicationDateStart;

	@Column(name = "application_end_date")
	private LocalDate applicationDateEnd;

	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDate createdAt;

	@Column(name = "outdated_at")
	private LocalDate outdatedAt;

	@Column(name = "deleted")
	private Boolean deleted;

}
