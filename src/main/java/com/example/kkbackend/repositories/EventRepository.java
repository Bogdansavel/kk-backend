package com.example.kkbackend.repositories;

import com.example.kkbackend.entities.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    @EntityGraph(value = "event-entity-graph-with-movies")
    List<Event> findAll();
}
