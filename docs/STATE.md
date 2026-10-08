# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-08 | **Rama:** `develop` | **Commit:** `383f4cb` merge — reskin M3 Expressive

## Hecho
- Rediseño Material 3 Expressive implementado en `feature/m3-expressive` (5 commits desde `develop`):
  toolchain subido (Gradle 8.14.3, AGP 8.13.2, Kotlin 2.2.21, KSP 2.2.21-2.0.4, compileSdk 36,
  BOM 2026.06.01, **material3 1.5.0-alpha18**, Room 2.7.2, Hilt 2.57.2) en `3e6dfe0`.
- Sistema de diseño nuevo: `ui/theme/` con tokens Stitch (navy/ámbar, 35 roles), tipografía
  Epilogue/Manrope (`res/font/` TTF variables), `Shape.kt`, `MaterialExpressiveTheme` +
  `MotionScheme.expressive()`, `enableEdgeToEdge()` en MainActivity.
- Reskin de las 7 pantallas: home (eyebrow, FAB squircle 64dp, `animateItem()`), detalle
  (tag ámbar, ArrowBack AutoMirrored), formulario (campos filled, drop zone dashed), recuerdos
  (header + contador), perfil (anillo dorado, stat cards), mapa (pill flotante, pins ámbar).
- Verificado: `verify.sh` = 79 tests 0 fallos; instalado en Xiaomi; capturas en
  `docs/specs/m3-expressive-redesign/evidencia-*.png` (home/detail/mapa/recuerdos/perfil/add dark).
- Stitch MCP conectado (`.devin/mcp_config.local.json`, gitignored); proyecto `17835720265042941367`
  con design system `assets/1b4a895e3dd24c3bae45c12074b3825a`. Referencias en `stitch/`.

## Siguiente paso
1. Rediseño M3 Expressive ya mergeado a `develop`. Candidatas siguientes: navegación type-safe
   o exportar viaje; también pulir modo claro (hoy solo usa color dinámico).
2. Verificación pendiente en dispositivo: modo claro dinámico, editar entrada, empty states,
   transiciones con springs. CameraScreen y editar conservan lógica previa.
3. PNG sin seguimiento bajo `docs/specs/dark-mode/` (evidencias previas del usuario).
   `feature/m3-expressive` puede borrarse solo con confirmación.

## Decisiones vigentes
- material3 **1.5.0-alpha18**: la más nueva compatible con SDK 36/AGP 8.13 (1.5.0-beta01 exige SDK 37).
- Dark = paleta de marca siempre; light = color dinámico en API 31+ (fallback dark en <31).
- Acentos en primer plano usan `primary`; `primaryContainer` solo para fondos rellenos.
- `maps-compose` 4.4.2 sin cambios; no subir versiones sin pedirlo.
- `local.properties` no se commitea. Sin `git push`. Commits `feat|fix|test|docs|chore:`.

## Bloqueos
- Ninguno.

## Deuda técnica
Sin esquema light de marca (solo dinámico), UI tests Compose, nav type-safe, exportar viaje,
CameraScreen (`LocalLifecycleOwner` deprecado), clustering, detekt/ktlint.
