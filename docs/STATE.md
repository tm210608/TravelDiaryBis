# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-15 | **Rama:** `feature/exportar-entradas` | **Commit:** `c4144c3` evidencia + SPEC marcada

## Hecho
- Feature "exportar entradas" completa y verificada (6 commits sobre `develop`):
  - domain: `ExportDocument`/`ExportedEntry` (`@Serializable`, sobre versionado
    `formatVersion=1` pensado como DTO de futura sync), `DiaryExporter`, `PhotoLoader`,
    `ExportStorage`, `ExportEntriesUseCase` (saveToUri / toTempZip).
  - data: `DiaryExporterImpl` (ZipOutputStream + kotlinx-json `encodeDefaults=true`),
    `ContentResolverPhotoLoader` (content/file/path; http no descarga), `ExportStorageImpl`,
    DI en `di/ExportModule.kt`. `buildConfig=true` para `VERSION_NAME`.
  - ui: `ExportUiState` (Idle/Exporting/ShareReady/Success/Error) + modo selección en
    `HomeViewModel`; icono exportar en TopBar (solo si hay entradas), SelectionTopBar
    (N seleccionadas/Todo/Exportar deshabilitado a 0 + menú), checkboxes en EntryListCard,
    FAB oculto, BackHandler, FileProvider (`${applicationId}.fileprovider`, cache `exports/`),
    ruta `home?export={export}` (Perfil -> selección directa).
- Verificado en EMULADOR API 36.1: 2 entradas con foto real CameraX -> Guardar en
  Downloads + ZIP inspeccionado (diary.json + foto) + share sheet + cancelar SAF +
  toggle + Perfil. Evidencia `docs/specs/exportar-entradas/evidencia-*.png` (11).
  Bug pillado en verificación: kotlinx omite defaults -> `encodeDefaults=true` (`a080097`).
- 94 tests 0 fallos (`scripts/verify.sh`).

## Siguiente paso
1. Merge `feature/exportar-entradas` -> `develop` (revisar diff; sin pantalla nueva).
2. Pendiente en Xiaomi físico: flujo exportar completo + lo arrastrado (modo claro
   dinámico, editar entrada, empty states, springs).
3. Candidatas: importar/restaurar (lee `formatVersion=1`), esquema light de marca,
   nav type-safe, sync online. Usuario apunta a Play Store a medio plazo.
4. PNG sin seguimiento en `docs/specs/dark-mode/` (evidencias del usuario).

## Decisiones vigentes
- Exportar = ZIP (`diary.json` + `photos/entry-<id>-0.<ext>`); Guardar SAF + Compartir
  FileProvider; SIN importación (otra spec).
- `formatVersion=1` debe seguir parseándose siempre (contrato para import/sync online).
- Room sin cambios (sin uuid por entrada; queda para la spec online).
- material3 1.5.0-alpha18, maps-compose 4.4.2; no subir versiones sin pedir.
- `local.properties` no se commitea. Sin push. Commits `feat|fix|test|docs|chore:`.

## Bloqueos
- Emulador API 36.1: Pixel Launcher roto (ANR en bucle); desactivado con
  `pm disable-user com.google.android.apps.nexuslauncher` (queda FallbackHome).
  Verificación hecha en emulador, pendiente en Xiaomi físico.

## Deuda técnica
Migraciones Room reales antes de Play Store (hoy destructive fallback borra datos),
política de privacidad + Data Safety (fotos/ubicación), keystore/R8/AAB, UI tests Compose,
importar diario, sync online, esquema light de marca, nav type-safe, CameraScreen
(`LocalLifecycleOwner` deprecado), clustering, detekt/ktlint.
