package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.Address;
import generated.se.sundsvall.susanavet.Application;
import generated.se.sundsvall.susanavet.EducationEvent;
import generated.se.sundsvall.susanavet.Execution;
import generated.se.sundsvall.susanavet.Fee;
import generated.se.sundsvall.susanavet.PaceOfStudy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
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

	public List<EducationEventEntity> toEventEntities(List<EducationEvent> events) {
		if (events == null) {
			return List.of();
		}
		return events.stream().map(this::toEventEntity).filter(Objects::nonNull).toList();
	}

	private EducationEventEntity toEventEntity(final EducationEvent event) {
		if (event == null) {
			return null;
		}

		final var location = firstOrNull(event.getLocations());
		final var fee = firstOrNull(event.getFees());
		final var execution = event.getExecution();
		final var application = event.getApplication();

		return EducationEventEntity.builder()
			.withEducationEventId(event.getIdentifier())
			.withEducationInfoId(event.getEducation())
			.withEducationProviderId(firstOrNull(event.getProviders()))
			.withTitle(firstStringValue(event.getTitle()))
			.withCity(ofNullable(location).map(Address::getTown).orElse(null))
			.withMunicipalityId(ofNullable(location).map(Address::getAreaCode).orElse(null))
			.withCoursePostUrl(firstUrlValue(event.getUrl()))
			.withSeats(event.getPlaces())
			.withCurrencyType(ofNullable(fee).map(Fee::getCurrency).orElse(null))
			.withCost(ofNullable(fee).map(Fee::getTotalAmount).map(BigDecimal::valueOf).orElse(null))
			.withLanguageOfInstructions(firstOrNull(event.getLanguageOfInstructions()))
			.withStartDate(ofNullable(execution).map(Execution::getStart).orElse(null))
			.withEndDate(ofNullable(execution).map(Execution::getEnd).orElse(null))
			.withStudyPace(ofNullable(event.getPaceOfStudy()).map(PaceOfStudy::getPercentage).map(String::valueOf).orElse(null))
			.withLectureType(event.getDistance() != null ? "Distance" : "Classroom")
			.withApplicationDateStart(ofNullable(application).map(Application::getFirst).orElse(null))
			.withApplicationDateEnd(ofNullable(application).map(Application::getLast).orElse(null))
			.withCancelled(event.getIsCancelled())
			.build();
	}
}
