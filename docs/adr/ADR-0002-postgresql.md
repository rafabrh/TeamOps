# ADR-0002: PostgreSQL 16 como banco padrão

## Contexto
Banco relacional robusto, open-source e com excelente integração Spring Data JPA/Flyway. Clientes Oracle podem existir; queremos manter opcional de perfil/driver.

## Decisão
Usar **PostgreSQL 16** em dev/prod. **Flyway** para versionamento de schema e seeds controlados. Testes de integração com **Testcontainers** (postgres).

## Alternativas Consideradas
- MySQL/MariaDB: OK, mas Postgres oferece JSONB, CTEs e ecossistema mais maduro para nosso caso.
- Oracle como padrão: agrega custo/complexidade; manter como perfil opcional futuro.

## Consequências
+ (+) Features avançadas (JSONB, window functions), tooling sólido, comunidade ampla.
+ (+) Fluxo de migração previsível (Flyway), testes determinísticos (Testcontainers).
- (–) Scripts extras se cliente exigir Oracle (dialetos/migrações específicas).

## Status
Aceito — 2025-09-17
