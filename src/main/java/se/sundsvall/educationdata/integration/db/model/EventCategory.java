package se.sundsvall.educationdata.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Entity
@Table(name = "event_category",
	uniqueConstraints = @UniqueConstraint(name = "uq_event_category", columnNames = {
		"education_event_id", "direction_id"
	}),
	indexes = @Index(name = "idx_event_category_event", columnList = "education_event_id"))
public class EventCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private String id;

	@Column(name = "education_event_id")
	private String educationEventId;

	@Column(name = "direction_id")
	private String directionId;
}
