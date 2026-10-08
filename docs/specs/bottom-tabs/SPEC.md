# SPEC: Tabs Recuerdos y Perfil

> Metodología SDMD (Spec Driven Mobile Development) — la spec es la fuente de verdad, el código la sirve.
> "Done" = build + tests + app instalada + flujo ejecutado + evidencia.

---

## Qué construimos

Las tabs "Recuerdos" y "Perfil" del BottomNavigationBar dejan de ser decorativas:
- **Recuerdos**: cuadrícula de fotos de todas las entradas; tocar una foto abre el detalle del viaje.
- **Perfil**: avatar + estadísticas del diario en solo lectura (viajes totales, favoritos, países, continentes).

Además se corrige el comportamiento actual en Home: tocar Recuerdos/Perfil iluminaba
la tab sin cambiar de pantalla; ahora navega de verdad.

---

## Criterios de aceptación (testeables, sin ambigüedad)

Leyenda: `[x]` verificado; `[ ]` implementado en código pero SIN verificar en dispositivo.

- [ ] Tab "Recuerdos" navega a MemoriesScreen con su tab marcada (índice 2)
- [ ] MemoriesScreen muestra una cuadrícula con una celda por cada entrada con `imageUrl` no vacío (foto + título)
- [ ] Tocar una foto en Recuerdos navega a `detail/{entryId}` de esa entrada
- [ ] Si no hay entradas con foto, MemoriesScreen muestra estado vacío con mensaje
- [ ] Tab "Perfil" navega a ProfileScreen con su tab marcada (índice 3)
- [ ] ProfileScreen muestra avatar (mismo estilo "AL" del TopBar) y 4 estadísticas: viajes totales, favoritos, países visitados (distinct `country` no vacío), continentes visitados (distinct `continentOf(country)` no nulo)
- [ ] Las estadísticas se actualizan al volver a Perfil tras añadir una entrada (son Flow, no snapshot)
- [ ] El BottomNavigationBar funciona igual en las 3 pantallas que lo muestran (Home, Recuerdos, Perfil): las 4 tabs navegan; "Inicio" vuelve a Home sin duplicar entradas en el back stack
- [ ] Tab "Mapa" sigue navegando a MapScreen (sin regresión)
- [ ] Back desde Recuerdos/Perfil vuelve a Home con tab "Inicio" marcada
- [ ] La app compila con `./gradlew compileDebugKotlin` y los tests pasan con `./gradlew testDebugUnitTest`

---

## Fuera de alcance (no se construye en esta feature)

- Editar nombre/foto de perfil, ajustes, cierre de sesión (futura spec "perfil editable")
- Zoom de foto a pantalla completa desde la cuadrícula
- BottomNavigationBar en MapScreen (MapScreen sigue sin bar, con back)
- Navegación type-safe (refactor aparte)
- Insignias/badges en las tabs

---

## UI states (sealed interface)

```kotlin
sealed interface MemoriesUiState {
    data object Loading : MemoriesUiState
    data class Success(val entries: List<TravelEntry>) : MemoriesUiState
    data object Empty : MemoriesUiState
}

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val stats: TravelStats) : ProfileUiState
}

data class TravelStats(
    val totalTrips: Int,
    val favourites: Int,
    val countries: Int,
    val continents: Int,
)
```

- **Memories Loading**: CircularProgressIndicator; **Success**: LazyVerticalGrid 3 columnas
  (AsyncImage + título superpuesto); **Empty**: "Aún no tienes recuerdos. Añade una entrada con foto."
- **Profile Loading**: indicador; **Success**: avatar + tarjetas de estadísticas.

---

## Navegación

- `Screen.kt`: añadir `object Memories : Screen("memories")` y `object Profile : Screen("profile")`
- Home (tab Recuerdos) → Memories; Home (tab Perfil) → Profile
- Memories (foto) → `detail/{entryId}`; Memories (tab Inicio/Mapa/Perfil) → home/map/profile
- Profile (tab Inicio/Mapa/Recuerdos) → home/map/memories
- Navegación entre tabs con `launchSingleTop = true` + `popUpTo(Screen.Home.route)` para no apilar duplicados
- Back desde Memories/Profile → Home (popBackStack al start destination)

---

## Arquitectura

- `domain/usecase/GetMemoriesUseCase.kt` (NUEVO) — Flow de entradas con `imageUrl.isNotBlank()`
- `domain/usecase/GetTravelStatsUseCase.kt` (NUEVO) — Flow<TravelStats> calculado
  desde `getAllEntriesStream` (usa `continentOf` para continentes)
- `domain/model/TravelStats.kt` — data class (o dentro del use case, como MapUiState)
- `ui/viewmodel/MemoriesViewModel.kt` (NUEVO) — StateFlow<MemoriesUiState>
- `ui/viewmodel/ProfileViewModel.kt` (NUEVO) — StateFlow<ProfileUiState>
- `ui/screens/MemoriesScreen.kt` (NUEVO) — grid + bottom bar (selectedTab = 2)
- `ui/screens/ProfileScreen.kt` (NUEVO) — stats + bottom bar (selectedTab = 3)
- `ui/screens/TravelDiaryHomeScreen.kt` — `onTabSelected`: índices 1/2/3 navegan
  (callbacks `onMapClick`, `onMemoriesClick`, `onProfileClick`); quitar el
  `else viewModel.onTabSelected(index)` para tabs que navegan (solo índice 0 queda local).
  `selectedTab` del VM queda siempre en 0 → al volver, "Inicio" aparece marcado.
- `navigation/TravelDiaryNavGraph.kt` — routes `memories` y `profile`
- `data/` — sin cambios (solo lectura de Room)

---

## Permisos, lifecycle y persistencia

- Sin permisos nuevos. Solo lectura de Room.
- ViewModels con `stateIn(WhileSubscribed)`: las stats y fotos se recalculan al volver.
- Rotación: estado preservado por ViewModel.

---

## Plan de tests

- [ ] build: `scripts/verify.sh`
- [ ] unit: `GetMemoriesUseCaseTest` — filtra imageUrl vacío, devuelve vacío si ninguna
- [ ] unit: `GetTravelStatsUseCaseTest` — cuenta total/favoritos/países/continentes
      (país desconocido no suma continente; duplicados de país cuentan 1)
- [ ] unit: `MemoriesViewModelTest` — Loading/Empty/Success (patrón
      `backgroundScope.launch` + `advanceUntilIdle()`)
- [ ] unit: `ProfileViewModelTest` — Loading/Success con stats correctas
- [ ] dispositivo: `scripts/verify.sh --device` + flujo: Home→Recuerdos→foto→detalle→back,
      Home→Perfil (stats), tabs cruzadas, back
- [ ] evidencia: capturas en `docs/specs/bottom-tabs/` (memories-grid, memories-empty,
      profile-stats, tab-navigation)

---

## Edge cases

- Entrada con `imageUrl` en blanco → no aparece en Recuerdos (sí cuenta en stats de viajes)
- 0 entradas → Recuerdos Empty, Perfil muestra todo a 0
- País duplicado en varias entradas → cuenta 1 en "países visitados"
- País no mapeado a continente → suma país pero no continente
- Navegación rápida entre tabs → no se apilan pantallas (singleTop)
- Rotación en Recuerdos/Perfil → estado preservado

---

*Spec elaborada siguiendo SDMD — la diferencia no está en usar una IA mejor, está en darle contexto, criterios verificables y una definición clara de cuándo el trabajo está realmente terminado.*
