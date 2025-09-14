package com.example.libraryserver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 10;
    private static final int WINDOW_SECONDS = 10;

    private final Map<String, Deque<Instant>> requestLog = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        Instant now = Instant.now();

        requestLog.putIfAbsent(clientIp, new ArrayDeque<>());
        Deque<Instant> deque = requestLog.get(clientIp);

        synchronized (deque) {
            // remove old timestamps outside the 10s window
            while (!deque.isEmpty() && deque.peekFirst().isBefore(now.minusSeconds(WINDOW_SECONDS))) {
                deque.pollFirst();
            }

            if (deque.size() >= MAX_REQUESTS) {
                response.setStatus(429); // "Too Many Requests"
                response.getWriter().write("Rate limit exceeded. Try again later.");
                return;
            }

            deque.addLast(now);
        }

        filterChain.doFilter(request, response);
    }
}
