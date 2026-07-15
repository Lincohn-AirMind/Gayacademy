package com.gayacademy.user;

import com.gayacademy.common.util.SecurityUtils;
import com.gayacademy.user.dto.UpdateProfileRequest;
import com.gayacademy.user.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints de perfil e relacionamentos")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Retorna o perfil do usuario autenticado")
    public ResponseEntity<UserProfileResponse> getMe() {
        return ResponseEntity.ok(userService.getMeuPerfil(SecurityUtils.requireCurrentUserId()));
    }

    @PutMapping("/me/profile")
    @Operation(summary = "Atualiza os dados do perfil do usuario autenticado")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.atualizarPerfil(SecurityUtils.requireCurrentUserId(), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna o perfil de um usuario por ID (respeitando privacidade)")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable UUID id) {
        // meuId pode ser null aqui (endpoint publico para perfis publicos)
        return ResponseEntity.ok(userService.getPerfilPublico(id, SecurityUtils.getCurrentUserId()));
    }
}