package com.gayacademy.user.dto;

import com.gayacademy.user.domain.enums.Objetivo;
import com.gayacademy.user.domain.enums.Privacidade;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateProfileRequest(
        @Size(max = 80) String nomeExibicao,
        @Size(max = 280) String bio,
        @Size(max = 500) String avatarUrl,
        Short alturaCm,
        BigDecimal pesoKg,
        Objetivo objetivo,
        Privacidade privacidade
) {}