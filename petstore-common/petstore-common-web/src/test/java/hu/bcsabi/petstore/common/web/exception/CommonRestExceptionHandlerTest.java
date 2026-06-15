package hu.bcsabi.petstore.common.web.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import hu.bcsabi.petstore.common.core.exception.BadRequestException;
import hu.bcsabi.petstore.common.core.exception.BusinessException;
import hu.bcsabi.petstore.common.core.exception.BusinessObjectNotFoundException;
import hu.bcsabi.petstore.common.core.exception.TechnicalException;
import hu.bcsabi.petstore.common.core.problem.ProblemType;
import hu.bcsabi.petstore.common.web.config.ProblemProperties;
import hu.bcsabi.petstore.common.web.config.ProblemProperties.IncludeDetail;

/**
 * Unit tests for {@link CommonRestExceptionHandler}.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
class CommonRestExceptionHandlerTest {

    private static final String BASE_URI = "https://api.petstore.example/errors";

    private static final String TEST_PROBLEM_CODE = "problem.test-problem.title";
    private static final String TEST_PROBLEM_ENG = "Test problem";

    private final StaticMessageSource messageSource = new StaticMessageSource();

    private CommonRestExceptionHandler handler;

    @BeforeEach
    void beforeEach() {
        messageSource.addMessage(TEST_PROBLEM_CODE, Locale.ENGLISH, TEST_PROBLEM_ENG);
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        handler = new CommonRestExceptionHandler(messageSource, new ProblemProperties(BASE_URI, IncludeDetail.ALWAYS));
    }

    @Nested
    class BadRequestTest {

        private static final HttpStatus EXPECTED_STATUS = HttpStatus.BAD_REQUEST;

        @Test
        void shouldMapBadRequestException() {
            // given
            BadRequestException exception = new BadRequestException(TestProblemType.TEST, "Bad request occurred");

            // when
            ProblemDetail result = handler.handleBadRequest(exception);

            // then
            assertThat(result.getStatus()).isEqualTo(EXPECTED_STATUS.value());
            assertThat(result.getType()).isEqualTo(URI.create(BASE_URI + "/" + TestProblemType.TEST.code()));
            assertThat(result.getTitle()).isEqualTo(TEST_PROBLEM_ENG);
            assertThat(result.getDetail()).isEqualTo(exception.getMessage());
        }
    }

    @Nested
    class NotFoundTest {

        private static final HttpStatus EXPECTED_STATUS = HttpStatus.NOT_FOUND;

        @Test
        void shouldMapBusinessObjectNotFoundException() {
            // given
            BusinessObjectNotFoundException exception = new BusinessObjectNotFoundException(TestProblemType.TEST, "Order not found");

            // when
            ProblemDetail result = handler.handleNotFound(exception);

            // then
            assertThat(result.getStatus()).isEqualTo(EXPECTED_STATUS.value());
            assertThat(result.getType()).isEqualTo(URI.create(BASE_URI + "/" + TestProblemType.TEST.code()));
            assertThat(result.getTitle()).isEqualTo(TEST_PROBLEM_ENG);
            assertThat(result.getDetail()).isEqualTo(exception.getMessage());
        }
    }

    @Nested
    class UnprocessableContentTest {

        private static final HttpStatus EXPECTED_STATUS = HttpStatus.UNPROCESSABLE_CONTENT;

        @Test
        void shouldMapBusinessException() {
            // given
            BusinessException exception = new BusinessException(TestProblemType.TEST, "Pet is not available");

            // when
            ProblemDetail result = handler.handleBusiness(exception);

            // then
            assertThat(result.getStatus()).isEqualTo(EXPECTED_STATUS.value());
            assertThat(result.getType()).isEqualTo(URI.create(BASE_URI + "/" + TestProblemType.TEST.code()));
            assertThat(result.getTitle()).isEqualTo(TEST_PROBLEM_ENG);
            assertThat(result.getDetail()).isEqualTo(exception.getMessage());
        }
    }

    @Nested
    class InternalServerErrorTest {

        private static final HttpStatus EXPECTED_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

        @Test
        void shouldMapTechnicalException() {
            // given
            TechnicalException exception = new TechnicalException(TestProblemType.TEST, "Unexpected failure");

            // when
            ProblemDetail result = handler.handleTechnical(exception);

            // then
            assertThat(result.getStatus()).isEqualTo(EXPECTED_STATUS.value());
            assertThat(result.getType()).isEqualTo(URI.create(BASE_URI + "/" + TestProblemType.TEST.code()));
            assertThat(result.getTitle()).isEqualTo(TEST_PROBLEM_ENG);
            assertThat(result.getDetail()).isEqualTo(exception.getMessage());
        }
    }

    private enum TestProblemType implements ProblemType {

        TEST;

        @Override
        public String code() {
            return "test-problem";
        }
    }

}
