package com.example.kkbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RegistrationInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String gender;
    private String contact;
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
