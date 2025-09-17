# ADR-0003: Autenticação JWT (Resource Server)

## Contexto
Precisamos de autenticação stateless, interoperável, simples de escalar horizontalmente e pronta para integração com IdPs/SSO (Keycloak / Spring Authorization Server).

## Decisão
Usar **JWT** assinado (chave assimétrica RSA). Fluxo de **Access Token** curto + **Refresh Token**. Autorização via RBAC (roles/authorities) no Spring Security com `@PreAuthorize`.

## Alternativas Consideradas
- Sessões stateful (server-side): não escalam tão bem e complicam horizontalização.
- Opaque tokens sem introspecção: exigem chamada ao IdP a cada requisição.

## Consequências
+ (+) Sem estado no servidor, compatível com gateways e múltiplos frontends.
+ (+) Tracing/observability simples (propagar subject/claims).
- (–) Gestão de chaves/rotação, clock skew, fluxo de refresh exige cuidado.

## Status
Aceito — 2025-09-17
