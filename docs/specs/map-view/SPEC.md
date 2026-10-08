# SPEC: Mapa de Viajes

> Metodología SDMD (Spec Driven Mobile Development) — la spec es la fuente de verdad, el código la sirve.
> "Done" = build + tests + app instalada + flujo ejecutado + evidencia.

---

## Qué construimos

Una pantalla de mapa accesible desde el tab "Mapa" del BottomNavigationBar
que muestra todos los viajes/entradas con coordenadas GPS como markers en un
mapa interactivo. Al tocar un marker, se muestra un info window con título y
ubicación. Al tocar el info window, se navega al detalle de la entrada.

---

## Criterios de aceptación (testeables, sin ambigüedad)

Leyenda: `[x]` verificado; `[ ]` implementado en código pero SIN verificar en dispositivo.

- [x] El tab "Mapa" del BottomNavigationBar navega a la pantalla MapScreen (verificado 2026-10-08 en Xiaomi, evidencia mapa-tiles.png)
- [x] MapScreen muestra un Google Map a pantalla completa (tiles cargando con MAPS_API_KEY)
- [x] Cada TravelEntry con latitude Y longitude no-null se muestra como un Marker (1 marker visible)
- [x] Las entradas sin coordenadas (lat/long null) NO aparecen en el mapa (unit test del use case)
- [x] Cada Marker tiene título = entry.title y snippet = entry.location (InfoWindow "Con GPS" / "UbicacionGPS")
- [x] Al tocar un Marker, se muestra un InfoWindow con título y ubicación
- [x] Al tocar el InfoWindow, se navega a DetailScreen(entryId) (evidencia detalle-desde-mapa.png)
- [x] Si no hay entradas con coordenadas, estado Empty (unit test + verificado en UI, evidencia mapa-empty-state.png)
- [ ] El mapa centra la cámara en todos los markers visibles con padding apropiado (sin verificar: solo había 1 marker)
- [x] Si hay un solo marker, la cámara se centra en él con zoom 10 (centrado en Talavera de la Reina)
- [ ] MapScreen respeta el tema (light/dark mode) de la app
- [ ] MapScreen funciona en orientación portrait y landscape (solo portrait verificado)
- [x] La app compila sin errores con `./gradlew compileDebugKotlin`
- [x] Los tests unitarios pasan con `./gradlew test`
- [x] Los tests nuevos pasan

---

## Fuera de alcance (no se construye en esta feature)

- Clustering de markers (futuro: maps-compose-utils)
- Filtros en el mapa (por país, favoritos)
- Rutas/direcciones entre markers
- Offline tiles / descarga de mapas
- Edición de coordenadas desde el mapa
- Búsqueda en el mapa
- StreetView
- Mapa en pantalla de detalle individual

---

## UI States (sealed interface)

```kotlin
sealed interface MapUiState {
    data object Loading : MapUiState
    data class Success(val entries: List<TravelEntry>) : MapUiState
    data object Empty : MapUiState
}
```

- **Loading**: mostrando CircularProgressIndicator centrado
- **Success(entries)**: mapa con markers
- **Empty**: mensaje "Aún no tienes viajes con ubicación. Añade una entrada para verla aquí."

---

## Navegación

- Home (tab Mapa) → MapScreen
- MapScreen → DetailScreen(entryId) al tocar info window
- MapScreen → back con botón de atrás o gesto predictive back

---

## Arquitectura

Sigue la Clean Architecture existente (capas domain/data/ui):

- `domain/usecase/GetEntriesWithLocationUseCase.kt` — filtra entradas con lat/long no-null
- `ui/viewmodel/MapViewModel.kt` — StateFlow<MapUiState>, usa GetEntriesWithLocationUseCase
- `ui/screens/MapScreen.kt` — Compose UI con GoogleMap composable
- `navigation/Screen.kt` — añadir `object Map : Screen("map")`
- `navigation/TravelDiaryNavGraph.kt` — añadir route composable

---

## Lifecycle

- MapViewModel sobrevive a cambios de configuración (rotación)
- El mapa se libera correctamente al salir de la pantalla

---

## Permisos

- No requiere permisos nuevos (el mapa no necesita location activa)
- INTERNET permission implícito en Google Maps SDK

---

## Persistencia

- Solo lectura: lee entradas existentes de Room vía GetEntriesUseCase/Repository
- No escribe datos

---

## Builds

- compileSdk 35, targetSdk 35, minSdk 26 (sin cambios)
- Nueva dependencia: `com.google.maps.android:maps-compose:9.0.0`
- API key gestionada via `secrets-gradle-plugin` (local.properties, no commiteada)

---

## Plan de tests

- [x] build compila: `./gradlew compileDebugKotlin`
- [x] unit tests: `MapViewModelTest` (states Loading/Success/Empty, filtrado de entradas sin coords)
- [x] `GetEntriesWithLocationUseCaseTest` (filtra nulls, devuelve vacío si todos son null)
- [x] device: instalar en dispositivo físico (Xiaomi 2412DPC0AG) y ejecutar flujo
- [x] evidencia: capturas de pantalla (docs/evidence/mapa-empty-state.png, mapa-tiles.png, mapa-marker.png, detalle-desde-mapa.png)

> **Nota:** `MAPS_API_KEY` configurada en `local.properties` y verificada en dispositivo 2026-10-08:
> tiles, marker con InfoWindow (título + ubicación) y navegación al detalle funcionando.
> Pendiente: multi-marker bounds, dark mode y landscape.

---

## Edge cases

- 0 entradas → estado Empty
- Entradas con coords pero sin título → marker con título vacío (aceptable)
- Entradas con coords idénticas → markers se solapan (aceptable, clustering en futuro)
- Rotación de pantalla → el estado se preserva
- Back desde MapScreen → vuelve a Home sin crash

---

## Dependencias a añadir

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| `com.google.maps.android:maps-compose` | 9.0.0 | GoogleMap composable |
| `com.google.android.gms:play-services-maps` | 19.0.0 | Maps SDK runtime |
| `com.google.android.libraries.mapsplatform.secrets-gradle-plugin` | 2.0.1 | API key management |

---

*Spec elaborada siguiendo SDMD — la diferencia no está en usar una IA mejor, está en darle contexto, criterios verificables y una definición clara de cuándo el trabajo está realmente terminado.*
