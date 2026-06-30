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
@Table(name = "reference_category",
	uniqueConstraints = @UniqueConstraint(
		name = "uq_reference_category_direction",
		columnNames = {
			"category_id", "direction_id"
		}),
	indexes = @Index(
		name = "index_reference_category_direction",
		columnList = "direction_id"))
public class ReferenceCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "category_id")
	private String categoryId;

	@Column(name = "category_name")
	private String categoryName;

	@Column(name = "direction_id")
	private String directionId;

	@Column(name = "direction_name")
	private String directionName;

}
