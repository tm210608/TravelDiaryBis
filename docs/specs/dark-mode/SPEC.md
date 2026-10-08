# SPEC: Tema oscuro global — Atlas nocturno

> Metodología SDMD (Spec Driven Mobile Development) — la spec es la fuente de verdad, el código la sirve.
> "Done" = build + tests + app instalada + flujo ejecutado + evidencia.

---

## Qué construimos

La app ya está envuelta por `TravelDiaryTheme` en `MainActivity` y selecciona el esquema según Android; esta feature no vuelve a cablear esa integración. Rediseñamos y verificamos la apariencia oscura de Inicio, Añadir, Detalle, Mapa, Recuerdos y Perfil con la dirección **Atlas nocturno**: tinta azul profunda, superficies de pizarra, agua en turquesa y un acento cálido de ruta, tomando el diario de viaje como referencia. No se añade un selector propio; el mapa existente sigue el mismo modo del sistema.

---

## Dirección visual

- Fondo base / tinta cartográfica: `#101B22`
- Superficie / pizarra oscura: `#1B2931`
- Superficie elevada: `#253640`
- Primario / agua turquesa: `#78C9C4`
- Acento / ruta ámbar: `#E7AE6A`
- Texto principal / papel cálido: `#ECE8DC`

Firma visual: las superficies de contenido se sienten como páginas de un atlas nocturno; el turquesa identifica acciones principales y el ámbar se reserva para marcas de viaje/favoritos. Se conserva la tipografía actual y el layout, rediseñando los tokens de color oscuro y eliminando colores de día hardcodeados solo cuando se usen como fondo/texto de UI. Blanco/negro de alto contraste sobre foto o visor de cámara se mantiene intencional.

---

## Criterios de aceptación (testeables, sin ambigüedad)

- [ ] `MainActivity` mantiene el contenido dentro de `TravelDiaryTheme`, que selecciona el esquema claro/oscuro a partir del modo Android del sistema
- [ ] Los tokens de `DarkColorScheme` usan la paleta Atlas nocturno definida arriba (fondo, superficie, superficie elevada, primario, acento y texto)
- [ ] Con sistema en modo claro, todas las pantallas conservan el esquema claro actual
- [ ] Al activar modo oscuro Android, Inicio, Añadir, Detalle, Recuerdos y Perfil muestran fondo, superficies, texto, campos, tarjetas, botones, barras e iconos legibles según Atlas nocturno
- [ ] Mapa mantiene su estilo cartográfico oscuro existente y cambia junto con el sistema; al volver a modo claro, vuelve al estilo claro
- [ ] Cámara conserva preview real y controles visibles; el negro del visor y overlays sobre imagen se consideran intencionales, no fondos de UI que haya que recolorear
- [ ] Snackbar, dialogs, estados Loading/Empty/Error, bottom navigation, FAB, formularios y teclado/navegación de sistema mantienen contraste legible en ambos temas
- [ ] Cambiar el tema del sistema mientras la app está en foreground actualiza el esquema sin reiniciar ni perder el estado del formulario/navegación
- [ ] No aparece ajuste propio Claro/Oscuro/Sistema ni se persiste una preferencia de tema de app
- [ ] Build `./gradlew compileDebugKotlin` y tests `./gradlew testDebugUnitTest` pasan

---

## Fuera de alcance (no se construye en esta feature)

- Selector manual de tema dentro de Perfil/Ajustes
- Cambiar paleta clara, tipografías, layouts o navegación
- Rediseñar el estilo JSON del mapa oscuro (se reutiliza el estilo actual)
- Modo sepia, alto contraste configurable o temas descargables
- Cambiar colores fotográficos, imágenes, video-preview o contenido externo

---

## UI states

No se introducen nuevos estados de pantalla. `TravelDiaryTheme` selecciona `LightColorScheme` o `DarkColorScheme` según `isSystemInDarkTheme()` y Compose recompone los componentes que usan `MaterialTheme.colorScheme`.

- **Claro**: esquema claro actual, sin cambio visual funcional.
- **Oscuro**: tokens Atlas nocturno en componentes Material y en superficies/textos personalizados.
- Al cambiar configuración del sistema, el tema se actualiza reactivamente; ViewModels/navegación conservan su estado.

---

## Navegación

Sin cambios de rutas. `MainActivity` envuelve `TravelDiaryNavGraph` con `TravelDiaryTheme`; todas las destinations heredan el mismo esquema. MapScreen conserva su lógica de `isSystemInDarkTheme()` para JSON de mapa.

---

## Arquitectura

- `MainActivity.kt` — sin cambio funcional; confirmar en ejecución que `TravelDiaryTheme` sigue envolviendo el NavGraph
- `ui/theme/Color.kt` — tokens semánticos de la paleta Atlas nocturno; conservar tokens claros actuales
- `ui/theme/Theme.kt` — actualizar `DarkColorScheme` y mantener selección por `isSystemInDarkTheme()`
- `ui/components/CommonComponents.kt`, `ui/components/EntryCards.kt` — revisar colores hardcodeados en texto/superficies
- `ui/screens/TravelDiaryHomeScreen.kt`, `AddTravelEntryScreen.kt`, `TravelDetailScreen.kt`, `MapScreen.kt`, `MemoriesScreen.kt`, `ProfileScreen.kt`, `CameraScreen.kt` — asegurar que la UI use tokens del tema y contraste correcto; preservar negro/blanco intencional sobre preview/fotos
- `res/raw/map_style_dark.json` — sin cambios, validar coexistencia con el esquema global
- `data/`, `domain/`, Room y navigation routes — sin cambios

---

## Permisos, lifecycle y persistencia

- Sin permisos ni escritura de datos.
- Sin preferencia persistida: el tema efectivo siempre sigue Android.
- Los cambios de configuración no recrean ni descartan contenido fuera del comportamiento normal de Activity; verificar que formulario y back stack sobreviven la recreación.

---

## Plan de tests

- [ ] build: `scripts/verify.sh`
- [ ] unit: no requiere nueva lógica de dominio; mantener todos los tests existentes en verde
- [ ] UI/preview: comprobar componentes principales con `LightColorScheme` y `DarkColorScheme` si la infraestructura actual lo permite
- [ ] dispositivo: `scripts/verify.sh --device`; en Xiaomi, cambiar Quick Settings entre claro/oscuro con app visible y validar Home, Añadir, Detalle, Mapa, Recuerdos, Perfil y Cámara
- [ ] evidencia: capturas en `docs/specs/dark-mode/` de Inicio, formulario, Detalle, Mapa, Recuerdos y Perfil en tema oscuro; Home en tema claro para regresión

---

## Edge cases

- Sistema cambia de claro a oscuro mientras se edita texto → no pierde el contenido del formulario
- Sistema cambia el tema estando en Detalle o en una tab secundaria → conserva destination y estado
- Estilos con colores hardcodeados en superficies/textos no deben producir etiquetas invisibles ni fondos claros inesperados
- Texto blanco sobre foto/preview sigue legible y no se convierte en texto oscuro por heredar `onSurface`
- El JSON de estilo oscuro de Maps debe activarse y desactivarse en sincronía con el tema Compose del sistema
- La configuración de tema cambia al regresar desde Configuración rápida sin cerrar la app

---

*Spec elaborada siguiendo SDMD — la diferencia no está en usar una IA mejor, está en darle contexto, criterios verificables y una definición clara de cuándo el trabajo está realmente terminado.*
