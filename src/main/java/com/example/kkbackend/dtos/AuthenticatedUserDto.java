package com.example.kkbackend.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthenticatedUserDto {
    @NotNull
    private Long authDate;

    private String firstName;

    @NotEmpty
    private String id;

    private String lastName;

    private String photoUrl;

    private String username;

    @NotEmpty
    private String hash;
}
