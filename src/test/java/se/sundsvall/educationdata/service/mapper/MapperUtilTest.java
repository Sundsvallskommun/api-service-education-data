package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.LangString;
import generated.se.sundsvall.susanavet.LangStringNode;
import generated.se.sundsvall.susanavet.LangUrl;
import generated.se.sundsvall.susanavet.UrlNode;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MapperUtilTest {

	@Test
	void firstOrNull() {
		final var values = List.of("first", "second");
		final var result = MapperUtil.firstOrNull(values);
		assertThat(result).isEqualTo("first");
	}

	@Test
	void firstOrNull_null() {
		final List<String> values = null;
		final var result = MapperUtil.firstOrNull(values);
		assertThat(result).isNull();
	}

	@Test
	void firstOrNull_empty() {
		final var values = List.<String>of();
		final var result = MapperUtil.firstOrNull(values);
		assertThat(result).isNull();
	}

	@Test
	void firstStringValue() {
		final var langString = new LangString().strings(List.of(
			new LangStringNode().value("first"),
			new LangStringNode().value("second")));
		final var result = MapperUtil.firstStringValue(langString);
		assertThat(result).isEqualTo("first");
	}

	@Test
	void firstStringValue_null() {
		final LangString langString = null;
		final var result = MapperUtil.firstStringValue(langString);
		assertThat(result).isNull();
	}

	@Test
	void firstStringValue_empty() {
		final var langString = new LangString().strings(List.of());
		final var result = MapperUtil.firstStringValue(langString);
		assertThat(result).isNull();
	}

	@Test
	void firstUrlValue() {
		final var langUrl = new LangUrl().urls(List.of(
			new UrlNode().value("url1"),
			new UrlNode().value("url2")));
		final var result = MapperUtil.firstUrlValue(langUrl);
		assertThat(result).isEqualTo("url1");
	}

	@Test
	void firstUrlValue_null() {
		final LangUrl langUrl = null;
		final var result = MapperUtil.firstUrlValue(langUrl);
		assertThat(result).isNull();
	}

	@Test
	void firstUrlValue_Empty() {
		final var langUrl = new LangUrl().urls(List.of());
		final var result = MapperUtil.firstUrlValue(langUrl);
		assertThat(result).isNull();
	}
}
