package se.sundsvall.educationdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategory;
import se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegration;

@Service
public class PlannedEducationService {

	private final PlannedEducationIntegration integration;
	private final ReferenceCategoryRepository referenceCategoryRepository;

	public PlannedEducationService(PlannedEducationIntegration integration, ReferenceCategoryRepository referenceCategoryRepository) {
		this.integration = integration;
		this.referenceCategoryRepository = referenceCategoryRepository;
	}

	@Transactional
	public void getCategoryInfo() throws JsonProcessingException {
		List<ReferenceCategory> categories = integration.getAllAreas();

		if (categories.isEmpty())
			throw Problem.valueOf(HttpStatus.BAD_GATEWAY,
				"No content");

		referenceCategoryRepository.deleteAllInBatch();
		referenceCategoryRepository.saveAll(categories);
	}
}
