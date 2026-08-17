package se.sundsvall.educationdata.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegration;

import static se.sundsvall.dept44.problem.Problem.badGateway;

@Service
public class PlannedEducationService {

	private final PlannedEducationIntegration plannedEducationIntegration;
	private final ReferenceCategoryRepository referenceCategoryRepository;

	public PlannedEducationService(PlannedEducationIntegration plannedEducationIntegration, ReferenceCategoryRepository referenceCategoryRepository) {
		this.plannedEducationIntegration = plannedEducationIntegration;
		this.referenceCategoryRepository = referenceCategoryRepository;
	}

	@Transactional
	public void importReferenceCategories() {
		var categories = plannedEducationIntegration.getAllReferenceCategories();

		if (categories.isEmpty()) {
			throw badGateway(
				"No content");
		}

		referenceCategoryRepository.deleteAllInBatch();
		referenceCategoryRepository.saveAll(categories);
	}
}
