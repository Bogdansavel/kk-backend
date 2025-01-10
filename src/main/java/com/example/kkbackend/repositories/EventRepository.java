package com.example.kkbackend.repositories;

import com.example.kkbackend.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query("SELECT event FROM Event event " +
            "JOIN FETCH event.movie movie " +
            "JOIN FETCH movie.ratings ratings " +
            "JOIN FETCH event.members members")
    List<Event> getAllWithMovies();
}
