package com.example.libraryserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "books", indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "author_id")
})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    // simple ISBN-10 or ISBN-13 pattern (beginner-friendly)
    @Pattern(regexp = "^(?:\\d{9}[\\dXx]|\\d{13})$", message = "ISBN must be 10 or 13 digits (X allowed for ISBN-10)")
    private String isbn;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    private LocalDate publicationDate;

    @Enumerated(EnumType.STRING)
    private BookGenre genre;

    @Size(max = 2048)
    private String coverImageUrl;

    public Book() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public BookGenre getGenre() { return genre; }
    public void setGenre(BookGenre genre) { this.genre = genre; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
}
