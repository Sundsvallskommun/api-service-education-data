package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.plannededucation.ApiResponseListedAdultEducationEvents;
import generated.se.sundsvall.plannededucation.EmbeddedAdultEducationEventsRM;
import generated.se.sundsvall.plannededucation.ListedAdultEducationEventRM;
import generated.se.sundsvall.plannededucation.ListedAdultEducationEventsRM;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.educationdata.integration.db.model.EventCategoryEntity;

@Component
public class PlannedEducationMapper {

	public EventCategoryEntity toEventCategory(String directionId, String educationEventId) {
		return EventCategoryEntity.builder()
			.withDirectionId(directionId)
			.withEducationEventId(educationEventId)
			.build();
	}

	public List<String> toEventIdList(ApiResponseListedAdultEducationEvents response) {
		return Optional.ofNullable(response.getBody())
			.map(ListedAdultEducationEventsRM::getEmbedded)
			.map(EmbeddedAdultEducationEventsRM::getListedAdultEducationEvents)
			.orElseGet(List::of)
			.stream()
			.map(ListedAdultEducationEventRM::getEducationEventId)
			.filter(Objects::nonNull)
			.toList();
	}
}
