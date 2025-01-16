package com.example.kkbackend.dtos;

import java.util.List;

public record CountyEntryDto (
        String country, List<MovieDto> movies
) {
}
