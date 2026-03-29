package com.thesis.neptunmock.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        log.debug("Processing request to: {}", requestURI);

        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {

                String neptunCode = jwtUtil.extractNeptunCode(jwt);
                log.debug("Extracted neptun code from token: {}", neptunCode);

                if (neptunCode != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    log.debug("Loading user details for: {}", neptunCode);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(neptunCode);
                    log.debug("User details loaded: {}", userDetails.getUsername());

                    if (jwtUtil.validateToken(jwt, neptunCode)) {
                        log.debug("Token is valid for user: {}", neptunCode);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("Successfully authenticated user: {}", neptunCode);
                    } else {
                        log.warn("Token validation failed for user: {}", neptunCode);
                    }
                } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    log.debug("User already authenticated");
                }
            } else {
                log.debug("No JWT token found in request to: {}", requestURI);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }


    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}