package com.gayacademy.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuração do WebSocket com STOMP (CHT-06).
 *
 * <p>Contratos implementados:
 * <ul>
 *   <li>CHT-API-01 — Handshake em /ws/chat com Bearer JWT (via StompAuthChannelInterceptor)</li>
 *   <li>CHT-API-02 — App publica em /app/chat.*</li>
 *   <li>CHT-API-03 — Remetente recebe em /user/queue/ack</li>
 *   <li>CHT-API-04 — Destinatário recebe em /user/queue/mensagens</li>
 * </ul>
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws/chat")          // CHT-API-01
                .setAllowedOriginPatterns("*")    // ajustar em produção
                .withSockJS();                    // fallback para ambientes sem WebSocket nativo
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefixo para destinos gerenciados pelos @MessageMapping do servidor
        registry.setApplicationDestinationPrefixes("/app");

        // Prefixo para filas pessoais (/user/queue/ack, /user/queue/mensagens)
        registry.setUserDestinationPrefix("/user");

        // Broker em memória — suficiente para instância única.
        // Para multi-instância com RabbitMQ STOMP relay, substituir por:
        //   registry.enableStompBrokerRelay("/topic", "/queue")
        //           .setRelayHost(...).setRelayPort(61613)...
        registry.enableSimpleBroker("/topic", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Autentica o JWT no frame STOMP CONNECT (CHT-API-01)
        registration.interceptors(stompAuthChannelInterceptor);
    }
}
