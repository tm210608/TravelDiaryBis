# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-16 | **Rama:** `develop` | **Commit:** handoff con skill `ui-polish-stitch` y conceptos Stitch aprobados (107 tests JVM + 1 androidTest verdes)

## Hecho en esta sesion
- `feature/exportar-entradas` mergeada a `develop` (`71b0c0d`).
- Feature **importar-entradas** implementada, verificada en emulador + **Xiaomi fisico** (round-trip export->import real, 2->4 viajes, fotos visibles) y mergeada a `develop` (`6ac566f`). Evidencias en `docs/specs/importar-entradas/evidencia-01..08*.png`.
- Feature **migraciones-room** implementada, verificada en **Xiaomi fisico** y mergeada a `develop` (`d9abe6c`):
  - `exportSchema = true`, esquemas `app/schemas/.../2.json` y `3.json` commiteados.
  - `MIGRATION_2_3` (`data/local/Migrations.kt`): anade `uuid TEXT NOT NULL DEFAULT ''` y rellena UUID v4 unico por fila existente.
  - Eliminado `fallbackToDestructiveMigration()` (bloqueador Play Store resuelto).
  - `uuid` propagado por `TravelEntry`, `TravelEntryEntity`, `EntryDetails`, `ExportedEntry` (`formatVersion=1` intacto) e importador.
  - `MigrationTest` con `MigrationTestHelper` en `androidTest` PASSED en Xiaomi fisico + upgrade real v2->v3 sin perdida de datos.
- Exploracion visual + prototipado en **Stitch MCP** (proyecto `projects/3867507816919538879` — *"TravelDiary — Conceptos Visuales v2"*):
  - Diagnosticado por que el Xiaomi en modo claro se veia plano: `Theme.kt` usaba `dynamicLightColorScheme(context)` en claro en vez de paleta de marca.
  - Generados y descargados 2 mockups de referencia en `docs/specs/ui-polish-concepts/`:
    1. `stitch-01-atlas-campo-home.png`: Modo Claro de marca **"Cuaderno de Campo Cartografico"** (`#F4EFE6` papel topografico, `#141B24` tinta marina, `#C83E2B` bermellon brujula, cuadricula sutil, tarjetas troqueladas como billetes con talon de fecha y coordenadas GPS en monoespaciada).
    2. `stitch-02-pasaporte-y-contactos-dark.png`: Modo Oscuro evolucionado **"Pasaporte de Expedicion & Hoja de Contactos 35mm"** (cabecera pasaporte con km Haversine offline, sellos entintados rotados por lugar visitado y grid de Recuerdos como negativo Kodak 400).
  - Creada la skill `.devin/skills/ui-polish-stitch/SKILL.md` (`/ui-polish-stitch`).

## Siguiente paso (manana, al abrir sesion con "Lee AGENTS.md y docs/STATE.md y continua")
Ejecutar el **plan de pulido visual aprobado en 2 specs SDMD cortas**:
1. **Spec 1 (`ui-cuaderno-campo-home`)**:
   - Tema Light de marca **"Cuaderno de Campo"** en `Color.kt`/`Theme.kt` (desactivar `dynamicLightColorScheme` por defecto para que el modo claro tenga personalidad propia en el Xiaomi) + voz tipografica monoespaciada para telemetria (`Type.kt`).
   - Fondo con cuadricula cartografica sutil y rediseño de `EntryListCard`, Hero destacado y `DetailScreen` como **billetes troquelados con talon de fecha y coordenadas GPS visibles** (`stitch-01-atlas-campo-home.png`).
2. **Spec 2 (`ui-pasaporte-y-contactos`)**:
   - `ProfileScreen` como **Pasaporte de Expedicion** (tira telemetrica con calculo offline de KM Haversine + seccion **"Visados y Sellos de Ruta"** generados dinamicamente a partir de las entradas reales con `Canvas` y rotacion) (`stitch-02-pasaporte-y-contactos-dark.png`).
   - `MemoriesScreen` como **Hoja de Contactos 35mm** (bordes de carrete y numeracion `01A`, `02A` en mono ambar) + `Polyline` de hilo de ruta en `MapScreen`.

## Decisiones vigentes
- Dualidad visual de marca obligatoria (sin dynamic color que lave la identidad):
  - Claro = **"Cuaderno de Campo Cartografico"** (`#F4EFE6` papel, `#141B24` tinta, `#C83E2B` bermellon).
  - Oscuro = **"Atlas Nocturno"** (`#12121D` obsidiana, `#FFDCA1` ambar linterna, `#B4C9DF` pizarra).
- Exportar/Importar ZIP (`formatVersion=1` con `uuid`) y migraciones Room reales (`v3`) activos y verificados.
- `local.properties` no se commitea. Sin push. Commits `feat|fix|test|docs|chore:`.
- 5 PNG sin seguimiento en `docs/specs/dark-mode/` (evidencias del usuario; no tocar sin confirmacion).

## Bloqueos
- Ninguno. Xiaomi fisico (`2412DPC0AG`) verificado y funcionando con DB v3.

## Deuda tecnica
- Spec 1 y Spec 2 del pulido visual Stitch (siguiente paso inmediato).
- Pre-Play Store: politica de privacidad + Data Safety (fotos/ubicacion), keystore/R8/AAB.
- UI tests Compose, sync online, nav type-safe, CameraScreen (`LocalLifecycleOwner` deprecado), clustering, detekt/ktlint.
