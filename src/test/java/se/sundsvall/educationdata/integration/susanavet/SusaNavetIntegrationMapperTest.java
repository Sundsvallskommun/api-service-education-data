package se.sundsvall.educationdata.integration.susanavet;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.util.Util;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SusaNavetIntegrationMapperTest {

	private final SusaNavetIntegrationMapper mapper = new SusaNavetIntegrationMapper(new ObjectMapper());

	private static final byte[] JSON = """
		{
		  "educationEvents": [],
		  "page": {
		    "totalPages": 3
		  }
		}
		""".getBytes();

	@Test
	void toEducationEventWithPagesTest() throws IOException {
		final var result = mapper.toEducationEventWithPages(JSON);

		assertThat(result.totalPages()).isEqualTo(3);
		assertThat(Util.unzip(result.entity().getJsonBody())).isEqualTo(JSON);
		assertThat(result.entity().getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
	}

	@Test
	void toEducationInfosWithPagesTest() throws IOException {
		final var result = mapper.toEducationInfosWithPages(JSON);

		assertThat(result.totalPages()).isEqualTo(3);
		assertThat(Util.unzip(result.entity().getJsonBody())).isEqualTo(JSON);
		assertThat(result.entity().getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
	}

	@Test
	void toEducationProviderWithPagesTest() throws IOException {
		final var result = mapper.toEducationProviderWithPages(JSON);

		assertThat(result.totalPages()).isEqualTo(3);
		assertThat(Util.unzip(result.entity().getJsonBody())).isEqualTo(JSON);
		assertThat(result.entity().getDateCollected()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
	}
}
