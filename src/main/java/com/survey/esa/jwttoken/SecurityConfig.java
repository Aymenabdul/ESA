package com.survey.esa.jwttoken;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // Enable CORS configuration
            .csrf(csrf -> csrf.disable())  // Disable CSRF using new API
            .httpBasic(httpBasic -> httpBasic.disable())  // Disable HTTP Basic Authentication using new API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api2/signup", "/api2/login","/api/users/all").permitAll()  // Allow signup and login without authentication
                .anyRequest().authenticated()  // Protect other routes
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);  // Add JWT filter

        return http.build();
    }
}
