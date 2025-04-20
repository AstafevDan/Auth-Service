package com.dan.authservice.filter;

import com.dan.authservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр аутентификации JWT, который обрабатывает входящие запросы и проверяет JWT токен.
 * Этот фильтр применяется ко всем запросам, кроме указанного пути аутентификации ({@value PATH}).
 *
 * <p>Фильтр ожидает, что JWT токен будет передан в заголовке {@value AUTHORIZATION}
 * с префиксом {@value PREFIX}.</p>
 *
 * <p>Для извлечения и валидации JWT токена используется {@link JwtService}.
 * Для загрузки данных пользователя используется {@link UserDetailsService}.</p>
 *
 * @author Даниил Астафьев
 * @version 1.0
 * @see OncePerRequestFilter
 * @see JwtService
 * @see UserDetailsService
 */
@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Путь для аутентификации, который исключается из фильтрации.
     */
    public static final String PATH = "/api/v1/auth";

    /**
     * Название заголовка, в котором передается JWT токен.
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * Префикс, который используется перед JWT токеном в заголовке авторизации.
     */
    public static final String PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().contains(PATH)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith(PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(PREFIX.length());
        userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
