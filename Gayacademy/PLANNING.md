# Planejamento / Requisitos

Selecione um requisito pelo qual será responsável e coloque o nome ao lado.


Comentario (domínio)       ✅ quase pronto — adicionar campo content
CommentRepository          ✅ pronto
Migration SQL (posts/comments/likes)  ❌ não existe
CommentService             [x] feito
CommentController          [x] existe e está pronto
CommentRequest DTO         [x] feito
CommentResponse DTO        [x] feito
PostResponse DTO           [x] feito
PostService                [x] feito
PostController             [x] feito
Configurar websocket       []
Chat                       []
ChatRepository             []
ChatService                []
Chat                       []
ChatRepository             []
ChatController             []
ChatReponse                []
ChatRequest                []
Message                    []
MessageRepository          []
MessageController          []
MessageService             []
MessageResponse            []
MessageRequest             []
Notificacao                []
Adm                        []
AdmController              []
AdmService                 []
AdmRepository              []



## 1. Requisitos Funcionais da API

### A. Módulo de Usuário e Grafo Social (USR)
- [ feito ] USR-01: Cadastro e login com autenticação JWT usando Spring Security e suporte a refresh token.
////////////////////
## IMPORTANTE ###
# adm
A conta de Adm não cria conteudo, só deleta, altera (suspende) e faz uso de GET's (a principio será repetido de user)
(as rotas abaixo são esquemáticas, podem alterar o endpoint)
rotas :
adm/ 
get
put
delete

A conta de users permite criar conteudo, deletar o proprio conteudo, alterar e fazer get de conteudos do servidor(feed,etc...)
rotas: 
user/
post
get
get/login
put
delete


////////////////////

- [ ] USR-02: Endpoints de relacionamento social para seguir, deixar de seguir, bloquear e aceitar solicitações.
- [ ] USR-03: Perfil fitness com biometria básica (altura, peso e objetivos) e configuração de privacidade entre perfil público e privado.

### B. Módulo de Conteúdo, Feed e Interações (CNT/FED)
- [ ] CNT-01: Criação de posts com suporte a texto e associação de mídia ao conteúdo publicado.
- [ ] FED-01: Endpoint de feed com paginação por cursor (ID do último post ou timestamp) e parâmetro de tamanho da página, evitando duplicidade no scroll infinito.
- [ ] FED-02: Feed híbrido que intercale postagens de perfis seguidos com conteúdos de descoberta baseados em engajamento e proximidade.
- [ ] INT-01: Endpoint assíncrono para dar e remover like em posts.
- [ ] INT-02: Tratamento de idempotência para likes, ignorando requisições duplicadas do mesmo usuário para o mesmo post em até 1 segundo.
- [ ] INT-03: Endpoint de comentários com paginação infinita.
- [ ] INT-04: Cada interação no feed deve ser enviada imediatamente pelo aplicativo mobile em requisição individual, sem processamento em lote posterior.
- [ ] INT-05: As respostas das interações devem ser rápidas e enxutas, retornando ao menos status da ação e contador atualizado para suportar optimistic updates no cliente.

#### Contratos de Referência do Feed e Interações
- [ ] FED-API-01: O endpoint GET /api/v1/feed deve aceitar cursor opcional e size opcional, com padrão 15 e limite máximo 20.
- [ ] FED-API-02: A resposta do feed deve incluir content, proximoCursor e temMais, com objetos resumidos de post e autor.
- [ ] INT-API-01: O endpoint POST /api/v1/posts/{id}/like deve operar como toggle seguro de curtida, retornando postId, curtido e totalLikes.

### C. Módulo de Upload de Mídia (UPM)
- [ ] UPM-01: Endpoint seguro para geração de Presigned URL de upload, recebendo contentType e contentLength.
- [ ] UPM-02: Validação de metadados antes da geração da URL, aceitando apenas image/jpeg, image/png e video/mp4, com limites máximos de tamanho por tipo de arquivo.
- [ ] UPM-03: Endpoint de callback/webhook, ou consumo de evento do storage, para confirmar upload concluído e vincular a mídia ao post ou perfil correspondente.
- [ ] UPM-04: Após obter a Presigned URL, o aplicativo mobile deve realizar upload direto do binário ao storage via HTTP PUT, sem repassar o arquivo pela API principal.

#### Contratos de Referência de Upload
- [ ] UPM-API-01: O endpoint POST /api/v1/media/upload-url deve receber nomeArquivo, contentType e contentLength.
- [ ] UPM-API-02: A resposta deve retornar mediaId, uploadUrl, publicUrl e expiraEmSegundos.

### D. Módulo de Comunidades (COM)
- [ ] COM-01: Criação e gestão de comunidades baseadas em interesses, como grupos temáticos de treino, dieta ou modalidade.
- [ ] COM-02: Moderação de comunidades com endpoints para banimento de usuários e aprovação de posts em grupos moderados.

### E. Módulo de Chat Privado e em Grupo (CHT)
- [ ] CHT-01: Mensageria em tempo real com conexão persistente via WebSocket para entrega instantânea.
- [ ] CHT-02: Protocolo de confirmação de recebimento com ACK_SERVER após processar e persistir a mensagem, permitindo atualizar o status local de pendente para enviado.
- [ ] CHT-03: Eventos de status de entrega para enviado, entregue e lido.
- [ ] CHT-04: Retenção de mensagens para destinatários offline, com envio em lote assim que o usuário restabelecer conexão.
- [ ] CHT-05: Histórico de conversa com paginação reversa baseada na mensagem mais antiga carregada em tela, permitindo paginação para trás.
- [ ] CHT-06: A mensageria em tempo real deve usar protocolo STOMP sobre WebSocket, com rotas de entrada e canais de retorno definidos por usuário.

#### Contratos de Referência do Chat
- [ ] CHT-API-01: O handshake WebSocket deve ocorrer em /ws/chat com autenticação Bearer JWT.
- [ ] CHT-API-02: O app deve publicar mensagens em /app/chat.enviar contendo clientMessageId, destinatarioId, tipo e conteudo.
- [ ] CHT-API-03: O remetente deve receber ACK em /user/queue/ack com clientMessageId, serverMessageId, status e timestamp.
- [ ] CHT-API-04: O destinatário online deve receber mensagens em /user/queue/mensagens com identificador, remetente, tipo, conteúdo e data de envio.
- [ ] CHT-API-05: O endpoint GET /api/v1/chat/conversas/{id_destinatario}/historico deve aceitar antesDeMessageId opcional e limit com máximo padronizado de 20 itens por requisição.

## 2. Requisitos Não Funcionais

Não é necessário atribuir responsável nominal nesta seção.

### A. Requisitos Gerais (GNF)
- [ ] GNF-01: Todos os endpoints de listagem devem usar paginação obrigatória por cursor ou por page e size.
- [ ] GNF-02: O tamanho máximo por página deve ser limitado no back-end a 20 itens por requisição.
- [ ] GNF-03: As respostas da API devem usar DTOs enxutos, evitando objetos inteiros e estruturas aninhadas desnecessárias.

### B. Requisitos Não Funcionais do Feed (FED-NF)
- [ ] FED-NF01: O feed deve usar cache de timeline no Redis com Sorted Sets (ZSET) para armazenar temporariamente IDs de posts de usuários ativos.
- [ ] FED-NF02: A composição do feed deve evitar consultas pesadas em tempo real, como ORDER BY RANDOM() e joins excessivos no banco relacional.
- [ ] FED-NF03: As interações de alta frequência, como likes, devem priorizar escrita em Redis com sincronização assíncrona posterior no banco relacional (write-behind).
- [ ] FED-NF04: As rotas de interação do feed devem possuir rate limiting por token de usuário para mitigar abuso e automação de cliques em massa.

### C. Requisitos Não Funcionais de Upload de Mídia (UPM-NF)
- [ ] UPM-NF01: A Presigned URL deve expirar em no máximo 3 minutos.
- [ ] UPM-NF02: As credenciais do provedor de storage devem permanecer apenas nas variáveis de ambiente da API, sem exposição ao cliente mobile.

### D. Requisitos Não Funcionais do Chat (CHT-NF)
- [ ] CHT-NF01: O módulo de chat deve suportar comunicação bidirecional escalonável, preferencialmente com Spring WebFlux ou com uso de Kafka/RabbitMQ como message broker entre instâncias.
- [ ] CHT-NF02: O histórico de mensagens e os estados de entrega devem ser persistidos em banco NoSQL orientado a documentos, como MongoDB.
- [ ] CHT-NF03: A presença dos usuários (ONLINE/OFFLINE) deve ser rastreada em estrutura de leitura rápida, como Redis, para decidir entre entrega imediata e fila offline.
