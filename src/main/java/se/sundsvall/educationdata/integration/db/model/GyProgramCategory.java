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
public class GyProgramCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "program_code")
	private String programCode;

	@Column(name = "program_name")
	private String programName;

	@Column(name = "category")
	private String category; // Lista med flera?

	@Column(name = "vocational")
	private Boolean vocational;
}
