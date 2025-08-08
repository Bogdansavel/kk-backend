package com.example.kkbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
                                @NamedAttributeNode(value = "ratings", subgraph = "member-subgraph"),
                                @NamedAttributeNode("member")
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
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TelegramMessage> telegramMessages = new ArrayList<>();
    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
    private Set<Member> members = new HashSet<>();
    @Column(length = 10000)
    private String description;
    private String posterUrl;

    @PreRemove
    private void removeMemberAssociations() {
        for (Member member : this.getMembers()) {
            member.getEvents().remove(this);
        }
    }
}
