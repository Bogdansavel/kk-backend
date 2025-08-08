package com.example.kkbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
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
    private String ratePhotoName;
    @Column(length = 10000)
    private String posterUrl;
    @Column(columnDefinition="TEXT")
    private String kinopoiskData;
    private Boolean isReady;
    private Integer language;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @OneToMany(mappedBy = "movie")
    private List<Rate> ratings = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    private Round round;

    public int averageRating() {
        if (ratings.isEmpty()) {
            return 0;
        }
        return ratings.stream().map(Rate::getRating).mapToInt(Integer::intValue).sum() / ratings.size();
    }

    public int getControversy() {
        var max = ratings.stream().map(Rate::getRating).max(Comparator.naturalOrder());
        var min = ratings.stream().map(Rate::getRating).min(Comparator.naturalOrder());
        if (max.isEmpty() || min.isEmpty()) {return 0;}
        return max.get() - min.get();
    }
}
