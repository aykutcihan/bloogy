package com.example.bloogy.configration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuration class to handle Cross-Origin Resource Sharing (CORS) settings.
 * This ensures that the API can accept requests from different domains.
 */
@Configuration
public class CorsConfig {

    /**
     * Configures a CORS filter to define the allowed origins, headers, and methods.
     *
     * @return a CorsFilter with the specified configuration.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Allow cookies and authentication headers
        config.addAllowedOrigin("http://localhost:18080");
        config.addAllowedOrigin("http://localhost:18081");
        config.addAllowedHeader("*"); // Allow all headers
        config.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, PUT, DELETE, etc.)

        source.registerCorsConfiguration("/**", config); // Apply this configuration to all endpoints
        return new CorsFilter(source);
    }
}
