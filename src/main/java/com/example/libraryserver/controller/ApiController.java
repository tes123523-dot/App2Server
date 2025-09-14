package com.example.libraryserver.controller;

import com.example.libraryserver.dto.AuthorDTO;
import com.example.libraryserver.dto.BookDTO;
import com.example.libraryserver.model.Author;
import com.example.libraryserver.model.BookGenre;
import com.example.libraryserver.service.AuthorService;
import com.example.libraryserver.service.BookService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Versioned REST API for authors and books.
 */
@RestController
@RequestMapping("/api/v1")
public class ApiController {
    private final AuthorService authorService;
    private final BookService bookService;

    public ApiController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    // Authors
    @GetMapping("/authors")
    @PreAuthorize("hasAnyRole('API_USER','API_ADMIN')")
    public List<AuthorDTO> listAuthors() {
        return authorService.listAll();
    }

    // CSV import for authors
    @PostMapping("/authors/import")
    @PreAuthorize("hasRole('API_ADMIN')")
    public ResponseEntity<String> importAuthors(@RequestParam("file") MultipartFile file) throws IOException {
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            String[] line;
            int count = 0;
            boolean firstLine = true; // Skip header row

            while (true) {
                try {
                    line = csvReader.readNext();
                    if (line == null) break;
                } catch (com.opencsv.exceptions.CsvValidationException e) {
                    return ResponseEntity.badRequest().body("Invalid CSV format: " + e.getMessage());
                }

                // Skip header row
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                // Ensure at least firstName and lastName exist
                if (line.length >= 2) {
                    String firstName = line[0].trim();
                    String lastName = line[1].trim();
                    String biography = line.length >= 3 ? line[2].trim() : null;

                    // Skip empty rows
                    if (firstName.isEmpty() && lastName.isEmpty()) continue;

                    AuthorDTO dto = new AuthorDTO();
                    dto.setFirstName(firstName);
                    dto.setLastName(lastName);
                    dto.setBiography(biography);

                    try {
                        authorService.create(dto);
                        count++;
                    } catch (Exception ignored) {
                        // Duplicate authors are ignored
                    }
                }
            }

            return ResponseEntity.ok("Imported authors: " + count);
        }
    }

    @GetMapping("/authors/export")
    @PreAuthorize("hasAnyRole('API_USER','API_ADMIN')")
    public ResponseEntity<InputStreamResource> exportAuthors() throws IOException {
        List<AuthorDTO> authors = authorService.listAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos))) {
            writer.writeNext(new String[]{"firstName","lastName","biography"});
            for (AuthorDTO a : authors) {
                writer.writeNext(new String[]{a.getFirstName(), a.getLastName(), a.getBiography() == null ? "" : a.getBiography()});
            }
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
        InputStreamResource resource = new InputStreamResource(bis);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=authors.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @GetMapping("/authors/{id}")
    @PreAuthorize("hasAnyRole('API_USER','API_ADMIN')")
    public ResponseEntity<AuthorDTO> getAuthor(@PathVariable Long id) {
        return authorService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/authors")
    @PreAuthorize("hasRole('API_ADMIN')")
    public AuthorDTO createAuthor(@Valid @RequestBody AuthorDTO dto) {
        return authorService.create(dto);
    }

    @PutMapping("/authors/{id}")
    @PreAuthorize("hasRole('API_ADMIN')")
    public AuthorDTO updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDTO dto) {
        return authorService.update(id, dto);
    }

    @DeleteMapping("/authors/{id}")
    @PreAuthorize("hasRole('API_ADMIN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Books
    @GetMapping("/books")
    @PreAuthorize("hasAnyRole('API_USER','API_ADMIN')")
    public Page<BookDTO> listBooks(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String sort) {
        return bookService.listPaged(page, size, sort);
    }

    // Bulk update for books
    @PostMapping("/books/bulk")
    @PreAuthorize("hasRole('API_ADMIN')")
    public List<BookDTO> bulkUpsertBooks(@RequestBody List<@Valid BookDTO> dtos) {
        List<BookDTO> result = new ArrayList<>();
        for (BookDTO dto : dtos) {
            if (dto.getId() == null) result.add(bookService.create(dto));
            else result.add(bookService.update(dto.getId(), dto));
        }
        return result;
    }

    @GetMapping("/books/{id}")
    @PreAuthorize("hasAnyRole('API_USER','API_ADMIN')")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        try {
            BookDTO dto = bookService.listPaged(0, Integer.MAX_VALUE, null).getContent().stream()
                    .filter(b -> b.getId().equals(id)).findFirst().orElse(null);
            if (dto == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/books")
    @PreAuthorize("hasRole('API_ADMIN')")
    public BookDTO createBook(@Valid @RequestBody BookDTO dto) {
        return bookService.create(dto);
    }

    @PutMapping("/books/{id}")
    @PreAuthorize("hasRole('API_ADMIN')")
    public BookDTO updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        return bookService.update(id, dto);
    }

    @DeleteMapping("/books/{id}")
    @PreAuthorize("hasRole('API_ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }




}
