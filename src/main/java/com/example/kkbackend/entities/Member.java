package com.example.kkbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String userName;
    private String photoUrl;
    @Column(nullable = false)
    private boolean freshBlood;
    @Column(nullable = false)
    private String firstName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Event_Member",
            joinColumns = { @JoinColumn(name = "member_id") },
            inverseJoinColumns = { @JoinColumn(name = "event_id") }
    )
    private Set<Event> events;

    @OneToMany(mappedBy = "member")
    private List<Rate> ratings = new ArrayList<>();
}
