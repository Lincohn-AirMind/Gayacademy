package com.gayacademy.chat.controller;

import com.gayacademy.chat.dto.ChatResponse;
import com.gayacademy.chat.dto.MessageResponse;
import com.gayacademy.chat.service.ChatService;
import com.gayacademy.common.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST API do módulo de chat — histórico e inbox de conversas.
 * Contratos: CHT-API-05, GNF-01, GNF-02.
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Validated
@Tag(name = "Chat", description = "Histórico e inbox de conversas")
public class ChatController {

    private final ChatService chatService;

    /**
     * Lista todas as conversas do usuário autenticado, ordenadas pela última 
     * atividade.
     */
    @GetMapping("/conversas")
    @Operation(summary = "Listar conversas do inbox")
    public ResponseEntity<List<ChatResponse>> listarConversas() {
        return ResponseEntity.ok(
                chatService.listarConversas(SecurityUtils.requireCurrentUserId()));
    }

    /**
     * Retorna o histórico de mensagens de uma conversa com paginação reversa (CHT-05).
     *
     * @param destinatarioId ID do outro participante da conversa.
     * @param antesDeMessageId ID da mensagem mais antiga já carregada (cursor).
     * @param limit            Itens por página — máximo 20 (GNF-02).
     */
    @GetMapping("/conversas/{destinatarioId}/historico")
    @Operation(summary = "Histórico de mensagens com paginação reversa (CHT-API-05)")
    public ResponseEntity<List<MessageResponse>> historico(
            @PathVariable UUID destinatarioId,
            @RequestParam(required = false) String antesDeMessageId,
            @RequestParam(defaultValue = "20")int limit
    ) {
        List<MessageResponse> historico = chatService.buscarHistorico(
                SecurityUtils.requireCurrentUserId(),
                destinatarioId,
                antesDeMessageId,
                limit
        );
        return ResponseEntity.ok(historico);
    }
}
