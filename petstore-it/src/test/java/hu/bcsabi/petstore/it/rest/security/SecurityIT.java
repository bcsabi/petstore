package hu.bcsabi.petstore.it.rest.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import hu.bcsabi.petstore.it.AbstractIT;

/**
 * Verifies the x-api-key enforcement and that the rejection is an RFC 9457 problem response.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
class SecurityIT extends AbstractIT {

    private RestTestClient restTestClient;

    @BeforeEach
    void beforeEach() {
        this.restTestClient = RestTestClient.bindToServer()
            .baseUrl(baseUrl + "/api/store/order")
            .build();
    }

    @Test
    void shouldReturn401WhenApiKeyIsMissing() {
        restTestClient.get()
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(PROBLEM_BASE + "/unauthorized");
    }

    @Test
    void shouldReturn401WhenApiKeyIsInvalid() {
        restTestClient.get()
            .header(API_KEY_HEADER, "definitely-wrong-key")
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(PROBLEM_BASE + "/unauthorized");
    }

}
