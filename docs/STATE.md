# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-13 | **Rama:** `feature/rediseno-postal` (NO pusheada; `main` sí al día en origin) | 167 tests JVM verdes | verify.sh OK | SPEC verificada con evidencia

## Hecho el 2026-11-13 — feature "rediseno-postal" implementada y verificada
SPEC: `docs/specs/rediseno-postal/SPEC.md` (todos los criterios `[x]`). Evidencia: `docs/specs/rediseno-postal/evidence/`.
- **Home "postal"**: cabecera solo "Travel Diary" + avatar (icono persona o foto); selector Viajes|Favoritos; cada entrada = marco crema con foto inset (o degradado+icono), título serif y "Ciudad, País · fecha"; corazón sobre la foto que persiste `isFavourite` de verdad (antes solo cosmético).
- **Fuera del Home**: buscador, chips continente, carrusel destacado, estadísticas, Visados, contadores "[00]", footer técnico, fondo cuadriculado.
- **Barra inferior**: Inicio/Mapa/Fotos/Perfil en todas las pantallas. Export sigue en Perfil ("Exportar diario"); modo selección del Home conservado.
- **Componentes borrados** (sin uso): `TopBar`, `SearchBar`, `FilterChipsRow`, `ArchiveFooter`, `FeaturedEntriesRow`, `FeaturedCard`, `EntryListCard`. `HomeViewModel` sin search/chips/featured/weather; nuevo `favouritesOnly` + `toggleFavourite`.
- **Type.kt**: `SerifAppTitle`/`SerifCardTitle` (`FontFamily.Serif`, sin fuentes nuevas). Rejilla `LazyVerticalGrid` adaptativa (1-2 cols, landscape verificado 2 cols). Modo oscuro verificado.
- **Bonus**: `EditProfileViewModel` guarda borrador en `SavedStateHandle` (MIUI recreaba la actividad al volver del photo picker y se perdían los cambios).
- Commits: `6e45532` rediseño base, `2d30684` iteración feedback (nombre app, favoritos, marco premium), `93c01ff`+`cdc121e` evidencia+spec.
- Verificado en `emulator-5560` con 8 entradas mock con fotos reales (DB inyectada vía run-as; móvil del usuario ocupado con TradeJob). El usuario ya vio una versión en su móvil y aprobó el rumbo pidiendo: nombre de app, pestaña favoritos y "toque premium".

## Siguiente paso
1. **Usuario revisa el Home en el emulador** (app abierta con datos mock). Si le gusta → decidir si extender el estilo postal a Detalle/Perfil/Recuerdos (nueva spec) o dar por buena esta iteración.
2. **Pendiente instalación en su móvil**: instalar cuando libere el dispositivo (actualizar fuerza cierre de la app).
3. Merge a `main`/`develop` + push SOLO con su aprobación (rama sin pushear).
4. Play Store (sin cambios): backup keystore, cuenta dev 25 USD, GitHub Pages (`main` + `/docs` → `privacy.html`), AAB en `app/build/outputs/bundle/release/`.

## Decisiones vigentes
- `applicationId`=`com.traveldiary.app`; launcher `com.example.traveldiary.MainActivity`.
- `feature/rediseno-postal` nace de `feature/pulido-ux` (ya mergeada a `main`), contiene la spec.
- Picsum.photos caído (522) y el emulador sin HTTPS saliente: fotos mock bajadas de Wikimedia Commons y metidas vía `adb shell run-as ... base64 -d` + DB por `cat > databases/travel_database`.
- `screenshots-review/` en `.gitignore` (scratch de capturas).
- adb sin `-s` falla con >1 dispositivo; el móvil vuelve a conectarse solo por WiFi (`adb disconnect` temporal).

## Deuda técnica
- `develop`, `feature/play-store-release` sin push; `chore/lint-detekt-ktlint` sin borrar.
- ANR en emulador-5556 (2026-11-13, arranque en frío): vigilar en físico.
- Al abrir la app a veces parpadea la pantalla previa del task (snapshot de recents de MIUI/Android); no es defecto del diseño.
- `kotlinOptions.jvmTarget` deprecated (warning).
