package com.gayacademy.chat.dto;

import com.gayacademy.chat.domain.Conversa;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de resposta para a lista de conversas do inbox (CHT-API-05).
 * Retorna dados mínimos — sem objetos aninhados desnecessários (GNF-03).
 */
public record ChatResponse(

        /** ID canônico da conversa (menor UUID : maior UUID). */
        String conversaId,

        /** ID do outro participante da conversa. */
        UUID participanteId,

        String nomeExibicaoParticipante,

        String ultimaMensagem,

        OffsetDateTime updatedAt
) {
    /**
     * Constrói o DTO a partir da entidade, determinando qual dos dois
     * participantes é o "outro" em relação ao usuário solicitante.
     */
    public static ChatResponse from(Conversa conversa, UUID meuId,
         String nomeExibicaoOutro) {
            
        UUID outroId = conversa.getUser1Id().equals(meuId)
                ? conversa.getUser2Id()
                : conversa.getUser1Id();

        return new ChatResponse(
                conversa.conversaId(),
                outroId,
                nomeExibicaoOutro,
                conversa.getUltimaMensagem(),
                conversa.getUpdatedAt()
        );
    }
}
