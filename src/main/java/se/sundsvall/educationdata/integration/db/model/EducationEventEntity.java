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
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "title", length = 100)
	private String title;

	@Column(name = "education_event_id", length = 64)
	private String educationEventId;

	@Column(name = "education_info_id", length = 64)
	private String educationInfoId;

	@Column(name = "education_provider_id", length = 64)
	private String educationProviderId;

	@Column(name = "city", length = 100)
	private String city;

	@Column(name = "municipality_id", length = 6)
	private String municipalityId;

	@Column(name = "course_post_url", length = 255)
	private String coursePostUrl;

	@Column(name = "seats")
	private Integer seats;

	@Column(name = "currency_type", length = 10)
	private String currencyType;

	@Column(name = "cost", length = 10)
	private BigDecimal cost;

	@Column(name = "lecture_type", length = 32)
	private String lectureType;

	@Column(name = "study_pace", length = 10)
	private String studyPace;

	@Column(name = "language", length = 32)
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

	@Column(name = "cancelled")
	private Boolean cancelled;

}
