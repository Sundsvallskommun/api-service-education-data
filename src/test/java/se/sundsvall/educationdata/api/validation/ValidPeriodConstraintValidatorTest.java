package se.sundsvall.educationdata.api.validation;

import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.educationdata.api.model.StatisticsParameters;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ValidPeriodConstraintValidatorTest {

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	private final ValidPeriodConstraintValidator validator = new ValidPeriodConstraintValidator();

	@ParameterizedTest(name = "start{0}, end{1}")
	@CsvSource(value = {
		"2026-05-01, 2026-11-01, true",
		"2026-05-01, 2026-05-01, true",
		"2026-11-01, 2026-05-01, false",
		"null, 2026-05-01, true",
		"2026-05-01, null, true",
		"null, null, true"
	}, nullValues = "null")
	void isValid(final LocalDate startDate, final LocalDate endDate, final boolean expected) {
		final var parameters = StatisticsParameters.builder()
			.withStartDate(startDate)
			.withEndDate(endDate)
			.build();
		assertThat(validator.isValid(parameters, constraintValidatorContextMock)).isEqualTo(expected);
	}

	@Test
	void isValidWhenPeriodIsNull() {
		assertThat(validator.isValid(null, constraintValidatorContextMock)).isTrue();
	}

}
