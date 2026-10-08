# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-08 | **Rama:** `develop` | **PR #29 mergeada** (merge commit `c3af9fc`) — map-view en develop

## Hecho
- Feature Mapa de Viajes (SDMD) VERIFICADA en dispositivo: tiles, marker, InfoWindow -> detalle, estado vacío. Evidencias en `docs/evidence/`. SPEC actualizada.
- `MAPS_API_KEY` añadida a `local.properties` (restringida a app Android + SHA-1 debug).
- Verificado en dispositivo: tab Mapa -> MapScreen, estado vacío (0 viajes), mapa con marker GPS real (Talavera), InfoWindow -> DetailScreen.
- 39 tests OK; app instalada en Xiaomi 2412DPC0AG sin crash.
- Sistema de contexto commiteado: AGENTS.md, docs/, scripts/verify.sh, skills.
- MCP de GitHub arreglado: se eliminó `.devin/mcp_config.local.json` (pisaba la URL) + `devin mcp login github --scopes repo,read:org,read:user,gist,workflow`. Token OAuth en `%APPDATA%/devin/mcp/oauth/`. Push y PR funcionando.
- `.devin/mcp_config.json` + `.devin/.gitignore` commiteados (sin secretos).

## Siguiente paso
1. Nueva feature (flujo SDMD: `/spec` -> `/implement-spec` -> `/verify` -> `/handoff`) desde `develop`. Deuda candidata: filter chips, dark mode, tabs Recuerdos/Perfil, nav type-safe.
2. Opcional: verificar multi-marker bounds, dark mode y landscape (criterios `[ ]` restantes en SPEC map-view).
3. Opcional: borrar `feature/map-view` (remota y local) — ya mergeada.

## Decisiones vigentes
- maps-compose 4.4.2 (9.x exige compileSdk 37/AGP 9.1).
- Contexto en capas: AGENTS.md corto + docs bajo demanda; una sesión por unidad de trabajo.
- `stash@{0} backup-sprint1-old-arch` conserva el trabajo viejo. No aplicar entero.
- Git push: usar token OAuth de `%APPDATA%/devin/mcp/oauth/*.json` como `x-access-token` en URL (gh no autenticado; credential manager vacío).

## Bloqueos
- Ninguno.

## Deuda técnica (futuras specs)
Filter chips funcionales, nav type-safe, dark mode, fixes de cámara, tabs Recuerdos/Perfil, clustering, subir AGP/compileSdk, detekt/ktlint.
