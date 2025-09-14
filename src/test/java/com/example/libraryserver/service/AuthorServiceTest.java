package com.example.libraryserver.service;

import com.example.libraryserver.dto.AuthorDTO;
import com.example.libraryserver.model.Author;
import com.example.libraryserver.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceTest {

    private AuthorRepository authorRepository;
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        authorRepository = Mockito.mock(AuthorRepository.class);
        authorService = new AuthorService(authorRepository);
    }

    @Test
    void testCreateNewAuthor() {
        AuthorDTO dto = new AuthorDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        when(authorRepository.findByFirstNameAndLastName("John", "Doe"))
                .thenReturn(Optional.empty());

        Author saved = new Author();
        saved.setId(1L);
        saved.setFirstName("John");
        saved.setLastName("Doe");

        when(authorRepository.save(any(Author.class))).thenReturn(saved);

        AuthorDTO result = authorService.create(dto);
        assertNotNull(result.getId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testDuplicateAuthorThrows() {
        AuthorDTO dto = new AuthorDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");

        Author existing = new Author();
        existing.setId(99L);

        when(authorRepository.findByFirstNameAndLastName("Jane", "Smith"))
                .thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> authorService.create(dto));
    }

    @Test
    void testFindById() {
        Author a = new Author();
        a.setId(42L);
        a.setFirstName("Foo");
        a.setLastName("Bar");

        when(authorRepository.findById(42L)).thenReturn(Optional.of(a));

        Optional<AuthorDTO> dto = authorService.findById(42L);
        assertTrue(dto.isPresent());
        assertEquals("Foo", dto.get().getFirstName());
    }
}
