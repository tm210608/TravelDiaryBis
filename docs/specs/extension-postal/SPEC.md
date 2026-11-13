# SPEC: Extensión del estilo "Postal" al resto de pantallas

Estado: APROBADA (2026-11-13)
Continuación de `docs/specs/rediseno-postal/SPEC.md` (verificada). Misma dirección
visual: cálida, editorial, foto-led, sin etiquetas técnicas ni densidad.

## Qué construimos

Llevar el estilo postal aprobado en Home a las pantallas que conservan el diseño
técnico anterior ("bitácora/expedición"): Detalle, Perfil, Recuerdos y los
formularios (Nueva entrada, Editar entrada, Editar perfil). Mismo lenguaje ya
disponible en el tema: fondo cálido, tarjetas crema con sombra suave, títulos
serif (`SerifAppTitle`, `SerifCardTitle`), texto sans sobrio, acento terracota.

## Criterios de aceptación (testeables, sin ambigüedad)

### Detalle (`TravelDetailScreen`)
- [ ] La foto de portada ocupa la cabecera a todo lo ancho (como hoy) y debajo
      aparecen: título de la entrada, línea "Ciudad, País · fecha", la
      descripción ("La Historia") y la galería de fotos si la hay.
- [ ] El botón "Ver mapa" sigue presente cuando la entrada tiene coordenadas y
      navega igual que ahora.
- [ ] Ya NO aparecen: la tarjeta "TALÓN DE EXPEDICIÓN" con sello/estética de
      ticket, coordenadas GPS en crudo, etiqueta `tag` como chip técnico,
      divisores tipo ticket ni textos en mayúsculas de archivo.
- [ ] Botones flotantes de atrás, editar y favorito siguen funcionando igual;
      se les da estilo de círculo translúcido (como el corazón de la postal).

### Perfil (`ProfileScreen`)
- [ ] Cabecera simple: título serif + avatar (mismo patrón del Home) que abre
      "Editar perfil".
- [ ] Las estadísticas (km, días, tramos GPS) se muestran en una tarjeta cálida
      simple, sin estética de ticket ni contadores tipo archivo.
- [ ] La sección "Visados y Sellos de Ruta" se mantiene tal cual está
      (ya encaja con la estética postal; decisión del usuario).
- [ ] Las tarjetas de exportar/importar siguen presentes y funcionando.

### Recuerdos (`MemoriesScreen`)
- [ ] La galería se muestra como rejilla de postales foto-led: cada elemento es
      una postal con su foto ocupando todo el marco (estilo `PostalEntryCard`
      pero centrada en la foto; título del viaje y fecha debajo).
- [ ] Tocar una foto mantiene el comportamiento actual (abrir el visor/detalle
      que hoy implementa la pantalla).
- [ ] El estado vacío conserva el mensaje + botón "Crear tu primer viaje" con el
      estilo del empty state del Home.

### Formularios (Nueva entrada, Editar entrada, Editar perfil)
- [ ] Mismos campos, mismos diálogos (GPS, descartar cambios, confirmación),
      misma lógica: solo cambia el cromado visual.
- [ ] Título del TopAppBar en serif; campos con bordes suaves sobre fondo cálido;
      botón principal de guardar con el acento terracota.
- [ ] Ya NO aparecen etiquetas técnicas en mayúsculas ni estilo mono de archivo.

### Global
- [ ] `scripts/verify.sh` en verde; tests existentes siguen pasando.
- [ ] Modo oscuro equivalente en las pantallas tocadas (paleta Atlas Nocturno).
- [ ] Navegación y rutas sin cambios; datos/Room/use cases sin cambios.

## Fuera de alcance

- Home (ya rediseñado), Mapa, Splash y Cámara.
- Cambios funcionales: campos nuevos, lógica de guardado, permisos, exportación.
- Nuevas fuentes empaquetadas (se sigue con `FontFamily.Serif` + Epilogue/Manrope).
- Animaciones o transiciones nuevas.

## UI states

Se conservan los `UiState`/estados actuales de cada pantalla (Loading / Error /
Empty / contenido). Esta feature es de restyling: no se añaden estados nuevos;
en Loading/Error se mantiene el comportamiento actual con los colores del tema.

## Navegación

Sin cambios. Se verifica: Detalle↔atrás, Detalle→Editar, Detalle→Ver mapa,
Perfil→Editar perfil, Recuerdos→visor, formularios→guardar→atrás.

## Arquitectura

Solo capa `ui/` (y tema). Sin cambios en `domain/` ni `data/`.

- `ui/screens/TravelDetailScreen.kt`: reescritura del layout a postal.
- `ui/screens/ProfileScreen.kt`: cabecera + tarjeta de estadísticas renovadas;
  `PassportStamp` y la sección de visados se conservan.
- `ui/screens/MemoriesScreen.kt`: rejilla de postales foto-led.
- `ui/screens/AddTravelEntryScreen.kt`, `EditTravelEntryScreen.kt`,
  `EditProfileScreen.kt`: cromado (títulos serif, fondo, botones, campos).
- `ui/components/EntryCards.kt`: variante de postal foto-led para Recuerdos
  (`MemoryPostalCard` o similar) reutilizando el patrón de `PostalEntryCard`.
- `ui/components/CommonComponents.kt` / nuevos componentes compartidos: cabecera
  postal reutilizable (`PostalHeader`) extraída del patrón del Home.
- `ui/theme/Type.kt`: estilos serif ya existentes; ampliar solo si falta alguno.
- Los estilos `Mono*` quedan para la sección de visados; no se borran.

## Permisos, lifecycle y persistencia

Sin cambios: mismas pantallas de lectura/escritura actuales, mismos permisos
(cámara/fotos/ubicación en los formularios que ya los usan), misma supervivencia
a rotación y tema.

## Plan de tests

- [ ] build: `scripts/verify.sh` en verde.
- [ ] unit: tests existentes en verde; añadir/ajustar solo si un ViewModel toca.
- [ ] dispositivo: `scripts/verify.sh --device` en emulador con datos mock
      (o móvil si está libre) + flujo: Home→Detalle→Editar→atrás; Perfil;
      Recuerdos; Nueva entrada guarda; Editar perfil guarda.
- [ ] evidencia: capturas por pantalla en `docs/specs/extension-postal/evidence/`.

## Edge cases

- Entrada sin foto: la cabecera del detalle muestra degradado+icono (mismo
  placeholder de la postal) en vez de espacio vacío.
- Entrada sin descripción: la sección "La Historia" se omite, no muestra texto
  vacío.
- Entrada sin GPS: el botón "Ver mapa" se omite (comportamiento actual).
- Recuerdos con muchas fotos: rejilla con scroll normal.
- Perfil sin nombre/foto: avatar con icono de persona.
- Landscape y modo oscuro en cada pantalla tocada.
