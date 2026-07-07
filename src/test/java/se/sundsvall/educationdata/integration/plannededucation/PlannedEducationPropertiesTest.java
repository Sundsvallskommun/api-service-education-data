package se.sundsvall.educationdata.integration.plannededucation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.educationdata.Application;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class PlannedEducationPropertiesTest {

	@Autowired
	private PlannedEducationIntegrationProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(30);
		assertThat(properties.readTimeout()).isEqualTo(5);
	}
}
