package com.distribution.colis.service;

import com.distribution.colis.dto.AuthResponse;
import com.distribution.colis.dto.LoginRequest;
import com.distribution.colis.dto.RegisterRequest;
import com.distribution.colis.entity.Role;
import com.distribution.colis.entity.User;
import com.distribution.colis.entity.UserRole;
import com.distribution.colis.repository.RoleRepository;
import com.distribution.colis.repository.UserRepository;
import com.distribution.colis.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .active(true)
                .build();

        // Default role: CLIENT
        Role role = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        user.setUserRoles(List.of(userRole));

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));
        var user = userRepository.findByEmail(request.email())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }
}
