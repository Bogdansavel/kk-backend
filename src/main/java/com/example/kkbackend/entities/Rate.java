package com.example.kkbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int rating;
    private boolean liked;
    private boolean discussable;
    @Column(length = 1000)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public boolean isCommented() {
        return comment != null && !comment.isEmpty();
    }
}
