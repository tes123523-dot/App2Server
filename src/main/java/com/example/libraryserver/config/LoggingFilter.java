package com.example.libraryserver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();

        log.info("➡️ Incoming Request: {} {}", method, uri);

        filterChain.doFilter(request, response);

        long duration = System.currentTimeMillis() - start;

        int status = response.getStatus();
        log.info("⬅️ Response: {} {} | Status: {} | Time: {} ms",
                method, uri, status, duration);
    }
}
