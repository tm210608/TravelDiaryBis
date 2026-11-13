# SPEC: Rediseño "Postal" de la pantalla principal

Estado: VERIFICADA (2026-11-13)
Referencia visual elegida por el usuario: `stitch-home-postal.png` (opción A, proyecto Stitch "TravelDiary — Opciones Home baja densidad").

Desviaciones aprobadas por el usuario tras la primera iteración:
- El título de cabecera es "Travel Diary" (nombre de la app) en vez de "Mi Diario".
- El avatar sin foto muestra un icono de persona en vez de la inicial.
- Bajo la cabecera hay un selector Viajes | Favoritos (acceso directo a favoritos pedido por el usuario).
- La línea de lugar incluye también la fecha, y la postal lleva marco crema tipo foto montada ("toque premium" pedido por el usuario).
- El corazón persiste `isFavourite` vía `ToggleFavouriteUseCase` (antes solo cambiaba el dibujo).

## Qué construimos

Rediseño de la pantalla principal (Home) con estilo "postal": cada entrada es una
tarjeta grande con su foto de portada ocupando casi todo el ancho, y debajo solo el
título y el lugar. Se elimina toda la capa de "datos técnicos" de la pantalla
(buscador, filtros, estadísticas, etiquetas en mayúsculas). La barra inferior se
renombra a nombres convencionales. El objetivo declarado por el usuario: menos
letra, menos información por pantalla, más calidez visual.

## Criterios de aceptación (testeables, sin ambigüedad)

- [x] La cabecera de Home muestra SOLO el título "Travel Diary" y el avatar
      circular. Sin subtítulos, contadores, escala, folio ni icono de exportar.
      (Verificado en `evidence/home-viajes.png`.)
- [x] Cada entrada se muestra como tarjeta postal: marco crema tipo foto montada,
      imagen de portada con esquinas redondeadas, y debajo título + línea
      "Ciudad, País · fecha". (Verificado en `evidence/home-viajes.png`.)
- [x] Si la entrada no tiene foto, la zona de imagen muestra un degradado cálido
      con icono de cámara. (Verificado: "Camping en el Pirineo", `evidence/home-viajes.png`.)
- [x] El corazón de favorito permanece como icono sobre la foto (esquina superior
      derecha), relleno en favoritas, y persiste `isFavourite` al tocarlo.
- [x] En Home ya NO aparecen: barra de búsqueda, chips de filtro por continente,
      carrusel "Registro Destacado", fila de estadísticas, sección "Visados y
      Sellos de Ruta" ni contadores "[00]".
- [x] El estado vacío muestra mensaje breve + botón de nueva entrada.
      (Verificado en `evidence/home-vacio.png`.)
- [x] La barra inferior muestra Inicio, Mapa, Fotos, Perfil en todas las
      pantallas que la usan; los destinos no cambian. (Verificado en Home y Perfil.)
- [x] El FAB muestra "+" con el texto "Nueva entrada" (se mantuvo la versión
      con texto, igual que antes).
- [x] El acceso a exportar/importar sigue existiendo desde Perfil
      ("Exportar diario / ZIP con entradas y fotos", verificado en dispositivo).
- [x] El modo oscuro funciona: paleta "Atlas Nocturno" con las mismas postales.
      (Verificado en captura oscura del emulador.)
- [x] Título largo no rompe la tarjeta: 1 línea con ellipsis en el título y en la
      línea de lugar.
- [x] En landscape la rejilla adaptativa muestra 2 columnas sin cortes.
      (Verificado en captura landscape del emulador.)
- [x] Tocar una postal navega al detalle (verificado: detalle de "Kioto" con
      galería de 2 fotos); "+" navega a nueva entrada; las pestañas navegan
      como antes. Rutas sin cambios.
- [x] Extra (post-SPEC): selector Viajes|Favoritos filtra correctamente en
      dispositivo (verificado: 4 favoritas en `evidence/home-favoritos.png`).

## Fuera de alcance

- Rediseño de las pantallas Detalle, Nueva entrada, Editar, Recuerdos, Mapa,
  Perfil, Splash y Cámara (mantienen su diseño actual salvo la barra inferior
  compartida, que sí cambia de nombres).
- Cambios en modelo de datos, Room, use cases, exportación/importación, cámara,
  GPS ni mapa.
- Fuentes tipográficas empaquetadas nuevas: se usará la familia serif del sistema
  (`FontFamily.Serif`) o la ya presente en el proyecto; NO se añaden archivos de
  fuente ni Google Fonts en esta iteración.
- Animaciones elaboradas de entrada o transiciones nuevas.

## UI states

Se mantiene el `sealed interface` actual de `HomeViewModel` (Loading / Success /
Empty o equivalente). En Loading se puede mostrar el contenido vacío o un
placeholder simple sin spinners adicionales; en Empty, el estado vacío descrito;
en Success, la lista de postales.

## Navegación

Sin cambios: `Home -> Detail(entryId)` al tocar postal, `Home -> AddEntry` desde
el FAB, tabs del bottom bar igual que ahora (Mapa, Fotos/Recuerdos, Perfil).

## Arquitectura

Solo capa `ui/` (y tema). Sin cambios en `domain/` ni `data/`.

- `ui/screens/TravelDiaryHomeScreen.kt`: reescritura del layout a diseño postal.
- `ui/components/EntryCards.kt`: nuevo composable `PostalEntryCard` (o equivalente)
  con foto/degradado+icono, corazón de favorito, título y lugar.
- `ui/components/CommonComponents.kt`: `BottomNavigationBar` con las nuevas
  etiquetas (Inicio, Mapa, Fotos, Perfil) y mismo esquema de iconos.
- `ui/theme/Color.kt` + `Theme.kt`: tokens "Postal" claros (fondo crema ~#F7F2EA,
  tinta cálida oscura, acento terracota ~#C26749) y equivalentes oscuros; los
  componentes nuevos consumen estos tokens, sin romper los existentes.
- `ui/theme/Type.kt`: escala tipográfica de Home (título serif para "Mi Diario" y
  títulos de postal; cuerpo sans para el resto).
- `ui/viewmodel/HomeViewModel.kt`: eliminar estado de búsqueda y chips si queda
  sin uso; ajustar `HomeViewModelTest` en consecuencia. No añadir lógica nueva.

## Permisos, lifecycle y persistencia

Sin cambios: la pantalla es de solo lectura, no introduce permisos ni escritura.
Debe sobrevivir rotación y cambio de tema claro/oscuro sin perder estado (misma
lógica de ViewModel actual).

## Plan de tests

- [x] build: `scripts/verify.sh` en verde (167 tests JVM, 0 fallos).
- [x] unit: `HomeViewModelTest` adaptado (fuera tests de búsqueda/chips/featured;
      nuevos: filtro favoritos, toggleFavourite); `EditProfileViewModelTest` con
      test de borrador restaurado tras recreación.
- [x] dispositivo: verificado en emulador `emulator-5560` con 8 entradas mock con
      fotos reales (el móvil del usuario estaba ocupado con otro proyecto).
      Home ya visto previamente en el móvil del usuario tras `installDebug`.
- [x] evidencia: capturas en `docs/specs/rediseno-postal/evidence/`
      (home-viajes, home-favoritos, home-vacio, home-scroll, verify-device).

## Edge cases

- Entrada sin foto -> degradado + icono (ver criterio).
- Entrada sin `location`/`country` -> la línea de lugar se omite, no muestra
  texto vacío ni comas sueltas.
- Entrada con varias fotos -> la postal usa solo la portada (`imageUrl`).
- Perfil sin foto -> avatar con inicial (comportamiento actual).
- Muchas entradas -> scroll vertical normal (LazyColumn), sin paginación nueva.
- Modo oscuro y landscape cubiertos en criterios.
