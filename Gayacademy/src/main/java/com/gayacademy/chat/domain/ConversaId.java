package com.gayacademy.chat.domain;

import java.io.Serializable;
import java.util.UUID;

/**
 * Chave composta da entidade Conversa.
 * user1Id é sempre o menor dos dois UUIDs (ordenação lexicográfica).
 */
public record ConversaId(UUID user1Id, UUID user2Id) implements Serializable {}
