package com.example.libraryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class LibraryServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryServerApplication.class, args);
    }
}
