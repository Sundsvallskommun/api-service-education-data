package se.sundsvall.educationdata;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import se.sundsvall.dept44.ServiceApplication;
import se.sundsvall.dept44.util.jacoco.ExcludeFromJacocoGeneratedCoverageReport;

import static org.springframework.boot.SpringApplication.run;

@EnableAsync
@EnableFeignClients
@ServiceApplication
@ExcludeFromJacocoGeneratedCoverageReport
public class Application {
	public static void main(final String... args) {
		run(Application.class, args);
	}
}
