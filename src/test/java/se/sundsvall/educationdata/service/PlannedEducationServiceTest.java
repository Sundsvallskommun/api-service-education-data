package se.sundsvall.educationdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategory;
import se.sundsvall.educationdata.integration.plannededucation.PlannedEducationIntegration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlannedEducationServiceTest {

	@Mock
	private PlannedEducationIntegration integration;

	@Mock
	private ReferenceCategoryRepository repository;

	@InjectMocks
	private PlannedEducationService service;

	@Test
	void getCategoryInfo_updateRows() throws JsonProcessingException {
		final var rows = List.of(ReferenceCategory.builder().withCategoryId("1").build());
		when(integration.getAllAreas()).thenReturn(rows);

		service.getCategoryInfo();

		final var order = inOrder(repository);
		order.verify(repository).deleteAllInBatch();
		order.verify(repository).saveAll(rows);

		verify(integration).getAllAreas();
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void getCategoryInfo_emptyRows() throws JsonProcessingException {
		final List<ReferenceCategory> rows = List.of();
		when(integration.getAllAreas()).thenReturn(rows);

		assertThatThrownBy(() -> service.getCategoryInfo())
			.hasMessageContaining("No content");

		verify(integration).getAllAreas();
		verifyNoMoreInteractions(integration);
	}
}
