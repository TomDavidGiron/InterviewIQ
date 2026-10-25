package com.cvoptimizer.cv_backend.config;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // Tika pulls in an old Brotli decoder (org.brotli:dec), which makes Apache HttpClient
                // advertise "br". OpenAI then replies Brotli-compressed and the body decodes to just "{",
                // so every AI call failed to parse. Ask servers for uncompressed responses instead.
                .requestFactoryBuilder(ClientHttpRequestFactoryBuilder.httpComponents()
                        .withHttpClientCustomizer(HttpClientBuilder::disableContentCompression))
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(60))
                .build();
    }
}
