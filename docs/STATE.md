# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-08 | **Rama:** `feature/map-view` (commit `2e098a3`, pusheada a origin) | `develop` = origin/develop | **PR #29 abierta** a develop

## Hecho
- Feature Mapa de Viajes (SDMD): use case, MapViewModel, MapScreen, navegación; 39 tests OK; instalada en Xiaomi 2412DPC0AG sin crash.
- Verificado en dispositivo SIN API key: tab Mapa -> MapScreen con título "Mapa de Viajes" y estado vacío correcto (0 viajes). Evidencia: `docs/evidence/mapa-empty-state.png`.
- Sistema de contexto commiteado: AGENTS.md, docs/, scripts/verify.sh, skills.
- MCP de GitHub arreglado: se eliminó `.devin/mcp_config.local.json` (pisaba la URL) + `devin mcp login github --scopes repo,read:org,read:user,gist,workflow`. Token OAuth en `%APPDATA%/devin/mcp/oauth/`. Push y PR funcionando.
- `.devin/mcp_config.json` + `.devin/.gitignore` commiteados (sin secretos).

## Siguiente paso
1. Añadir `MAPS_API_KEY=...` a `local.properties` (Google Cloud, Maps SDK for Android) y verificar en dispositivo: tiles, markers, info window -> detalle (hace falta una entrada con ubicación).
2. Revisar/mergear PR #29 -> `develop`.

## Decisiones vigentes
- maps-compose 4.4.2 (9.x exige compileSdk 37/AGP 9.1).
- Contexto en capas: AGENTS.md corto + docs bajo demanda; una sesión por unidad de trabajo.
- `stash@{0} backup-sprint1-old-arch` conserva el trabajo viejo. No aplicar entero.
- Git push: usar token OAuth de `%APPDATA%/devin/mcp/oauth/*.json` como `x-access-token` en URL (gh no autenticado; credential manager vacío).

## Bloqueos
- Sin `MAPS_API_KEY` el mapa no pinta tiles; markers/info window sin verificar.

## Deuda técnica (futuras specs)
Filter chips funcionales, nav type-safe, dark mode, fixes de cámara, tabs Recuerdos/Perfil, clustering, subir AGP/compileSdk, detekt/ktlint.
