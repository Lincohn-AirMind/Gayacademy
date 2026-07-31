package com.gayacademy.chat.controller;

import com.gayacademy.chat.dto.AckResponse;
import com.gayacademy.chat.dto.ChatRequest;
import com.gayacademy.chat.service.ChatService;
import com.gayacademy.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

/**
 * Controller STOMP — processa mensagens publicadas pelo cliente em /app/chat.*.
 * 
 * Contratos (CHT-API-02 a CHT-API-04):
 * 
 *   Cliente publica em {@code /app/chat.enviar}
 *   Remetente recebe ACK em {@code /user/queue/ack}
 *   Destinatário recebe mensagem em {@code /user/queue/mensagens}
 * 
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Recebe uma mensagem do cliente, persiste e faz a entrega (CHT-01, CHT-02).
     * O ACK é enviado de volta apenas ao remetente via @SendToUser.
     */
    @MessageMapping("/chat.enviar")
    @SendToUser("/queue/ack")
    public AckResponse enviar(@Payload ChatRequest request, Principal principal) {
        UUID remetenteId = UUID.fromString(principal.getName());
        return chatService.enviarMensagem(remetenteId, request);
    }

    /**
     * Cliente notifica que recebeu a mensagem — muda status para DELIVERED (CHT-03).
     * Payload esperado: conversaId como String.
     */
    @MessageMapping("/chat.entregue")
    public void marcarEntregue(@Payload String conversaId, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        chatService.marcarEntregue(userId, conversaId);
    }

    /**
     * Cliente notifica que leu a conversa — muda status para READ (CHT-03).
     * Payload esperado: conversaId como String.
     */
    @MessageMapping("/chat.lido")
    public void marcarLido(@Payload String conversaId, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        chatService.marcarLido(userId, conversaId);
    }

    /**
     * Cliente se reconecta online — recebe mensagens retidas (CHT-04).
     */
    @MessageMapping("/chat.online")
    public void online(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        chatService.entregarMensagensPendentes(userId);
    }
}
