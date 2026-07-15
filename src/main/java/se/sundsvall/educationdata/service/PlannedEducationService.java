package se.sundsvall.educationdata.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;
import se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegration;

import static se.sundsvall.dept44.problem.Problem.badGateway;

@Service
public class PlannedEducationService {

	private final PlannedEducationIntegration integration;
	private final ReferenceCategoryRepository referenceCategoryRepository;

	public PlannedEducationService(PlannedEducationIntegration integration, ReferenceCategoryRepository referenceCategoryRepository) {
		this.integration = integration;
		this.referenceCategoryRepository = referenceCategoryRepository;
	}

	@Transactional
	public void getCategoryInfo() {
		List<ReferenceCategoryEntity> categories = integration.getAllAreas();

		if (categories.isEmpty()) {
			throw badGateway(
				"No content");
		}

		referenceCategoryRepository.deleteAllInBatch();
		referenceCategoryRepository.saveAll(categories);
	}
}
