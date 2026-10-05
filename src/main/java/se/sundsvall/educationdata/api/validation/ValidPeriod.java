package se.sundsvall.educationdata.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPeriodConstraintValidator.class)
public @interface ValidPeriod {

	String message() default "startDate must be on or before endDate";

	String start() default "startDate";

	String end() default "endDate";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
