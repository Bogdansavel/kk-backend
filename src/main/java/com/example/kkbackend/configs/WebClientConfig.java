package com.example.kkbackend.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WebClientConfig {
    @Value("${kinopoisk-db.url}")
    private String kinopoiskDbUrl;

    @Bean
    public RestClient kinopoiskDbRestClient() {
        return buildRestClient(kinopoiskDbUrl);
    }

    private RestClient buildRestClient(String appUrl) {
        return RestClient.builder().baseUrl(appUrl).build();
    }
}
