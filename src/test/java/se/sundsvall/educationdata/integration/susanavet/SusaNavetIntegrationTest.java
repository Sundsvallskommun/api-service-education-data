package se.sundsvall.educationdata.integration.susanavet;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
	private static final int SIZE = 1;

	@Test
	void getEducationEvents() {
		var json = "json".getBytes();

		when(clientMock.getAllEducationEvents(PAGE, SIZE)).thenReturn(json);

		assertThat(integration.getEducationEvents(1, 1)).isSameAs(json);

		verify(clientMock).getAllEducationEvents(PAGE, SIZE);
	}

	@Test
	void getEducationInfos() throws IOException {
		var json = "json".getBytes();

		when(clientMock.getAllEducationInfos(PAGE, SIZE)).thenReturn(json);

		assertThat(integration.getEducationInfos(PAGE, SIZE)).isSameAs(json);

		verify(clientMock).getAllEducationInfos(PAGE, SIZE);
		verifyNoMoreInteractions(clientMock);

	}

	@Test
	void getEducationProviders() throws IOException {
		var json = "json".getBytes();

		when(clientMock.getAllEducationProviders(PAGE, SIZE)).thenReturn(json);

		assertThat(integration.getEducationProviders(PAGE, SIZE)).isSameAs(json);

		verify(clientMock).getAllEducationProviders(PAGE, SIZE);
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getEducationEventsThrowsWhenNull() {
		when(clientMock.getAllEducationEvents(PAGE, SIZE)).thenReturn(null);

		assertThatThrownBy(() -> integration.getEducationEvents(PAGE, SIZE))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Empty body");

		verify(clientMock).getAllEducationEvents(PAGE, SIZE);
	}

	@Test
	void getEducationEventsThrowsWhenEmpty() {
		when(clientMock.getAllEducationEvents(PAGE, SIZE)).thenReturn(new byte[0]);

		assertThatThrownBy(() -> integration.getEducationEvents(PAGE, SIZE))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Empty body");

		verify(clientMock).getAllEducationEvents(PAGE, SIZE);
	}

	@Test
	void getEducationInfosThrowsWhenNull() {
		when(clientMock.getAllEducationInfos(PAGE, SIZE)).thenReturn(null);

		assertThatThrownBy(() -> integration.getEducationInfos(PAGE, SIZE))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Empty body");

		verify(clientMock).getAllEducationInfos(PAGE, SIZE);
	}

	@Test
	void getEducationInfosThrowsWhenEmpty() {
		when(clientMock.getAllEducationInfos(PAGE, SIZE)).thenReturn(new byte[0]);

		assertThatThrownBy(() -> integration.getEducationInfos(PAGE, SIZE))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Empty body");

		verify(clientMock).getAllEducationInfos(PAGE, SIZE);
	}

	@Test
	void getEducationProvidersThrowsWhenNull() {
		when(clientMock.getAllEducationProviders(PAGE, SIZE)).thenReturn(null);

		assertThatThrownBy(() -> integration.getEducationProviders(PAGE, SIZE))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Empty body");

		verify(clientMock).getAllEducationProviders(PAGE, SIZE);
	}

	@Test
	void getEducationProvidersThrowsWhenEmpty() {
		when(clientMock.getAllEducationProviders(PAGE, SIZE)).thenReturn(new byte[0]);

		assertThatThrownBy(() -> integration.getEducationProviders(PAGE, SIZE))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Empty body");

		verify(clientMock).getAllEducationProviders(PAGE, SIZE);
	}
}
