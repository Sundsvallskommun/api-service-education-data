package se.sundsvall.educationdata.service.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import se.sundsvall.educationdata.util.Util;

import static org.assertj.core.api.Assertions.assertThat;

class EducationProvidersMapperTest {

	private final EducationProvidersMapper providersMapper = new EducationProvidersMapper();
	private final byte[] json = "{\"page\":{\"totalPages\":1}}".getBytes();

	@Test
	void toZippedProviders_test() {
		final var entity = providersMapper.toZippedProviders(json, 0);

		assertThat(entity.getPage()).isZero();
		assertThat(entity.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(Util.unzip(entity.getJsonBody())).isEqualTo(json);
	}
}
