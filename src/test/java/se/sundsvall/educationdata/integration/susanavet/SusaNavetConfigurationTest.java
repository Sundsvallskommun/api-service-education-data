package se.sundsvall.educationdata.integration.susanavet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SusaNavetConfigurationTest {

	@Mock
	private SusaNavetIntegrationProperties propertiesMock;
	@Spy
	private FeignMultiCustomizer feignMultiCustomizerSpy;
	@Mock
	private FeignBuilderCustomizer feignBuilderCustomizerMock;

	@Test
	void testFeignBuilderCustomizer() {
		final var configuration = new SusaNavetIntegrationConfiguration(propertiesMock);
		when(propertiesMock.readTimeout()).thenReturn(2);
		when(propertiesMock.connectTimeout()).thenReturn(1);
		when(feignMultiCustomizerSpy.composeCustomizersToOne()).thenReturn(feignBuilderCustomizerMock);

		try (final MockedStatic<FeignMultiCustomizer> staticMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			staticMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			final var customizer = configuration.feignCustomizer();

			final ArgumentCaptor<ProblemErrorDecoder> captor = ArgumentCaptor.forClass(ProblemErrorDecoder.class);
			verify(feignMultiCustomizerSpy).withErrorDecoder(captor.capture());
			verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(2, 1);   // (readTimeout, connectTimeout) — match your config's arg order
			verify(feignMultiCustomizerSpy).composeCustomizersToOne();
			verify(propertiesMock).readTimeout();
			verify(propertiesMock).connectTimeout();

			assertThat(captor.getValue()).hasFieldOrPropertyWithValue("integrationName", "susa-navet");
			assertThat(customizer).isSameAs(feignBuilderCustomizerMock);
		}
	}

}
