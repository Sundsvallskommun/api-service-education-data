package se.sundsvall.educationdata.api.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidFilterConstraintValidatorTest {

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	@Mock
	private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilderMock;

	@Mock
	private ValidFilter validFilterMock;

	@InjectMocks
	private ValidFilterConstraintValidator validator;

	@ParameterizedTest
	@ValueSource(strings = {
		"lectureType", "languageOfInstructions", "studyPace", "studyLocation"
	})
	void validEducationFilterTest(final String educationFilter) {
		when(validFilterMock.type()).thenReturn(FilterType.EDUCATION);
		validator.initialize(validFilterMock);

		var valid = validator.isValid(educationFilter, constraintValidatorContextMock);
		assertThat(valid).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"invalid", "not-valid", "wrong", ""
	})
	void invalidEducationFilterTest(final String educationFilter) {
		when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);
		when(validFilterMock.type()).thenReturn(FilterType.EDUCATION);
		validator.initialize(validFilterMock);

		var valid = validator.isValid(educationFilter, constraintValidatorContextMock);
		assertThat(valid).isFalse();

		verify(constraintValidatorContextMock).disableDefaultConstraintViolation();
		verify(constraintValidatorContextMock).buildConstraintViolationWithTemplate("given value %s is not valid, valid values are %s".formatted(educationFilter, ReflectionTestUtils.getField(validator, "validValues")));
		verify(constraintViolationBuilderMock).addConstraintViolation();
	}
}
