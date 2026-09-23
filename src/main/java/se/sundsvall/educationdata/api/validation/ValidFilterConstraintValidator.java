package se.sundsvall.educationdata.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidFilterConstraintValidator implements ConstraintValidator<ValidFilter, String> {

	private static final String ERROR_MESSAGE_TEMPLATE = "given value %s is not valid, valid values are %s";
	private static final List<String> VALID_EDUCATION_VALUES = List.of("lectureType", "languageOfInstructions", "studyPace", "studyLocation");
	// Reserved for the statistics resource, which will be merged from a separate branch in the next PR
	private static final List<String> VALID_STATISTICS_VALUES = List.of();
	private List<String> validValues;

	@Override
	public void initialize(final ValidFilter constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
		if (constraintAnnotation.type() == FilterType.EDUCATION) {
			validValues = VALID_EDUCATION_VALUES;
		} else {
			validValues = VALID_STATISTICS_VALUES;
		}
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		var valid = validValues.contains(value);

		if (!valid) {
			useCustomMessageForValidation(context, ERROR_MESSAGE_TEMPLATE.formatted(value, validValues));
		}
		return valid;
	}

	private void useCustomMessageForValidation(final ConstraintValidatorContext constraintValidatorContext, final String message) {
		constraintValidatorContext.disableDefaultConstraintViolation();
		constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
	}
}
