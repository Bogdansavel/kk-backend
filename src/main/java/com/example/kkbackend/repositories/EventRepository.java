package com.example.kkbackend.repositories;

import com.example.kkbackend.entities.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID>, CustomEventRepository {
    Event findFirstByOrderByDateDesc();

    @Query(
            value = """
        select e
        from Event e
        left join fetch e.movie
        where e.movie.name like :movieName
        """,
            countQuery = """
        select count(e)
        from Event e
        left join fetch e.movie
        where e.movie.name like :movieName
        """
    )
    List<Event> findAllByMovieName(
            @Param("movieName") String movieName,
            Pageable pageable
    );

    @Query(
            value = """
        select e.id
        from Event e
        order by e.date desc
        """,
            countQuery = """
        select count(e)
        from Event e
        """
    )
    List<UUID> findAllMovies(
            Pageable pageable
    );

    @Query(value = """
    select distinct e
    from Event e
    left join fetch e.movie m
    left join fetch m.ratings rs
    left join fetch rs.member
    left join fetch m.member
    where e.id in :eventIds
    order by e.date desc
    """
    )
    List<Event> findAllByIdWithDetails(
            @Param("eventIds") List<UUID> eventIds
    );
}
