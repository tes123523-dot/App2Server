package com.example.libraryserver.service;

import com.example.libraryserver.dto.BookDTO;
import com.example.libraryserver.model.Author;
import com.example.libraryserver.model.Book;
import com.example.libraryserver.repository.AuthorRepository;
import com.example.libraryserver.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Simple book service with paging, sorting, and logging.
 */
@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public BookDTO toDto(Book b) {
        BookDTO dto = new BookDTO();
        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setIsbn(b.getIsbn());
        if (b.getAuthor() != null) dto.setAuthorId(b.getAuthor().getId());
        dto.setPublicationDate(b.getPublicationDate());
        dto.setGenre(b.getGenre());
        dto.setCoverImageUrl(b.getCoverImageUrl());
        return dto;
    }

    public Book toEntity(BookDTO dto) {
        Book b = new Book();
        b.setId(dto.getId());
        b.setTitle(dto.getTitle());
        b.setIsbn(dto.getIsbn());
        if (dto.getAuthorId() != null) {
            Author a = authorRepository.findById(dto.getAuthorId()).orElseThrow(() -> {
                log.error("Author with id={} not found while mapping book", dto.getAuthorId());
                return new IllegalArgumentException("Author not found");
            });
            b.setAuthor(a);
        }
        b.setPublicationDate(dto.getPublicationDate());
        b.setGenre(dto.getGenre());
        b.setCoverImageUrl(dto.getCoverImageUrl());
        return b;
    }

    public BookDTO create(BookDTO dto) {
        log.info("Creating book: {} (ISBN={})", dto.getTitle(), dto.getIsbn());
        Book b = toEntity(dto);
        Book saved = bookRepository.save(b);
        log.debug("Book saved with id={}", saved.getId());
        return toDto(saved);
    }

    public BookDTO update(Long id, BookDTO dto) {
        log.info("Updating book id={}", id);
        Book existing = bookRepository.findById(id).orElseThrow(() -> {
            log.error("Book with id={} not found", id);
            return new IllegalArgumentException("Book not found");
        });
        existing.setTitle(dto.getTitle());
        existing.setIsbn(dto.getIsbn());
        if (dto.getAuthorId() != null) {
            Author a = authorRepository.findById(dto.getAuthorId()).orElseThrow(() -> {
                log.error("Author with id={} not found while updating book id={}", dto.getAuthorId(), id);
                return new IllegalArgumentException("Author not found");
            });
            existing.setAuthor(a);
        }
        existing.setPublicationDate(dto.getPublicationDate());
        existing.setGenre(dto.getGenre());
        existing.setCoverImageUrl(dto.getCoverImageUrl());
        Book saved = bookRepository.save(existing);
        log.debug("Book updated: id={}, title={}", saved.getId(), saved.getTitle());
        return toDto(saved);
    }

    public void delete(Long id) {
        log.warn("Deleting book with id={}", id);
        bookRepository.deleteById(id);
    }

    public Page<BookDTO> listPaged(int page, int size, String sort) {
        Sort s = Sort.by("title").ascending();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                s = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
            } else {
                s = Sort.by(sort);
            }
        }
        Pageable pageable = PageRequest.of(page, size, s);
        log.info("Fetching paged books: page={}, size={}, sort={}", page, size, sort);
        Page<Book> p = bookRepository.findAll(pageable);
        return p.map(this::toDto);
    }
}
