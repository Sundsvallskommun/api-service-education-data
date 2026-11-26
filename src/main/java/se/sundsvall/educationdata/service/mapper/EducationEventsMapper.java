package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.Address;
import generated.se.sundsvall.susanavet.Application;
import generated.se.sundsvall.susanavet.CExecutionCondition;
import generated.se.sundsvall.susanavet.DistanceEducation;
import generated.se.sundsvall.susanavet.EducationEvent;
import generated.se.sundsvall.susanavet.Execution;
import generated.se.sundsvall.susanavet.ExecutionConditionCode;
import generated.se.sundsvall.susanavet.ExtensionE;
import generated.se.sundsvall.susanavet.Fee;
import generated.se.sundsvall.susanavet.KVEventExtension;
import generated.se.sundsvall.susanavet.PaceOfStudy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEventPageEntity;
import se.sundsvall.educationdata.util.Util;

import static java.util.Optional.ofNullable;
import static se.sundsvall.educationdata.service.mapper.MapperUtil.firstOrNull;
import static se.sundsvall.educationdata.service.mapper.SusaNavetMapper.firstStringValue;
import static se.sundsvall.educationdata.service.mapper.SusaNavetMapper.firstUrlValue;

@Component
public class EducationEventsMapper {

	public SusaEducationEventPageEntity toZippedEvents(byte[] json, int page) {
		return SusaEducationEventPageEntity.builder()
			.withJsonBody(Util.zip(json))
			.withPage(page)
			.withDateCollected(LocalDate.now(ZoneId.systemDefault()))
			.build();
	}

	public List<EducationEventEntity> toEventEntities(List<EducationEvent> events, final Set<String> municipalityIds) {
		return Optional.ofNullable(events).orElse(Collections.emptyList()).stream()
			.filter(Objects::nonNull)
			.map(event -> toEventEntity(event, municipalityIds))
			.toList();
	}

	private EducationEventEntity toEventEntity(final EducationEvent event, final Set<String> municipalityIds) {

		final var location = toLocation(event.getLocations(), municipalityIds);
		final var fee = firstOrNull(event.getFees());
		final var execution = event.getExecution();
		final var application = event.getApplication();

		return EducationEventEntity.builder()
			.withEducationEventId(event.getIdentifier())
			.withEducationInfoId(event.getEducation())
			.withEducationProviderId(firstOrNull(event.getProviders()))
			.withContractor(toContractor(event.getExtensions()))
			.withTitle(firstStringValue(event.getTitle()))
			.withCity(ofNullable(location).map(Address::getTown).orElse(null))
			.withStudySite(ofNullable(location).map(Address::getStudyLocation).map(SusaNavetMapper::firstStringValue).orElse(null))
			.withMunicipalityId(ofNullable(location).map(Address::getAreaCode).orElse(null))
			.withCoursePostUrl(firstUrlValue(event.getUrl()))
			.withSeats(event.getPlaces())
			.withCurrencyType(ofNullable(fee).map(Fee::getCurrency).orElse(null))
			.withCost(ofNullable(fee).map(Fee::getTotalAmount).map(BigDecimal::valueOf).orElse(null))
			.withLanguageOfInstructions(firstOrNull(event.getLanguageOfInstructions()))
			.withStartDate(ofNullable(execution).map(Execution::getStart).orElse(null))
			.withEndDate(ofNullable(execution).map(Execution::getEnd).orElse(null))
			.withStartType(ofNullable(execution).map(Execution::getCondition).map(CExecutionCondition::getCode)
				.filter(code -> code != ExecutionConditionCode.UNKNOWN_DEFAULT_OPEN_API).map(ExecutionConditionCode::getValue).orElse(null))
			.withStudyPace(ofNullable(event.getPaceOfStudy()).map(PaceOfStudy::getPercentage).map(String::valueOf).orElse(null))
			.withLectureType(toLectureType(event.getDistance()))
			.withApplicationDateStart(ofNullable(application).map(Application::getFirst).orElse(null))
			.withApplicationDateEnd(ofNullable(application).map(Application::getLast).orElse(null))
			.withApplicationUrl(ofNullable(application).map(Application::getUrl).map(SusaNavetMapper::firstUrlValue).orElse(null))
			.withCancelled(event.getIsCancelled())
			.build();
	}

	private static String toLectureType(final DistanceEducation distance) {
		if (distance == null) {
			return "Classroom";
		}
		return "Distance";
	}

	private static Address toLocation(final List<Address> locations, final Set<String> municipalityIds) {
		return ofNullable(locations).orElse(List.of()).stream()
			.filter(Objects::nonNull)
			.filter(location -> location.getAreaCode() != null && municipalityIds.contains(location.getAreaCode()))
			.findFirst()
			.orElse(firstOrNull(locations));
	}

	private static String toContractor(final List<ExtensionE> extensions) {
		return ofNullable(extensions).orElse(List.of()).stream()
			.filter(KVEventExtension.class::isInstance)
			.map(KVEventExtension.class::cast)
			.map(KVEventExtension::getContractor)
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(null);
	}
}
