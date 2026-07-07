package se.sundsvall.educationdata.integration.susanavet.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.educationdata.Application;
import se.sundsvall.educationdata.integration.susanavet.SusaNavetIntegrationProperties;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class SusaNavetPropertiesTest {

    @Autowired
    private SusaNavetIntegrationProperties properties;

    @Test
    void testProperties() {
        assertThat(properties.connectTimeout()).isEqualTo(5);
        assertThat(properties.readTimeout()).isEqualTo(30);
    }
}
