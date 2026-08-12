package se.sundsvall.educationdata.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;
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
	void importReferenceCategories_updateRows() {
		final var rows = List.of(ReferenceCategoryEntity.builder().withCategoryId("1").build());
		when(integration.getAllReferenceCategories()).thenReturn(rows);

		service.importReferenceCategories();

		final var order = inOrder(repository);
		order.verify(repository).deleteAllInBatch();
		order.verify(repository).saveAll(rows);

		verify(integration).getAllReferenceCategories();
		verifyNoMoreInteractions(integration, repository);
	}

	@Test
	void importReferenceCategories_emptyRows() {
		final List<ReferenceCategoryEntity> rows = List.of();
		when(integration.getAllReferenceCategories()).thenReturn(rows);

		assertThatThrownBy(() -> service.importReferenceCategories())
			.hasMessageContaining("No content");

		verify(integration).getAllReferenceCategories();
		verifyNoMoreInteractions(integration);
	}
}
