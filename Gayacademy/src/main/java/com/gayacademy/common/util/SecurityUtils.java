package com.gayacademy.common.util;

import com.gayacademy.common.exception.BusinessException;
import com.gayacademy.user.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static CustomUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details;
        }
        return null;
    }

    public static UUID getCurrentUserId() {
        CustomUserDetails details = getCurrentUserDetails();
        return details == null ? null : details.getUserId();
    }

    /**
     * Retorna o ID do usuario autenticado. Lanca 401 se nao houver autenticacao.
     * Use este metodo em endpoints que EXIGEM autenticacao.
     */
    public static UUID requireCurrentUserId() {
        UUID id = getCurrentUserId();
        if (id == null) {
            throw new BusinessException("Autenticacao obrigatoria", HttpStatus.UNAUTHORIZED);
        }
        return id;
    }

    public static boolean isAuthenticated() {
        return getCurrentUserDetails() != null;
    }
}