package se.sundsvall.educationdata.service.mapper;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.educationdata.api.model.Education;
import se.sundsvall.educationdata.api.model.PagedEducationResponse;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;

@Component
public class EducationMapper {

	public Education toEducation(final EducationEventEntity educationEvent, final EducationInfoEntity educationInfo) {
		final var builder = Education.builder()
			.withId(educationEvent.getEducationEventId())
			.withName(educationEvent.getTitle())
			.withStudyLocation(educationEvent.getCity())
			.withMunicipalityId(educationEvent.getMunicipalityId())
			.withUrl(educationEvent.getCoursePostUrl())
			.withNumberOfSeats(educationEvent.getSeats())
			.withCost(educationEvent.getCost())
			.withCurrency(educationEvent.getCurrencyType())
			.withLectureType(educationEvent.getLectureType())
			.withStudyPace(educationEvent.getStudyPace())
			.withLanguageOfInstructions(educationEvent.getLanguageOfInstructions())
			.withStart(educationEvent.getStartDate())
			.withEnd(educationEvent.getEndDate())
			.withEarliestApplication(educationEvent.getApplicationDateStart())
			.withLatestApplication(educationEvent.getApplicationDateEnd())
			.withCancelled(educationEvent.getCancelled());

		if (educationInfo != null) {
			builder.withCode(educationInfo.getCode())
				.withSchoolType(educationInfo.getSchoolType())
				.withEducationType(educationInfo.getEducationType())
				.withInformation(educationInfo.getDescription())
				.withCredits(educationInfo.getCredits())
				.withCreditType(educationInfo.getCreditType())
				.withDuration(educationInfo.getDuration())
				.withEligibility(educationInfo.getEducationEligibility())
				.withRecommendedPriorKnowledge(educationInfo.getRecommendedPriorKnowledge())
				.withDegree(educationInfo.getDegree());
		}

		return builder.build();
	}

	public PagedEducationResponse toPagedEducationResponse(
		final Page<EducationEventEntity> page, final Map<String, EducationInfoEntity> infosById) {
		return PagedEducationResponse.builder()
			.withEducations(page.getContent().stream()
				.map(event -> toEducation(event, infosById.get(event.getEducationInfoId())))
				.toList()).withMetaData(PagingAndSortingMetaData.create()
					.withPageData(page))
			.build();
	}
}
