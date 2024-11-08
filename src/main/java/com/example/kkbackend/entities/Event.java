package com.example.kkbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id")
    private Movie movie;
    private String language;
    private java.sql.Date date;
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<TelegramMessage> telegramMessages = new ArrayList<>();
    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
    private Set<Member> members = new HashSet<>();
}
