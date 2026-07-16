package apptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.educationdata.Application;
import se.sundsvall.educationdata.integration.db.ReferenceCategoryRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationEventRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationInfoRepository;
import se.sundsvall.educationdata.integration.db.SusaEducationProviderRepository;

import java.time.Duration;

@WireMockAppTestSuite(files = "classpath:/SchedulerIT/", classes = Application.class)
public class SchedulerResourceIT extends AbstractAppTest {

    @Autowired
    private ReferenceCategoryRepository referenceCategoryRepository;
    @Autowired
    private SusaEducationEventRepository eventRepository;
    @Autowired
    private SusaEducationInfoRepository infoRepository;
    @Autowired
    private SusaEducationProviderRepository providerRepository;

    private static final String MUNICIPALITY = "2281";
    private static final String PATH = "/" + MUNICIPALITY + "/scheduler/trigger";
    private static final String INVALID_PATH = "/9999/scheduler/trigger";


    @Test
    void test01_triggerImport_Accepted() {
        setupCall()
            .withServicePath(PATH)
            .withHttpMethod(POST)
            .withExpectedResponseStatus(ACCEPTED)
            .sendRequestAndVerifyResponse();

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            assertThat(referenceCategoryRepository.count()).isGreaterThan(0);
            assertThat(eventRepository.count()).isGreaterThan(0);
            assertThat(infoRepository.count()).isGreaterThan(0);
            assertThat(providerRepository.count()).isGreaterThan(0);
    });
    }

    @Test
    void test02_triggerImport_BadRequest() {
        setupCall()
                .withServicePath(INVALID_PATH)
                .withHttpMethod(POST)
                .withExpectedResponseStatus(BAD_REQUEST)
                .sendRequestAndVerifyResponse();
    }
}
