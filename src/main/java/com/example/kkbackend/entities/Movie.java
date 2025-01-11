package com.example.kkbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int kinopoiskId;
    private String name;
    private String photoName;
    private String posterUrl;
    private String kinopoiskData;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @OneToMany(mappedBy = "movie")
    private List<Rate> ratings = new ArrayList<>();

    public int averageRating() {
        if (ratings.isEmpty()) {
            return 0;
        }
        return ratings.stream().map(Rate::getRating).mapToInt(Integer::intValue).sum() / ratings.size();
    }
}
