package hu.bcsabi.petstore.order.security;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import hu.bcsabi.petstore.common.web.config.ProblemProperties;
import hu.bcsabi.petstore.common.web.config.ProblemProperties.IncludeDetail;
import hu.bcsabi.petstore.order.problem.OrderProblemType;

import tools.jackson.databind.ObjectMapper;

/**
 * Writes an RFC 9457 problem response for unauthenticated requests.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Component
public class ProblemDetailAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ProblemProperties problemProperties;
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    public ProblemDetailAuthenticationEntryPoint(ProblemProperties problemProperties, MessageSource messageSource, ObjectMapper objectMapper) {
        this.problemProperties = problemProperties;
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        String code = OrderProblemType.UNAUTHORIZED.code();
        String title = getLocalizedTitle(code, request.getLocale());
        URI type = UriComponentsBuilder.fromUriString(problemProperties.baseUri()).pathSegment(code).build().toUri();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setType(type);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        if (problemProperties.includeDetail() == IncludeDetail.ALWAYS) {
            problemDetail.setDetail(authenticationException.getMessage());
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problemDetail);
    }

    private String getLocalizedTitle(String code, Locale locale) {
        String messageCode = "problem." + code + ".title";
        return messageSource.getMessage(messageCode, null, messageCode, locale);
    }
}
