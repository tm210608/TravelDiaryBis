# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-29 | **Rama:** `feature/home-filter-chips` (desde `develop`, sin push) | **Feature:** filtro real de chips

## Hecho
- SPEC `docs/specs/home-filter-chips/SPEC.md` VERIFICADA: chips Todo/Europa/Asia/América/Favoritos filtran de verdad.
- Domain: `Continent`, `HomeFilter`, `ContinentResolver` (diccionario país→continente ES+EN, sin tildes/mayúsculas, ONU M49), `FilterEntriesUseCase` (chip AND búsqueda).
- `HomeViewModel`: `selectedFilter` en `SavedStateHandle`, `listState: HomeListState { Results, NoResults }`, `onFilterSelected`, `onShowAll` (chip Todo + limpia búsqueda).
- Home: estado "No hay viajes en X" / "No hay resultados para \"q\"" + botón "Ver todos". `filterChips` eliminado de SampleData.
- 57 tests OK (18 nuevos). Verificado en emulador Pixel 6 API 34 (VM Devin), no en el Xiaomi. Evidencias en la carpeta de la spec.

## Siguiente paso
1. Push de `feature/home-filter-chips` y PR contra `develop` (pendiente de confirmación del usuario).
2. Opcional: probar en el Xiaomi (`scripts/verify.sh --device`).
3. Nueva feature SDMD desde `develop`: tabs Recuerdos/Perfil, dark mode o nav type-safe.

## Decisiones vigentes
- Continente deducido del texto `country` (sin campo en Room ni migración). País desconocido, África y Oceanía solo en "Todo".
- El chip sobrevive a la rotación pero vuelve a "Todo" al reabrir la app (sin DataStore).
- maps-compose 4.4.2 (9.x exige compileSdk 37/AGP 9.1).
- Contexto en capas: AGENTS.md corto + docs bajo demanda; una sesión por unidad de trabajo.
- `stash@{0} backup-sprint1-old-arch` conserva el trabajo viejo. No aplicar entero.

## Bloqueos
- Ninguno.

## Deuda técnica (futuras specs)
Contador "1 viajes" sin plural, "Momentos Destacados" visible aunque no haya favoritos, nav type-safe, dark mode,
fixes de cámara, tabs Recuerdos/Perfil, clustering, subir AGP/compileSdk, detekt/ktlint, borrar rama `feature/map-view`.
