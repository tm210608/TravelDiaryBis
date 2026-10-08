# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-08 | **Rama:** `develop` | **Commit:** `b358d2f` mergeado — cierre de pendientes de map-view

## Hecho
- Criterios pendientes de map-view verificados en Xiaomi 2412DPC0AG:
  - multi-marker bounds (Talavera + Barcelona),
  - dark mode del mapa vía `MapStyleOptions` JSON,
  - orientación landscape.
- `MapScreen.kt` ajustado: padding de bounds en dp/píxeles y estilo dark según `isSystemInDarkTheme()`.
- Nuevo recurso `app/src/main/res/raw/map_style_dark.json`.
- Evidencias añadidas: `mapa-multi-marker-bounds.png`, `mapa-dark-mode.png`, `mapa-landscape.png`.
- Build OK (`compileDebugKotlin`), 39 unit tests OK (`testDebugUnitTest`), app instalada y ejecutada sin crash.

## Siguiente paso
1. Nueva feature SDMD desde `develop` (`/spec` -> `/implement-spec` -> `/verify` -> `/handoff`). Candidatas: filter chips funcionales, tabs Recuerdos/Perfil, navegación type-safe.
2. Opcional: borrar `feature/map-view` (remota y local) — ya mergeada.

## Decisiones vigentes
- `maps-compose` 4.4.2 (no subir a 9.x sin pedirlo; requiere compileSdk 37/AGP 9.1).
- Dark mode del mapa con estilo JSON local; dark mode global de la app pendiente.
- `local.properties` con `MAPS_API_KEY` no se commiteea.
- Commits atómicos en rama `feature/` o `fix/` y merge `--no-ff` a `develop`.

## Bloqueos
- Ninguno.

## Deuda técnica (futuras specs)
Filter chips funcionales, nav type-safe, dark mode global de la app, fixes de cámara, tabs Recuerdos/Perfil, clustering, subir AGP/compileSdk, detekt/ktlint.
