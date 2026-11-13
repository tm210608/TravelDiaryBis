# SPEC: Rediseño "Postal" de la pantalla principal

Estado: APROBADA (2026-11-13)
Referencia visual elegida por el usuario: `stitch-home-postal.png` (opción A, proyecto Stitch "TravelDiary — Opciones Home baja densidad").

## Qué construimos

Rediseño de la pantalla principal (Home) con estilo "postal": cada entrada es una
tarjeta grande con su foto de portada ocupando casi todo el ancho, y debajo solo el
título y el lugar. Se elimina toda la capa de "datos técnicos" de la pantalla
(buscador, filtros, estadísticas, etiquetas en mayúsculas). La barra inferior se
renombra a nombres convencionales. El objetivo declarado por el usuario: menos
letra, menos información por pantalla, más calidez visual.

## Criterios de aceptación (testeables, sin ambigüedad)

- [ ] La cabecera de Home muestra SOLO el título "Mi Diario" y el avatar circular
      (foto de perfil o inicial, como ahora). No muestra subtítulos, contadores,
      escala, folio ni el icono de exportar.
- [ ] Cada entrada se muestra como tarjeta postal: imagen de portada
      (`TravelEntry.imageUrl`) a todo lo ancho disponible con esquinas redondeadas,
      y debajo únicamente el título de la entrada y la línea de lugar
      (`location`/`country` con el formato que hoy muestra la lista).
- [ ] Si la entrada no tiene foto, la zona de imagen muestra un degradado cálido
      suave con un icono discreto (cámara o mapa), no un espacio vacío ni texto.
- [ ] El corazón de favorito permanece accesible como icono pequeño sobre la foto
      (esquina superior derecha) y sigue alternando `isFavourite` al tocarlo.
- [ ] En Home ya NO aparecen: barra de búsqueda, chips de filtro por continente,
      carrusel "Registro Destacado", fila de estadísticas (km/días/tramos),
      sección "Visados y Sellos de Ruta" ni contadores "[00]".
- [ ] El estado vacío (0 entradas) muestra un mensaje breve en español invitando a
      crear el primer viaje más el botón de nueva entrada; sin bloques extra.
- [ ] La barra inferior se renombra en TODAS las pantallas que la usan:
      Inicio, Mapa, Fotos, Perfil (antes Cuaderno, Rutas, Recuerdos, Índice).
      Los destinos de cada pestaña no cambian.
- [ ] El FAB muestra el icono "+" con el texto "Nueva entrada" (como hoy) o solo
      "+" si el diseño final lo prefiere — decidir en implementación y documentar.
- [ ] El acceso a exportar/importar sigue existiendo desde la pestaña Perfil
      (pantalla `ProfileScreen`), aunque desaparezca de la cabecera de Home.
- [ ] El modo oscuro sigue funcionando: Home en oscuro usa la paleta oscura
      equivalente (fondo oscuro cálido, mismas tarjetas) sin añadir texto.
- [ ] Título largo de entrada no rompe la tarjeta: máximo 2 líneas con ellipsis.
- [ ] En landscape la lista de postales pasa a 2 columnas o se limita el ancho de
      tarjeta; no hay cortes ni solapamientos.
- [ ] Tocar una postal navega al detalle; "+" navega a nueva entrada; las pestañas
      navegan como antes. Navegación sin cambios de rutas.

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

- [ ] build: `scripts/verify.sh` en verde (incluye tests JVM).
- [ ] unit: `HomeViewModelTest` adaptado (quitar tests de búsqueda/chips si el
      estado se elimina); los tests de favoritos/listado siguen pasando.
- [ ] dispositivo: `installDebug` en el móvil del usuario — revisión visual del
      Home con entradas con foto, sin foto y estado de favorito.
- [ ] evidencia: capturas del resultado en `docs/specs/rediseno-postal/`.

## Edge cases

- Entrada sin foto -> degradado + icono (ver criterio).
- Entrada sin `location`/`country` -> la línea de lugar se omite, no muestra
  texto vacío ni comas sueltas.
- Entrada con varias fotos -> la postal usa solo la portada (`imageUrl`).
- Perfil sin foto -> avatar con inicial (comportamiento actual).
- Muchas entradas -> scroll vertical normal (LazyColumn), sin paginación nueva.
- Modo oscuro y landscape cubiertos en criterios.
