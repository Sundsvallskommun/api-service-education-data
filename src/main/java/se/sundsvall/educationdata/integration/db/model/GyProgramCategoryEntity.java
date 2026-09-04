package se.sundsvall.educationdata.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "gy_program_category",
	uniqueConstraints = @UniqueConstraint(name = "uq_gy_program_code", columnNames = "program_code"))
public class GyProgramCategoryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "program_code", length = 64)
	private String programCode;

	@Column(name = "program_name", length = 100)
	private String programName;

	@Column(name = "category", length = 32)
	private String category;

	@Column(name = "vocational")
	private Boolean vocational;
}
