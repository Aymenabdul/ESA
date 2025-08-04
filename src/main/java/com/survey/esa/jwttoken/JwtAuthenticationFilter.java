package com.survey.esa.jwttoken;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip the filter for public routes like login and signup
        if (isPublicRoute(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Log the entire Authorization header for debugging
        String token = getJwtFromRequest(request);
        System.out.println("Received Authorization Header: " + request.getHeader("Authorization"));

        if (token != null) {
            System.out.println("Received token: " + token);

            // Validate token format
            if (isValidJwtFormat(token)) {
                if (jwtUtil.validateToken(token)) {
                    // CORRECTED LINES: We swap the method calls to get the correct data
                    String email = jwtUtil.extractEmail(token);      // Extracts the 'sub' claim (email)
                    String name = jwtUtil.extractUsername(token);    // Extracts the 'name' claim (username)

                    System.out.println("Extracted email: " + email);
                    System.out.println("Extracted name: " + name);

                    // We now use the 'email' to create the authentication token as it's the principal
                    SecurityContextHolder.getContext()
                            .setAuthentication(new UsernamePasswordAuthenticationToken(email, null, null));
                } else {
                    System.out.println("JWT Validation failed for token: " + token);
                }
            } else {
                System.out.println("Invalid token format for token: " + token);
            }
        } else {
            System.out.println("No token found in request.");
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(HttpServletRequest request) {
        // Check if the request is for signup or login routes
        String uri = request.getRequestURI();
        return uri.equals("/api2/login") || uri.equals("/api2/signup");
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);  // Remove "Bearer " prefix
            System.out.println("Extracted Token: " + token);
            return token;
        }
        return null;
    }

    private boolean isValidJwtFormat(String token) {
        return token != null && token.split("\\.").length == 3;  // Valid JWT should have 3 parts
    }
}
