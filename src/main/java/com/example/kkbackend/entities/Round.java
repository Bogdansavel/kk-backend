package com.example.kkbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Round {
    @Id
    private long id;
    @OneToMany(mappedBy = "round", fetch = FetchType.LAZY)
    private List<Movie> movies = new LinkedList<>();

    @OneToOne
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    private TelegramMessage message;

    @OneToOne
    @JoinColumn(name = "poll_message_id", referencedColumnName = "id")
    private TelegramMessage pollMessage;

    @Column(nullable = false)
    boolean isActive = false;

    public Round(long id, boolean isActive) {
        this.id = id;
        this.isActive = isActive;
    }
}
