# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-08 | **Rama:** `develop` | **Commit:** `e560f99` mergeado — filter chips funcionales

## Hecho
- Feature filter-chips completa (SPEC -> implement -> verify -> merge), verificada en Xiaomi:
  - `domain/model/Continent.kt`: enum + `continentOf(country)` con tabla ~140 países
    (español/inglés, case y acentos-insensitive vía `Normalizer`).
  - `domain/usecase/FilterEntriesByChipUseCase.kt`: Todo / Europa / Asia / América / Favoritos.
  - `HomeViewModel`: `selectedChip` ahora `StateFlow`; `entriesList` combina
    entries + query + chip (AND). Eliminado `insertSampleEntry`/`AddEntryUseCase` sin uso.
  - HomeScreen: `selectedChip` con `collectAsState()`.
- Evidencias en `docs/specs/filter-chips/` (europa, asia, américa, favoritos, chip+búsqueda, landscape).
- 57 unit tests OK, build OK, app en dispositivo sin crash. Rotación conserva el chip.

## Siguiente paso
1. Nueva feature SDMD desde `develop` (`/spec` -> `/implement-spec` -> `/verify` -> `/handoff`).
   Candidatas: tabs Recuerdos/Perfil, editar entrada, navegación type-safe, dark mode global.
2. Opcional: borrar `feature/map-view` y `feature/filter-chips` (ya mergeadas).

## Decisiones vigentes
- `maps-compose` 4.4.2 (no subir a 9.x sin pedirlo; requiere compileSdk 37/AGP 9.1).
- Continente derivado de `country` con tabla interna (offline); país desconocido solo en "Todo".
  Sin chips África/Oceanía por ahora.
- `local.properties` con `MAPS_API_KEY` no se commitea.
- Commits atómicos en rama `feature/` o `fix/` y merge `--no-ff` a `develop`.

## Bloqueos
- Ninguno.

## Deuda técnica (futuras specs)
Tabs Recuerdos/Perfil, nav type-safe, dark mode global, editar entrada, fixes de cámara,
chips África/Oceanía si hacen falta, clustering, subir AGP/compileSdk, detekt/ktlint,
`Icons.Filled.ArrowBack` deprecados.
