package com.example.kkbackend.service;

import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.repositories.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    @Override
    public Movie getById(UUID id) {
        return movieRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                MessageFormat.format("Movie with id {0} doesn't exist!", id)));
    }
}
