package se.sundsvall.educationdata.Util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.Test;
import se.sundsvall.educationdata.util.Util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;

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

	@Test
	void zipWrapsIOExceptionInUncheckedIOException() {
		try (var mockedConstruction = mockConstruction(GZIPOutputStream.class,
			(mock, context) -> doThrow(new IOException("boom")).when(mock).write(any(byte[].class)))) {

			assertThatExceptionOfType(UncheckedIOException.class)
				.isThrownBy(() -> Util.zip("some data".getBytes(StandardCharsets.UTF_8)))
				.withMessage("Failed to gzip json body")
				.withCauseInstanceOf(IOException.class);
		}
	}
}
