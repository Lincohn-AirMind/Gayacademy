package com.gayacademy.user.auth;

import com.gayacademy.common.config.JwtProperties;
import com.gayacademy.common.exception.BusinessException;
import com.gayacademy.user.auth.dto.*;
import com.gayacademy.user.domain.RefreshToken;
import com.gayacademy.user.domain.User;
import com.gayacademy.user.domain.UserProfile;
import com.gayacademy.user.domain.enums.Privacidade;
import com.gayacademy.user.repository.RefreshTokenRepository;
import com.gayacademy.user.repository.UserProfileRepository;
import com.gayacademy.user.repository.UserRepository;
import com.gayacademy.user.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email ja cadastrado", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username ja cadastrado", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.senha()))
                .nomeExibicao(request.nomeExibicao())
                .ativo(true)
                .emailVerificado(false)
                .build();
        user = userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .user(user)
                .privacidade(Privacidade.PUBLICO)
                .build();
        userProfileRepository.save(profile);

        log.info("Novo usuario registrado: {} ({})", user.getUsername(), user.getId());

        return gerarTokens(user, httpRequest);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmailOrUsername(request.identificador())
                .orElseThrow(() -> new BusinessException("Credenciais invalidas", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.senha(), user.getPasswordHash())) {
            throw new BusinessException("Credenciais invalidas", HttpStatus.UNAUTHORIZED);
        }

        if (!Boolean.TRUE.equals(user.getAtivo())) {
            throw new BusinessException("Conta desativada", HttpStatus.FORBIDDEN);
        }

        log.info("Login efetuado: {} ({})", user.getUsername(), user.getId());

        return gerarTokens(user, httpRequest);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request, HttpServletRequest httpRequest) {
        String rawToken = request.refreshToken();

        UUID userId;
        try {
            userId = jwtService.extractUserId(rawToken);
        } catch (Exception e) {
            throw new BusinessException("Refresh token invalido", HttpStatus.UNAUTHORIZED);
        }

        String tokenHash = jwtService.hashToken(rawToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException("Refresh token nao encontrado", HttpStatus.UNAUTHORIZED));

        if (!stored.isValido()) {
            refreshTokenRepository.revogarTodosDoUsuario(stored.getUserId(), OffsetDateTime.now());
            throw new BusinessException("Refresh token expirado ou revogado", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuario nao encontrado", HttpStatus.UNAUTHORIZED));

        stored.setRevokedAt(OffsetDateTime.now());
        refreshTokenRepository.save(stored);

        AuthResponse novo = gerarTokens(user, httpRequest);

        String novoHash = jwtService.hashToken(novo.refreshToken());
        refreshTokenRepository.findByTokenHash(novoHash)
                .ifPresent(novoToken -> {
                    stored.setReplacedById(novoToken.getId());
                    refreshTokenRepository.save(stored);
                });

        return novo;
    }

    @Transactional
    public void logout(RefreshRequest request) {
        String tokenHash = jwtService.hashToken(request.refreshToken());
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(rt -> {
            if (rt.getRevokedAt() == null) {
                rt.setRevokedAt(OffsetDateTime.now());
                refreshTokenRepository.save(rt);
                log.info("Refresh token revogado: user={}", rt.getUserId());
            }
        });
    }

    private AuthResponse gerarTokens(User user, HttpServletRequest httpRequest) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        RefreshToken rt = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(jwtService.hashToken(refreshToken))
                .expiresAt(jwtService.getRefreshTokenExpirationDateTime())
                .userAgent(httpRequest != null ? httpRequest.getHeader("User-Agent") : null)
                .ipAddress(httpRequest != null ? httpRequest.getRemoteAddr() : null)
                .build();
        refreshTokenRepository.save(rt);

        long expiresIn = jwtProperties.getAccessTokenExpiration().getSeconds();

        return AuthResponse.of(accessToken, refreshToken, expiresIn,
                UserSummaryResponse.from(user));
    }
}