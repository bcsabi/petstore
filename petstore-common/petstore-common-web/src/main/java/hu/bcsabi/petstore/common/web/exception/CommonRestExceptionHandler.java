package hu.bcsabi.petstore.common.web.exception;

import java.net.URI;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.UriComponentsBuilder;

import hu.bcsabi.petstore.common.core.exception.BadRequestException;
import hu.bcsabi.petstore.common.core.exception.BaseException;
import hu.bcsabi.petstore.common.core.exception.BusinessException;
import hu.bcsabi.petstore.common.core.exception.BusinessObjectNotFoundException;
import hu.bcsabi.petstore.common.core.exception.TechnicalException;
import hu.bcsabi.petstore.common.web.config.ProblemProperties;
import hu.bcsabi.petstore.common.web.config.ProblemProperties.IncludeDetail;

/**
 * Maps the application exceptions to RFC 9457 problem responses.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@RestControllerAdvice
public class CommonRestExceptionHandler {

    private static final String UNEXPECTED_ERROR_CODE = "unexpected-error";

    private static final Logger LOG = LoggerFactory.getLogger(CommonRestExceptionHandler.class);

    private final MessageSource messageSource;
    private final ProblemProperties problemProperties;

    public CommonRestExceptionHandler(MessageSource messageSource, ProblemProperties problemProperties) {
        this.messageSource = messageSource;
        this.problemProperties = problemProperties;
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException exception) {
        LOG.error("Bad request exception occurred", exception);
        return toProblemDetail(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(BusinessObjectNotFoundException.class)
    public ProblemDetail handleNotFound(BusinessObjectNotFoundException exception) {
        LOG.error("Business object not found", exception);
        return toProblemDetail(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException exception) {
        LOG.error("Business exception occurred", exception);
        return toProblemDetail(HttpStatus.UNPROCESSABLE_CONTENT, exception);
    }

    @ExceptionHandler(TechnicalException.class)
    public ProblemDetail handleTechnical(TechnicalException exception) {
        LOG.error("Technical exception occurred", exception);
        return toProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception exception) {
        LOG.error("Unexpected exception occurred", exception);
        return toProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    private ProblemDetail toProblemDetail(HttpStatus status, Exception exception) {
        String code = exception instanceof BaseException be
            ? be.getProblemType().code()
            : UNEXPECTED_ERROR_CODE;

        String title = getLocalizedTitle(code);
        URI type = UriComponentsBuilder.fromUriString(problemProperties.baseUri()).pathSegment(code).build().toUri();

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(type);
        problemDetail.setTitle(title);

        if (problemProperties.includeDetail() == IncludeDetail.ALWAYS) {
            problemDetail.setDetail(exception.getMessage());
        }

        return problemDetail;
    }

    private String getLocalizedTitle(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageCode = "problem." + code + ".title";
        return messageSource.getMessage(messageCode, null, messageCode, locale);
    }

}
