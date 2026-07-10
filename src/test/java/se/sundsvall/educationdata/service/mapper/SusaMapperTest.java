package se.sundsvall.educationdata.service.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.util.Util;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SusaMapperTest {

	private final SusaMapper mapper = new SusaMapper();
	private final byte[] json = "{\"page\":{\"totalPages\":1}}".getBytes();

	@Test
	void toZippedEvents() {
		final var entity = mapper.toZippedEvents(json, 2);

		assertThat(entity.getPage()).isEqualTo(2);
		assertThat(entity.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(Util.unzip(entity.getJsonBody())).isEqualTo(json);
	}

	@Test
	void toZippedInfos() {
		final var entity = mapper.toZippedInfos(json, 5);

		assertThat(entity.getPage()).isEqualTo(5);
		assertThat(entity.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(Util.unzip(entity.getJsonBody())).isEqualTo(json);
	}

	@Test
	void toZippedProviders() {
		final var entity = mapper.toZippedProviders(json, 0);

		assertThat(entity.getPage()).isZero();
		assertThat(entity.getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(Util.unzip(entity.getJsonBody())).isEqualTo(json);
	}
}
