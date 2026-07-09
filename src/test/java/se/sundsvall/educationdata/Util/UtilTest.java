package se.sundsvall.educationdata.Util;

import java.io.UncheckedIOException;
import org.junit.jupiter.api.Test;
import se.sundsvall.educationdata.util.Util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UtilTest {

	@Test
	void zip_unzipTest() {
		final var json = "json".getBytes();

		final var zipped = Util.zip(json);
		final var unzipped = Util.unzip(zipped);

		assertThat(unzipped).isEqualTo(json);
		assertThat(zipped).isNotEqualTo(json);
	}

	@Test
	void unzip_invalidData() {
		assertThatThrownBy(() -> Util.unzip(new byte[] {
			1, 2, 3
		}))
			.isInstanceOf(UncheckedIOException.class)
			.hasMessageContaining("Failed to gunzip json body");
	}
}
