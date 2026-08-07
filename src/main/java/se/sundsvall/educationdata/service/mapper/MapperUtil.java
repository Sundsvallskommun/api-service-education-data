package se.sundsvall.educationdata.service.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

final class MapperUtil {

	private MapperUtil() {}

	static <T> T firstOrNull(List<T> values) {
		return Optional.ofNullable(values).orElse(Collections.emptyList())
			.stream()
			.findFirst()
			.orElse(null);
	}
}
