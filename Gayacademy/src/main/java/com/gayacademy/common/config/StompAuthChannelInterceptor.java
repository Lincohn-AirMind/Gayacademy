package com.gayacademy.common.config;

import com.gayacademy.user.security.CustomUserDetailsService;
import com.gayacademy.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Interceptor de canal STOMP que valida o Bearer JWT no frame CONNECT (CHT-API-01).
 * O principal definido aqui fica disponível nos @MessageMapping via {@link java.security.Principal}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("WebSocket CONNECT sem token JWT — conexão recusada");
            throw new IllegalArgumentException("Token JWT obrigatório para conexão WebSocket");
        }

        String token = authHeader.substring(7);
        try {
            java.util.UUID userId = jwtService.extractUserId(token);
            UserDetails userDetails = userDetailsService.loadUserById(userId);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userId.toString(),          // name() usado como principal.getName()
                            null,
                            userDetails.getAuthorities()
                    );

            accessor.setUser(auth);
        } catch (Exception e) {
            log.warn("JWT inválido ou expirado na conexão WebSocket: {}", e.getMessage());
            throw new IllegalArgumentException("Token JWT inválido ou expirado");
        }

        return message;
    }
}
