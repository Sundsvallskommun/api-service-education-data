package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.EducationInfoListResponse;
import generated.se.sundsvall.susanavet.EducationInfoResponse;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.EducationInfoEntityRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationInfoRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationInfosMapper;
import tools.jackson.databind.ObjectMapper;

@Service
public class EducationInfosService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationInfoRepository infoRepository;
	private final EducationInfoEntityRepository infoEntityRepository;
	private final EducationEventEntityRepository eventEntityRepository;
	private final ObjectMapper objectMapper;
	private final EducationInfosMapper infosMapper;

	public EducationInfosService(SusaNavetIntegration susaNavetIntegration, SusaEducationInfoRepository infoRepository, EducationInfoEntityRepository infoEntityRepository, EducationEventEntityRepository eventEntityRepository, ObjectMapper objectMapper,
		EducationInfosMapper infosMapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.infoRepository = infoRepository;
		this.infoEntityRepository = infoEntityRepository;
		this.eventEntityRepository = eventEntityRepository;
		this.objectMapper = objectMapper;
		this.infosMapper = infosMapper;
	}

	public void savePageInfoJsonTable(int page, int size) {
		var municipalityFilteredIds = eventEntityRepository.findAllByDistinctId();

		var json = susaNavetIntegration.getEducationInfos(page, size);
		infoRepository.save(infosMapper.toZippedInfos(json, page));

		var response = objectMapper.readValue(json, EducationInfoListResponse.class);
		var susaInfos = response.getEducationInfos();

		saveFilteredInfos(susaInfos, municipalityFilteredIds);
	}

	public void saveAllPagesInfoJsonTable(int size) {
		var municipalityFilteredIds = eventEntityRepository.findAllByDistinctId();

		int page = 0;
		var json = susaNavetIntegration.getEducationInfos(page, size);
		var response = objectMapper.readValue(json, EducationInfoListResponse.class);

		var pageInfo = response.getPage();
		var totalPages = (pageInfo == null || pageInfo.getTotalPages() == null) ? 0 : pageInfo.getTotalPages();

		infoRepository.save(infosMapper.toZippedInfos(json, page));
		var susaInfos = response.getEducationInfos();
		saveFilteredInfos(susaInfos, municipalityFilteredIds);

		for (page = 1; page < totalPages; page++) {
			json = susaNavetIntegration.getEducationInfos(page, size);
			infoRepository.save(infosMapper.toZippedInfos(json, page));

			response = objectMapper.readValue(json, EducationInfoListResponse.class);
			susaInfos = response.getEducationInfos();
			saveFilteredInfos(susaInfos, municipalityFilteredIds);
		}
	}

	public void saveFilteredInfos(List<EducationInfoResponse> infos, Set<String> filteredIds) {
		var filtered = infos.stream().filter(i -> i.getContent() != null
			&& filteredIds.contains(i.getContent().getIdentifier())).toList();
		infoEntityRepository.saveAll(infosMapper.toInfoEntities(filtered));
	}
}
