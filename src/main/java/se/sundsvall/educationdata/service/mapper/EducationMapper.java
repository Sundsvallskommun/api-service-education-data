package se.sundsvall.educationdata.service.mapper;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.educationdata.api.model.Education;
import se.sundsvall.educationdata.api.model.PagedEducationResponse;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.EducationProviderEntity;

import static java.util.Optional.ofNullable;

@Component
public class EducationMapper {

	public Education toEducation(final EducationEventEntity educationEvent, final EducationInfoEntity educationInfo, final EducationProviderEntity educationProvider) {
		final var builder = Education.builder()
			.withId(educationEvent.getEducationEventId())
			.withEventTitle(educationEvent.getTitle())
			.withStudyLocation(educationEvent.getCity())
			.withStudySite(educationEvent.getStudySite())
			.withMunicipalityId(educationEvent.getMunicipalityId())
			.withContractor(educationEvent.getContractor())
			.withUrl(educationEvent.getCoursePostUrl())
			.withNumberOfSeats(educationEvent.getSeats())
			.withCost(educationEvent.getCost())
			.withCurrency(educationEvent.getCurrencyType())
			.withLectureType(educationEvent.getLectureType())
			.withStudyPace(educationEvent.getStudyPace())
			.withLanguageOfInstructions(educationEvent.getLanguageOfInstructions())
			.withStart(educationEvent.getStartDate())
			.withEnd(educationEvent.getEndDate())
			.withStartType(educationEvent.getStartType())
			.withEarliestApplication(educationEvent.getApplicationDateStart())
			.withLatestApplication(educationEvent.getApplicationDateEnd())
			.withCancelled(educationEvent.getCancelled());

		if (educationInfo != null) {
			builder.withCode(educationInfo.getCode())
				.withTitle(educationInfo.getTitle())
				.withSchoolType(educationInfo.getSchoolType())
				.withEducationType(educationInfo.getEducationType())
				.withInformation(educationInfo.getDescription())
				.withCredits(educationInfo.getCredits())
				.withCreditType(educationInfo.getCreditType())
				.withDuration(educationInfo.getDuration())
				.withDurationUnit(educationInfo.getDurationUnit())
				.withEligibility(educationInfo.getEducationEligibility())
				.withRecommendedPriorKnowledge(educationInfo.getRecommendedPriorKnowledge())
				.withDegree(educationInfo.getDegree());
		}

		if (educationProvider != null) {
			builder.withProvider(educationProvider.getName())
				.withProviderUrl(educationProvider.getUrl());
		}

		return builder.build();
	}

	public PagedEducationResponse toPagedEducationResponse(
		final Page<EducationEventEntity> page, final Map<String, EducationInfoEntity> infosById, final Map<String, EducationProviderEntity> providersById) {
		return PagedEducationResponse.builder()
			.withEducations(page.getContent().stream()
				.map(event -> toEducation(event,
					ofNullable(event.getEducationInfoId()).map(infosById::get).orElse(null),
					ofNullable(event.getEducationProviderId()).map(providersById::get).orElse(null))).toList()).withMetaData(PagingAndSortingMetaData.create()
						.withPageData(page))
			.build();
	}
}
