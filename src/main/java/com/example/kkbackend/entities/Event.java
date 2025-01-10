package com.example.kkbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@NamedEntityGraph(
        name = "event-entity-graph-with-movies",
        attributeNodes = {
                @NamedAttributeNode(value = "movie", subgraph = "movie-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "movie-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "ratings", subgraph = "member-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "member-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("member")
                        }
                )
        }
)
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
