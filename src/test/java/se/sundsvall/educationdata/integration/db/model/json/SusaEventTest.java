package se.sundsvall.educationdata.integration.db.model.json;

import java.time.LocalDate;
import java.time.Month;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

public class SusaEventTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(SusaEvent.class, allOf(
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
		var jsonBody = "json body in text";
		var dateCollected = LocalDate.of(2025, Month.JUNE, 1);

		// Act
		var bean = PlannedEducationVuxen.builder()
			.withId(id)
			.withJsonBody(jsonBody)
			.withDateCollected(dateCollected).build();

		// Assert
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getJsonBody()).isEqualTo(jsonBody);
		assertThat(bean.getDateCollected()).isEqualTo(dateCollected);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SusaEvent.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SusaEvent()).hasAllNullFieldsOrProperties();
	}
}
