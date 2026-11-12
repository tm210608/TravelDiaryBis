# SPEC: Pulido UX (splash, icono, banners, galería, avatar, retoques)

## Qué construimos
Una pasada de pulido visual y de detalle sobre toda la app: splash de marca con animación al abrir, icono de launcher regenerado desde el diseño de Play Store, banners superior e inferior con la misma identidad visual y separados del fondo, selección de varias fotos desde la galería (una portada + resto en rejilla del detalle), avatar de perfil más grande con avatar predeterminado y edición de nombre/foto, la pestaña Rutas conserva el banner inferior, y una lista de micro-retoques (etiqueta residual, estados vacíos con acción, FAB con texto, nombre de pestaña Satélite).

## Criterios de aceptación (testeables, sin ambigüedad)

### Splash e icono
- [x] Al lanzar la app se muestra una pantalla de marca propia (fondo acorde al tema papel/mapa + logo/nombre del diario) con animación de entrada corta (fade/scale) y transición a Home en ≤1,5 s sin parpadeo.
- [x] El splash del sistema de Android sigue funcionando (windowSplashScreen con icono/fondo a juego) antes de la pantalla de marca, sin flash en negro ni en blanco.
- [x] El icono del launcher usa el diseño de `docs/play-store/icon-512.png` adaptado a adaptive icon (foreground + background), visible correcto en launcher circular y cuadrado.

### Banners y navegación inferior
- [x] En Home la cabecera ("Bitácora de Viaje / Mi Diario de Viaje" + metadatos) va dentro de una tarjeta/banda con color de superficie distinto al fondo cuadriculado, visualmente separada.
- [x] El banner inferior (BottomNavigationBar) usa el mismo lenguaje visual que el superior (misma familia de color/estilo "archivo"), coherente en las 4 pestañas.
- [x] La pestaña "Rutas" muestra el mapa (o su estado vacío) SIN ocultar el banner inferior; ya no navega a una pantalla completa aparte.
- [x] La pestaña "Satélite" se renombra a "Recuerdos" (etiqueta e icono coherentes con la pantalla que abre).

### Mapa con OpenStreetMap (sustituye a Google Maps)
- [x] `MapScreen`/`Rutas` usa `osmdroid.MapView` (en `AndroidView`) en vez de `GoogleMap`: marcadores de las entradas con ubicación, zoom/pan fluido, tap en marcador → Detail (igual que hoy).
- [x] Atribución "© OpenStreetMap contributors" visible en el mapa (la pone osmdroid; verificar que no queda tapada).
- [x] Se eliminan del build: `maps-compose`, `play-services-maps` y el `manifestPlaceholders["MAPS_API_KEY"]` (manifest y CI). Sin API key ni restricciones.
- [x] Tiles: proveedor OSM por defecto o Carto (más limpio); el mapa sigue funcionando online; sin internet muestra el fondo vacío sin crashear.

### Galería de fotos (portada + resto)
- [x] En Nuevo Recuerdo hay dos acciones: "Tomar foto" (cámara actual) y "Elegir de galería" (Photo Picker de Android, sin permiso en API 33+; en API 26-32 con READ_MEDIA_IMAGES o picker legacy).
- [x] Desde galería se pueden elegir VARIAS fotos; la primera (o la marcada) es la portada (`imageUrl`) y el resto se guardan en la entrada.
- [x] Las fotos de galería se copian a `files/photos/` de la app (no se guardan URIs efímeras del picker).
- [x] En Detalle se ve la portada grande + rejilla con el resto de fotos de la entrada.
- [x] Las entradas existentes con una sola foto siguen viéndose igual (migración sin pérdida: foto actual = portada, lista de resto vacía).

### Avatar y perfil
- [x] El avatar de Home es visiblemente más grande que ahora.
- [x] Si el usuario no tiene foto de perfil se muestra un avatar predeterminado (iniciales o icono) en vez de "AL" fijo.
- [~] Tocar el avatar abre la pantalla de edición de perfil: editar nombre y foto (galería) — verificado en dispositivo. Persistencia del nombre/foto y reflejo en Home/Pasaporte cubierta por tests unitarios (EditProfileViewModel + DataStore repo); no se ejercitó el guardado manual en dispositivo.

### Micro-retoques
- [x] El campo "Etiqueta" de Nuevo Recuerdo muestra solo el placeholder, sin "Trip" pre-rellenado.
- [x] Los estados vacíos de "Todos los Viajes", "Recuerdos" y "Rutas/Mapa" incluyen un botón/CTA "Crear tu primer viaje" que navega a AddEntry.
- [x] El FAB "+" de Home muestra texto "Nueva entrada" (extended FAB) o tooltip.

## Fuera de alcance
- Reordenar fotos de la galería ni elegir portada distinta de la primera (la primera foto elegida es la portada; se puede cambiar editando).
- Edición de perfil con más campos (bio, país, etc.).
- Sincronización/nube; todo sigue local.
- Cambios de colores/tema global de la app (solo coherencia banner superior/inferior).
- Capa satélite del mapa (OSM no la trae; si se quiere, otro spec con proveedor tipo Esri).
- Descarga offline de regiones de mapa (osmdroid la soporta; se valora aparte).

## UI states
- `AddEntryUiState` (nuevo): `Idle | PhotosSelected(count) | Saving | Error` — el área de foto muestra: vacío → iconos cámara+galería; con fotos → miniatura portada + fila de miniaturas.
- `ProfileUiState` (nuevo): `Loading | Success(name, photoUri?)` — photoUri null → avatar predeterminado.
- Splash: estado único; no interactivo; sale solo tras ~1s o cuando el primer frame de Home está listo (lo que tarde más, máx 1,5s).
- Estados vacíos existentes (Home/Recuerdos/Rutas) ganan el CTA; resto igual.

## Navegación
- Splash → `Home` (la ruta inicial pasa a ser Splash o se usa la splash del sistema + composable de marca).
- `Home` avatar → `EditProfile` (nuevo destino `@Serializable object EditProfile`).
- `EditProfile` → atrás a `Home` (o a `Profile`/Índice según origen; spec: siempre accesible también desde Índice).
- Pestaña Rutas → destino `Map` como tab dentro del scaffold con bottom bar (eliminar la navegación a pantalla completa).
- `Home/Recuerdos/Rutas` vacío → `AddEntry` vía CTA.
- `AddEntry` → galería (Photo Picker) y → `Camera` (flujo existente, ambos resultados conviven: fotos galería en `savedStateHandle["galleryImageUris"]` + `capturedImageUri`).

## Arquitectura
- domain/
  - `model/TravelEntry.kt`: añadir `photoUris: List<String>` (vacía por defecto). `imageUrl` sigue = portada.
  - `model/UserProfile.kt` (nuevo): `name: String, photoUri: String?`.
  - `repository/ProfileRepository` (nuevo interfaz): `getProfileStream(): Flow<UserProfile>`, `updateProfile(profile)`.
  - `usecase/`: `GetProfileUseCase`, `UpdateProfileUseCase` (nuevos, `@Inject`).
  - `usecase/AddTravelEntryUseCase`/`UpdateTravelEntryUseCase`: pasar `photoUris`.
- data/
  - `local/TravelEntryEntity`: nueva columna `photoUris` (JSON via TypeConverter en `Converters`). Room v3 → v4 con migración `addColumn` (no destructiva).
  - `local/profile/` o DataStore Preferences (nueva dep `androidx.datastore-preferences`) para `UserProfile` (name, photoUri). Recomendado DataStore por ser un único registro clave-valor.
  - `repository/ProfileRepositoryImpl` (nuevo).
  - `repository/TravelRepositoryImpl`: mapear `photoUris`.
- di/: provider de DataStore y de ProfileRepository en `DatabaseModule.kt` (o nuevo `ProfileModule`).
- ui/
  - `screens/SplashScreen.kt` (nuevo, composable de marca + animación).
  - `screens/EditProfileScreen.kt` (nuevo: nombre + foto galería + guardar).
  - `viewmodel/EditProfileViewModel.kt` (nuevo), `HomeViewModel` (expone profile), `AddEntryViewModel`/`EditEntryViewModel` (galería multi-pick, copia a files/photos), `DetailViewModel` (sin cambio de estado, muestra photoUris).
  - `components/CommonComponents.kt`: TopBar en tarjeta, BottomNavigationBar restyle, avatar grande + navegación, extended FAB.
  - `components/PhotoGalleryGrid.kt` (nuevo) para detalle y miniaturas en formulario.
  - `utils/ImageStorage.kt` o ampliar helper existente: copiar URI de galería → files/photos (reutilizar la lógica de copia que ya usa la cámara).
- navigation/
  - `Destinations.kt`: añadir `EditProfile`; revisar `Map` para que sea tab.
  - `TravelDiaryNavGraph.kt`: Splash como start (o start=Home + splash del sistema + overlay), ruta EditProfile.
- res/: `mipmap-anydpi-v26/ic_launcher` regenerado (foreground desde icon-512 + background color del tema), `values/themes.xml` windowSplashScreenBackground.
- mapa (osmdroid):
  - `screens/MapScreen.kt`: `AndroidView` con `org.osmdroid.views.MapView`, `Marker` por entrada con lat/lon, `onClick` → Detail. Lifecycle del MapView respetado (onResume/onPause).
  - `build.gradle.kts`/`libs.versions.toml`: añadir `org.osmdroid:osmdroid-android` (versión estable ≥7 días publicada); quitar `maps-compose`, `play-services-maps`, `MAPS_API_KEY` del manifest placeholder, `AndroidManifest` (meta-data) y CI.
  - `MyApplication`/init: `Configuration.getInstance().load()` con userAgent = packageName (lo exige la política de tiles de OSM).

## Permisos, lifecycle y persistencia
- Sin permiso nuevo en Android 13+ (Photo Picker). En API 26–32 usar `ActivityResultContracts.GetMultipleContents`/`PickMultipleVisualMedia` con fallback.
- Mapa: usa `INTERNET` (ya concedido). Respetar la política de uso del tile server de OSM (userAgent propio, sin precaching masivo).
- Las fotos elegidas se copian a almacenamiento interno antes de guardar la entrada (evita URIs revocadas).
- Migración Room no destructiva: `ALTER TABLE` añade `photoUris` (TEXT, JSON, default "[]"). Tests de migración si el proyecto ya los tiene; si no, fallback a migración simple + test de DAO.
- Rotación: el formulario conserva fotos seleccionadas (SavedStateHandle/ViewModel).
- Borrado de entrada: borrar también ficheros de `photoUris` (misma regla que imageUrl actual; si no existe, anotar como deuda).

## Plan de tests
- [x] build: `scripts/verify.sh`
- [x] unit:
  - Mapper/entity roundtrip con `photoUris`.
  - `AddEntryViewModel`: pick galería → URIs copiadas, portada = primera, resto en photoUris.
  - `EditProfileViewModel`: update name/photo → estado Success y reflejo en HomeViewModel.
  - ViewModels con `stateIn`: `backgroundScope.launch { collect {} }` + `advanceUntilIdle()`.
- [x] dispositivo: `scripts/verify.sh --device` (BUILD+TESTS OK, 175 tests) + flujo manual en móvil físico: splash→home, crear entrada con 2 fotos de galería (portada+rejilla en detalle), marcador OSM→detalle, bottom bar en Rutas, atribución OSM visible. Pendiente menor: editar perfil con foto guardada en dispositivo.
- [x] evidencia: capturas en `docs/specs/pulido-ux/` (splash, home banner, crear con galería, detalle rejilla, editar perfil, rutas-tab con mapa OSM).

## Edge cases
- Usuario cancela el picker → formulario intacto, sin estado de error.
- Elige solo 1 foto → portada, rejilla vacía (comportamiento actual).
- Foto de galería que ya no existe al copiar → descartar esa, snackbar "No se pudo añadir una foto", resto OK.
- Perfil sin nombre aún → nombre por defecto "Viajero" / iniciales.
- Entrada antigua sin photoUris → lista vacía, detalle igual que hoy.
- Rotación con picker abierto: Android lo gestiona; selección previa se conserva.
- Tab Rutas sin internet → el mapa muestra cuadrícula vacía sin crash; marcadores no dependen de red (usan lat/lon locales).
- Entrada sin lat/lon → no genera marcador; estado Empty del mapa si ninguna entrada tiene ubicación (ya existe).

## Verificación (12 nov 2026)
- `scripts/verify.sh`: BUILD+TESTS OK (175 tests, 0 fallos) tras `ktlintFormat` + fix detekt.
- Móvil físico (2412DPC0AG): splash de marca, Home (banner en tarjeta, avatar grande "V", FAB "Nueva entrada", CTA vacío), pestaña Recuerdos, Rutas con bottom bar + tiles OSM + marcador + línea de ruta, marcador→detalle, Nuevo Recuerdo con Cámara+Galería (2 fotos: 1ª portada, 2ª en rejilla del detalle), Editar perfil. Entrada "Viaje prueba OSM" capturó GPS tras el fix de permisos.
- Emulador: atribución "© OpenStreetMap contributors" visible sobre el bottom bar; `LAYER_TYPE_SOFTWARE` en emuladores por el bug conocido de tiles de osmdroid.
- Hallazgos durante verificación (corregidos): GPS nunca se adjuntaba al guardar (permiso async); entradas sin coords no podían ganar GPS al editar; faltaba CopyrightOverlay de OSM.
- Pendiente menor: guardar nombre/foto de perfil en dispositivo (cubierto por tests); icono del launcher usa el play-icon de icon-512 — valorar un diseño más propio de diario antes de Play Store.
