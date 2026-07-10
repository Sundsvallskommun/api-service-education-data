package se.sundsvall.educationdata.integration.db.model.json;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "susa_education_event_raw")
public class SusaEducationEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", length = 36)
	private String id;

	@Lob
	@Column(name = "json_body", columnDefinition = "LONGBLOB", nullable = false, updatable = false)
	private byte[] jsonBody;

	@Column(name = "page", length = 6, nullable = false, updatable = false)
	private Integer page;

	@Column(name = "date_collected", nullable = false, updatable = false)
	private LocalDate dateCollected;

}
