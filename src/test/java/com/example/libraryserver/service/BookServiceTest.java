package com.example.libraryserver.service;

import com.example.libraryserver.dto.BookDTO;
import com.example.libraryserver.model.Author;
import com.example.libraryserver.model.Book;
import com.example.libraryserver.model.BookGenre;
import com.example.libraryserver.repository.AuthorRepository;
import com.example.libraryserver.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        authorRepository = mock(AuthorRepository.class);
        bookService = new BookService(bookRepository, authorRepository);
    }

    @Test
    void testCreateBook() {
        BookDTO dto = new BookDTO();
        dto.setTitle("Java Basics");
        dto.setIsbn("123456789X");
        dto.setGenre(BookGenre.FICTION);
        dto.setPublicationDate(LocalDate.of(2020, 1, 1));

        Author author = new Author();
        author.setId(1L);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        dto.setAuthorId(1L);

        Book b = bookService.toEntity(dto);
        b.setId(99L);
        when(bookRepository.save(any(Book.class))).thenReturn(b);

        BookDTO saved = bookService.create(dto);
        assertNotNull(saved.getId());
        assertEquals("Java Basics", saved.getTitle());
    }

    @Test
    void testUpdateBookNotFound() {
        BookDTO dto = new BookDTO();
        dto.setTitle("Updated");
        when(bookRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookService.update(123L, dto));
    }
}
