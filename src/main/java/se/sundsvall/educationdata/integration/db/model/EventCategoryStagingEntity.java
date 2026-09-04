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

/**
 * Temporary table for holding relations between event and directions during a refresh.
 * Rows are staged per direction as they are fetched and then gets merged into
 * EventCategoryEntity once every direction has been fetched successfully. This means a failed run cant
 * partially update the data, with an updated event having lost the relation to its previous direction
 * without getting assigned the new ones.
 * <p>
 * A table is used rather than an in-memory list so that the relations won't have to be buffered until
 * all directions are fetched.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Entity
@Table(name = "event_category_staging",
	uniqueConstraints = @UniqueConstraint(name = "uq_event_category_staging", columnNames = {
		"education_event_id", "direction_id"
	}),
	indexes = @Index(name = "idx_event_category_staging_event", columnList = "education_event_id"))
public class EventCategoryStagingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "education_event_id", length = 64)
	private String educationEventId;

	@Column(name = "direction_id", length = 64)
	private String directionId;
}
