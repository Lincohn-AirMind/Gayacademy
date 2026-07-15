package com.gayacademy.user.auth;

import com.gayacademy.user.auth.dto.AuthResponse;
import com.gayacademy.user.auth.dto.LoginRequest;
import com.gayacademy.user.auth.dto.RefreshRequest;
import com.gayacademy.user.auth.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacao", description = "Endpoints de cadastro, login e gestao de tokens")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Cadastra um novo usuario e retorna tokens JWT")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                 HttpServletRequest httpRequest) {
        AuthResponse response = authService.register(request, httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica usuario (email ou username) e retorna tokens JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renova access token usando refresh token (com rotacao)")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request,
                                                HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.refresh(request, httpRequest));
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalida o refresh token informado")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }
}