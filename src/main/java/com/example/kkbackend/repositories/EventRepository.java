package com.example.kkbackend.repositories;

import com.example.kkbackend.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID>, CustomEventRepository {
    @EntityGraph(value = "event-entity-graph-with-movies")
    @Override
    List<Event> findAll();

    @EntityGraph(value = "event-entity-graph-with-movies")
    Page<Event> findAllByOrderByDateDesc(Pageable pageable);

    @EntityGraph(value = "event-entity-graph-with-movies")
    List<Event> findAllByDateBetween(Date publicationTimeStart, Date publicationTimeEnd);

    Event findFirstByOrderByDateDesc();
}
