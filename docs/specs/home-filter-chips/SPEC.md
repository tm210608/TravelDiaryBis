# SPEC: Filtro real de chips en Home

> **Estado:** APROBADA
> Metodología SDMD — la spec es la fuente de verdad, el código la sirve.
> "Done" = build + tests + app instalada + flujo ejecutado + evidencia.

---

## Qué construimos
Los chips de Home (Todo / Europa / Asia / América / Favoritos) pasan a filtrar de verdad la lista de viajes.
El continente se deduce del texto libre `country` con un diccionario local país→continente (ES + EN,
sin distinguir mayúsculas ni tildes), sin tocar Room. El filtro se combina con la búsqueda y, si no hay
resultados, se muestra un estado vacío con botón "Ver todos".

## Criterios de aceptación (testeables, sin ambigüedad)
Leyenda: `[x]` verificado con evidencia; `[~]` verificado en unit test (pendiente dispositivo); `[ ]` pendiente.

**Resolución de continente (`ContinentResolver`)**
- [ ] AC-01: "France", "Francia", "francia ", "FRANCIA" -> `EUROPA` (trim + case-insensitive).
- [ ] AC-02: "Japón" y "Japon" -> `ASIA` (insensible a tildes/diacríticos).
- [ ] AC-03: "México", "Estados Unidos", "EEUU", "USA", "Brasil", "Cuba", "Canada" -> `AMERICA` (Norte, Centro, Sur y Caribe).
- [ ] AC-04: Países de África u Oceanía (p. ej. "Marruecos", "Australia") -> `AFRICA` / `OCEANIA` (no aparecen en ningún chip de continente).
- [ ] AC-05: País no reconocido ("Marte", errata, vacío) -> `null`.
- [ ] AC-06: Transcontinentales siguen la ONU (M49): "Rusia" -> `EUROPA`, "Turquía" -> `ASIA`, "Egipto" -> `AFRICA`.

**Filtrado (`FilterEntriesUseCase`)**
- [ ] AC-07: Chip "Todo" -> todas las entradas (incluidas las de país no reconocido, África y Oceanía).
- [ ] AC-08: Chip "Europa" / "Asia" / "América" -> solo entradas cuyo `country` resuelve a ese continente.
- [ ] AC-09: Chip "Favoritos" -> solo entradas con `isFavourite == true`, de cualquier continente.
- [ ] AC-10: Chip + búsqueda se combinan con AND (la búsqueda sigue mirando title/location/country).
- [ ] AC-11: Se conserva el orden original de las entradas.

**Home (ViewModel + UI)**
- [ ] AC-12: Al pulsar un chip, la lista "Todos los Viajes" y el contador "N viajes" reflejan el resultado filtrado.
- [ ] AC-13: "Momentos Destacados" muestra los favoritos del resultado filtrado (máx. 3).
- [ ] AC-14: Si hay viajes en la BD pero el filtro (chip y/o búsqueda) deja 0, se muestra "No hay viajes en <Chip>"
  (o "No hay resultados para \"<query>\"" si hay búsqueda) y un botón "Ver todos".
- [ ] AC-15: "Ver todos" vuelve el chip a "Todo" y limpia la búsqueda.
- [ ] AC-16: El chip seleccionado sobrevive a la rotación (`SavedStateHandle`); al reabrir la app vuelve a "Todo".
- [ ] AC-17: Build y tests OK con `scripts/verify.sh`; tests nuevos pasan.

## Fuera de alcance
- Campo `continent` en Room, migraciones o selector de continente en el formulario de alta.
- Deducir continente por coordenadas GPS o geocoding.
- Chips de África, Oceanía u "Otros"; multiselección de chips.
- Filtrar el mapa (`MapScreen`) por chip.
- Persistir el chip entre aperturas de la app (DataStore).
- Estado vacío de BD sin viajes (comportamiento actual sin cambios).

## UI states
```kotlin
enum class HomeFilter(val label: String) { TODO("Todo"), EUROPA("Europa"), ASIA("Asia"), AMERICA("América"), FAVORITOS("Favoritos") }

sealed interface HomeListState {
    data class Results(val entries: List<TravelEntry>, val featured: List<TravelEntry>) : HomeListState
    data class NoResults(val filter: HomeFilter, val query: String) : HomeListState // BD con datos, filtro deja 0
}
```
- `Results`: cabecera "Momentos Destacados" (featured), "Todos los Viajes" + "N viajes" y lista.
- `NoResults`: secciones de lista sustituidas por mensaje + botón "Ver todos" (`testTag = "home_no_results"`).
- Si la BD está vacía se emite `Results(emptyList(), emptyList())` (igual que hoy).

## Navegación
Sin cambios. Todo ocurre dentro de `home`.

## Arquitectura
- **domain/model/** `Continent.kt` (enum `EUROPA, ASIA, AMERICA, AFRICA, OCEANIA`), `HomeFilter.kt`.
- **domain/** `ContinentResolver.kt` (object puro: normaliza con `java.text.Normalizer` y busca en un mapa país→continente, ES+EN + variantes comunes: EEUU, USA, UK, Reino Unido, Holanda...).
- **domain/usecase/** `FilterEntriesUseCase.kt` (`@Inject constructor()`, `operator fun invoke(entries, filter, query): List<TravelEntry>`; sin repositorio, función pura).
- **ui/viewmodel/** `HomeViewModel`: inyecta `FilterEntriesUseCase` + `SavedStateHandle`; `selectedFilter: StateFlow<HomeFilter>` (`savedStateHandle.getStateFlow`), `listState: StateFlow<HomeListState>` vía `combine(allEntries, selectedFilter, searchQuery)`; `onFilterSelected(HomeFilter)`, `onShowAll()`. Sustituye `selectedChip: State<String>` (mutableStateOf) por StateFlow.
- **ui/components/** `FilterChipsRow` recibe `List<HomeFilter>` / `HomeFilter` (o mapea labels en la pantalla).
- **ui/screens/** `TravelDiaryHomeScreen`: pinta `HomeListState` y el estado `NoResults`.
- **model/SampleData.kt**: `filterChips` deja de usarse (se sustituye por `HomeFilter.entries`); se elimina la `val`.

## Permisos, lifecycle y persistencia
- Sin permisos nuevos. Solo lectura de entradas; sin cambios en Room.
- Rotación: chip en `SavedStateHandle`; búsqueda mantiene el comportamiento actual.

## Plan de tests
- [ ] build: `scripts/verify.sh`
- [ ] unit `ContinentResolverTest`: AC-01..AC-06.
- [ ] unit `FilterEntriesUseCaseTest`: AC-07..AC-11.
- [ ] unit `HomeViewModelTest` (actualizado, `SavedStateHandle()` real + `backgroundScope.launch{collect}` + `advanceUntilIdle()`): AC-12..AC-16 (rotación = restaurar desde `SavedStateHandle` con valor previo).
- [ ] dispositivo/emulador: `scripts/verify.sh --device`; flujo: crear viajes en Francia, Japón, México, "Marte" (1 favorito) -> pulsar cada chip -> búsqueda + chip -> estado vacío -> "Ver todos" -> rotar.
- [ ] evidencia: capturas en `docs/specs/home-filter-chips/` (chip-europa.png, chip-favoritos.png, no-results.png).

## Edge cases
- `country` con espacios, mayúsculas, tildes o "ñ" (España/Espana) -> se normaliza.
- `country` vacío o desconocido -> solo en "Todo".
- Entrada favorita de país desconocido -> aparece en "Favoritos" y "Todo".
- Cambiar de chip con la búsqueda activa -> se mantiene la búsqueda (AND).
- Se añade/borra un viaje con un chip activo -> la lista filtrada se actualiza sola (Flow).
