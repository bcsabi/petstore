package hu.bcsabi.petstore.order.grpc.exception;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.grpc.server.advice.GrpcAdvice;
import org.springframework.grpc.server.advice.GrpcExceptionHandler;

import hu.bcsabi.petstore.common.core.exception.BadRequestException;
import hu.bcsabi.petstore.common.core.exception.BaseException;
import hu.bcsabi.petstore.common.core.exception.BusinessException;
import hu.bcsabi.petstore.common.core.exception.BusinessObjectNotFoundException;
import hu.bcsabi.petstore.common.core.exception.TechnicalException;
import hu.bcsabi.petstore.common.web.config.ProblemProperties;
import hu.bcsabi.petstore.common.web.config.ProblemProperties.IncludeDetail;

import io.grpc.Status;

/**
 * Maps the application exceptions to gRPC statuses, mirroring the REST
 * {@code CommonRestExceptionHandler}. The status description carries the same
 * localized problem title as the REST {@code title}; the raw exception message is
 * never exposed to the client.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@GrpcAdvice
public class GrpcExceptionAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcExceptionAdvice.class);

    private final MessageSource messageSource;
    private final ProblemProperties problemProperties;

    public GrpcExceptionAdvice(MessageSource messageSource, ProblemProperties problemProperties) {
        this.messageSource = messageSource;
        this.problemProperties = problemProperties;
    }

    @GrpcExceptionHandler(BadRequestException.class)
    public Status handleBadRequest(BadRequestException exception) {
        return toStatus(Status.INVALID_ARGUMENT, exception);
    }

    @GrpcExceptionHandler(BusinessObjectNotFoundException.class)
    public Status handleNotFound(BusinessObjectNotFoundException exception) {
        return toStatus(Status.NOT_FOUND, exception);
    }

    @GrpcExceptionHandler(BusinessException.class)
    public Status handleBusiness(BusinessException exception) {
        return toStatus(Status.FAILED_PRECONDITION, exception);
    }

    @GrpcExceptionHandler(TechnicalException.class)
    public Status handleTechnical(TechnicalException exception) {
        LOG.error("Technical failure", exception);
        return toStatus(Status.INTERNAL, exception);
    }

    @GrpcExceptionHandler(Exception.class)
    public Status handleUnexpected(Exception exception) {
        LOG.error("Unexpected failure", exception);
        return Status.INTERNAL.withDescription("Internal server error");
    }

    private Status toStatus(Status status, BaseException exception) {
        String title = getLocalizedTitle(exception.getProblemType().code());

        if (problemProperties.includeDetail() == IncludeDetail.ALWAYS) {
            return status.withDescription(title + ": " + exception.getMessage());
        }

        return status.withDescription(title);
    }

    private String getLocalizedTitle(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageCode = "problem." + code + ".title";
        return messageSource.getMessage(messageCode, null, messageCode, locale);
    }

}
