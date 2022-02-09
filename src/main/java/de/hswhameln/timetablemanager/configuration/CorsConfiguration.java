package de.hswhameln.timetablemanager.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration {

    private final String allowedOrigin;

    public CorsConfiguration(
            @Value("${timetablemanager.allowed-origin}") String allowedOrigin
    ) {
        this.allowedOrigin = allowedOrigin;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins(CorsConfiguration.this.allowedOrigin);
            }
        };
    }
}
