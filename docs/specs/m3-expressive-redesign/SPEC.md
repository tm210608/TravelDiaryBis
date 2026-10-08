# SPEC: Rediseño Material 3 Expressive

## Qué construimos
Reskin visual completo de la app al lenguaje **Material 3 Expressive**, usando como
referencia el design system y los 6 mockups generados en Google Stitch
(`docs/specs/m3-expressive-redesign/stitch/`). Incluye el upgrade de toolchain
necesario para las APIs Expressive y un tema nuevo (color, tipografía, shapes,
motion). **Reskin fiel**: no se añade lógica ni features nuevas; solo cambia la
capa visual de las pantallas existentes.

Design system Stitch: `assets/1b4a895e3dd24c3bae45c12074b3825a`
Proyecto Stitch: `projects/17835720265042941367`

## Decisiones tomadas (con el usuario)
- Toolchain: **salto moderado** — compileSdk/targetSdk **36**, AGP ~8.9.x,
  Kotlin ~2.2.x, KSP acorde, Compose BOM reciente + `material3` fijado a la
  versión **1.5.0 más nueva que aún admita compileSdk 36** (las builds post-sept-2026
  exigen 37.1/AGP 9.2 → fuera). Se resuelve empíricamente; fallback: la última alpha
  compatible.
- Modo claro: **color dinámico** (`dynamicLightColorScheme`/`dynamicDarkColorScheme`)
  en API 31+; en API 26-30 se usa el esquema de marca dark como único tema.
- Fuentes: **Epilogue (titulares) + Manrope (cuerpo)** empaquetadas como TTF en
  `res/font/` (offline-first, sin Downloadable Fonts).
- Extras propuestos por Stitch (badge nivel, "Próximo objetivo", cita editorial,
  filtros extra, chips de etiqueta sugeridos, contador en mapa): **fuera**.

## Criterios de aceptación (testeables, sin ambigüedad)
- [ ] `./gradlew compileDebugKotlin` compila con la toolchain nueva
      (AGP ~8.9.x, Kotlin ~2.2.x, compileSdk 36) sin warnings de versión.
- [ ] `Theme.kt` usa `MaterialExpressiveTheme` (o `MaterialTheme` + `MotionScheme`
      si la versión fijada no expone el wrapper) con el esquema dark completo de
      `stitch/DESIGN.md` (~35 roles: surfaceContainer*, outlineVariant, etc.).
- [ ] En API 31+ el tema sigue el wallpaper del usuario (dynamic color); en API
      26-30 se aplica el dark de marca.
- [ ] `Type.kt` existe con la escala de DESIGN.md (Epilogue display/headline,
      Manrope body/label) servida desde `res/font/` — la app renderiza offline
      con esas fuentes.
- [ ] `Shape.kt` existe con la escala expresiva (cards 24-28dp, pill/search 50%,
      FAB 24dp).
- [ ] Home reproduce `stitch/home.png`: eyebrow "BIENVENIDO DE NUEVO", chips,
      carrusel destacados, lista con tags, FAB squircle, NavigationBar. El stagger
      con `delay(index * 100)` desaparece (animación del tema / `animateItem`).
- [ ] Detalle reproduce `stitch/detail.png`: hero ~40%, glass buttons, tag,
      metadatos, secciones "La Historia" / "Ubicación" / card compartir.
- [ ] Add/Edit reproduce `stitch/add_entry.png`: drop zone dashed, fields filled
      variant, "Guardar" en TopAppBar.
- [ ] Recuerdos reproduce `stitch/memories.png`: grid 3 col + cabecera
      "Recuerdos" + subtítulo (sin cita ni filtros extra).
- [ ] Perfil reproduce `stitch/profile.png`: avatar con anillo, 4 stat cards,
      bottom nav (sin badge ni "Próximo objetivo").
- [ ] Mapa reproduce `stitch/map.png`: pill flotante con back+título, markers con
      tinte oro, card de preview inferior (sin contador "3 de 12").
- [ ] Cero emojis en UI (👋, 📍 eliminados → iconos Material Symbols/drawables o
      `material-icons-extended` ya presente).
- [ ] Cero colores hardcodeados en screens/components (`Color(0xFF...)`,
      `Color.White` fuera de scrims) → todo a `colorScheme`.
- [ ] `Icons.Filled.ArrowBack` deprecados migrados a `AutoMirrored`.
- [ ] Los 66 tests actuales siguen pasando (`scripts/verify.sh`).

## Fuera de alcance
- Features nuevas vistas en los mockups (badge nivel, progreso de objetivo, cita,
  filtros de Recuerdos, chips sugeridos, contador de cards en mapa).
- Editar nombre/avatar de perfil, navegación type-safe, clustering de markers,
  light theme bespoke, dark-mode toggle manual.
- Subir maps-compose (queda 4.4.2; si rompe con compileSdk 36 se evalúa un bump
  menor dentro de la línea 4.x/5.x compatible, nunca a 9.x).

## UI states
Sin cambios en los `sealed interface` existentes. Se rediseña el render:
- `Loading` → `CircularWavyProgressIndicator`/`LoadingIndicator` Expressive si la
  versión fijada lo expone; si no, `CircularProgressIndicator` actual.
- `Empty` (Mapa/Recuerdos) → icono grande en tonal container + título + texto
  actual. Sin CTA nueva (no existen hoy).
- `Error` (Detalle) → mismo copy + botón reintentar con estilo nuevo.
- `Success` → layouts según mockups.

## Navegación
Sin cambios de rutas ni de grafo. Solo cambia la presentación de
`BottomNavigationBar` (indicador pill oro, iconos outlined→filled al seleccionar,
igual que hoy pero con tokens nuevos).

## Arquitectura
```
gradle/libs.versions.toml        agp, kotlin, ksp, composeBom, material3 (pin explícito)
app/build.gradle.kts             compileSdk/targetSdk 36
gradle-wrapper.properties        si AGP lo exige
app/src/main/res/font/           epilogue_{regular,semibold,bold,extrabold}.ttf
                                 manrope_{regular,medium,semibold,bold}.ttf
ui/theme/Color.kt                esquema completo dark desde DESIGN.md
ui/theme/Type.kt                 NUEVO: escala Epilogue/Manrope
ui/theme/Shape.kt                NUEVO: escala expresiva
ui/theme/Theme.kt                MaterialExpressiveTheme + dynamic color + motionScheme
ui/components/CommonComponents.kt TopBar (eyebrow), SearchBar (pill), FilterChipsRow,
                                 BottomNavigationBar (tokens nuevos)
ui/components/EntryCards.kt      FeaturedCard, EntryListCard (radios, tonal surfaces)
ui/components/TravelEntryFormFields.kt  filled fields, drop zone dashed, switch
ui/screens/*                     6 pantallas según stitch/*.png
```
Sin tocar: `domain/`, `data/`, `di/`, `navigation/`, `viewmodel/` (reskin fiel).

## Permisos, lifecycle y persistencia
Sin cambios. Fuentes empaquetadas → render offline garantizado. Rotación: los
layouts ya usan scroll; verificar que el hero de detalle no se corte en landscape.

## Plan de tests
- [ ] build: `scripts/verify.sh` (compile + 66 tests unitarios, 0 fallos)
- [ ] unit: no se añaden (no hay lógica nueva); los existentes deben pasar igual
- [ ] dispositivo: `scripts/verify.sh --device` en Xiaomi — recorrer Home → chips →
      detalle → back → Recuerdos → Perfil → Mapa → Add (abrir form, sin guardar)
- [ ] evidencia: capturas del dispositivo por pantalla en
      `docs/specs/m3-expressive-redesign/` comparadas con `stitch/*.png`

## Edge cases
- **material-icons**: material3 1.4+/1.5 ya no trae `material-icons-core`.
  Estrategia: mantener la dep explícita `material-icons-extended` (ya existe) y,
  si la fijada la retira, migrar a drawables XML de Material Symbols.
- **Alpha inestable**: si una API Expressive concreta falla en runtime, se usa el
  equivalente estable de la misma versión (nunca se baja a 1.2.x).
- **maps-compose 4.4.2 + compileSdk 36**: verificar que compila; markers con
  tinte oro vía `BitmapDescriptorFactory.defaultMarker(HUE)` (sin ícono custom).
- **Fuentes TTF**: licencias OFL OK; peso total <400KB; incluir solo 4 pesos por
  familia.
- **Dynamic color**: en API 31+ el oro de marca queda sustituido por el esquema
  del wallpaper — aceptado por la decisión "color dinámico".
- **Detalle en landscape**: hero con `height` fija 400dp puede comer la pantalla;
  mantener `fillMaxWidth` + ratio, no romper scroll.
- **Versión material3**: si ninguna 1.5.0-alpha compila con compileSdk 36, fallback
  documentado a la estable 1.4.0 aplicando el mismo DESIGN.md (se pierden
  `MaterialExpressiveTheme`/`ButtonGroup`, se mantiene el reskin).
