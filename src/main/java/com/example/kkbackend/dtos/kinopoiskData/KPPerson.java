package com.example.kkbackend.dtos.kinopoiskData;

import java.util.Objects;

public record KPPerson(int id, String photo, String name, String enName, String description, String profession,
                       String enProfession) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        KPPerson kpPerson = (KPPerson) o;
        return id == kpPerson.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
