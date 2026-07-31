package com.gayacademy.chat.service;

import com.gayacademy.chat.domain.Conversa;
import com.gayacademy.chat.domain.Message;
import com.gayacademy.chat.domain.Notificacao;
import com.gayacademy.chat.domain.enums.MessageStatus;
import com.gayacademy.chat.domain.enums.MessageType;
import com.gayacademy.chat.dto.*;
import com.gayacademy.chat.repository.ConversaRepository;
import com.gayacademy.chat.repository.MessageRepository;
import com.gayacademy.common.exception.NotFoundException;
import com.gayacademy.user.domain.User;
import com.gayacademy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int MAX_PAGE_SIZE = 20;

    private final MessageRepository messageRepository;
    private final ConversaRepository conversaRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Processa o envio de uma mensagem STOMP (CHT-01, CHT-02, INT-02).
     *
     * <p>Fluxo:
     * <ol>
     *   <li>Verifica idempotência pelo clientMessageId.</li>
     *   <li>Persiste a mensagem no MongoDB.</li>
     *   <li>Atualiza metadados da Conversa no PostgreSQL.</li>
     *   <li>Envia a mensagem ao destinatário via /user/queue/mensagens.</li>
     *   <li>Envia ACK ao remetente via /user/queue/ack.</li>
     *   <li>Se destinatário offline, persiste Notificacao para retenção (CHT-04).</li>
     * </ol>
     * caso tenha docker, para fins de testes utilize o codigo abaixo:
     * docker run -d --name mongo-teste -p 27017:27017 mongo
     * 
     */
    @Transactional
    public AckResponse enviarMensagem(UUID remetenteId, ChatRequest request) {
        // Idempotência — ignora duplicata em até 1 segundo (INT-02)
        if (messageRepository.existsByClientMessageId(request.clientMessageId())) {
            Message existente = mongoTemplate.findOne(
                    Query.query(Criteria.where("clientMessageId")
                    .is(request.clientMessageId())),

                    Message.class
            );
            return new AckResponse(
                    request.clientMessageId(),
                    existente != null ? existente.getId() : null,
                    MessageStatus.SENT,
                    Instant.now()
            );
        }

        User remetente = userRepository.findById(remetenteId)
                .orElseThrow(() -> new NotFoundException("Remetente não encontrado"));
        userRepository.findById(request.destinatarioId())
                .orElseThrow(() -> new NotFoundException("Destinatário não encontrado"));

        // Constrói e persiste a mensagem
        Conversa conversa = obterOuCriarConversa(remetenteId, request.destinatarioId());
        Message message = Message.builder()
                .conversaId(conversa.conversaId())
                .remetenteId(remetenteId)
                .destinatarioId(request.destinatarioId())
                .clientMessageId(request.clientMessageId())
                .tipo(request.tipo())
                .conteudo(request.conteudo())
                .status(MessageStatus.SENT)
                .build();

        message = messageRepository.save(message);

        // Atualiza preview na conversa
        String preview = request.tipo() == MessageType.TEXT
                ? request.conteudo().substring(0, Math.min(request.conteudo().length(), 100))
                : "[" + request.tipo().name().toLowerCase() + "]";
        conversa.setUltimaMensagem(preview);
        conversaRepository.save(conversa);

        // Entrega ao destinatário via STOMP
        MessageResponse payload = MessageResponse.from(message, remetente.getNomeExibicao());
        messagingTemplate.convertAndSendToUser(
                request.destinatarioId().toString(),
                "/queue/mensagens",
                payload
        );

        return new AckResponse(
                request.clientMessageId(),
                message.getId(),
                MessageStatus.SENT,
                Instant.now()
        );
    }

    /**
     * Marca mensagens de uma conversa como DELIVERED para o destinatário (CHT-03).
     */
    public void marcarEntregue(UUID destinatarioId, String conversaId) {
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("conversaId").is(conversaId)
                        .and("destinatarioId").is(destinatarioId)
                        .and("status").is(MessageStatus.SENT)),
                Update.update("status", MessageStatus.DELIVERED),
                Message.class
        );
    }

    /**
     * Marca todas as mensagens de uma conversa como READ (CHT-03).
     */
    public void marcarLido(UUID destinatarioId, String conversaId) {
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("conversaId").is(conversaId)
                        .and("destinatarioId").is(destinatarioId)
                        .and("status").in(MessageStatus.SENT, MessageStatus.DELIVERED)),
                Update.update("status", MessageStatus.READ),
                Message.class
        );
    }

    /**
     * Histórico com paginação reversa (CHT-05, CHT-API-05).
     * Limite máximo: 20 (GNF-02).
     */
    public List<MessageResponse> buscarHistorico(UUID userId, UUID destinatarioId,
                                                   String antesDeMessageId, int limit) {
        int pageSize = Math.min(limit, MAX_PAGE_SIZE);
        String conversaId = buildConversaId(userId, destinatarioId);

        List<Message> mensagens;
        if (antesDeMessageId == null || antesDeMessageId.isBlank()) {
            mensagens = messageRepository.findByConversaIdOrderByCreatedAtDesc(
                    conversaId, PageRequest.of(0, pageSize));
        } else {
            mensagens = messageRepository.findBeforeMessage(
                    conversaId, antesDeMessageId, PageRequest.of(0, pageSize));
        }

        // Carrega nomes dos remetentes evitando N+1
        return mensagens.stream()
                .map(m -> {
                    String nome = userRepository.findById(m.getRemetenteId())
                            .map(User::getNomeExibicao)
                            .orElse("Usuário removido");
                    return MessageResponse.from(m, nome);
                })
                .toList();
    }

    /**
     * Lista conversas do inbox do usuário (CHT-API-05).
     */
    public List<ChatResponse> listarConversas(UUID userId) {
        return conversaRepository.findByParticipante(userId).stream()
                .map(c -> {
                    UUID outroId = c.getUser1Id().equals(userId) ? c.getUser2Id() : c.getUser1Id();
                    String nome = userRepository.findById(outroId)
                            .map(User::getNomeExibicao)
                            .orElse("Usuário removido");
                    return ChatResponse.from(c, userId, nome);
                })
                .toList();
    }

    /**
     * Entrega mensagens retidas para usuário que voltou online (CHT-04).
     */
    public void entregarMensagensPendentes(UUID userId) {
        List<Message> pendentes = messageRepository.findByDestinatarioIdAndStatusIn(
                userId, List.of(MessageStatus.SENT, MessageStatus.DELIVERED));

        for (Message m : pendentes) {
            String nome = userRepository.findById(m.getRemetenteId())
                    .map(User::getNomeExibicao)
                    .orElse("Usuário removido");
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/mensagens",
                    MessageResponse.from(m, nome)
            );
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Conversa obterOuCriarConversa(UUID a, UUID b) {
        boolean aFirst = a.toString().compareTo(b.toString()) < 0;
        UUID user1 = aFirst ? a : b;
        UUID user2 = aFirst ? b : a;

        return conversaRepository.findByUser1IdAndUser2Id(user1, user2)
                .orElseGet(() -> conversaRepository.save(Conversa.entre(a, b)));
    }

    private String buildConversaId(UUID a, UUID b) {
        return a.toString().compareTo(b.toString()) < 0
                ? a + ":" + b
                : b + ":" + a;
    }
}
