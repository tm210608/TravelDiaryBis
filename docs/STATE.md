# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-08 | **Rama:** `develop` | **Commit:** `6a862a5` mergeado — tabs Recuerdos y Perfil

## Hecho
- Feature filter-chips completa y verificada en Xiaomi; tabla país→continente offline,
  filtros combinados con búsqueda. 57 tests OK. Evidencias en `docs/specs/filter-chips/`.
- Feature tabs Recuerdos/Perfil implementada y mergeada:
  - `GetMemoriesUseCase`: Flow de entradas con foto.
  - `GetTravelStatsUseCase` + `TravelStats`: total, favoritos, países y continentes.
  - MemoriesScreen: galería 3 columnas, foto navega al detalle, estado vacío implementado.
  - ProfileScreen: avatar y cuatro tarjetas de estadísticas.
  - Tabs en Home/Recuerdos/Perfil navegan; Mapa sigue abriendo MapScreen.
- Tests/build OK: `scripts/verify.sh` = 66 tests, 0 fallos.
- Verificado en Xiaomi: Home→Recuerdos→detalle→back; Perfil y sus estadísticas;
  tabs cruzadas; back a Home; Mapa. Evidencias en `docs/specs/bottom-tabs/`.
- Pendiente de verificación en dispositivo: estado vacío de Recuerdos y actualización
  de estadísticas tras añadir una entrada. Criterios quedan sin marcar en SPEC.

## Siguiente paso
1. Nueva feature SDMD desde `develop` (`/spec` → aprobación → `/implement-spec` → `/verify` → `/handoff`).
   Candidatas: editar entrada existente, dark mode global, navegación type-safe.
2. Opcional: borrar ramas `feature/map-view`, `feature/filter-chips` y `feature/bottom-tabs`
   (ya mergeadas); requiere confirmación antes de borrar.

## Decisiones vigentes
- `maps-compose` 4.4.2; no subir a 9.x sin pedirlo (requiere compileSdk 37/AGP 9.1).
- Continente derivado de `country` con tabla interna offline; país desconocido solo en "Todo".
- Recuerdos muestra todas las entradas con foto; Perfil es solo lectura (nombre/foto editable queda para otra spec).
- `local.properties` con `MAPS_API_KEY` no se commitea.
- Commits atómicos en rama `feature/` o `fix/` y merge `--no-ff` a `develop`.

## Bloqueos
- Ninguno.

## Deuda técnica (futuras specs)
Editar entrada, navegación type-safe, dark mode global, fixes de cámara,
chips África/Oceanía si hacen falta, clustering, subir AGP/compileSdk, detekt/ktlint,
`Icons.Filled.ArrowBack` deprecados, verificar estados vacíos y refresco de estadísticas en dispositivo.
