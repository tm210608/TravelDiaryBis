# Arquitectura

Ruta base: `app/src/main/java/com/example/traveldiary/`

## Capas
```
domain/
  model/TravelEntry.kt          data class (id, title, location, country, date: Instant, tag,
                                imageUrl, description, latitude?, longitude?, isFavourite)
  repository/TravelRepository   getAllEntriesStream, getFavouriteEntriesStream, getEntryStream(id),
                                insertEntry, deleteEntry, updateEntry
  usecase/                      Add, Delete, GetEntries, GetEntriesWithLocation, GetEntryById,
                                ToggleFavourite, Update  (class @Inject constructor, operator invoke)
data/
  local/                        TravelEntryEntity, TravelDao, TravelDatabase (Room v2,
                                fallbackToDestructiveMigration, exportSchema=false), Converters (Instant)
  mapper/TravelEntryMapper      toDomain / toEntity / toDomainList
  repository/TravelRepositoryImpl
di/DatabaseModule               Hilt: Database, Dao, Repository (Singleton)
ui/
  screens/                      TravelDiaryHomeScreen, AddTravelEntryScreen, CameraScreen,
                                TravelDetailScreen, MapScreen
  viewmodel/                    HomeViewModel, AddEntryViewModel, DetailViewModel, MapViewModel (+ MapUiState)
  components/                   CommonComponents (TopBar, SearchBar, FilterChipsRow, BottomNavigationBar),
                                EntryCards
  theme/                        Color.kt, Theme.kt
utils/LocationHelper.kt         ubicacion via play-services-location
model/SampleData.kt             solo `filterChips`; las listas de ejemplo ya no se usan
navigation/                     Screen.kt (sealed class con rutas string), TravelDiaryNavGraph.kt
```

## Navegación
Rutas: `home`, `add_entry`, `detail/{entryId}`, `camera`, `map`.
- Home -> Detail / AddEntry / Map (tab "Mapa" del BottomNavigationBar llama `onMapClick`).
- AddEntry -> Camera; resultado vía `savedStateHandle["capturedImageUri"]`.
- Map -> Detail al tocar el info window de un marker.
- Tabs "Recuerdos" y "Perfil" del bottom bar aun no navegan.

## Patrones
- UI state: `sealed interface` (ej. `MapUiState { Loading, Success, Empty }`).
- Flujo: `useCase().map{}.stateIn(viewModelScope, WhileSubscribed(5000), initial)`.
- Hilt: `@HiltViewModel` + `hiltViewModel()` en composables.
- API key de Maps: `local.properties` -> `manifestPlaceholders["MAPS_API_KEY"]` en `app/build.gradle.kts`.

## Estado conocido / deuda
- HomeViewModel: chips (Europa/Asia/América/Favoritos) seleccionables pero NO filtran; usa `mutableStateOf`.
- `insertSampleEntry` sin uso en HomeViewModel.
- Navegación aún con strings (no type-safe).
- Iconos `Icons.Filled.ArrowBack` deprecados (usar AutoMirrored) en AddTravelEntry/TravelDetail.
- CI: `.github/workflows/`.
