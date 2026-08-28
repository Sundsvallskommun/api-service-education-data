package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.plannededucation.ApiResponseListedAdultEducationEvents;
import generated.se.sundsvall.plannededucation.EmbeddedAdultEducationEventsRM;
import generated.se.sundsvall.plannededucation.ListedAdultEducationEventRM;
import generated.se.sundsvall.plannededucation.ListedAdultEducationEventsRM;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlannedEducationMapperTest {

	private final PlannedEducationMapper mapper = new PlannedEducationMapper();

	@Test
	void toEventCategory() {
		final var result = mapper.toEventCategory("1", "e.myh.yh.123");

		assertThat(result.getDirectionId()).isEqualTo("1");
		assertThat(result.getEducationEventId()).isEqualTo("e.myh.yh.123");
	}

	@Test
	void toEventCategoryStaging() {
		final var result = mapper.toEventCategoryStaging("1", "e.myh.yh.123");

		assertThat(result.getDirectionId()).isEqualTo("1");
		assertThat(result.getEducationEventId()).isEqualTo("e.myh.yh.123");
	}

	@Test
	void toEventIdList() {
		final var response = new ApiResponseListedAdultEducationEvents()
			.body(new ListedAdultEducationEventsRM()
				.embedded(new EmbeddedAdultEducationEventsRM()
					.listedAdultEducationEvents(List.of(
						new ListedAdultEducationEventRM().educationEventId("e.1"),
						new ListedAdultEducationEventRM().educationEventId("e.2")))));

		assertThat(mapper.toEventIdList(response)).containsExactly("e.1", "e.2");
	}

	@Test
	void toEventIdList_filtersNullIds() {
		final var response = new ApiResponseListedAdultEducationEvents()
			.body(new ListedAdultEducationEventsRM()
				.embedded(new EmbeddedAdultEducationEventsRM()
					.listedAdultEducationEvents(List.of(
						new ListedAdultEducationEventRM().educationEventId("e.1"),
						new ListedAdultEducationEventRM()))));

		assertThat(mapper.toEventIdList(response)).containsExactly("e.1");
	}

	@Test
	void toEventIdList_nullBody() {
		assertThat(mapper.toEventIdList(new ApiResponseListedAdultEducationEvents())).isEmpty();
	}

	@Test
	void toEventIdList_nullEmbedded() {
		final var response = new ApiResponseListedAdultEducationEvents().body(new ListedAdultEducationEventsRM());
		assertThat(mapper.toEventIdList(response)).isEmpty();
	}
}
