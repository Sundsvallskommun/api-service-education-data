package se.sundsvall.educationdata.integration.plannededucation;

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
class PlannedEducationIntegrationTest {

	@Mock
	private PlannedEducationClient clientMock;

	@InjectMocks
	private PlannedEducationIntegration integration;

	@Test
	void getEducationEvents() {
		var json = "json".getBytes();
		when(clientMock.getAllAreas()).thenReturn(ResponseEntity.ok(json));

		var result = integration.getAllAreas();

		assertThat(result).isEqualTo("json");
		verify(clientMock).getAllAreas();
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getEducationEvents_null() {
		when(clientMock.getAllAreas()).thenReturn(ResponseEntity.ok(null));

		var result = integration.getAllAreas();

		assertThat(result).isNull();
		verify(clientMock).getAllAreas();
		verifyNoMoreInteractions(clientMock);
	}
}
