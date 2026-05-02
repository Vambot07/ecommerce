package com.salihin.ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extract Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        String internalName = getAlreadyFilteredAttributeName();

        System.out.println("Checking attribute: " + internalName );
        System.out.println("Value before processing "+ request.getAttribute(internalName));

        // 2. Check if the header is missing or doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token and the username (email)
        jwt = authHeader.substring(7);
        System.out.println("JWT: " + jwt);
        userEmail = jwtService.extractUsername(jwt);
        System.out.println("Email: " + userEmail);



        // 4. If we have a username and the user is nor already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Get user details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);


            // 5. If the token is valid, configure Spring security to manually set authentication
            if (jwtService.isTokenValid(jwt, userDetails)) {
                System.out.println("Token is valid");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // 6. Update the security context holder
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Auth set: " + SecurityContextHolder.getContext().getAuthentication());
                System.out.println("Authorities: " + userDetails.getAuthorities());
            } else {
                System.out.println("Token is invalid");
            }
        }

        // 7. Pass the request to the next filter
        filterChain.doFilter(request, response);
    }
}
