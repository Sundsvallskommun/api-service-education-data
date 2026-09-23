package se.sundsvall.educationdata.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.educationdata.api.model.Education;
import se.sundsvall.educationdata.api.model.EducationParameters;
import se.sundsvall.educationdata.api.model.PagedEducationResponse;
import se.sundsvall.educationdata.integration.db.EducationEventEntityRepository;
import se.sundsvall.educationdata.integration.db.EducationInfoEntityRepository;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity;
import se.sundsvall.educationdata.integration.db.model.projection.LanguageOfInstructionsProjection;
import se.sundsvall.educationdata.integration.db.model.projection.LectureTypeProjection;
import se.sundsvall.educationdata.integration.db.model.projection.StudyLocationProjection;
import se.sundsvall.educationdata.integration.db.model.projection.StudyPaceProjection;
import se.sundsvall.educationdata.service.mapper.EducationMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

	@Mock
	private EducationEventEntityRepository educationEventEntityRepository;

	@Mock
	private EducationInfoEntityRepository educationInfoEntityRepository;

	@Mock
	private EducationMapper educationMapper;

	@InjectMocks
	EducationService educationService;

	private static final String EVENT_ID = "e.2282.123";
	private static final String INFO_ID = "i.123";
	private static final LocalDate LATEST_IMPORT_DATE = LocalDate.of(2026, Month.JUNE, 7);

	@Test
	void findByEducationIdLatestImportWhenNullDate() {
		final var event = EducationEventEntity.builder().withEducationEventId(EVENT_ID).withEducationInfoId(INFO_ID).withCreatedAt(LocalDate.of(2026, Month.JUNE, 7)).build();
		final var info = EducationInfoEntity.builder().withEducationInfoId(INFO_ID).build();
		final var education = Education.builder().withId(EVENT_ID).build();

		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findByEducationEventIdAndCreatedAt(EVENT_ID, LATEST_IMPORT_DATE)).thenReturn(Optional.of(event));
		when(educationInfoEntityRepository.findByEducationInfoIdAndCreatedAt(INFO_ID, LATEST_IMPORT_DATE)).thenReturn(Optional.of(info));
		when(educationMapper.toEducation(event, info)).thenReturn(education);

		final var result = educationService.findEducationById(EVENT_ID, null);

		assertThat(result).isSameAs(education);
		verify(educationEventEntityRepository).findLatestImportDate();
	}

	@Test
	void findByEducationIdNotFoundThrows() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findByEducationEventIdAndCreatedAt(EVENT_ID, LATEST_IMPORT_DATE)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> educationService.findEducationById(EVENT_ID, null))
			.isInstanceOf(Problem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Education with id 'e.2282.123' not found for import date '2026-06-07'");

		verifyNoInteractions(educationMapper, educationInfoEntityRepository);
	}

	@Test
	void findByEducationIdWithNoInfo() {
		final var event = EducationEventEntity.builder().withEducationEventId(EVENT_ID).withEducationInfoId(INFO_ID).withCreatedAt(LocalDate.of(2026, Month.JUNE, 7)).build();
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findByEducationEventIdAndCreatedAt(EVENT_ID, LATEST_IMPORT_DATE)).thenReturn(Optional.of(event));
		when(educationInfoEntityRepository.findByEducationInfoIdAndCreatedAt(INFO_ID, LATEST_IMPORT_DATE)).thenReturn(Optional.empty());

		educationService.findEducationById(EVENT_ID, null);

		verify(educationMapper).toEducation(event, null);
	}

	@Test
	void findEducationByIdUsesSpecificDate() {
		final var specificDate = LocalDate.of(2026, Month.JANUARY, 1);
		final var event = EducationEventEntity.builder().withEducationEventId(EVENT_ID).withEducationInfoId(INFO_ID).withCreatedAt(LocalDate.of(2026, Month.JANUARY, 1)).build();

		when(educationEventEntityRepository.findByEducationEventIdAndCreatedAt(EVENT_ID, specificDate))
			.thenReturn(Optional.of(event));
		when(educationInfoEntityRepository.findByEducationInfoIdAndCreatedAt(INFO_ID, specificDate))
			.thenReturn(Optional.empty());

		educationService.findEducationById(EVENT_ID, specificDate);

		verify(educationEventEntityRepository).findByEducationEventIdAndCreatedAt(EVENT_ID, specificDate);
		verify(educationEventEntityRepository, never()).findLatestImportDate();
		verify(educationInfoEntityRepository).findByEducationInfoIdAndCreatedAt(INFO_ID, specificDate);
		verify(educationMapper).toEducation(event, null);
	}

	@Test
	void findWithInfo() {
		final var parameters = new EducationParameters();
		final var event = EducationEventEntity.builder().withEducationEventId(EVENT_ID).withEducationInfoId(INFO_ID).withCreatedAt(LATEST_IMPORT_DATE).build();
		final var info = EducationInfoEntity.builder().withEducationInfoId(INFO_ID).build();

		final var page = new PageImpl<>(List.of(event));

		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);
		when(educationInfoEntityRepository.findByEducationInfoIdInAndCreatedAt(Set.of(INFO_ID), LATEST_IMPORT_DATE))
			.thenReturn(List.of(EducationInfoEntity.builder().withEducationInfoId(INFO_ID).build()));
		when(educationMapper.toPagedEducationResponse(any(), any())).thenReturn(PagedEducationResponse.builder().build());

		educationService.find(parameters, null);

		verify(educationInfoEntityRepository).findByEducationInfoIdInAndCreatedAt(Set.of(INFO_ID), LATEST_IMPORT_DATE);
		verify(educationMapper).toPagedEducationResponse(page, Map.of(INFO_ID, info));
	}

	@Test
	void findFilterValuesForLectureType() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findDistinctByCreatedAt(
			eq(LectureTypeProjection.class), eq(LATEST_IMPORT_DATE), any(Sort.class)))
			.thenReturn(List.of(() -> "distance"));

		assertThat(educationService.findFilterValues("lectureType", null))
			.containsExactly("distance");
	}

	@Test
	void findFilterValuesForLanguageOfInstructions() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findDistinctByCreatedAt(
			eq(LanguageOfInstructionsProjection.class), eq(LATEST_IMPORT_DATE), any(Sort.class)))
			.thenReturn(List.of(() -> "swe"));

		assertThat(educationService.findFilterValues("languageOfInstructions", null))
			.containsExactly("swe");
	}

	@Test
	void findFilterValuesForCity() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findDistinctByCreatedAt(
			eq(StudyLocationProjection.class), eq(LATEST_IMPORT_DATE), any(Sort.class)))
			.thenReturn(List.of(() -> "Sundsvall"));

		assertThat(educationService.findFilterValues("studyLocation", null))
			.containsExactly("Sundsvall");
	}

	@Test
	void findFilterValuesForStudyPace() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		when(educationEventEntityRepository.findDistinctByCreatedAt(
			eq(StudyPaceProjection.class), eq(LATEST_IMPORT_DATE), any(Sort.class)))
			.thenReturn(List.of(() -> "75.0"));

		assertThat(educationService.findFilterValues("studyPace", null))
			.containsExactly("75.0");
	}

	@Test
	void findFilterValuesNonExistentCase() {
		when(educationEventEntityRepository.findLatestImportDate()).thenReturn(LATEST_IMPORT_DATE);
		assertThat(educationService.findFilterValues("nonsense", null)).isEmpty();
	}

}
