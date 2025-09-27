[![CI](https://github.com/rafabrh/TeamOps/actions/workflows/ci.yml/badge.svg)](https://github.com/rafabrh/TeamOps/actions/workflows/ci.yml)


# TeamOps - Gestão de Projetos e Equipes

**TeamOps** é uma plataforma para gestão de projetos e equipes, projetada para ambientes corporativos que exigem **organização, controle de acessos, rastreabilidade e escalabilidade**.

O sistema oferece um conjunto robusto de funcionalidades para o **ciclo de vida de projetos**, desde a criação até a conclusão, contemplando também **alocação de pessoas, definição de papéis, permissões granulares e acompanhamento por métricas**.

---

##  Funcionalidades

### Gestão de Projetos
- Criação, edição, arquivamento e exclusão de projetos.  
- Associação de equipes, deadlines e status (ativo, concluído, em atraso).  
- Histórico de alterações em cada projeto.  

### Gestão de Equipes
- Cadastro de usuários.  
- Estruturação de equipes vinculadas a projetos.  
- Perfis de acesso baseados em papéis (Administrador, Gestor, Colaborador, Leitura).  

### Segurança
- Autenticação via **JWT**.  
- Autorização baseada em **roles** e permissões específicas.  
- Integração com **Spring Security**.  

### Controle e Auditoria
- Registro de todas as ações críticas (auditoria).  
- Logs de acessos e falhas de autenticação.  
- Métricas de uso e atividades por usuário.  

### API REST
- Endpoints versionados (`/v1/...`).  
- Documentação interativa via **Swagger/OpenAPI**.  
- Estrutura de DTOs para entrada/saída.  

### Métricas e Observabilidade
- Exposição de métricas via **Spring Actuator**.  
- Health checks para readiness e liveness.  
- Integração com **Prometheus/Grafana**.  

### Integrações
- Suporte a mensageria (**Apache Kafka**) para eventos futuros.  
- Exportação de dados (CSV/JSON).  
- Webhooks para notificações externas.  

### Infraestrutura
- Deploy containerizado (Docker + Docker Compose).  
- Banco de dados desacoplado da aplicação.  
- Configuração via variáveis de ambiente.  
- Migrations com **Flyway**.  


##  Arquitetura

```text
               +------------------------+
               |      Frontend UI       |
               | (React / Angular / Vue)|
               +-----------+------------+
                           |
                           v
               +------------------------+
               |      TeamOps API       |
               |     (Spring Boot 3)    |
               +-----------+------------+
                           |
        -------------------------------------------------
        |                   |                     |
        v                   v                     v
+---------------+   +------------------+   +----------------------+
| Security Layer|   | Business Services|   |   REST Controllers   |
| (Spring Sec.) |   | (Projects/Teams) |   | (Swagger/OpenAPI v3) |
+---------------+   +------------------+   +----------------------+
                           |
                           v
               +------------------------+
               |   Persistence Layer    |
               |  (Spring Data JPA)     |
               +-----------+------------+
                           |
                           v
               +------------------------+
               |     PostgreSQL 16      |
               |     (Dockerized DB)    |
               +------------------------+

 Tecnologias

Backend: Java 21, Spring Boot 3, Spring Data JPA, Spring Security, MapStruct, Flyway

Banco de Dados: PostgreSQL 16 (Docker)

Documentação: OpenAPI 3 (Swagger UI)

Build: Maven 3.9+

Observabilidade: Spring Actuator + Prometheus-ready

Mensageria (opcional): Apache Kafka

Infraestrutura: Docker, Docker Compose

 Como Executar
Pré-requisitos

Java 21

Maven 3.9+

Docker Desktop

Passos

Subir o banco de dados:

docker compose up -d


Executar a aplicação:

# Gera 32 bytes aleatórios de forma compatível
$bytes = New-Object 'System.Byte[]' 32
$rng = [System.Security.Cryptography.RNGCryptoServiceProvider]::Create()
$rng.GetBytes($bytes)
$rng.Dispose()

# Converte pra Base64 e exporta na sessão atual
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)

# (Opcional) visualizar
Write-Host "JWT_SECRET = $env:JWT_SECRET"

# Sobe a aplicação
mvn spring-boot:run


