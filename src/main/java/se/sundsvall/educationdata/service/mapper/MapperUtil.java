package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.LangString;
import generated.se.sundsvall.susanavet.LangUrl;

import java.util.List;

final class MapperUtil {

	private MapperUtil() {}

	static <T> T firstOrNull(List<T> values) {
		return values == null || values.isEmpty() ? null : values.getFirst();
	}

	static String firstStringValue(LangString langString) {
		final var node = firstOrNull(langString == null ? null : langString.getStrings());
		return node == null ? null : node.getValue();
	}

	static String firstUrlValue(LangUrl langUrl) {
		final var node = firstOrNull(langUrl == null ? null : langUrl.getUrls());
		return node == null ? null : node.getValue();
	}
}
