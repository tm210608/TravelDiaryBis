# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-16 | **Rama:** `feature/migraciones-room` | **Commit:** `19ab9c8` SPEC+evidencia

## Hecho
- `feature/importar-entradas` mergeada a `develop` con `--no-ff`; post-merge verde (107 tests).
- Feature "migraciones Room + uuid" completa y verificada (4 commits en rama):
  - Infra: `exportSchema=true`, `room.schemaLocation` (ksp) -> `app/schemas/*.json`
    commiteados (2.json generado ANTES de bump a v3), `testInstrumentationRunner`,
    assets androidTest -> schemas, deps `room-testing` + `androidx.test`.
  - `MIGRATION_2_3` (`data/local/Migrations.kt`): `ALTER TABLE … ADD uuid TEXT
    NOT NULL DEFAULT ''` + UPDATE por fila con `UUID.randomUUID()`.
  - `fallbackToDestructiveMigration` ELIMINADO: sin migración = crash ruidoso.
  - `uuid` en `TravelEntryEntity`/`TravelEntry` (default `UUID.randomUUID()`),
    `EntryDetails` lo propaga (isDirty sigue bien), mapper ambos sentidos,
    `ExportedEntry.uuid` (aditivo, formatVersion sigue en 1), importer lo
    conserva o genera si ausente (`ifEmpty`).
- Verificado en XIAOMI FÍSICO: `MigrationTest` instrumentado PASSED (crea DB v2,
  migra a v3, valida filas + uuids distintos). Upgrade in-place v2->v3 con
  datos reales: entradas intactas, app abre, export lleva 2 uuids reales.
  Evidencia `docs/specs/migraciones-room/` + androidTest-results.
- 107 tests JVM 0 fallos (`scripts/verify.sh`).

## Siguiente paso
1. Merge `feature/migraciones-room` -> `develop`.
2. Candidatas: esquema light de marca, nav type-safe, sync online (uuid ya
   preparado), descargar fotos http al exportar. Usuario apunta a Play Store.
3. PNG sin seguimiento en `docs/specs/dark-mode/` (evidencias del usuario).

## Decisiones vigentes
- Exportar = ZIP (`diary.json` `formatVersion=1` + `photos/`); Importar lee ese
  mismo formato y hace MERGE (nada se borra, siempre ids nuevos, sin dedup).
- `formatVersion=1` es contrato permanente; uuid va como campo aditivo dentro.
- uuid local en `TravelEntry`/Room v3; reimportar conserva uuid de origen
  (duplicados lo comparten — aceptado hasta spec de sync).
- Migraciones Room reales obligatorias; schema JSON se commitea por versión.
- material3 1.5.0-alpha18, maps-compose 4.4.2; no subir versiones sin pedir.
- `local.properties` no se commitea. Sin push. Commits `feat|fix|test|docs|chore:`.

## Bloqueos
- Emulador API 36.1: Pixel Launcher deshabilitado (ANR en bucle la sesión
  anterior); queda FallbackHome. Restaurar: `pm enable com.google.android.apps.nexuslauncher`.

## Deuda técnica
Política de privacidad + Data Safety (fotos/ubicación), keystore/R8/AAB, UI tests
Compose, sync online, esquema light de marca, nav type-safe, CameraScreen
(`LocalLifecycleOwner` deprecado), clustering, detekt/ktlint, CI para
connectedDebugAndroidTest (hoy manual).
Decisión de producto pendiente (no bug): las fotos con `imageUrl` http(s)
NO viajan en el ZIP — si la URL muere, la imagen desaparece. Valorar
descargarlas al exportar en una futura spec.
