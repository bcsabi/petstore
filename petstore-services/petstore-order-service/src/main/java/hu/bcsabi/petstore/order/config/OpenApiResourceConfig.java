package hu.bcsabi.petstore.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves the static OpenAPI contract under {@code /openapi/**}.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Configuration
public class OpenApiResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/openapi/**")
            .addResourceLocations("classpath:/openapi/");
    }

}
