package se.sundsvall.educationdata.api.validation;

import java.time.LocalDate;

public interface DatePeriod {
	LocalDate getStartDate();

	LocalDate getEndDate();
}
