# SPEC: Cuaderno de Campo — tema claro de marca + billetes troquelados

> SDMD — spec aprobada tras preguntas de alcance. Referencia visual:
> `docs/specs/ui-polish-concepts/stitch-01-atlas-campo-home.png` (Stitch).

## Qué construimos

El modo claro deja de ser `dynamicLightColorScheme` (aspecto lavado en el Xiaomi) y pasa a
una paleta propia de marca **"Cuaderno de Campo Cartográfico"**: papel topográfico
`#F4EFE6`, tinta marina `#141B24`, bermellón brújula `#C83E2B`. Home y Detalle adoptan la
metáfora del **billete troquelado**: fondo con cuadrícula cartográfica sutil, telemetría
(coordenadas GPS, fechas compactas) en fuente monoespaciada y tarjetas con muescas
laterales + divisor discontinuo y talón de fecha. Todo se implementa en Compose puro
(`GenericShape`, `Canvas`, `drawBehind`) y funciona igual bajo el tema oscuro "Atlas
Nocturno" existente.

## Criterios de aceptación (testeables, sin ambigüedad)

- [x] `TravelDiaryTheme` en claro usa el nuevo `LightColorScheme` "Cuaderno de Campo"
      (tokens definidos abajo). `dynamicLightColorScheme` ya no se invoca en ningún caso;
      el import de `dynamicLightColorScheme`/`LocalContext` desaparece de `Theme.kt`.
- [x] El sistema en claro muestra papel `#F4EFE6` de fondo, tinta marina y acento
      bermellón en Home, Añadir, Detalle, Recuerdos y Perfil (todos usan `colorScheme`;
      verificado en dispositivo en Home, Detalle y Perfil).
- [x] El sistema en oscuro mantiene el esquema "Atlas Nocturno" actual sin regresión de
      tokens (evidencia-05/06/07).
- [x] Home y Detalle renderizan una cuadrícula cartográfica sutil detrás del contenido
      (líneas cada ~48dp + cruces cada 4 celdas), visible en claro Y en oscuro con
      contraste sutil en ambos.
- [x] `EntryListCard` es un billete: forma con muescas semicirculares a izquierda y
      derecha, línea discontinua vertical que separa cuerpo y talón derecho con la fecha
      (`dd` grande + `MMM` + `yy` en mono). Con GPS, la línea inferior de la fila
      ubicación+tag muestra `41.3850°N · 2.1734°E` en mono; sin GPS muestra solo
      `Ubicación, País` (rama sin GPS cubierta por tests JVM; las entradas del
      dispositivo llevan coordenadas).
- [x] `FeaturedCard` ("Registro Destacado") muestra sobre la foto: telemetría mono
      `lat°N · lon°E · dd·MMM·yy` (o solo `dd·MMM·yy` sin GPS) y titular en blanco.
      Misma forma de billete con muescas.
- [x] "Registro Destacado" muestra las entradas favoritas (máx. 3); si no hay favoritos
      pero sí entradas, muestra las más recientes (máx. 3); si no hay entradas, la
      sección entera (cabecera + tarjetas) no se renderiza (en dispositivo la fila
      renderiza 2 tarjetas; las ramas están en código `filter/take/ifEmpty`).
- [x] En `TravelDetailScreen`, los `InfoChip` de fecha/ubicación y la Card de "Ubicación"
      se reemplazan por un único **"Talón de Expedición"** troquelado: fila con fecha
      (dd MMM yyyy + hora en mono), `Ubicación, País`, y columna derecha en mono con
      `lat°N`/`lon°E`. Con GPS, el talón entero es clickeable y abre `geo:` en Google
      Maps (verificado: `topResumedActivity=com.google.android.apps.maps`); sin GPS, no
      es clickeable y la columna de coordenadas muestra `SIN GPS`.
- [x] `SectionHeader` adopta la voz editorial: título en eyebrow mono en mayúsculas
      (`+0.05em`) sobre el título, y contador a la derecha en mono entre corchetes
      (p. ej. `[04]`). El `TextButton` sin acción desaparece.
- [x] El modo selección de exportación (checkbox a la izquierda, contador, menú
      Guardar/Compartir) sigue funcionando sin regresión visual ni funcional
      (evidencia-04).
- [x] Nuevos formateadores puros con tests JVM: `formatToCardinal` (lat y lon con 4
      decimales + cardinal N/S/E/W), `formatToCompact` (`dd·MMM·yy`), `formatToTicket`
      (dd/MMM/yy/HH:mm del stub).

## Fuera de alcance

- Spec 2 (`ui-pasaporte-y-contactos`): ProfileScreen pasaporte, MemoriesScreen 35mm,
      Polyline en MapScreen.
- Selector manual de tema, modo sepia u otros temas.
- Fuentes descargables: la voz mono usa `FontFamily.Monospace` del sistema.
- Cambios en lógica de filtros, búsqueda, exportación/importación o navegación.
- Rediseño de `AddTravelEntryScreen`, `CameraScreen`, `MapScreen` (reciben el tema claro
      por herencia, sin trabajo específico).

## UI states

Sin estados nuevos. `HomeViewModel`/`DetailViewModel` y sus `UiState` no cambian; la
feature es solo cromo visual sobre los mismos datos.

- **Con entradas**: grid + tarjetas billete; hero = favoritos o fallback a recientes.
- **Sin entradas**: Home muestra solo topbar/búsqueda/chips y la sección "Todos los
      Viajes" (comportamiento vacío actual, sin Empty state nuevo).
- **Selección de exportación**: checkbox sustituye la zona de la miniatura; muescas y
      talón se mantienen.

## Navegación

Sin rutas nuevas. En el Talón de Expedición, el tap con GPS abre
`geo:<lat>,<lon>?q=<lat>,<lon>` en `com.google.android.apps.maps` (mismo Intent que hoy,
movido a la tarjeta unificada).

## Arquitectura

- `ui/theme/Color.kt` — nuevos tokens "Cuaderno de Campo":
  `FieldPaper = #F4EFE6`, `FieldPaperContainer = #EAE0D2`, `FieldInk = #141B24`,
  `FieldInkVariant = #5C6470`, `FieldVermilion = #C83E2B` (primary),
  `FieldVermilionContainer = #F2D5CB`, `FieldAmber = #B87314` (secondary/acento),
  `FieldRule = #D8CFC0` (outline), `FieldRuleVariant = #E2DAC9`.
  Error claro estándar M3 (`#BA1A1A` / `#FFDAD6`). Tokens oscuros existentes intactos.
- `ui/theme/Theme.kt` — añade `LightColorScheme` con esos tokens y elimina la rama
  `dynamicLightColorScheme`; `when { darkTheme -> Dark; else -> Light }`.
- `ui/theme/Type.kt` — `MonoFamily = FontFamily.Monospace` y helpers de estilo mono
  (`mono` con tracking) para telemetría y contadores.
- `ui/theme/DiaryColors.kt` (o en Color.kt) — token de "tinta de rejilla" por tema:
  claro `FieldInk @ 5%`, oscuro `DiaryOnSurface @ 6%`.
- `ui/components/CartographyGrid.kt` — `Modifier.cartographyGrid()` (drawBehind) o
  `Box` de fondo: líneas cada 48dp + cruces cada 4 celdas.
- `ui/components/Ticket.kt` — `TicketShape` (`GenericShape`, parámetros
  `stubWidthFraction`, `notchRadius`) y `TicketDivider` (Canvas vertical con
  `PathEffect.dashPathEffect`).
- `ui/components/EntryCards.kt` — `EntryListCard` y `FeaturedCard` rediseñadas como
  billetes; checkbox de selección preservado.
- `ui/components/CommonComponents.kt` — `SectionHeader` editorial (eyebrow + contador
  mono) sin `TextButton` vacío; `TopBar` recibe eyebrow "BITÁCORA DE VIAJE" en mono.
- `ui/screens/TravelDiaryHomeScreen.kt` — fondo con grid, sección destacada con
  fallback (favoritos → recientes → oculta) y textos "REGISTRO DESTACADO"/"TODOS LOS
  VIAJES".
- `ui/screens/TravelDetailScreen.kt` — Talón de Expedición unificado + grid de fondo;
  elimina `InfoChip` de fecha/ubicación y la Card de Maps separada (el `geo:` pasa al
  talón).
- `domain/model/GeoFormat.kt` — formateadores puros testeables en JVM:
  `formatToCardinal(value: Double, pos: Char, neg: Char)`, `Instant.formatToCompact()`,
  `Instant.formatToTicket()` (devuelve componentes dd/MMM/yy/HH:mm, meses en `es`).

## Permisos, lifecycle y persistencia

- Sin permisos ni escritura de datos. Solo lectura de entradas existentes.
- Rotación: sin estado nuevo que preservar (todo deriva de StateFlows actuales).
- `FontFamily.Monospace` no requiere assets ni descarga.

## Plan de tests

- [x] build: `scripts/verify.sh` — 119 tests, 0 fallos
- [x] unit `GeoFormatTest` (JVM): cardinal N/S/E/W correcto en signo y formato,
      `formatToCompact` con meses en español y año 2 dígitos, `formatToTicket`
      descompone fecha/hora, entrada sin GPS -> cadena "Ubicación, País" sin grados.
- [x] unit: suite existente (`testDebugUnitTest`) sigue verde sin tocar ViewModels.
- [x] dispositivo `scripts/verify.sh --device` (Xiaomi físico 2412DPC0AG): en modo CLARO
      se ve el papel de marca (no colores dinámicos); billetes con muescas; grid
      visible; entradas con GPS; hero con fallback de favoritos; Talón de Expedición en
      Detalle abre Google Maps; en modo OSCURO no hay regresión.
- [x] evidencia: `evidencia-01-home.png` (claro), `evidencia-02-detalle-gps.png`,
      `evidencia-03-perfil.png`, `evidencia-04-seleccion.png`,
      `evidencia-05-home-dark.png`, `evidencia-06-detalle-dark.png`,
      `evidencia-07-home-dark-limpio.png`.

## Edge cases

- Entrada sin `latitude`/`longitude` -> solo "Ubicación, País" en lista/hero y
      "SIN GPS" no clickeable en el Talón de Expedición.
- Sin favoritos pero con entradas -> hero muestra recientes; sin entradas -> sección
      oculta.
- `location`/`country` largos -> ellipsis en mono sin romper la línea de telemetría.
- Modo selección + billete: checkbox ocupa el slot de la miniatura; el talón sigue
      mostrando la fecha.
- Títulos con caracteres multi-línea -> `maxLines` con ellipsis dentro del billete.
- Previews de Compose en IDE usan el `LightColorScheme` de marca (no requieren
      dispositivo para ver el papel).
