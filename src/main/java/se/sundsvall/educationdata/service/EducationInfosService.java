package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.EducationEventListResponse;
import generated.se.sundsvall.susanavet.EducationInfoListResponse;
import generated.se.sundsvall.susanavet.EducationInfoResponse;
import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.EducationInfoEntityRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationInfoRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.SusaMapper;
import tools.jackson.databind.ObjectMapper;

@Service
public class EducationInfosService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationInfoRepository infoRepository;
	private final EducationInfoEntityRepository infoEntityRepository;
	private final ObjectMapper objectMapper;
	private final SusaMapper mapper;

	public EducationInfosService(SusaNavetIntegration susaNavetIntegration, SusaEducationInfoRepository infoRepository, EducationInfoEntityRepository infoEntityRepository, ObjectMapper objectMapper, SusaMapper mapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.infoRepository = infoRepository;
		this.infoEntityRepository = infoEntityRepository;
		this.objectMapper = objectMapper;
		this.mapper = mapper;
	}

	public void savePageInfoJsonTable(int page, int size) {
		var json = susaNavetIntegration.getEducationInfos(page, size);
		infoRepository.save(mapper.toZippedInfos(json, page));

		var response = objectMapper.readValue(json, EducationInfoListResponse.class);
		var susaInfo = response.getEducationInfos();

		infoEntityRepository.saveAll(mapper.toInfoEntities(susaInfo));

	}

	public void saveAllPagesInfoJsonTable(int size) {
		int page = 0;
		var json = susaNavetIntegration.getEducationEvents(page, size);
		var response = objectMapper.readValue(json, EducationInfoListResponse.class);

		var pageInfo = response.getPage();
		var totalPages = (pageInfo == null || pageInfo.getTotalPages() == null) ? 0 : pageInfo.getTotalPages();

		infoRepository.save(mapper.toZippedInfos(json, page));
		var susaInfos = response.getEducationInfos();
		infoEntityRepository.saveAll(mapper.toInfoEntities(susaInfos));

		for (page = 1; page < totalPages; page++) {
			json = susaNavetIntegration.getEducationEvents(page, size);
			infoRepository.save(mapper.toZippedInfos(json, page));

			susaInfos = response.getEducationInfos();
			infoEntityRepository.saveAll(mapper.toInfoEntities(susaInfos));
		}
	}
}
