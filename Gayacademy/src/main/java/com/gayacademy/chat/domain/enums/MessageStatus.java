package com.gayacademy.chat.domain.enums;

/** Ciclo de vida de uma mensagem de chat. */
public enum MessageStatus {
    /** Enviada pelo cliente, ainda não confirmada pelo servidor. */
    PENDING,
    /** Persistida no servidor e entregue ao broker. */
    SENT,
    /** Entregue ao dispositivo do destinatário. */
    DELIVERED,
    /** Lida pelo destinatário. */
    READ
}
