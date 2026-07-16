package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.EducationEvent;
import generated.se.sundsvall.susanavet.EducationInfoResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProviderEntity;
import se.sundsvall.educationdata.util.Util;

@Component
public class SusaMapper {

	public SusaEducationEventEntity toZippedEvents(byte[] json, int page) {
		return SusaEducationEventEntity.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}

	public SusaEducationInfoEntity toZippedInfos(byte[] json, int page) {
		return SusaEducationInfoEntity.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}

	public SusaEducationProviderEntity toZippedProviders(byte[] json, int page) {
		return SusaEducationProviderEntity.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}

	public List<EducationEventEntity> toEventEntities(List<EducationEvent> events) {
		if (events == null) {
			return List.of();
		}
		return events.stream().map(this::toEventEntity).toList();
	}

	private EducationEventEntity toEventEntity(EducationEvent event) {

		final var titleNode = firstOrNull(event.getTitle() == null ? null : event.getTitle().getStrings());
		final var title = titleNode == null ? null : titleNode.getValue();

		final var courseUrlNode = firstOrNull(event.getUrl() == null ? null : event.getUrl().getUrls());
		final var courseUrl = courseUrlNode == null ? null : courseUrlNode.getValue();

		final var providerId = firstOrNull(event.getProviders());
		final var language = firstOrNull(event.getLanguageOfInstructions());

		final var location = firstOrNull(event.getLocations());
		final var city = location == null ? null : location.getTown();
		final var municipalityId = location == null ? null : location.getAreaCode();

		final var fee = firstOrNull(event.getFees());
		final var currency = fee == null ? null : fee.getCurrency();
		final var cost = (fee == null ? null : fee.getTotalAmount() == null ? null : BigDecimal.valueOf(fee.getTotalAmount()));

		final var execution = event.getExecution();
		final var startDate = execution == null ? null : execution.getStart();
		final var endDate = execution == null ? null : execution.getEnd();

		final var application = event.getApplication();
		final var applicationStart = application == null ? null : application.getFirst();
		final var applicationEnd = application == null ? null : application.getLast();

		final var studyPace = event.getPaceOfStudy();
		final var paceOfStudy = studyPace == null ? null : String.valueOf(studyPace.getPercentage());
		final var lectureType = event.getDistance() != null ? "Distance" : "Classroom";
		// borde läggas till i tabellen
		// final var studyTime = event.getTimeOfStudy();

		return EducationEventEntity.builder()
			.withEducationEventId(event.getIdentifier())
			.withEducationInfoId(event.getEducation())
			.withEducationProviderId(providerId)
			.withTitle(title)
			.withCity(city)
			.withMunicipalityId(municipalityId)
			.withCoursePostUrl(courseUrl)
			.withSeats(event.getPlaces())
			.withCurrencyType(currency)
			.withCost(cost)
			.withLanguageOfInstructions(language)
			.withStartDate(startDate)
			.withEndDate(endDate)
			.withStudyPace(paceOfStudy)
			.withLectureType(lectureType)
			.withApplicationDateStart(applicationStart)
			.withApplicationDateEnd(applicationEnd)
			.withDeleted(Boolean.TRUE.equals(event.getIsCancelled()))
			.build();
	}

	public List<EducationInfoEntity> toInfoEntities(List<EducationInfoResponse> infos) {
		if (infos == null) {
			return List.of();
		}
		return infos.stream().map(this::toInfoEntity).toList();
	}

	private EducationInfoEntity toInfoEntity(EducationInfoResponse info) {
		final var content = info == null ? null : info.getContent();

		if (content == null) {
			return null;
		}

		var infoId = content.getIdentifier();

		var code = content.getType() == null ? null : content.getType().getCode() == null ? null : String.valueOf(content.getType().getCode());

		final var titleNode = firstOrNull(content.getTitle() == null ? null : content.getTitle().getStrings());
		final var title = titleNode == null ? null : titleNode.getValue();

		final var descriptionNode = firstOrNull(content.getDescription() == null ? null : content.getDescription().getStrings());
		final var description = descriptionNode == null ? null : descriptionNode.getValue();

		final var subjects = content.getSubjects().toString();

		final var eligibilityNode = firstOrNull(content.getEligibility() == null ? null : content.getEligibility().getStrings());
		final var eligibility = eligibilityNode == null ? null : eligibilityNode.getValue();

		final var recommendedPriorKnowledgeNode = firstOrNull(content.getRecommendedPriorKnowledge() == null ? null : content.getRecommendedPriorKnowledge().getStrings());
		final var recommendedPriorKnowledge = recommendedPriorKnowledgeNode == null ? null : recommendedPriorKnowledgeNode.getValue();

		final var resultIsDegree = content.getResultIsDegree();
		final var degreeNode = firstOrNull(content.getDegrees() == null ? null : content.getDegrees().getStrings());
		final var degree = degreeNode == null ? null : degreeNode.getValue();

		final var urlNode = firstOrNull(content.getUrl() == null ? null : content.getUrl().getUrls());
		final var contentUrl = urlNode == null ? null : urlNode.getValue();

		final var schoolType = content.getType() == null ? null : content.getType().getCode() == null ? null : content.getType().getCode().toString();
		// borde kanske ändras till att vara double
		final var creditNode = content.getCredits();
		final var credits = creditNode == null ? null : creditNode.getCredits() == null ? null : String.valueOf(creditNode.getCredits());
		final var creditType = creditNode == null ? null : creditNode.getSystem().getCode() == null ? null : String.valueOf(creditNode.getSystem().getCode());

		final var extent = content.getExtent();
		final var duration = extent == null ? null : extent.getLength() == null ? null : String.valueOf(extent.getLength());
		final var expires = content.getExpires() == null ? null : LocalDateTime.parse(content.getExpires());

		final var studentAidEligible = content.getEligibleForStudentAid() == null ? null : String.valueOf(content.getEligibleForStudentAid());

		return EducationInfoEntity.builder()
			.withEducationInfoId(infoId)
			.withTitle(title)
			.withCode(code)
			.withDescription(description)
			.withEducationEligibility(eligibility)
			.withRecommendedPriorKnowledge(recommendedPriorKnowledge)
			.withResultIsDegree(resultIsDegree)
			.withDegree(degree)
			.withContentUrl(contentUrl)
			.withExpires(expires)
			.withSchoolType(schoolType)
			.withCredits(credits)
			.withCreditType(creditType)
			.withDuration(duration)
			.withStudentAidEligibility(studentAidEligible)
			.withSubjects(subjects)
			.withDeleted(false)
			.build();
	}

	private static <V> V firstOrNull(List<V> values) {
		return values == null || values.isEmpty() ? null : values.getFirst();
	}
}
