package se.sundsvall.educationdata.integration.susanavet;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "integration.susa-navet")
public record SusaNavetIntegrationProperties(
	@DefaultValue("5") int readTimeout,
	@DefaultValue("30") int connectTimeout) {
}
