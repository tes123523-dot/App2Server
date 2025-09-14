package com.example.libraryserver.repository;

import com.example.libraryserver.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
