package se.sundsvall.educationdata.integration.susanavet;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationEvent;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationInfo;
import se.sundsvall.educationdata.integration.db.model.json.SusaEducationProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SusaNavetIntegrationTest {

	@Mock
	private SusaNavetClient clientMock;

	@Mock
	private SusaNavetIntegrationMapper mapper;

	@InjectMocks
	private SusaNavetIntegration integration;

	final int PAGE = 1;
	final int SIZE = 1;

	@Test
	void getEducationEvents() throws IOException {
		var json = "json".getBytes();
		final var mapped = new SusaPage<>(SusaEducationEvent.builder().build(), 1);

		when(clientMock.getAllEducationEvents(PAGE, SIZE)).thenReturn(json);
		when(mapper.toEducationEventWithPages(json)).thenReturn(mapped);

		assertThat(integration.getEducationEventsWithPage(1, 1)).isSameAs(mapped);

		verify(clientMock).getAllEducationEvents(PAGE, SIZE);
		verify(mapper).toEducationEventWithPages(json);
		verifyNoMoreInteractions(clientMock, mapper);
	}

	@Test
	void getEducationEvents_null() {
		when(clientMock.getAllEducationEvents(PAGE, SIZE)).thenReturn(null);

		assertThatThrownBy(() -> integration.getEducationEventsWithPage(PAGE, SIZE))
			.hasMessageContaining("Empty body for page 1");

		verify(clientMock).getAllEducationEvents(PAGE, SIZE);
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getEducationEvents_emptyBody() {
		when(clientMock.getAllEducationEvents(PAGE, SIZE)).thenReturn(new byte[0]);

		assertThatThrownBy(() -> integration.getEducationEventsWithPage(PAGE, SIZE))
			.hasMessageContaining("Empty body for page 1");

		verify(clientMock).getAllEducationEvents(PAGE, SIZE);
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getEducationInfos() throws IOException {
		var json = "json".getBytes();
		final var mapped = new SusaPage<>(SusaEducationInfo.builder().build(), 1);

		when(clientMock.getAllEducationInfos(PAGE, SIZE)).thenReturn(json);
		when(mapper.toEducationInfosWithPages(json)).thenReturn(mapped);

		assertThat(integration.getEducationInfosWithPage(PAGE, SIZE)).isSameAs(mapped);

		verify(clientMock).getAllEducationInfos(PAGE, SIZE);
		verify(mapper).toEducationInfosWithPages(json);
		verifyNoMoreInteractions(clientMock);

	}

	@Test
	void getEducationInfos_null() {
		when(clientMock.getAllEducationInfos(PAGE, SIZE)).thenReturn(null);

		assertThatThrownBy(() -> integration.getEducationInfosWithPage(PAGE, SIZE))
			.hasMessageContaining("Empty body for page 1");

		verify(clientMock).getAllEducationInfos(PAGE, SIZE);
		verifyNoMoreInteractions(clientMock);
		verifyNoInteractions(mapper);
	}

	@Test
	void getEducationInfos_emptyBody() {
		when(clientMock.getAllEducationInfos(PAGE, SIZE)).thenReturn(new byte[0]);

		assertThatThrownBy(() -> integration.getEducationInfosWithPage(PAGE, SIZE))
			.hasMessageContaining("Empty body for page 1");

		verify(clientMock).getAllEducationInfos(PAGE, SIZE);
		verifyNoMoreInteractions(clientMock);
		verifyNoInteractions(mapper);
	}

	@Test
	void getEducationProviders() throws IOException {
		var json = "json".getBytes();
		final var mapped = new SusaPage<>(SusaEducationProvider.builder().build(), 1);

		when(clientMock.getAllEducationProviders(PAGE, SIZE)).thenReturn(json);
		when(mapper.toEducationProviderWithPages(json)).thenReturn(mapped);

		assertThat(integration.getEducationProvidersWithPage(PAGE, SIZE)).isSameAs(mapped);

		verify(clientMock).getAllEducationProviders(PAGE, SIZE);
		verify(mapper).toEducationProviderWithPages(json);
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getEducationProviders_null() {
		when(clientMock.getAllEducationProviders(PAGE, SIZE)).thenReturn(null);

		assertThatThrownBy(() -> integration.getEducationProvidersWithPage(PAGE, SIZE))
			.hasMessageContaining("Empty body for page 1");

		verify(clientMock).getAllEducationProviders(PAGE, SIZE);
		verifyNoMoreInteractions(clientMock);
		verifyNoInteractions(mapper);
	}

	@Test
	void getEducationProviders_emptyBody() {
		when(clientMock.getAllEducationProviders(PAGE, SIZE)).thenReturn(new byte[0]);

		assertThatThrownBy(() -> integration.getEducationProvidersWithPage(PAGE, SIZE))
			.hasMessageContaining("Empty body for page 1");

		verify(clientMock).getAllEducationProviders(PAGE, SIZE);
		verifyNoMoreInteractions(clientMock);
		verifyNoInteractions(mapper);
	}
}
