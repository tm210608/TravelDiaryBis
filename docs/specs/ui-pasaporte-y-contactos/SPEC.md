# SPEC: Pasaporte de Expedición + Hoja de Contactos 35mm

> SDMD — spec aprobada tras preguntas de alcance. Referencia visual:
> `docs/specs/ui-polish-concepts/stitch-02-pasaporte-y-contactos-dark.png` (Stitch).

## Qué construimos

Segunda fase del pulido visual de marca. `ProfileScreen` se convierte en un **Pasaporte
de Expedición**: cubierta con identidad del viajero y stats en mono, tira telemétrica con
**KM recorridos calculados offline (Haversine)** y sección **"Visados y Sellos de Ruta"**
con un sello entintado y rotado por cada país único visitado. `MemoriesScreen` pasa a ser
una **Hoja de Contactos 35mm**: marco de carrete oscuro con perforaciones y numeración
`01A` en mono ámbar (igual en claro y oscuro — la película física es oscura).
`MapScreen` gana un **hilo de ruta**: `Polyline` discontinua que une los markers por
orden de fecha ascendente.

## Criterios de aceptación (testeables, sin ambigüedad)

- [ ] `ProfileScreen` renderiza una cubierta de pasaporte: eyebrow mono
      "REGISTRO EXPEDICIONARIO", avatar `AL` en mono, nombre "Viajero" y línea de stats
      en mono `VIAJES NN · FAV NN · PAÍSES NN · CONT NN` (datos reales de
      `TravelStats`). Las 4 `StatCard` desaparecen.
- [ ] Tira telemétrica bajo la cubierta: eyebrow "KM RECORRIDOS" + número grande en
      mono formateado con separador de miles español (`1.234`) + etiqueta "KM"; más
      contador de tramos con GPS (`NN TRAMOS CON GPS`). Con 0 tramos muestra `0 KM ·
      0 TRAMOS`.
- [ ] `haversineKm` y `totalDistanceKm` son funciones puras en domain: suma de
      distancias entre entradas CON GPS consecutivas ordenadas por fecha ascendente;
      las entradas sin GPS no generan tramo.
- [ ] "Visados y Sellos de Ruta": un sello por **país único** (`country.trim()`
      no-vacío, case-insensitive), fecha = primera visita a ese país (`dd MMM yy`
      mono), código ISO3 por tabla + fallback 3 primeras letras en caps. Máx. 8,
      ordenados por primera visita descendente. Rotación determinística por país
      (mismo país => misma rotación entre ejecuciones), rango ±6°.
- [ ] Sellos dibujados en Compose puro: doble borde rectangular + texto mono en caps
      con alpha ~0.75 (efecto entintado), sin assets.
- [ ] Si no hay sellos, la sección muestra un placeholder "Completa tu primer visado"
      (no se oculta la cabecera).
- [ ] Exportar/Importar conservan posición al final, iconos y comportamiento intactos.
- [ ] `MemoriesScreen` Success: grid de 3 columnas sobre fondo de carrete oscuro
      (`#0E0D12` o similar) con columnas de perforaciones (agujeros de carrete) a
      izquierda y derecha dibujadas con `Canvas`; cada fotograma = foto cuadrada +
      numeración `01A`, `02A`... en mono ámbar debajo. Tap -> Detalle igual que hoy.
      Aspecto idéntico en claro y oscuro.
- [ ] `MemoriesScreen` Empty/Loading conservan su comportamiento actual.
- [ ] `MapScreen` Success dibuja `Polyline` de hilo de ruta: puntos = entradas con GPS
      ordenadas por fecha ASC, color `primary` del tema (vermillón claro / ámbar
      oscuro), trazo discontinuo (dash+gap). Con 1 solo punto no se dibuja polyline.
      Markers e InfoWindow -> Detalle sin cambios.
- [ ] `ProfileUiState.Success` pasa a llevar `stats` + `passport` (`PassportData`);
      `ProfileViewModelTest` actualizado. Sin estados nuevos ni navegación nueva.

## Fuera de alcance

- Selector de avatar/foto de perfil, nombre editable del viajero (sigue "Viajero"/"AL").
- Sellos interactivos (tap -> filtrar Home por país) ni animaciones de estampado.
- Escaneo OCR de pasaportes reales; tabla ISO limitada a países frecuentes.
- Numeración de carrete real por fecha/rollo; la numeración es posicional (`index+1`).
- Cambios en `AddTravelEntryScreen`, `CameraScreen`, `DetailScreen`, `HomeScreen`.
- Widgets/notificaciones de stats.

## UI states

- `ProfileUiState { Loading, Success(stats: TravelStats, passport: PassportData) }` —
      Success compone cubierta + tira KM + sellos + exportar/importar.
- `PassportData { kmTotal: Double, legs: Int, stamps: List<RouteStamp> }` y
      `RouteStamp { code: String, label: String, firstVisit: Instant, rotation: Float }`
      en `domain/model/`.
- `MemoriesUiState` y `MapUiState` sin cambios de modelo (mismo Success/Empty/Loading).

## Navegación

Sin rutas nuevas. Memories -> Detail (existente), Map InfoWindow -> Detail (existente),
Perfil -> export (ruta `home?export=true` existente) e import SAF picker intactos.

## Arquitectura

- `domain/model/GeoFormat.kt` — añade `haversineKm(lat1, lon1, lat2, lon2): Double`
      (R=6371km, fórmula Haversine) y `totalDistanceKm(entries): Pair<Double, Int>`
      (km y nº de tramos con GPS, ordenando por `date` asc) + `formatKm(km): String`
      (miles con `.` es).
- `domain/model/CountryCode.kt` — `countryCodeOf(country): String?`: normaliza
      (trim, sin acentos, case-insensitive), tabla es/en ~30 países, fallback 3 letras;
      null si blank.
- `domain/model/RouteStamp.kt` — `data class RouteStamp` + `buildRouteStamps(entries)`:
      agrupa por país normalizado, `firstVisit` = mínima fecha, orden desc por fecha,
      cap 8, `rotation` = `-6f + 12f * ((hash and 0x7FFF) % 5) / 4f` (determinista).
- `domain/usecase/GetPassportUseCase.kt` — `invoke(): Flow<PassportData>` desde
      `getAllEntriesStream()` (km+tramos+stamps en un map).
- `ui/viewmodel/ProfileViewModel.kt` — `combine(getTravelStatsUseCase(),
      getPassportUseCase()) { s, p -> ProfileUiState.Success(s, p) }`.
- `ui/components/PassportStamp.kt` — sello entintado: `Box` rotado, doble `border`,
      mono `code` grande + `label`/`date` pequeños, alpha ~0.75f.
- `ui/components/FilmStrip.kt` — fondo de carrete: `Modifier.filmStrip()` con
      perforaciones laterales (`drawBehind`, agujeros redondeados) + `filmAmber` token.
- `ui/screens/ProfileScreen.kt` — rediseño pasaporte (cubierta + tira KM + grid de
      sellos `LazyVerticalGrid` o `FlowRow` fijo + exportar/importar abajo).
- `ui/screens/MemoriesScreen.kt` — grid sobre `filmStrip`, celda = foto + `NNa` mono.
- `ui/screens/MapScreen.kt` — `Polyline(points=sorted, color=primary, pattern=Dash+Gap)`
      dentro del `GoogleMap` cuando `size >= 2`.

## Permisos, lifecycle y persistencia

- Sin permisos ni escritura de datos; todo deriva de flows existentes.
- Haversine 100% offline. Rotación: los `StateFlow` se recalculan, sin estado nuevo.

## Plan de tests

- [ ] build: `scripts/verify.sh`
- [ ] unit `HaversineTest`: distancia conocida Madrid->Barcelona (~505 km ±10), puntos
      idénticos -> 0, lista vacía/sin GPS -> (0.0, 0), orden por fecha respeta tramos.
- [ ] unit `CountryCodeTest`: `España`->ESP, `Spain`->ESP, `Japón`/`Japan`->JPN,
      acentos y case insensibles, desconocido->3 letras, blank->null.
- [ ] unit `RouteStampTest`: países duplicados -> 1 sello con fecha mínima, cap 8,
      blank country ignorado, rotación determinista (misma entrada -> misma rotación).
- [ ] unit `ProfileViewModelTest` actualizado: Success expone stats + passport.
- [ ] dispositivo `scripts/verify.sh --device` (Xiaomi): Perfil pasaporte en claro y
      oscuro, sellos con rotación, Recuerdos con perforaciones + `NNa` en ambos temas,
      Mapa con hilo de ruta, tap foto -> Detalle.
- [ ] evidencia: capturas en `docs/specs/ui-pasaporte-y-contactos/`.

## Edge cases

- 0 entradas o ninguna con `country` -> sellos vacíos -> placeholder "primer visado".
- `country` con espacios/typos distintos ("España " vs "espana") -> normalización los
      une; typos realmente distintos generan sellos separados (aceptado).
- País fuera de tabla ISO -> 3 letras (p. ej. "Atlantis" -> ATL).
- Una sola entrada con GPS -> polyline ausente, KM = 0, tramos = 0.
- >8 países -> se ven los 8 primeros por visita más reciente (sin "ver todos").
- Recuerdos sin foto cargable (imageUrl roto) -> celda con fondo oscuro + número.
- Rotación de pantalla en Perfil/Recuerdos -> grid recalcula, sin pérdida de estado.
