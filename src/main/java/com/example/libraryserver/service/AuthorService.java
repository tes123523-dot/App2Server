package com.example.libraryserver.service;

import com.example.libraryserver.dto.AuthorDTO;
import com.example.libraryserver.model.Author;
import com.example.libraryserver.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Beginner-friendly service with validations and logging.
 */
@Service
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;
    private static final Logger log = LoggerFactory.getLogger(AuthorService.class);

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public AuthorDTO toDto(Author a) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(a.getId());
        dto.setFirstName(a.getFirstName());
        dto.setLastName(a.getLastName());
        dto.setBiography(a.getBiography());
        return dto;
    }

    public Author toEntity(AuthorDTO dto) {
        Author a = new Author();
        a.setId(dto.getId());
        a.setFirstName(dto.getFirstName());
        a.setLastName(dto.getLastName());
        a.setBiography(dto.getBiography());
        return a;
    }

    public List<AuthorDTO> listAll() {
        log.info("Fetching all authors");
        return authorRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<AuthorDTO> findById(Long id) {
        log.info("Fetching author by id={}", id);
        return authorRepository.findById(id).map(this::toDto);
    }

    public AuthorDTO create(AuthorDTO dto) {
        log.info("Creating author: {} {}", dto.getFirstName(), dto.getLastName());
        authorRepository.findByFirstNameAndLastName(dto.getFirstName(), dto.getLastName()).ifPresent(a -> {
            log.warn("Duplicate author detected: {} {}", dto.getFirstName(), dto.getLastName());
            throw new IllegalArgumentException("Author already exists");
        });
        Author a = toEntity(dto);
        Author saved = authorRepository.save(a);
        log.debug("Author saved with id={}", saved.getId());
        return toDto(saved);
    }

    public AuthorDTO update(Long id, AuthorDTO dto) {
        log.info("Updating author id={}", id);
        Author existing = authorRepository.findById(id).orElseThrow(() -> {
            log.error("Author with id={} not found", id);
            return new IllegalArgumentException("Author not found");
        });
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setBiography(dto.getBiography());
        Author updated = authorRepository.save(existing);
        log.debug("Author updated: id={}, name={} {}", updated.getId(), updated.getFirstName(), updated.getLastName());
        return toDto(updated);
    }

    public void delete(Long id) {
        log.warn("Deleting author with id={}", id);
        authorRepository.deleteById(id);
    }
}
