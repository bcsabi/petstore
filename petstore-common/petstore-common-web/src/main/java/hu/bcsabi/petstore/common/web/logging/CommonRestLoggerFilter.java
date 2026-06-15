package hu.bcsabi.petstore.common.web.logging;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Common REST logging filter for http requests and responses.
 *
 * <p>Ordered just after Spring's {@code ServerHttpObservationFilter} (which runs at
 * {@link Ordered#HIGHEST_PRECEDENCE} {@code + 1}) so the request runs inside the tracing
 * scope and the log lines carry the {@code traceId}, yet still ahead of the security filter
 * chain so that rejected requests are logged too.
 *
 * @author csaba.baloghł
 * @since 0.1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class CommonRestLoggerFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CommonRestLoggerFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logRequestLine(request);

        try {
            filterChain.doFilter(request, response);
        } finally {
            logResponseLine(request, response);
        }
    }

    private void logRequestLine(HttpServletRequest request) {
        if (LOG.isInfoEnabled()) {
            String requestTarget = requestTarget(request);
            LOG.info("Incoming request: {}", requestTarget);
        }
    }

    private void logResponseLine(HttpServletRequest request, HttpServletResponse response) {
        if (LOG.isInfoEnabled()) {
            String requestTarget = requestTarget(request);
            LOG.info("Outgoing response: {}, status: {}", requestTarget, response.getStatus());
        }
    }

    private String requestTarget(HttpServletRequest request) {
        StringBuilder target = new StringBuilder()
            .append(request.getMethod())
            .append(' ')
            .append(request.getRequestURI());

        if (request.getQueryString() != null) {
            target.append('?').append(request.getQueryString());
        }

        return target.toString();
    }

}
