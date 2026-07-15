package se.sundsvall.educationdata.integration.plannededucation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "integration.planned-education")
public record PlannedEducationIntegrationProperties(
	@DefaultValue("5") int readTimeout,
	@DefaultValue("30") int connectTimeout) {
}
