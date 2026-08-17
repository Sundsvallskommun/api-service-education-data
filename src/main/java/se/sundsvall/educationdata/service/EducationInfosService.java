package se.sundsvall.educationdata.service;

import generated.se.sundsvall.susanavet.EducationInfoListResponse;
import generated.se.sundsvall.susanavet.EducationInfoResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.EducationInfoEntityRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationInfoPageRepository;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegration;
import se.sundsvall.educationdata.service.mapper.EducationInfosMapper;
import se.sundsvall.educationdata.util.Util;
import tools.jackson.databind.ObjectMapper;

import static java.util.Objects.nonNull;

@Service
public class EducationInfosService {
	private final SusaNavetIntegration susaNavetIntegration;
	private final SusaEducationInfoPageRepository susaEducationInfoPageRepository;
	private final EducationInfoEntityRepository educationInfoEntityRepository;
	private final EducationEventEntityRepository educationEventEntityRepository;
	private final ObjectMapper objectMapper;
	private final EducationInfosMapper educationInfosMapper;

	public EducationInfosService(SusaNavetIntegration susaNavetIntegration, SusaEducationInfoPageRepository susaEducationInfoPageRepository, EducationInfoEntityRepository educationInfoEntityRepository,
		EducationEventEntityRepository educationEventEntityRepository,
		ObjectMapper objectMapper,
		EducationInfosMapper educationInfosMapper) {
		this.susaNavetIntegration = susaNavetIntegration;
		this.susaEducationInfoPageRepository = susaEducationInfoPageRepository;
		this.educationInfoEntityRepository = educationInfoEntityRepository;
		this.educationEventEntityRepository = educationEventEntityRepository;
		this.objectMapper = objectMapper;
		this.educationInfosMapper = educationInfosMapper;
	}

	@Transactional
	public void saveAllPagesInfoJsonTable() {
		var municipalityFilteredIds = educationEventEntityRepository.getDistinctEducationInfoId();

		int page = 0;
		var json = susaNavetIntegration.getEducationInfos(page);
		var response = objectMapper.readValue(json, EducationInfoListResponse.class);

		var pageInfo = response.getPage();
		var totalPages = (pageInfo == null || pageInfo.getTotalPages() == null) ? 0 : pageInfo.getTotalPages();

		susaEducationInfoPageRepository.save(educationInfosMapper.toZippedInfos(json, page));
		var susaInfos = response.getEducationInfos();
		saveFilteredInfos(susaInfos, municipalityFilteredIds);

		for (page = 1; page < totalPages; page++) {
			json = susaNavetIntegration.getEducationInfos(page);
			susaEducationInfoPageRepository.save(educationInfosMapper.toZippedInfos(json, page));

			response = objectMapper.readValue(json, EducationInfoListResponse.class);
			susaInfos = response.getEducationInfos();
			saveFilteredInfos(susaInfos, municipalityFilteredIds);
		}
	}

	@Transactional
	public void createInfoEntitiesFromJson() {
		var jsonPageList = susaEducationInfoPageRepository.findAllByDateCollected(LocalDate.now(ZoneId.systemDefault()));
		var municipalityFilteredIds = educationEventEntityRepository.getDistinctEducationInfoId();

		for (var page : jsonPageList) {

			var json = Util.unzip(page.getJsonBody());
			var response = objectMapper.readValue(json, EducationInfoListResponse.class);
			saveFilteredInfos(response.getEducationInfos(), municipalityFilteredIds);
		}
	}

	public void saveFilteredInfos(List<EducationInfoResponse> infos, Set<String> filteredIds) {
		var filtered = infos.stream()
			.filter(info -> nonNull(info.getContent()))
			.filter(info -> filteredIds.contains(info.getContent().getIdentifier()))
			.toList();

		educationInfoEntityRepository.saveAll(educationInfosMapper.toInfoEntities(filtered));
	}
}
