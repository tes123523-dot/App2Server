package com.example.libraryserver.dto;

import com.example.libraryserver.model.BookGenre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class BookDTO {
    private Long id;

    @NotBlank(message = "Title required")
    private String title;

    @Pattern(regexp = "^(?:\\d{9}[\\dXx]|\\d{13})$", message = "ISBN must be 10 or 13 digits (X allowed)")
    private String isbn;

    private Long authorId;
    private LocalDate publicationDate;
    private BookGenre genre;

    @Size(max = 2048)
    private String coverImageUrl;

    public BookDTO() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public BookGenre getGenre() { return genre; }
    public void setGenre(BookGenre genre) { this.genre = genre; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
}
