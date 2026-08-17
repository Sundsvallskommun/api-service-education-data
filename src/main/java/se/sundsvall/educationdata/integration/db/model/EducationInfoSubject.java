package se.sundsvall.educationdata.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationInfoSubject {
	@Column(name = "subject_type", length = 32)
	private String type;

	@Column(name = "subject_code", length = 32)
	private String code;
}
