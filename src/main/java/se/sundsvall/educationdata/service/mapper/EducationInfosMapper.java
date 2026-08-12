package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.CConfiguration;
import generated.se.sundsvall.susanavet.CCredits;
import generated.se.sundsvall.susanavet.CSchoolType;
import generated.se.sundsvall.susanavet.CodeSubject;
import generated.se.sundsvall.susanavet.ConfigurationCode;
import generated.se.sundsvall.susanavet.Credits;
import generated.se.sundsvall.susanavet.EducationInfo;
import generated.se.sundsvall.susanavet.EducationInfoResponse;
import generated.se.sundsvall.susanavet.LangString;
import generated.se.sundsvall.susanavet.LangStringNode;
import generated.se.sundsvall.susanavet.TimeLength;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoSubject;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfoPageEntity;
import se.sundsvall.educationdata.util.Util;

import static java.util.Optional.ofNullable;
import static se.sundsvall.educationdata.service.mapper.MapperUtil.firstOrNull;
import static se.sundsvall.educationdata.service.mapper.SusaNavetMapper.firstStringValue;

@Component
public class EducationInfosMapper {

	public SusaEducationInfoPageEntity toZippedInfos(byte[] json, int page) {
		return SusaEducationInfoPageEntity.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}

	public List<EducationInfoEntity> toInfoEntities(List<EducationInfoResponse> infos) {
		if (infos == null) {
			return List.of();
		}
		return infos.stream().map(response -> response == null ? null : response.getContent())
			.filter(Objects::nonNull)
			.map(this::toInfoEntity)
			.filter(Objects::nonNull)
			.toList();
	}

	private EducationInfoEntity toInfoEntity(EducationInfo content) {

		final var creditNode = content.getCredits();

		return EducationInfoEntity.builder()
			.withEducationInfoId(content.getIdentifier())
			.withResultIsDegree(content.getResultIsDegree())
			.withExpires(content.getExpires())
			.withTitle(firstStringValue(content.getTitle()))
			.withDescription(firstStringValue(content.getDescription()))
			.withEducationEligibility(firstStringValue(content.getEligibility()))
			.withRecommendedPriorKnowledge(firstStringValue(content.getRecommendedPriorKnowledge()))
			.withDegree(toDegree(content.getDegrees()))
			.withSubjects(toSubjects(content.getSubjects()))
			.withCredits(ofNullable(creditNode).map(Credits::getCredits).orElse(null))
			.withCreditType(ofNullable(creditNode).map(Credits::getSystem).map(CCredits::getCode).map(String::valueOf).orElse(null))
			.withSchoolType(ofNullable(content.getType()).map(CSchoolType::getCode).map(String::valueOf).orElse(null))
			.withEducationType(ofNullable(content.getConfiguration()).map(CConfiguration::getCode).map(ConfigurationCode::getValue).orElse(null))
			.withDuration(ofNullable(content.getExtent()).map(TimeLength::getLength).map(String::valueOf).orElse(null))
			.build();
	}

	private List<String> toDegree(final List<LangString> degrees) {
		return Optional.ofNullable(degrees).orElse(Collections.emptyList()).stream()
			.map(langString -> firstOrNull(langString.getStrings()))
			.map(LangStringNode::getValue)
			.filter(Objects::nonNull)
			.toList();
	}

	private static List<EducationInfoSubject> toSubjects(final List<CodeSubject> subjects) {
		return ofNullable(subjects).orElse(List.of()).stream()
			.map(subject -> new EducationInfoSubject(subject.getType(), subject.getCode()))
			.toList();
	}
}
