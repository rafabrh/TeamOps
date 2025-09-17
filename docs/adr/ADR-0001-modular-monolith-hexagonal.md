# ADR-0001: Modular Monolith + Hexagonal (Ports & Adapters)

## Contexto
Precisamos de desenvolvimento rápido sem a complexidade operacional de microserviços, mantendo baixo acoplamento e fronteiras claras de domínio (users, teams, projects). Queremos “pronto para fatiar” no futuro, se o escopo e a escala justificarem.

## Decisão
Adotar **Modular Monolith** com **Arquitetura Hexagonal (Ports & Adapters)** e DDD tático.
- Domínios como módulos coesos (users, teams, projects, shared).
- “Ports” (interfaces) na camada de aplicação.
- “Adapters” para Web (REST), Persistência (JPA), Segurança (JWT), etc.
- Dependência do domínio apenas para dentro; detalhes técnicos plugáveis.

## Alternativas Consideradas
- Microsserviços desde o início: descartado (overhead de orquestração/observabilidade/latência sem ganho imediato).
- MVC anêmico único: descartado (acoplamento alto, difícil de evoluir).

## Consequências
+ (+) Separação de responsabilidades, testes mais simples, facilidade para extrair serviços no futuro.
+ (+) Redução de dívida técnica e de “god modules”.
- (–) Maior disciplina inicial em boundaries e dependências.

## Status
Aceito — 2025-09-17
