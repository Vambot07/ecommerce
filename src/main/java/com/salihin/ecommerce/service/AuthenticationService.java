package com.salihin.ecommerce.service;

import com.salihin.ecommerce.dto.AuthenticationRequest;
import com.salihin.ecommerce.dto.AuthenticationResponse;
import com.salihin.ecommerce.dto.RegisterRequest;
import com.salihin.ecommerce.entity.Role;
import com.salihin.ecommerce.entity.User;
import com.salihin.ecommerce.repository.UserRepository;
import com.salihin.ecommerce.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // 1. Create a new user from the request
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER) // Default role
                .build();

        // 2. Save the user to the database
        repository.save(user);

        // 3. Generate a JWT token for the new user
        var jwtToken = jwtService.generateToken(user);

        // 4. Return the token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Authenticate the user credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. If we reach here, the user is authenticated. Fetch the user from the DB
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        // 3. Generate a new JWT token
        var jwtToken = jwtService.generateToken(user);

        // 4. Return the token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}