# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-16 | **Rama:** `feature/importar-entradas` | **Commit:** evidencia Xiaomi + SPEC marcada

## Hecho
- `feature/exportar-entradas` mergeada a `develop` con `--no-ff`; post-merge verde (94 tests).
- Feature "importar entradas" completa y verificada (6 commits sobre `develop`):
  - domain: `ImportResult` (Success/InvalidFile/UnsupportedVersion/Empty),
    `DiaryImporter`, `ImportStorage`, `ImportEntriesUseCase` (inserta cada entrada
    como nueva vía `repository.insertEntry`, sin dedup).
  - data: `DiaryImporterImpl` (ZipInputStream + kotlinx-json `ignoreUnknownKeys=true`,
    tolerante a campos futuros; requiere `formatVersion=1` y `entries` no vacío),
    `ImportStorageImpl` (ContentResolver openInputStream + copia fotos a
    `filesDir/photos/import-<ts>-<i>.<ext>`, devuelve `file://path`), DI en
    `di/ExportModule.kt`.
  - ui: `ImportUiState` (Idle/Importing/Success(count)/Error) en `ProfileViewModel`
    + card "Importar diario" en Perfil (launcher `OpenDocument` mimes zip +
    spinner durante import) + snackbar éxito/error.
- Verificado en EMULADOR API 36.1 (round-trip real): ZIP exportado (2 entradas)
  importado sobre diario de 2 -> 4 viajes; reimportado -> 6 (duplicados
  intencionados); foto copiada `import-…jpg` y visible en Home; zip corrupto ->
  snackbar "No es un archivo de diario válido" sin tocar datos; cancelar SAF ->
  sin error. Evidencia `docs/specs/importar-entradas/evidencia-0*.png` (5).
- Verificado en XIAOMI FÍSICO (HyperOS, tema claro): export 2 entradas reales ->
  SAF Downloads -> import desde Perfil -> "2 entradas importadas" -> 4 viajes
  con fotos visibles. Las fotos de esas entradas son URLs remotas
  (unsplash) -> `photoFile=null` por diseño (no se descargan); el imageUrl
  se preserva y Coil las renderiza. Comportamiento correcto según spec.
  Evidencia `evidencia-06..08-xiaomi-*.png` (3).
- 107 tests 0 fallos (`scripts/verify.sh`).

## Siguiente paso
1. Merge `feature/importar-entradas` -> `develop` (sin pantalla nueva).
2. El diario del Xiaomi tiene 2 duplicados del round-trip ("Con GPS" x2,
   "Prueba Mapa" x2) — borrarlos a mano si molestan.
3. Candidatas: migraciones Room reales (previo Play Store), esquema light de
   marca, nav type-safe, sync online. Usuario apunta a Play Store a medio plazo.
4. PNG sin seguimiento en `docs/specs/dark-mode/` (evidencias del usuario).

## Decisiones vigentes
- Exportar = ZIP (`diary.json` `formatVersion=1` + `photos/`); Importar lee ese
  mismo formato y hace MERGE (nada se borra, siempre ids nuevos, sin dedup).
- `formatVersion=1` es contrato permanente (parseable en futuras versiones/sync).
- `imageUrl` de foto ausente en ZIP se conserva (http remoto sigue visible).
- Room sin cambios (sin uuid por entrada; queda para la spec online).
- material3 1.5.0-alpha18, maps-compose 4.4.2; no subir versiones sin pedir.
- `local.properties` no se commitea. Sin push. Commits `feat|fix|test|docs|chore:`.

## Bloqueos
- Emulador API 36.1: Pixel Launcher deshabilitado (ANR en bucle la sesión
  anterior); queda FallbackHome. Restaurar: `pm enable com.google.android.apps.nexuslauncher`.

## Deuda técnica
Migraciones Room reales antes de Play Store (hoy destructive fallback borra datos),
política de privacidad + Data Safety (fotos/ubicación), keystore/R8/AAB, UI tests
Compose, sync online, esquema light de marca, nav type-safe, CameraScreen
(`LocalLifecycleOwner` deprecado), clustering, detekt/ktlint.
Decisión de producto pendiente (no bug): las fotos con `imageUrl` http(s)
NO viajan en el ZIP — si la URL muere, la imagen desaparece. Valorar
descargarlas al exportar en una futura spec.
