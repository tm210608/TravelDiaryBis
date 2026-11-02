# SPEC: Pulido editorial — telemetría de registro + meteo real en destacada

> SDMD — spec aprobada tras preguntas de alcance. Referencias visuales:
> `docs/specs/ui-polish-concepts/stitch-01-atlas-campo-home.png`,
> `docs/specs/ui-polish-concepts/stitch-02-pasaporte-y-contactos-dark.png` (Stitch).

## Qué construimos

Tercera y última fase del pulido visual de marca: se aplica la **ornamentación editorial
restante** de los conceptos Stitch usando solo datos existentes. Home gana telemetría de
cabecera (`ARCHIVO CARTOGRÁFICO · NN EXPEDICIONES`), chips con contador real, eyebrow
`TAG · PAÍS` en las filas, descripción en la tarjeta destacada, insignia `GPS LOCK` y un
pie de archivo tipográfico. La tarjeta destacada muestra **meteo real de la fecha/lugar**
vía Open-Meteo (API gratuita, sin key) con degradación silenciosa offline. Perfil suma
`DÍAS EN RUTA` a la tira telemétrica y contador de visados. El bottom nav adopta la voz
metafórica `Cuaderno / Rutas / Satélite / Índice`.

## Criterios de aceptación (testeables, sin ambigüedad)

- [x] `TopBar` muestra bajo el eyebrow una línea de telemetría mono:
      `ARCHIVO CARTOGRÁFICO · NN EXPEDICIONES ACTIVAS` con NN = nº real de entradas
      (`entriesList.size`, formateado a 2 dígitos). Línea estática adicional a la derecha
      o debajo: `ESCALA 1:24.000 · FOLIO 001` (copy decorativo).
- [x] `SearchBar` cambia el placeholder a `Buscar expedición, coordenadas o recuerdo…`.
      No hay hint `⌘K` (sin sentido sin teclado hardware).
- [x] `FilterChipsRow` muestra el contador real por chip: `TODOS (NN)`, `FAVORITOS (NN)`
      y por continente el nº de entradas que cumplen ese filtro. El contador respeta el
      estado actual de búsqueda (mismo criterio que el filtrado de la lista).
- [x] `EntryListCard` añade eyebrow mono sobre el título: `TAG · PAÍS` en mayúsculas
      (p. ej. `URBANO · ESPAÑA`); si `country` está en blanco, solo `TAG`.
- [ ] `FeaturedCard` muestra bajo el título la primera línea de `entry.description`
      (mono o italic, `maxLines = 1`, ellipsis) solo si no está en blanco. — implementado,
      NO observado en dispositivo (las entradas del diario de prueba no tienen descripción).
- [x] `FeaturedCard` muestra insignia `GPS LOCK` (pill oscura, mono) solo cuando
      `entry.hasGps`; sin GPS no se renderiza.
- [x] `FeaturedCard` con GPS y conectividad muestra en la línea de telemetría el prefijo
      `NT°C · NH% ·` (prefijo, no sufijo, para que no se ellipsice: verificado online con
      21°C·40% y 19°C·88% reales en evidencia-02/05/06, y offline en evidencia-08 donde
      el meteo desaparece sin error) (temperatura y humedad reales de Open-Meteo para lat/lon/fecha de
      la entrada). Sin conectividad, sin GPS o ante error/timeout la línea queda como
      hoy, sin indicador de error ni reintentos visibles.
- [x] Cliente Open-Meteo en `data/remote`: elige `api.open-meteo.com/v1/forecast`
      (`past_days` cubre ≤92 días) o `archive-api.open-meteo.com/v1/archive` (>92 días)
      según la fecha de la entrada; hora = la más cercana a `entry.date`. Timeout ≤5 s.
      Cache en memoria por (lat/lon redondeados a 2 decimales, fecha) para no repetir
      llamadas en rotación ni recomposition.
- [x] `AndroidManifest.xml` declara `<uses-permission android:name="android.permission.INTERNET" />`
      explícito (hoy llega por manifest merger de Maps).
- [x] Al final de `Todos los Viajes` se renderiza un pie de archivo: 2 líneas en mono
      caps pequeñas, centradas, color `onSurfaceVariant`, p. ej.
      `REGISTRO DE EXPEDICIONES — TODOS LOS DATOS PERMANECEN EN ESTE DISPOSITIVO` /
      `FOLIO NN · GENERADO OFFLINE`. NN = nº de entradas.
- [x] `BottomNavigationBar` renombra las etiquetas a `Cuaderno`, `Rutas`, `Satélite`,
      `Índice` (mismos iconos, mismo orden, mismos callbacks; el `contentDescription`
      del icono usa la nueva etiqueta).
- [x] Perfil: la tira telemétrica añade `NN DÍAS EN RUTA` (nº de días naturales
      distintos con al menos una entrada; función pura en domain) y bajo la grid de
      sellos un contador `NN VISADOS REGISTRADOS` (nº real de sellos generados, no el
      cap de 8 ni un total inventado).
- [ ] Sin regresión: modo selección de exportación, navegación, temas claro/oscuro.
      — navegación y temas OK en dispositivo; modo selección NO re-probado en esta sesión.

## Fuera de alcance

- Meteo en `TravelDetailScreen`, `EntryListCard` o MapScreen (solo tarjeta destacada).
- Iconos de meteo, pronóstico futuro, avisos de "sin conexión" (la degradación es muda).
- Códigos de expedición inventados por entrada (`EXP-ARC-8 // KYOTO`): omitidos por no
  derivarse de datos reales.
- Rediseño de `AddTravelEntryScreen`, `CameraScreen`, `MapScreen`, `TravelDetailScreen`.
- Cambios en filtros, búsqueda, exportación/importación o modelo Room.
- Persistencia del meteo en Room (cache solo en memoria, se recalcula por sesión).

## UI states

Sin estados nuevos. El meteo viaja como dato opcional aditivo:

- `HomeViewModel` expone `featuredWeather: StateFlow<Map<Long, WeatherReading>>`
  (entryId -> lectura), poblado en segundo plano solo para las entradas destacadas con
  GPS; los fallos se tragan (mapa vacío).
- `WeatherReading { tempC: Double, humidityPct: Int? }` en `domain/model/`.
- `chipCounts: Map<String, Int>` derivado en el ViewModel (o conteo en composable a
  partir del mismo predicado de filtro — decidir en implementación lo que ya use el VM
  para filtrar).
- `PassportData`/`TravelStats` gana `daysOnRoute: Int` (días naturales distintos con
  entrada).

## Navegación

Sin rutas nuevas ni cambios de callbacks. Solo renombre de etiquetas del bottom nav.

## Arquitectura

- `AndroidManifest.xml` — `+ INTERNET` explícito.
- `domain/model/Weather.kt` — `WeatherReading` + `formatWeatherTelemetry(): String`
  (`"· NT°C · NH%"`, `N` redondeado sin decimales).
- `domain/model/DaysOnRoute.kt` (o en `GeoFormat.kt`/stats existentes) —
  `daysWithEntries(entries): Int` puro (distintos `LocalDate` de `entry.date`, zona del
  dispositivo).
- `domain/repository/WeatherRepository.kt` — `suspend fun weatherAt(lat, lon, instant): WeatherReading?`.
- `data/remote/OpenMeteoClient.kt` — URL builder puro `openMeteoUrl(lat, lon, instant): String`
  (elige host forecast/archive por antigüedad) + parser puro
  `parseHourly(json: String, targetEpochHour: Long): WeatherReading?` con
  `kotlinx.serialization` (dep ya presente) u `JSONObject`.
- `data/repository/WeatherRepositoryImpl.kt` — `HttpURLConnection` en `Dispatchers.IO`,
  timeout 5 s, `ConcurrentHashMap` de cache en memoria, cualquier excepción -> `null`.
- `di/` — binding/provide del `WeatherRepository` (Singleton).
- `ui/viewmodel/HomeViewModel.kt` — `featuredWeather` (mapa por id, fetch lazy sobre las
  destacadas con GPS, `catch {}` silencioso) + `chipCounts` + `entriesCount` para la
  cabecera.
- `ui/components/CommonComponents.kt` — `TopBar` (telemetría + contador), `SearchBar`
  (placeholder), `FilterChipsRow` (contadores), `BottomNavigationBar` (etiquetas),
  `ArchiveFooter` nuevo.
- `ui/components/EntryCards.kt` — eyebrow `TAG · PAÍS` en `EntryListCard`; descripción +
  `GPS LOCK` + sufijo meteo en `FeaturedCard`.
- `ui/screens/TravelDiaryHomeScreen.kt` — cablea `featuredWeather`/`chipCounts`, añade
  `ArchiveFooter` como último `item` del `LazyColumn`.
- `ui/screens/ProfileScreen.kt` — `NN DÍAS EN RUTA` en la tira + contador de visados.

## Permisos, lifecycle y persistencia

- `INTERNET` explícito (ya efectivo vía Maps). Sin permisos de runtime nuevos.
- Sin escritura en Room; cache de meteo solo en memoria (se pierde al morir el proceso:
  aceptado).
- Rotación: `StateFlow`s recalculan; la cache sobrevive mientras viva el proceso.
- Llamadas de red solo desde `viewModelScope` en `Dispatchers.IO`; nunca en composición.

## Plan de tests

- [x] build: `scripts/verify.sh` — 156 tests, 0 fallos
- [x] unit `OpenMeteoTest` (JVM): URL elige forecast ≤92 días y archive >92 días; parser
      extrae `temperature_2m`/`relative_humidity_2m` de la hora más cercana; JSON
      malformado/horas vacías -> `null`.
- [x] unit `DaysOnRouteTest` (JVM): 2 entradas el mismo día -> 1; días distintos -> N;
      lista vacía -> 0.
- [x] unit `HomeViewModelTest` actualizado: `chipCounts` por chip correctos con búsqueda
      activa; `featuredWeather` vacío ante repo que lanza excepción (fallo silencioso);
      entradas sin GPS no disparan fetch.
- [x] unit `WeatherFormatTest` (WeatherTest): redondeo y formato `· NT°C · NH%`.
- [x] dispositivo `scripts/verify.sh --device` (Xiaomi 2412DPC0AG): telemetría de
      cabecera, chips con `(NN)`, eyebrow en filas, `GPS LOCK`, meteo real con WiFi ON
      (evidencia-02/05/06), WiFi+datos OFF -> meteo ausente sin error (evidencia-08,
      captura tomada on-device durante la ventana sin radio), pie de archivo, nav
      metafórico, Perfil con `DÍAS EN RUTA` y `VISADOS REGISTRADOS`; claro y oscuro.
      Descripción en destacada NO observada (entradas sin descripción).
- [x] evidencia: capturas en `docs/specs/ui-polish-concepts/` (evidence_01..08).

## Edge cases

- Sin conexión / timeout / HTTP error / JSON inesperado -> meteo ausente, sin crash ni
  snackbar.
- Entrada destacada sin GPS -> sin `GPS LOCK` y sin llamada a Open-Meteo.
- `description` en blanco -> línea no renderizada (sin hueco).
- `country` en blanco -> eyebrow solo `TAG`.
- Humedad ausente en la respuesta -> solo `NT°C`; ambos ausentes -> sin sufijo.
- Entrada con fecha futura -> endpoint forecast; si la API no devuelve la hora, `null`.
- 0 entradas -> cabecera `00 EXPEDICIONES`, pie con `FOLIO 00`, chips `(00)`.
- Búsqueda activa + chip -> contadores reflejan el mismo filtrado que la lista.
- Rotación durante fetch de meteo -> cache evita duplicar llamadas al recomponer.
