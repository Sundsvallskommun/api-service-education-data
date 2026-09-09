package se.sundsvall.educationdata.apptest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.educationdata.Application;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

@Sql(scripts = "/db/scripts/testdata.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@WireMockAppTestSuite(files = "classpath:/EducationIT/", classes = Application.class)
class EducationIT extends AbstractAppTest {

    private static final String RESPONSE_FILE = "response.json";
    private static final String PATH = "/2281/educations";

    @Test
    void test01_findAll(){
        setupCall()
                .withServicePath(PATH + "?limit=2&sortBy=id")
                .withHttpMethod(GET)
                .withExpectedResponseStatus(OK)
                .withExpectedResponse(RESPONSE_FILE)
                .sendRequestAndVerifyResponse();
    }

    @Test
    void test02_findById(){
        setupCall()
                .withServicePath(PATH + "/e.1")
                .withHttpMethod(GET)
                .withExpectedResponseStatus(OK)
                .withExpectedResponse(RESPONSE_FILE)
                .sendRequestAndVerifyResponse();
    }

    @Test
    void test03_findBySchoolType(){
        setupCall()
                .withServicePath(PATH + "?schoolType=VUXGY&limit=2&sortBy=id")
                .withHttpMethod(GET)
                .withExpectedResponseStatus(OK)
                .withExpectedResponse(RESPONSE_FILE)
                .sendRequestAndVerifyResponse();
    }

    @Test
    void test04_findStudyLocationValues(){
        setupCall()
                .withServicePath(PATH + "/filters/studyLocation/values")
                .withHttpMethod(GET)
                .withExpectedResponseStatus(OK)
                .withExpectedResponse(RESPONSE_FILE)
                .sendRequestAndVerifyResponse();
    }

}
