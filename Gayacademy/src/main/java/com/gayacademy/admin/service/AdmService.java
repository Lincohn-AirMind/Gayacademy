package com.gayacademy.admin.service;

import com.gayacademy.admin.dto.AdminSuspendRequest;
import com.gayacademy.admin.dto.AdminUserResponse;
import com.gayacademy.admin.repository.AdmRepository;
import com.gayacademy.common.exception.NotFoundException;
import com.gayacademy.user.domain.User;
import com.gayacademy.user.domain.enums.UserRole;
import com.gayacademy.user.repository.CommentRepository;
import com.gayacademy.user.repository.PostRepository;
import com.gayacademy.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdmService {

    private static final int PAGE_SIZE = 20;

    private final UserRepository userRepository;
    private final AdmRepository admRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /** Lista todos os usuários com busca opcional e paginação (GNF-01, GNF-02). */
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> listarUsuarios(String filtro, int page) {
        return admRepository.buscarUsuarios(
                filtro == null || filtro.isBlank() ? null : filtro,
                PageRequest.of(page, PAGE_SIZE)
        ).map(AdminUserResponse::from);
    }

    /** Retorna os dados de um usuário pelo ID. */
    @Transactional(readOnly = true)
    public AdminUserResponse buscarUsuario(UUID id) {
        return admRepository.findById(id)
                .map(AdminUserResponse::from)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    /**
     * Suspende ou reativa um usuário (campo {@code ativo}).
     * Regras:
     * <ul>
     *   <li>Admin não pode alterar o próprio status.</li>
     *   <li>Admin não pode suspender outra conta de administrador.</li>
     * </ul>
     */
    @Transactional
    public AdminUserResponse atualizarStatus(UUID adminId, UUID id, AdminSuspendRequest request) {
        if (adminId.equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Não é possível alterar o status da própria conta");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Não é possível suspender ou reativar outra conta de administrador");
        }

        user.setAtivo(request.ativo());
        return AdminUserResponse.from(admRepository.save(user));
    }

    /**
     * Remove permanentemente um usuário comum.
     * Regras:
     * <ul>
     *   <li>Admin não pode deletar a própria conta.</li>
     *   <li>Admin não pode deletar outra conta de administrador.</li>
     * </ul>
     */
    @Transactional
    public void deletarUsuario(UUID adminId, UUID id) {
        if (adminId.equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Não é possível deletar a própria conta");
        }

        User user = admRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Não é possível deletar outra conta de administrador");
        }

        admRepository.delete(user);
    }

    /** Remove um post de qualquer usuário (moderação). */
    @Transactional
    public void deletarPost(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("Post não encontrado");
        }
        postRepository.deleteById(postId);
    }

    /** Remove um comentário de qualquer usuário (moderação). */
    @Transactional
    public void deletarComentario(UUID commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comentário não encontrado");
        }
        commentRepository.deleteById(commentId);
    }
}
