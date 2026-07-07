package se.sundsvall.educationdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.SusaEducationInfoRepository;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfo;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;

@Service
public class EducationInfosService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationInfoRepository infoRepository;
	private final ObjectMapper objectMapper;

	public EducationInfosService(SusaNavetIntegration susaNavetIntegration, SusaEducationInfoRepository infoRepository, ObjectMapper objectMapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.infoRepository = infoRepository;
		this.objectMapper = objectMapper;
	}

	public void savePageInfoJsonTable(int page, int size) {
		String json = susaNavetIntegration.getEducationInfos(page, size);
		if (json == null) {
			throw new IllegalStateException(
				"Empty body for page %d".formatted(page));
		}

		var raw = SusaEducationInfo.builder()
			.withJsonBody(json)
			.withDateCollected(LocalDate.now())
			.build();
		infoRepository.save(raw);
	}

	public void saveAllPagesInfoJsonTable(int size) throws JsonProcessingException {
		int page = 0;
		int totalPages;

		do {
			String json = susaNavetIntegration.getEducationInfos(page, size);
			if (json == null) {
				throw new IllegalStateException(
					"Empty body for page %d".formatted(page));
			}

			totalPages = objectMapper.readTree(json)
				.path("page")
				.path("totalPages").asInt();

			var raw = SusaEducationInfo.builder()
				.withJsonBody(json)
				.withDateCollected(LocalDate.now())
				.build();
			infoRepository.save(raw);

			page++;
		} while (page < totalPages);
	}
}
