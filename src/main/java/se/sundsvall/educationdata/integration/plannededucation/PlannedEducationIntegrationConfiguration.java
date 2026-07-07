package se.sundsvall.educationdata.integration.plannededucation;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
public class PlannedEducationIntegrationConfiguration {

	private final PlannedEducationIntegrationProperties properties;

	private static final String CLIENT_ID = "planned-education";

	public PlannedEducationIntegrationConfiguration(PlannedEducationIntegrationProperties properties) {
		this.properties = properties;
	}

	@Bean
	FeignBuilderCustomizer feignCustomizer() {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID))
			.withRequestTimeoutsInSeconds(properties.readTimeout(), properties.connectTimeout())
			.composeCustomizersToOne();
	}
}
