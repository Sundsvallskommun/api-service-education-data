package se.sundsvall.educationdata.integration.db.model;

import java.time.LocalDate;
import java.time.Month;
import java.util.Random;
import java.util.UUID;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class EducationProviderEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(EducationProviderEntity.class, allOf(
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
		var educationProviderId = "p.sv.16931486";
		var name = "Sundsvalls gymnasium";
		var url = "https://sundsvall.se/gymnasium";
		var city = "Sundsvall";
		var municipalityId = "2281";
		var createdAt = LocalDate.of(2026, Month.JUNE, 7);

		// Act
		var provider = EducationProviderEntity.builder()
			.withId(id)
			.withEducationProviderId(educationProviderId)
			.withName(name)
			.withUrl(url)
			.withCity(city)
			.withMunicipalityId(municipalityId)
			.withCreatedAt(createdAt)
			.build();

		// Assert
		assertThat(provider.getId()).isEqualTo(id);
		assertThat(provider.getEducationProviderId()).isEqualTo(educationProviderId);
		assertThat(provider.getName()).isEqualTo(name);
		assertThat(provider.getUrl()).isEqualTo(url);
		assertThat(provider.getCity()).isEqualTo(city);
		assertThat(provider.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(provider.getCreatedAt()).isEqualTo(createdAt);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(EducationProviderEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new EducationProviderEntity()).hasAllNullFieldsOrProperties();
	}

}
