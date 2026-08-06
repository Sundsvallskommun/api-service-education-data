package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.LangString;
import generated.se.sundsvall.susanavet.LangStringNode;
import generated.se.sundsvall.susanavet.LangUrl;
import generated.se.sundsvall.susanavet.UrlNode;

import java.util.Collections;
import java.util.Optional;

final class SusaNavetMapper {
	private SusaNavetMapper() {}

	static String firstStringValue(LangString langString) {
		return Optional.ofNullable(langString)
			.map(LangString::getStrings).orElse(Collections.emptyList())
			.stream()
			.findFirst()
			.map(LangStringNode::getValue)
			.orElse(null);
	}

	static String firstUrlValue(final LangUrl langUrl) {
		return Optional.ofNullable(langUrl)
			.map(LangUrl::getUrls).orElse(Collections.emptyList())
			.stream()
			.findFirst()
			.map(UrlNode::getValue)
			.orElse(null);
	}
}
