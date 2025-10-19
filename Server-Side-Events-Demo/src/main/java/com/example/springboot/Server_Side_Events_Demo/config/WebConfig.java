package com.example.springboot.Server_Side_Events_Demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/sse/events")
                        .allowedOrigins("http://localhost:63342")
                        .allowedMethods("GET")
                        .allowCredentials(true);

                registry.addMapping("/SSE/reactive-events")
                        .allowedOrigins("http://localhost:63342")
                        .allowedMethods("GET")
                        .allowCredentials(true);
            }
        };
    }
}
