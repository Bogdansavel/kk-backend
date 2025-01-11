package com.example.kkbackend.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class KinopoiskDbClient {
    @Value("${kinopoisk-db.token}")
    private String token;

    private final RestClient kinopoiskDbRestClient;

    public String getMovieByKinopoiskId(int kinopoiskId) {
        return kinopoiskDbRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("movie")
                        .pathSegment(String.valueOf(kinopoiskId))
                        .build())
                .header("X-API-KEY", token)
                .retrieve()
                .body(String.class);
    }
}
