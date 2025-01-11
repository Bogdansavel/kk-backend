package com.example.kkbackend.dtos.kinopoiskData;

public record KPPerson (
        int id,
        String photo,
        String name,
        String enName,
        String description,
        String profession,
        String enProfession
) {
}
