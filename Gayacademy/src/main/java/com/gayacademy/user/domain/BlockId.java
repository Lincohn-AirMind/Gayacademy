package com.gayacademy.user.domain;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BlockId implements Serializable {
    private UUID blockerId;
    private UUID blockedId;
}