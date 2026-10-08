# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-08 | **Rama:** `feature/map-view` (commit feat `c097f1b` + sistema de contexto sin commitear) | `develop` = origin/develop

## Hecho
- Repo sincronizado con origin/develop (Clean Architecture, use cases, version catalog).
- Feature Mapa de Viajes (SDMD): use case, MapViewModel, MapScreen, navegación; 39 tests OK; instalada en Xiaomi 2412DPC0AG.
- Sistema de contexto: AGENTS.md, docs/, scripts/verify.sh, skills.

## Siguiente paso
1. Añadir `MAPS_API_KEY=...` a `local.properties` (Google Cloud, Maps SDK for Android) y verificar en dispositivo: markers, info window -> detalle, estado vacío. Ver SPEC map-view.
2. Decidir push/PR de `feature/map-view` -> `develop` (no pusheado).

## Decisiones vigentes
- maps-compose 4.4.2 (9.x exige compileSdk 37/AGP 9.1).
- Contexto en capas: AGENTS.md corto + docs bajo demanda; una sesión por unidad de trabajo.
- `stash@{0} backup-sprint1-old-arch` conserva el trabajo viejo (nav type-safe, chips, dark mode). No aplicar entero.

## Bloqueos
- Sin `MAPS_API_KEY` el mapa no pinta tiles; markers/info window sin verificar.

## Deuda técnica (futuras specs)
Filter chips funcionales, nav type-safe, dark mode, fixes de cámara, tabs Recuerdos/Perfil, clustering, subir AGP/compileSdk, detekt/ktlint.
