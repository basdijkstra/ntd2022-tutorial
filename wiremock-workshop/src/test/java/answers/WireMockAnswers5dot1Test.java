package answers;

import answers.extensions.HttpDeleteFilter;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;

public class WireMockAnswers5dot1Test {

    private RequestSpecification requestSpec;

    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance().
            options(wireMockConfig().
                    port(9876).
                    extensions(new HttpDeleteFilter())
            ).build();

    @BeforeEach
    public void createRequestSpec() {

        requestSpec = new RequestSpecBuilder().
            setBaseUri("http://localhost").
            setPort(9876).
            build();
    }

    public void setupStubExercise5dot1() {

        wiremock.stubFor(any(urlEqualTo("/http-delete-filter"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("Your request has been processed!")
            ));
    }

    @Test
    public void anIncomingHttpGETShouldBeProcessedNormally() {

        /***
         * Use this test to test your implementation of the request filter
         * This request should be processed normally as it uses an HTTP GET
         */

        setupStubExercise5dot1();

        given().
            spec(requestSpec).
        when().
            get("/http-delete-filter").
        then().
            assertThat().
            statusCode(200).
        and().
            body(org.hamcrest.Matchers.equalTo("Your request has been processed!"));
    }

    @Test
    public void anIncomingHttpDELETEShouldBeFilteredOut() {

        /***
         * Use this test to test your implementation of the request filter
         * This request should be filtered out as it uses an HTTP DELETE
         */

        given().
            spec(requestSpec).
        when().
            delete("/http-delete-filter").
        then().
            assertThat().
            statusCode(403).
        and().
            body("errors[0].title", org.hamcrest.Matchers.equalTo("HTTP DELETE is not allowed!"));
    }
}
