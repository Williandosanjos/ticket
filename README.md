# API Tickets

API REST para registro e acompanhamento de tickets de atendimento. Desenvolvida como desafio técnico para a vaga de Desenvolvedor Backend Júnior (Java), com foco em código limpo, regras de negócio bem isoladas e testes automatizados.

## Tecnologias

- Java 17+
- Spring Boot 4.1.1 (Web MVC, Data JPA, Validation, Security)
- PostgreSQL 18
- Flyway (versionamento do banco)
- Lombok
- JUnit 5 e Mockito
- Maven (wrapper incluído)

## Como rodar

### Pré-requisitos

- JDK 17 ou superior
- PostgreSQL instalado e em execução (porta padrão 5432)

### 1. Criar o banco de dados

O Flyway cria as tabelas, mas não cria o banco. Execute uma vez:

```sql
CREATE DATABASE db_ticket;
```

### 2. Configurar as credenciais

A aplicação lê usuário e senha do banco por variáveis de ambiente, para que nenhuma credencial fique no repositório:

| Variável | Descrição | Padrão |
|---|---|---|
| `DB_USER` | Usuário do PostgreSQL | `postgres` |
| `DB_PASSWORD` | Senha do PostgreSQL | sem padrão |

**Linux/macOS**

```bash
export DB_PASSWORD=sua_senha
```

**Windows (PowerShell)**

```powershell
$env:DB_PASSWORD="sua_senha"
```

### 3. Subir a aplicação

```bash
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd spring-boot:run`. A API fica disponível em `http://localhost:8080`.

### 4. Rodar os testes

```bash
./mvnw test
```

## Modelo de dados

**Ticket**

| Campo | Tipo | Observação |
|---|---|---|
| `id` | UUID | gerado automaticamente |
| `titulo` | texto | obrigatório, 5 a 100 caracteres |
| `descricao` | texto | obrigatória, até 1000 caracteres |
| `emailUsuario` | texto | obrigatório, formato de e-mail válido |
| `prioridade` | enum | `BAIXA`, `MEDIA`, `ALTA` |
| `status` | enum | `ABERTO`, `EM_ANDAMENTO`, `RESOLVIDO`, `FECHADO` |
| `criadoEm` | data/hora | preenchido na criação |
| `atualizadoEm` | data/hora | atualizado a cada alteração |

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/tickets` | Cria um ticket |
| `GET` | `/tickets/{id}` | Busca um ticket por id |
| `GET` | `/tickets` | Lista com filtros (`status`, `prioridade`) e paginação (`page`, `size`) |
| `PUT` | `/tickets/{id}` | Atualiza título, descrição e prioridade |
| `PATCH` | `/tickets/{id}/status` | Altera o status |
| `DELETE` | `/tickets/{id}` | Remove o ticket (somente se estiver `ABERTO`) |
| `GET` | `/tickets/atrasados` | Lista tickets de prioridade `ALTA` sem resposta há mais de 24 horas |

### Exemplo: criar ticket

```http
POST /tickets
Content-Type: application/json

{
  "titulo": "Não consigo acessar minha conta",
  "descricao": "Ao tentar entrar, a página retorna erro.",
  "emailUsuario": "usuario@email.com",
  "prioridade": "ALTA"
}
```

Resposta `201 Created`:

```json
{
  "id": "3f2b8c1e-7a4d-4e9b-9c55-1b2d3e4f5a6b",
  "titulo": "Não consigo acessar minha conta",
  "descricao": "Ao tentar entrar, a página retorna erro.",
  "emailUsuario": "usuario@email.com",
  "prioridade": "ALTA",
  "status": "ABERTO",
  "criadoEm": "2026-09-24T10:30:00",
  "atualizadoEm": "2026-09-24T10:30:00"
}
```

## Regras de negócio

1. Todo ticket nasce com status `ABERTO`.
2. Transições de status permitidas: `ABERTO` → `EM_ANDAMENTO` → `RESOLVIDO` → `FECHADO`. Também é permitida a reabertura `RESOLVIDO` → `EM_ANDAMENTO`. Qualquer outra transição é rejeitada.
3. Ticket `FECHADO` não pode ser editado.
4. Um mesmo e-mail não pode ter mais de 5 tickets em aberto ao mesmo tempo.
5. Somente tickets com status `ABERTO` podem ser removidos.
6. Tickets de prioridade `ALTA` sem resposta há mais de 24 horas aparecem em `/tickets/atrasados`.

## Tratamento de erros

Os erros são devolvidos em JSON padronizado:

| Código | Situação |
|---|---|
| `400` | Dados de entrada inválidos |
| `404` | Ticket não encontrado |
| `422` | Regra de negócio violada |

## Estrutura do projeto

```
src/main/java/com/boaventura/ticket
├── config        # configurações (segurança)
├── controller    # camada HTTP
├── dto           # objetos de entrada e saída
├── entity        # entidades JPA
├── enums         # Status e Prioridade
├── exception     # exceções de negócio e handler global
├── repository    # acesso a dados (Spring Data JPA)
└── service       # regras de negócio

src/main/resources
└── db/migration  # scripts Flyway
```

## Decisões técnicas

- **Camadas separadas.** O controller apenas recebe e responde requisições; as regras de negócio ficam no service.
- **DTOs de entrada e saída.** A entidade nunca é exposta diretamente pela API, o que permite evoluir o banco sem quebrar o contrato.
- **Flyway para o schema.** O banco é versionado por migrations. O Hibernate apenas valida o schema (`ddl-auto=validate`), sem alterá-lo.
- **UUID como identificador.** Evita ids sequenciais previsíveis na API.
- **Enums salvos como texto** (`EnumType.STRING`), para que reordenar valores no código não corrompa os dados.
- **Datas de auditoria na entidade** (`@PrePersist` e `@PreUpdate`), mantendo `criadoEm` e `atualizadoEm` consistentes.
- **Credenciais fora do código.** Usuário e senha do banco vêm de variáveis de ambiente.
- **Transições de status no próprio enum.** A regra de quem pode ir para onde fica encapsulada em `Status`, simples de testar e de estender.

## Estratégia de testes

- **Unitários** do service com JUnit 5 e Mockito, cobrindo as regras de negócio: transições de status, edição de ticket fechado e limite de tickets abertos por e-mail.
- **Integração** do controller com MockMvc, validando códigos HTTP e formato das respostas de erro.

## Fluxo de trabalho (Git)

- A `main` deve estar sempre estável.
- Cada funcionalidade é desenvolvida em uma branch curta (`feat/...`, `test/...`, `docs/...`) e integrada por merge ou pull request.
- Commits pequenos, no padrão Conventional Commits (`feat:`, `fix:`, `test:`, `refactor:`, `docs:`, `chore:`, `ci:`).
- Pull requests para a `main` disparam o workflow de build e testes no GitHub Actions.

## Status do projeto

- [x] Configuração inicial (Spring Boot, JPA, Flyway, PostgreSQL)
- [x] Entidade `Ticket`, enums e migration V1
- [x] Repository
- [ ] DTOs e validações
- [ ] Tratamento global de erros
- [ ] Criação e busca de tickets
- [ ] Listagem com filtros e paginação
- [ ] Atualização e remoção
- [ ] Transição de status
- [ ] Endpoint de tickets atrasados
- [ ] Testes automatizados

## Melhorias futuras

- Documentação interativa com Swagger/OpenAPI
- `docker-compose` com PostgreSQL para subir o ambiente com um comando
- Testes de integração com Testcontainers
- Ordenação configurável na listagem
- Autenticação e autorização (o Spring Security hoje está liberado para todas as rotas)

## Autor

Desenvolvido por Willian.