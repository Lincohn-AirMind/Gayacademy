package com.gayacademy.common.dto;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> content,
        String proximoCursor,
        boolean temMais
) {}