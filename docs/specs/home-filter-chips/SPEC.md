# SPEC: Filtro real de chips en Home

> **Estado:** VERIFICADA (2026-10-29, emulador Pixel 6 API 34 en VM Devin; 57 tests OK)
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
- [~] AC-01: "France", "Francia", "francia ", "FRANCIA" -> `EUROPA` (trim + case-insensitive). *(ContinentResolverTest; en emulador "Francia" sale en Europa)*
- [~] AC-02: "Japón" y "Japon" -> `ASIA` (insensible a tildes/diacríticos). *(ContinentResolverTest; en emulador "Japón" sale en Asia)*
- [~] AC-03: "México", "Estados Unidos", "EEUU", "USA", "Brasil", "Cuba", "Canada" -> `AMERICA` (Norte, Centro, Sur y Caribe). *(ContinentResolverTest; en emulador "México" sale en América)*
- [~] AC-04: Países de África u Oceanía (p. ej. "Marruecos", "Australia") -> `AFRICA` / `OCEANIA` (no aparecen en ningún chip de continente). *(ContinentResolverTest; en emulador "Australia" solo sale en Todo)*
- [~] AC-05: País no reconocido ("Marte", errata, vacío) -> `null`. *(ContinentResolverTest; en emulador "Marte" solo sale en Todo/Favoritos)*
- [~] AC-06: Transcontinentales siguen la ONU (M49): "Rusia" -> `EUROPA`, "Turquía" -> `ASIA`, "Egipto" -> `AFRICA`. *(ContinentResolverTest)*

**Filtrado (`FilterEntriesUseCase`)**
- [x] AC-07: Chip "Todo" -> todas las entradas (incluidas las de país no reconocido, África y Oceanía). *(FilterEntriesUseCaseTest; ver-todos.png: 6 viajes incl. Marte y Australia)*
- [x] AC-08: Chip "Europa" / "Asia" / "América" -> solo entradas cuyo `country` resuelve a ese continente. *(FilterEntriesUseCaseTest; chip-europa.png: Torre Eiffel + Coliseo; Asia/América comprobados por uiautomator)*
- [x] AC-09: Chip "Favoritos" -> solo entradas con `isFavourite == true`, de cualquier continente. *(chip-favoritos.png: Kyoto, Base Lunar (Marte), Coliseo)*
- [x] AC-10: Chip + búsqueda se combinan con AND (la búsqueda sigue mirando title/location/country). *(FilterEntriesUseCaseTest + HomeViewModelTest; no-results-busqueda.png: Asia + "roma" -> 0)*
- [~] AC-11: Se conserva el orden original de las entradas. *(FilterEntriesUseCaseTest)*

**Home (ViewModel + UI)**
- [x] AC-12: Al pulsar un chip, la lista "Todos los Viajes" y el contador "N viajes" reflejan el resultado filtrado. *(chip-europa.png: "2 viajes")*
- [x] AC-13: "Momentos Destacados" muestra los favoritos del resultado filtrado (máx. 3). *(chip-europa.png: destacados solo Coliseo)*
- [x] AC-14: Si hay viajes en la BD pero el filtro (chip y/o búsqueda) deja 0, se muestra "No hay viajes en <Chip>"
  (o "No hay resultados para \"<query>\"" si hay búsqueda) y un botón "Ver todos". *(no-results.png: "No hay viajes en Asia"; no-results-busqueda.png: "No hay resultados para \"roma\"")*
- [x] AC-15: "Ver todos" vuelve el chip a "Todo" y limpia la búsqueda. *(HomeViewModelTest; ver-todos.png: chip Todo, búsqueda vacía, 6 viajes)*
- [x] AC-16: El chip seleccionado sobrevive a la rotación (`SavedStateHandle`); al reabrir la app vuelve a "Todo". *(HomeViewModelTest; rotacion-europa.png y vuelta a portrait con Europa/2 viajes; reabrir-todo.png tras force-stop)*
- [x] AC-17: Build y tests OK con `scripts/verify.sh`; tests nuevos pasan. *(scripts/verify.sh: 57 tests, 0 fallos)*

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
- [x] build: `scripts/verify.sh`
- [x] unit `ContinentResolverTest`: AC-01..AC-06.
- [x] unit `FilterEntriesUseCaseTest`: AC-07..AC-11.
- [x] unit `HomeViewModelTest` (actualizado, `SavedStateHandle()` real + `backgroundScope.launch{collect}` + `advanceUntilIdle()`): AC-12..AC-16 (rotación = restaurar desde `SavedStateHandle` con valor previo).
- [x] dispositivo/emulador: `scripts/verify.sh --device`; flujo: crear viajes en Francia, Japón, México, "Marte" (1 favorito) -> pulsar cada chip -> búsqueda + chip -> estado vacío -> "Ver todos" -> rotar.
- [x] evidencia: capturas en `docs/specs/home-filter-chips/` (chip-europa, chip-favoritos, no-results, no-results-busqueda, ver-todos, rotacion-europa, reabrir-todo).
- Nota: verificado en emulador (no en el Xiaomi). Datos sembrados con sqlite3 vía `adb root`.

## Edge cases
- `country` con espacios, mayúsculas, tildes o "ñ" (España/Espana) -> se normaliza.
- `country` vacío o desconocido -> solo en "Todo".
- Entrada favorita de país desconocido -> aparece en "Favoritos" y "Todo".
- Cambiar de chip con la búsqueda activa -> se mantiene la búsqueda (AND).
- Se añade/borra un viaje con un chip activo -> la lista filtrada se actualiza sola (Flow).
