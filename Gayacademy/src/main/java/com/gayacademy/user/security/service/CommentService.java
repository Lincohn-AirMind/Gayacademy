package com.gayacademy.user.security.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gayacademy.user.domain.PostActions.Comentario;
import com.gayacademy.user.repository.CommentRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final int PAGES_SIZE = 20;

    private final CommentRepository commentRepository;

@Transactional
//inicia a procura sem cursor para nao dar erro no 
//postgresql.
public List<Comentario> getFirstPage(@NonNull UUID idPost){

PageRequest limiteCinco = PageRequest.of(0, PAGES_SIZE);

return commentRepository.findFirstPage(idPost, limiteCinco);

}
//varre o bd de comments em busca dos comentarios
//atrelados ao id do post respectivo
    @Transactional
    public List<Comentario> getPageUntilIdPost(
        @NonNull UUID idPost,
        @NonNull OffsetDateTime cursor
    ) {
        PageRequest pageable = PageRequest.of(0, PAGES_SIZE);
        return commentRepository.findNextCommentsPage(idPost, cursor, pageable);
    }

//salvar no bd um comentario
@Transactional
public Boolean saveComment(Comentario comment){
    if("null".equals(comment.getText())||
    comment.getText().isBlank()|| 
comment.getCreatedAt()==null||
comment.getPost()==null||
comment.getUser()==null){
    return false;
}
commentRepository.save(comment);
return true;
}

//deletar comentário
@Transactional
public Boolean deleteComment(@NonNull UUID idComment){
    
if(!commentRepository.existsById(idComment)){
    return false;
}
commentRepository.deleteById(idComment);
return true;
}

}

