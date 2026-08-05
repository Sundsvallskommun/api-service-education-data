package se.sundsvall.educationdata.service.mapper;

import generated.se.sundsvall.susanavet.CCredits;
import generated.se.sundsvall.susanavet.CSchoolType;
import generated.se.sundsvall.susanavet.CodeSubject;
import generated.se.sundsvall.susanavet.Credits;
import generated.se.sundsvall.susanavet.CreditsCode;
import generated.se.sundsvall.susanavet.EducationInfo;
import generated.se.sundsvall.susanavet.EducationInfoResponse;
import generated.se.sundsvall.susanavet.LangString;
import generated.se.sundsvall.susanavet.LangStringNode;
import generated.se.sundsvall.susanavet.LangUrl;
import generated.se.sundsvall.susanavet.SchoolTypeCode;
import generated.se.sundsvall.susanavet.UrlNode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.util.Util;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EducationInfosMapperTest {

	private final EducationInfosMapper infosMapper = new EducationInfosMapper();
	private final byte[] json = "{\"page\":{\"totalPages\":1}}".getBytes();

	@Test
	void toZippedInfos_test() {
		final var entity = infosMapper.toZippedInfos(json, 5);

		assertThat(entity.getPage()).isEqualTo(5);
		assertThat(entity.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(Util.unzip(entity.getJsonBody())).isEqualTo(json);
	}

	@Test
	void toInfoEntities_test() {
		final var content = new EducationInfo()
			.identifier("info-123")
			.type(new CSchoolType()
				.type("schoolType")
				.code(SchoolTypeCode.YH))
			.title(new LangString()
				.strings(List.of(new LangStringNode()
					.lang("swe")
					.value("Mekaniker"))))
			.description(new LangString()
				.strings(List.of(new LangStringNode()
					.lang("swe")
					.value("Laga bilar"))))
			.eligibility(new LangString()
				.strings(List.of(new LangStringNode()
					.lang("swe")
					.value("Grundläggande behörighet"))))
			.recommendedPriorKnowledge(new LangString()
				.strings(List.of(new LangStringNode()
					.lang("swe")
					.value("no knowledge needed"))))
			.resultIsDegree(false)
			.degrees(List.of(new LangString()
				.strings(List.of(new LangStringNode()
					.lang("swe")
					.value("Yrkeshögskoleexamen")))))
			.subjects(List.of(new CodeSubject()
				.type("SV_Subject")
				.code("MODG1000")))
			.credits(new Credits()
				.credits(120.0)
				.system(new CCredits().code(CreditsCode.HP)))
			.url(new LangUrl()
				.urls(List.of(new UrlNode()
					.lang("swe")
					.value("https://example.com/info"))))
			.expires(LocalDateTime.parse("2027-06-07T13:37"));

		final var response = new EducationInfoResponse()
			.id("info-123")
			.status("ACTIVE")
			.content(content);

		final var result = infosMapper.toInfoEntities(List.of(response));

		assertThat(result).hasSize(1);

		final var row = result.getFirst();

		assertThat(row.getEducationInfoId()).isEqualTo("info-123");
		assertThat(row.getSchoolType()).isEqualTo("YH");
		assertThat(row.getTitle()).isEqualTo("Mekaniker");
		assertThat(row.getDescription()).isEqualTo("Laga bilar");
		assertThat(row.getEducationEligibility()).isEqualTo("Grundläggande behörighet");
		assertThat(row.getRecommendedPriorKnowledge()).isEqualTo("no knowledge needed");
		assertThat(row.getResultIsDegree()).isFalse();
		assertThat(row.getDegree()).isEqualTo(List.of("Yrkeshögskoleexamen"));
		assertThat(row.getExpires()).isEqualTo(LocalDateTime.parse("2027-06-07T13:37"));
		assertThat(row.getCredits()).isEqualTo(120.0);
		assertThat(row.getCreditType()).isEqualTo("hp");
	}

	@Test
	void toInfoEntity_nullDegrees_returnsEmptyList() {
		final var content = new EducationInfo().identifier("i.id.123").degrees(null);
		final var row = infosMapper.toInfoEntities(List.of(new EducationInfoResponse().content(content))).getFirst();
		assertThat(row.getDegree()).isEmpty();
	}

	@Test
	void toInfoEntities_nullTest() {
		final List<EducationInfoResponse> infos = null;
		final var result = infosMapper.toInfoEntities(infos);
		assertThat(result).isEmpty();
	}

	@Test
	void toInfoEntities_nullContentTest() {
		final var infos = List.of(new EducationInfoResponse().content(null));
		final var result = infosMapper.toInfoEntities(infos);
		assertThat(result).isEmpty();
	}

	@Test
	void toInfoEntities_nullElementTest() {
		final var infos = new ArrayList<EducationInfoResponse>();
		infos.add(null);
		final var result = infosMapper.toInfoEntities(infos);
		assertThat(result).isEmpty();
	}
}
