package com.example.kkbackend.repositories;

import com.example.kkbackend.entities.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomEventRepositoryImpl implements CustomEventRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void customDelete(Event event) {
        entityManager.remove(event);
    }
}
