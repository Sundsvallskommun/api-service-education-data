package se.sundsvall.educationdata.integration.db.model;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.educationdata.integration.db.EventCategoryRepository;
import se.sundsvall.educationdata.integration.db.EventCategoryStagingRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("junit")
class EventCategoryRepositoryTest {

	@Autowired
	private EventCategoryRepository eventCategoryRepository;
	@Autowired
	private EventCategoryStagingRepository eventCategoryStagingRepository;
	@Autowired
	private TestEntityManager entityManager;

	@Test
	void deleteRemovesAllRelationsForStagedEvent() {
		eventCategoryRepository.saveAll(List.of(
			eventCategoryEntity("e.1", "d.1"),
			eventCategoryEntity("e.1", "d.2")));
		eventCategoryStagingRepository.save(eventCategoryStagingEntity("e.1", "d.1"));
		entityManager.flush();
		entityManager.clear();

		eventCategoryRepository.deleteEventRelationsForStagedEventIds();
		entityManager.clear();

		assertThat(eventCategoryRepository.findAll()).isEmpty();
	}

	@Test
	void deleteLeavesNonStagedEvents() {
		eventCategoryRepository.saveAll(List.of(
			eventCategoryEntity("e.1", "d.1"),
			eventCategoryEntity("e.2", "d.1")));
		eventCategoryStagingRepository.save(eventCategoryStagingEntity("e.2", "d.1"));
		entityManager.flush();
		entityManager.clear();

		eventCategoryRepository.deleteEventRelationsForStagedEventIds();
		entityManager.clear();

		assertThat(eventCategoryRepository.findAll())
			.extracting(EventCategoryEntity::getEducationEventId, EventCategoryEntity::getDirectionId)
			.containsExactly(tuple("e.1", "d.1"));
	}

	@Test
	void deleteOnEmptyStaging() {
		eventCategoryRepository.saveAll(List.of(
			eventCategoryEntity("e.1", "d.1"),
			eventCategoryEntity("e.2", "d.1")));
		entityManager.flush();
		entityManager.clear();

		eventCategoryRepository.deleteEventRelationsForStagedEventIds();
		entityManager.clear();

		assertThat(eventCategoryRepository.findAll())
			.extracting(EventCategoryEntity::getEducationEventId, EventCategoryEntity::getDirectionId)
			.containsExactly(tuple("e.1", "d.1"), tuple("e.2", "d.1"));
	}

	@Test
	void insertCopiesToStaging() {
		eventCategoryStagingRepository.saveAll(List.of(
			eventCategoryStagingEntity("e.1", "d.1"),
			eventCategoryStagingEntity("e.1", "d.2")));
		entityManager.flush();
		entityManager.clear();

		eventCategoryRepository.saveStagedToEventCategory();
		entityManager.clear();

		assertThat(eventCategoryRepository.findAll())
			.extracting(EventCategoryEntity::getEducationEventId, EventCategoryEntity::getDirectionId)
			.containsExactly(tuple("e.1", "d.1"), tuple("e.1", "d.2"));
	}

	@Test
	void insertDeduplicates() {

		eventCategoryRepository.saveAll(List.of(
			eventCategoryEntity("e.1", "d.1"),
			eventCategoryEntity("e.1", "d.2")));
		entityManager.flush();
		entityManager.clear();

		eventCategoryStagingRepository.saveAll(List.of(
			eventCategoryStagingEntity("e.1", "d.1"),
			eventCategoryStagingEntity("e.1", "d.1")));
		entityManager.clear();

		assertThat(eventCategoryRepository.findAll())
			.extracting(EventCategoryEntity::getEducationEventId, EventCategoryEntity::getDirectionId)
			.containsOnly(tuple("e.1", "d.1"), tuple("e.1", "d.2"));

	}

	@Test
	void deleteAndInsertTogether() {
		eventCategoryRepository.saveAll(List.of(
			eventCategoryEntity("e.1", "d.1"),
			eventCategoryEntity("e.1", "d.2"),
			eventCategoryEntity("e.2", "d.1")));

		eventCategoryStagingRepository.saveAll(List.of(
			eventCategoryStagingEntity("e.1", "d.3")));
		entityManager.flush();
		entityManager.clear();

		eventCategoryRepository.deleteEventRelationsForStagedEventIds();
		eventCategoryRepository.saveStagedToEventCategory();
		entityManager.clear();

		assertThat(eventCategoryRepository.findAll())
			.extracting(EventCategoryEntity::getEducationEventId, EventCategoryEntity::getDirectionId)
			.containsExactly(tuple("e.1", "d.3"), tuple("e.2", "d.1"));
	}

	private EventCategoryEntity eventCategoryEntity(final String eventId, final String directionId) {
		return EventCategoryEntity.builder()
			.withEducationEventId(eventId)
			.withDirectionId(directionId)
			.build();
	}

	private EventCategoryStagingEntity eventCategoryStagingEntity(final String eventId, final String directionId) {
		return EventCategoryStagingEntity.builder()
			.withEducationEventId(eventId)
			.withDirectionId(directionId)
			.build();
	}
}
