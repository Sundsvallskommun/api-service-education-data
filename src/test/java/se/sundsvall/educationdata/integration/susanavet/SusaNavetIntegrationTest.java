package se.sundsvall.educationdata.integration.susanavet;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SusaNavetIntegrationTest {

	@Mock
	private SusaNavetClient clientMock;

	@InjectMocks
	private SusaNavetIntegration integration;

	private static final int PAGE = 1;
	private static final int SIZE = 2000;

	@Test
	void getEducationEvents() {
		var json = "json".getBytes();

		when(clientMock.getAllEducationEvents(PAGE, SIZE)).thenReturn(json);

		assertThat(integration.getEducationEvents(1)).isSameAs(json);

		verify(clientMock).getAllEducationEvents(PAGE, SIZE);
	}

	@Test
	void getEducationInfos() {
		var json = "json".getBytes();

		when(clientMock.getAllEducationInfos(PAGE, SIZE)).thenReturn(json);

		assertThat(integration.getEducationInfos(PAGE)).isSameAs(json);

		verify(clientMock).getAllEducationInfos(PAGE, SIZE);
		verifyNoMoreInteractions(clientMock);

	}

	@Test
	void getEducationProviders() {
		var json = "json".getBytes();

		when(clientMock.getAllEducationProviders(PAGE, SIZE)).thenReturn(json);

		assertThat(integration.getEducationProviders(PAGE)).isSameAs(json);

		verify(clientMock).getAllEducationProviders(PAGE, SIZE);
		verifyNoMoreInteractions(clientMock);
	}
}
