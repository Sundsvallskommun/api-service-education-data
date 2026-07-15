package se.sundsvall.educationdata.integration.db.model;

import java.util.UUID;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class EventCategoryEntityTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(EventCategoryEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		var id = UUID.randomUUID().toString();
		var educationEventId = "educationEventId";
		var directionId = "directionId";

		// Act
		var bean = EventCategoryEntity.builder()
			.withId(id).withEducationEventId(educationEventId).withDirectionId(directionId)
			.build();

		// Assert
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getEducationEventId()).isEqualTo(educationEventId);
		assertThat(bean.getDirectionId()).isEqualTo(directionId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(EventCategoryEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new EventCategoryEntity()).hasAllNullFieldsOrProperties();
	}
}
