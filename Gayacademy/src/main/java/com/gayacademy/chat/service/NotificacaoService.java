package com.gayacademy.chat.service;

import com.gayacademy.chat.domain.Notificacao;
import com.gayacademy.chat.dto.NotificacaoResponse;
import com.gayacademy.chat.repository.NotificacaoRepository;
import com.gayacademy.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private static final int MAX_PAGE_SIZE = 20;

    private final NotificacaoRepository notificacaoRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Persiste e envia a notificação ao destinatário via WebSocket.
     * Se o usuário estiver offline, a notificação fica retida no MongoDB.
     */
    public Notificacao criarEEnviar(UUID destinatarioId, String tipo, String titulo,
                                     String conteudo, String referenciaId) {
        Notificacao notificacao = Notificacao.builder()
                .destinatarioId(destinatarioId)
                .tipo(tipo)
                .titulo(titulo)
                .conteudo(conteudo)
                .referenciaId(referenciaId)
                .lida(false)
                .build();

        notificacao = notificacaoRepository.save(notificacao);

        // Entrega imediata via STOMP — sem efeito se usuário estiver offline
        messagingTemplate.convertAndSendToUser(
                destinatarioId.toString(),
                "/queue/notificacoes",
                NotificacaoResponse.from(notificacao)
        );

        return notificacao;
    }

    /**
     * Retorna notificações não lidas do usuário, mais recentes primeiro (máx 20).
     */
    public List<NotificacaoResponse> listarNaoLidas(UUID userId, int limit) {
        int pageSize = Math.min(limit, MAX_PAGE_SIZE);
        return notificacaoRepository
                .findByDestinatarioIdAndLidaFalseOrderByCreatedAtDesc(
                        userId, PageRequest.of(0, pageSize))
                .stream()
                .map(NotificacaoResponse::from)
                .toList();
    }

    /**
     * Retorna todas as notificações do usuário com paginação (máx 20).
     */
    public List<NotificacaoResponse> listarTodas(UUID userId, int limit) {
        int pageSize = Math.min(limit, MAX_PAGE_SIZE);
        return notificacaoRepository
                .findByDestinatarioIdOrderByCreatedAtDesc(
                        userId, PageRequest.of(0, pageSize))
                .stream()
                .map(NotificacaoResponse::from)
                .toList();
    }

    /**
     * Contagem de notificações não lidas — usado para exibir badge no cliente.
     */
    public long contarNaoLidas(UUID userId) {
        return notificacaoRepository.countByDestinatarioIdAndLidaFalse(userId);
    }

    /**
     * Marca uma notificação específica como lida.
     */
    public void marcarComoLida(String notificacaoId, UUID userId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new NotFoundException("Notificação não encontrada"));

        if (!notificacao.getDestinatarioId().equals(userId)) {
            throw new NotFoundException("Notificação não encontrada");
        }

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    /**
     * Marca todas as notificações não lidas do usuário como lidas.
     */
    public void marcarTodasComoLidas(UUID userId) {
        List<Notificacao> pendentes =
                notificacaoRepository.findByDestinatarioIdAndLidaFalse(userId);
        pendentes.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(pendentes);
    }

    /**
     * Entrega notificações retidas ao usuário que acabou de se conectar.
     */
    public void entregarPendentes(UUID userId) {
        notificacaoRepository
                .findByDestinatarioIdAndLidaFalse(userId)
                .forEach(n -> messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue/notificacoes",
                        NotificacaoResponse.from(n)
                ));
    }
}
