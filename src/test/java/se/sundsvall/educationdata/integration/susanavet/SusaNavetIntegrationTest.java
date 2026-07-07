package se.sundsvall.educationdata.integration.susanavet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

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

	final int page = 1;
	final int size = 1;

	@Test
	void getEducationEvents() {
		var json = "json".getBytes();
		when(clientMock.getAllEducationEvents(page, size)).thenReturn(ResponseEntity.ok(json));

		var result = integration.getEducationEvents(page, size);

		assertThat(result).isEqualTo("json");
		verify(clientMock).getAllEducationEvents(page, size);
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getEducationEvents_null() {
		when(clientMock.getAllEducationEvents(page, size)).thenReturn(ResponseEntity.ok(null));

		var result = integration.getEducationEvents(page, size);

		assertThat(result).isNull();
		verify(clientMock).getAllEducationEvents(page, size);
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getEducationInfos() {
		var json = "json".getBytes();
		when(clientMock.getAllEducationInfos(page, size)).thenReturn(ResponseEntity.ok(json));

		var result = integration.getEducationInfos(page, size);

		assertThat(result).isEqualTo("json");
		verify(clientMock).getAllEducationInfos(page, size);
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getEducationInfos_null() {
		when(clientMock.getAllEducationInfos(page, size)).thenReturn(ResponseEntity.ok(null));

		var result = integration.getEducationInfos(page, size);

		assertThat(result).isNull();
		verify(clientMock).getAllEducationInfos(page, size);
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getEducationProviders() {
		var json = "json".getBytes();
		when(clientMock.getAllEducationProviders(page, size)).thenReturn(ResponseEntity.ok(json));

		var result = integration.getEducationProviders(page, size);

		assertThat(result).isEqualTo("json");
		verify(clientMock).getAllEducationProviders(page, size);
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getEducationProviders_null() {
		when(clientMock.getAllEducationProviders(page, size)).thenReturn(ResponseEntity.ok(null));

		var result = integration.getEducationProviders(page, size);

		assertThat(result).isNull();
		verify(clientMock).getAllEducationProviders(page, size);
		verifyNoMoreInteractions(clientMock);
	}
}
