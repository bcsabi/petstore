package hu.bcsabi.petstore.it;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("integration")
@SpringBootTest(classes = ITConfig.class)
public abstract class AbstractIT {

    protected static final String API_KEY_HEADER = "x-api-key";

    protected static final String PROBLEM_BASE = "https://api.petstore.hu/errors";

    @Value("${project.base-url}")
    protected String baseUrl;

    @Value("${project.api-key}")
    protected String apiKey;

}
