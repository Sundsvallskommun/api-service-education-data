package se.sundsvall.educationdata.integration.db.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

public class ReferenceCategoryTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(ReferenceCategory.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		var id = 1L;
		var categoryId = "categoryId";
		var categoryName = "categoryName";
		var directionName = "directionName";
		var directionId = "directionId";

		// Act
		var bean = ReferenceCategory.builder()
			.withId(id)
			.withCategoryId(categoryId)
			.withCategoryName(categoryName)
			.withDirectionId(directionId)
			.withDirectionName(directionName)
			.build();

		// Assert
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getCategoryId()).isEqualTo(categoryId);
		assertThat(bean.getCategoryName()).isEqualTo(categoryName);
		assertThat(bean.getDirectionId()).isEqualTo(directionId);
		assertThat(bean.getDirectionName()).isEqualTo(directionName);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ReferenceCategory.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new ReferenceCategory()).hasAllNullFieldsOrProperties();
	}
}
