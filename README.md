TeamOps
Visão Geral

TeamOps é uma plataforma para gestão de projetos e equipes, projetada para atender ambientes corporativos que exigem organização, controle de acessos, rastreabilidade e escalabilidade.

O sistema fornece um conjunto robusto de funcionalidades para o gerenciamento do ciclo de vida de projetos, desde a criação até a conclusão, contemplando também alocação de pessoas, definição de papéis, permissões granulares e acompanhamento por métricas.

A aplicação foi desenvolvida em Java 21 com Spring Boot 3, persistência em PostgreSQL 16, conteinerização com Docker e provisionamento simplificado via Docker Compose.

Funcionalidades

Gestão de Projetos

Criação, edição, arquivamento e exclusão de projetos.

Associação de equipes, deadlines e status (ativo, concluído, em atraso).

Histórico de alterações em cada projeto.

Gestão de Equipes

Cadastro de usuários.

Estruturação de equipes vinculadas a projetos.

Perfis de acesso baseados em papéis (Administrador, Gestor, Colaborador, Leitura).

Segurança

Autenticação via JWT.

Autorização baseada em roles e permissões específicas.

Integração com Spring Security.

Controle e Auditoria

Registro de todas as ações críticas (auditoria).

Logs de acessos e falhas de autenticação.

Métricas de uso e atividades por usuário.

API REST

Endpoints versionados (/v1/...).

Documentação interativa via Swagger/OpenAPI.

Estrutura de DTOs para entrada/saída.

Métricas e Observabilidade

Exposição de métricas via Actuator.

Health checks para readiness e liveness.

Dashboard integrável com Prometheus/Grafana.

Integrações

Suporte a mensageria (Apache Kafka) para eventos futuros.

Suporte para exportação de dados (CSV/JSON).

Webhooks para notificações externas.

Infraestrutura

Deploy containerizado.

Separação clara entre aplicação e banco de dados.

Configuração externa via variáveis de ambiente.

Migrations gerenciadas pelo Flyway.

Arquitetura
                      +------------------------+
                      |      Frontend UI       |
                      |  (React/Angular/Vue)   |
                      +-----------+------------+
                                  |
                                  v
                       +----------+-----------+
                       |   TeamOps API        |
                       |   (Spring Boot 3)    |
                       +----------+-----------+
                                  |
        ---------------------------------------------------
        |                    |                          |
        v                    v                          v
+---------------+   +------------------+     +----------------------+
| Security Layer|   | Business Services|     |   REST Controllers   |
| (Spring Sec.) |   | (Project/TeamOps)|     | (Swagger/OpenAPI v3) |
+---------------+   +------------------+     +----------------------+
        |                    |                          |
        ---------------------------------------------------
                                  |
                                  v
                       +----------+-----------+
                       |   Persistence Layer  |
                       |  (Spring Data JPA)   |
                       +----------+-----------+
                                  |
                                  v
                       +----------+-----------+
                       |    PostgreSQL 16     |
                       |   (Dockerized DB)    |
                       +----------------------+


Tecnologias

Backend: Java 21, Spring Boot 3, Spring Data JPA, Spring Security, MapStruct, Flyway.

Banco de Dados: PostgreSQL 16 (Docker).

Documentação da API: OpenAPI 3 (Swagger UI).

Build e Dependências: Maven 3.9+.

Observabilidade: Spring Actuator, métricas Prometheus-ready.

Mensageria (opcional): Apache Kafka.

Infraestrutura: Docker, Docker Compose.

Como Executar
Pré-requisitos

Java 21

Maven 3.9+

Docker Desktop

Passos

Subir o banco de dados:

docker compose up -d


Executar a aplicação:

mvn clean spring-boot:run
