package com.salihin.ecommerce.config;

import com.salihin.ecommerce.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. WhiteList the auth endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 2. Secure the Product endpoints based on roles!!
                        // Allow any authenticated user (CUSTOMER or ADMIN) to GET products
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/products/**").authenticated()

                        // Only allow ADMIN to POST, PUT, or DELETE products
                        .requestMatchers("/api/v1/products/**").hasRole("ADMIN")

                        // 3. Cart endpoints - accessible to any authenticated user
                        .requestMatchers("/api/v1/cart/**").authenticated()

                        // 4. Order endpoints - accessible to any authenticated user
                        .requestMatchers("/api/v1/orders/**").authenticated()

                        // 5. Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
