package se.sundsvall.educationdata.service.mapper;

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

}
