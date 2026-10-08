# SPEC: Filter Chips funcionales

> Metodología SDMD (Spec Driven Mobile Development) — la spec es la fuente de verdad, el código la sirve.
> "Done" = build + tests + app instalada + flujo ejecutado + evidencia.

---

## Qué construimos

Los filter chips de la Home ("Todo", "Europa", "Asia", "América", "Favoritos")
pasan de ser puramente visuales a filtrar de verdad la lista "Todos los Viajes".
El continente de cada entrada se deriva de su campo `country` mediante una tabla
interna país→continente (offline, sin red ni cambios de base de datos).
El filtro de chip se combina (AND) con la búsqueda de texto existente.

---

## Criterios de aceptación (testeables, sin ambigüedad)

- [ ] Chip "Todo" (default al entrar) muestra todas las entradas
- [ ] Chip "Europa" muestra solo entradas cuyo `country` mapea a Europa (p.ej. France, España, Greece)
- [ ] Chip "Asia" muestra solo entradas cuyo `country` mapea a Asia (p.ej. Japan, China)
- [ ] Chip "América" muestra solo entradas cuyo `country` mapea a América (p.ej. Brazil, Mexico, USA)
- [ ] Chip "Favoritos" muestra solo entradas con `isFavourite = true`
- [ ] Entrada con `country` vacío, nulo o no reconocido solo aparece con chip "Todo" (y "Favoritos" si aplica)
- [ ] Selección de un solo chip a la vez; tocar otro chip deselecciona el anterior
- [ ] Chip + búsqueda se combinan: con "Asia" activo y query "Paris" la lista queda vacía
- [ ] "Momentos Destacados" NO se ve afectado por el chip (siempre favoritos, take(3))
- [ ] El contador "N viajes" del SectionHeader refleja el tamaño de la lista filtrada
- [ ] La selección de chip sobrevive a rotación (ViewModel, no rememberSaveable necesario)
- [ ] La app compila con `./gradlew compileDebugKotlin` y los tests pasan con `./gradlew testDebugUnitTest`

---

## Fuera de alcance (no se construye en esta feature)

- Chips "África" / "Oceanía" (las entradas de esos continentes solo salen en "Todo")
- Multi-select de chips
- Campo `continent` en la base de datos ni desplegable en el formulario
- Persistencia del chip seleccionado entre sesiones (al reabrir vuelve a "Todo")
- Filtrar "Momentos Destacados" por chip
- Normalización con transliteración avanzada (se acepta matching básico case/acentos-insensitive)

---

## UI states

No se introduce un sealed interface nuevo: la lista filtrada sigue siendo
`StateFlow<List<TravelEntry>>` derivado por `combine`. Estado vacío = lista
vacía; la LazyColumn simplemente no renderiza items (comportamiento actual).

```kotlin
// HomeViewModel
private val _selectedChip = MutableStateFlow("Todo")
val selectedChip: StateFlow<String>

val entriesList: StateFlow<List<TravelEntry>> = combine(
    _allEntries, _searchQuery, _selectedChip
) { entries, query, chip -> /* filtro chip AND filtro query */ }
```

Nota: `selectedChip` migra de `mutableStateOf` a `MutableStateFlow` (el screen
pasa a `collectAsState()`). `FilterChipsRow` no cambia de firma.

---

## Navegación

Sin cambios. Todo ocurre dentro de Home.

---

## Arquitectura

- `domain/model/Continent.kt` (NUEVO) — `enum class Continent { EUROPE, ASIA, AMERICA, AFRICA, OCEANIA }`
  + `fun continentOf(country: String): Continent?` con tabla interna país→continente,
  matching case-insensitive e insensitive a acentos, nombres en español e inglés
  (p.ej. "España"/"Spain", "Japón"/"Japan", "Alemania"/"Germany"). Cobertura:
  países comunes (~60+); lo no mapeado devuelve null.
- `domain/usecase/FilterEntriesByChipUseCase.kt` (NUEVO) — dada una lista y un
  chip label, devuelve la lista filtrada. Lógica pura, testeable sin coroutines.
  ("Todo" → todas; "Favoritos" → isFavourite; resto → continentOf(country) == chip)
- `ui/viewmodel/HomeViewModel.kt` — `selectedChip` a StateFlow; `entriesList`
  combina `_allEntries + _searchQuery + _selectedChip` aplicando primero chip y
  después query. Eliminar `insertSampleEntry`/`AddEntryUseCase` si quedan sin uso.
- `ui/screens/TravelDiaryHomeScreen.kt` — `selectedChip` pasa a `collectAsState()`;
  el contador del header ya usa `entriesList.size` (sin cambio de código).
- `model/SampleData.kt` — `filterChips` se mantiene igual.
- `data/` y `navigation/` — sin cambios.

---

## Permisos, lifecycle y persistencia

- Sin permisos nuevos. Solo lectura de Room (sin escritura).
- La tabla país→continente es una constante en código (offline-first).
- Selección de chip vive en el ViewModel: sobrevive a rotación, no a proceso muerto.

---

## Plan de tests

- [ ] build: `scripts/verify.sh`
- [ ] unit: `FilterEntriesByChipUseCaseTest` — cada chip filtra bien; país
      desconocido/vacío solo en "Todo"; "Favoritos" por isFavourite;
      case-insensitive ("france", "FRANCE", "Francia")
- [ ] unit: `ContinentTest` (o dentro del anterior) — continentOf mapea países
      conocidos y devuelve null en desconocidos
- [ ] unit: actualizar `HomeViewModelTest` — `selectedChip` ya es StateFlow;
      test de combinación chip+query con `backgroundScope.launch` +
      `advanceUntilIdle()` (patrón WhileSubscribed documentado en AGENTS.md)
- [ ] dispositivo: `scripts/verify.sh --device` + flujo manual: crear entradas
      con países de distintos continentes, tocar cada chip, combinar con búsqueda
- [ ] evidencia: capturas en `docs/specs/filter-chips/` (chip Europa filtrando,
      chip Favoritos, chip+búsqueda)

---

## Edge cases

- `country` vacío o con espacios → null → solo "Todo"
- `country` con typo ("Frncia") → null → solo "Todo" (limitación aceptada)
- País de África/Oceanía (p.ej. "Egypt", "Australia") → continentOf lo mapea,
  pero no hay chip para él → solo "Todo"
- "Favoritos" + búsqueda → favoritos que además matchean el query
- Lista sin entradas → LazyColumn vacía en cualquier chip (sin crash)
- Rotación → chip seleccionado se conserva
- 0 resultados tras filtrar → contador "0 viajes", lista vacía

---

*Spec elaborada siguiendo SDMD — la diferencia no está en usar una IA mejor, está en darle contexto, criterios verificables y una definición clara de cuándo el trabajo está realmente terminado.*
