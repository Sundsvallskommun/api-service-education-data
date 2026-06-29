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

public class GyProgramCategoryTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(GyProgramCategory.class, allOf(
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
		var programName = "programName";
		var programCode = "programCode";
		boolean vocational = true;
		var category = "category";
		// Act
		var bean = GyProgramCategory.builder()
			.withId(id)
			.withProgramName(programName)
			.withProgramCode(programCode)
			.withVocational(vocational)
			.withCategory(category)
			.build();

		// Assert
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getProgramName()).isEqualTo(programName);
		assertThat(bean.getProgramCode()).isEqualTo(programCode);
		assertThat(bean.getVocational()).isEqualTo(vocational);
		assertThat(bean.getCategory()).isEqualTo(category);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(GyProgramCategory.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new GyProgramCategory()).hasAllNullFieldsOrProperties();
	}
}
