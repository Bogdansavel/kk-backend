package com.example.kkbackend.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record EventMovieDto (MovieDto movie, String language, String date, List<MemberDto> members) {
}
