package hu.bcsabi.petstore.common.grpc.exception;

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
import hu.bcsabi.petstore.common.grpc.config.GrpcProblemProperties;
import hu.bcsabi.petstore.common.grpc.config.GrpcProblemProperties.IncludeDetail;

import io.grpc.Status;

/**
 * Maps the application exceptions to gRPC statuses.
 * Mmirroring the REST {@code CommonRestExceptionHandler}.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@GrpcAdvice
public class CommonGrpcExceptionAdvice {

    private static final String UNEXPECTED_ERROR_CODE = "unexpected-error";

    private static final Logger LOG = LoggerFactory.getLogger(CommonGrpcExceptionAdvice.class);

    private final MessageSource messageSource;
    private final GrpcProblemProperties problemProperties;

    public CommonGrpcExceptionAdvice(MessageSource messageSource, GrpcProblemProperties problemProperties) {
        this.messageSource = messageSource;
        this.problemProperties = problemProperties;
    }

    @GrpcExceptionHandler(BadRequestException.class)
    public Status handleBadRequest(BadRequestException exception) {
        LOG.error("Bad request exception occurred", exception);
        return toStatus(Status.INVALID_ARGUMENT, exception);
    }

    @GrpcExceptionHandler(BusinessObjectNotFoundException.class)
    public Status handleNotFound(BusinessObjectNotFoundException exception) {
        LOG.error("Business object not found", exception);
        return toStatus(Status.NOT_FOUND, exception);
    }

    @GrpcExceptionHandler(BusinessException.class)
    public Status handleBusiness(BusinessException exception) {
        LOG.error("Business exception occurred", exception);
        return toStatus(Status.FAILED_PRECONDITION, exception);
    }

    @GrpcExceptionHandler(TechnicalException.class)
    public Status handleTechnical(TechnicalException exception) {
        LOG.error("Technical exception occurred", exception);
        return toStatus(Status.INTERNAL, exception);
    }

    @GrpcExceptionHandler(Exception.class)
    public Status handleUnexpected(Exception exception) {
        LOG.error("Unexpected exception occurred", exception);
        return toStatus(Status.INTERNAL, exception);
    }

    private Status toStatus(Status status, Exception exception) {
        String code = exception instanceof BaseException be
            ? be.getProblemType().code()
            : UNEXPECTED_ERROR_CODE;

        String title = getLocalizedTitle(code);

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
