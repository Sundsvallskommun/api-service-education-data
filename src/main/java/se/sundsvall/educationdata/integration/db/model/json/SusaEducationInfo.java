package se.sundsvall.educationdata.integration.db.model.json;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Entity
@Table(name = "susa_education_info_raw")
public class SusaEducationInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private String id;

	@Column(name = "json_body", columnDefinition = "LONGTEXT", nullable = false, updatable = false)
	private String jsonBody;

	@Column(name = "date_collected", nullable = false, updatable = false)
	private LocalDate dateCollected;

}
