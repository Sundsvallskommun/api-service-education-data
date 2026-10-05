package se.sundsvall.educationdata.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPeriodConstraintValidator implements ConstraintValidator<ValidPeriod, DatePeriod> {

	@Override
	public boolean isValid(final DatePeriod period, final ConstraintValidatorContext context) {
		if (period == null || period.getStartDate() == null || period.getEndDate() == null) {
			return true;
		}
		return !period.getStartDate().isAfter(period.getEndDate());
	}
}
