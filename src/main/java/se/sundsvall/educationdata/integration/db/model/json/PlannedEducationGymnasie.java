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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "planned_education_gymnasie_raw")
public class PlannedEducationGymnasie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "json_body", columnDefinition = "TEXT", nullable = false, updatable = false)
	private String jsonBody;

	@Column(name = "date_collected", nullable = false, updatable = false)
	private LocalDate dateCollected;

}
