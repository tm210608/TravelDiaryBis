# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-26 | **Rama:** `develop` | **Commit:** merge `feature/ui-cuaderno-campo-home` (`15d1789`, 119 tests JVM verdes)

## Hecho en esta sesión
- **Spec 1 del plan Stitch implementada y verificada** (`ui-cuaderno-campo-home`), mergeada a `develop`:
  - `LightColorScheme` de marca "Cuaderno de Campo" en `ui/theme/Color.kt`/`Theme.kt` (papel `#F4EFE6`, tinta `#141B24`, bermellón `#C83E2B`). `dynamicLightColorScheme` eliminado: el modo claro ya tiene identidad propia en el Xiaomi.
  - Voz monoespaciada en `Type.kt` (`MonoEyebrow`/`MonoTelemetry`/`MonoStubDay`/`MonoStubMeta`, `FontFamily.Monospace`).
  - `ui/components/CartographyGrid.kt` (`Modifier.cartographyGrid`, líneas 48dp + cruces) y `Ticket.kt` (`ticketShape` con muescas laterales vía `Path.op` + `TicketDivider` discontinuo) — activos en claro Y oscuro.
  - `EntryListCard` y `FeaturedCard` rediseñadas como billetes con talón de fecha (`formatToTicket`) y telemetría GPS en mono; sin GPS solo "Ubicación, País".
  - `TravelDiaryHomeScreen`: "Registro Destacado" = favoritos → fallback recientes → oculta; cabeceras editoriales (`EXPEDICIÓN`/`HOJA DE RUTA` + contador `[02]`).
  - `TravelDetailScreen`: `InfoChip` + Card de Maps sustituidos por **Talón de Expedición** unificado (clickeable a `geo:` solo con GPS; `SIN GPS` si falta). `InfoChip` borrado.
  - `domain/model/GeoFormat.kt` + `GeoFormatTest` (12 tests): `formatToCardinal`, `toCardinalLat/Lon`, `telemetryLine`, `formatToCompact`, `formatToTicket` (meses `es`).
- Verificado en Xiaomi físico: papel claro sin dynamic color, grid visible claro+oscuro, talón abre Google Maps, modo selección intacto. 7 capturas en `docs/specs/ui-cuaderno-campo-home/`.

## Siguiente paso (mañana, al abrir sesión con "Lee AGENTS.md y docs/STATE.md y continúa")
Ejecutar `/spec ui-pasaporte-y-contactos` (Spec 2 del plan Stitch, ya descrita en la spec anterior):
- `ProfileScreen` como **Pasaporte de Expedición** (tira telemétrica con km Haversine offline + "Visados y Sellos de Ruta" dinámicos con `Canvas` y rotación).
- `MemoriesScreen` como **Hoja de Contactos 35mm** (bordes de carrete + numeración `01A` en mono ámbar) + `Polyline` de hilo de ruta en `MapScreen`.
- Referencia: `docs/specs/ui-polish-concepts/stitch-02-pasaporte-y-contactos-dark.png`.

## Decisiones vigentes
- Dualidad visual de marca (sin dynamic color): Claro = Cuaderno de Campo (`#F4EFE6`/`#141B24`/`#C83E2B`); Oscuro = Atlas Nocturno (`#12121D`/`#FFDCA1`/`#B4C9DF`).
- Metáfora física fijada: billete troquelado + cuadrícula cartográfica + telemetría mono en ambos temas.
- Exportar/Importar ZIP (`formatVersion=1` con `uuid`) y migraciones Room reales (`v3`) activos.
- `local.properties` no se commitea. Sin push. Commits `feat|fix|test|docs|chore:`.

## Bloqueos
- Ninguno. Xiaomi físico (`2412DPC0AG` / `MVVSE6A6S4IVQ8MJ`) verificado con el nuevo tema.

## Deuda técnica
- **Bug preexistente detectado en verificación:** `exportRequested` es argumento de nav pegajoso — volver a Home (p. ej. desde Detalle) re-activa el modo selección porque `LaunchedEffect(exportRequested)` se re-dispara. Candidato a fix: consumir el arg una vez (`rememberSaveable` o limpiar `backStackEntry.arguments`).
- Spec 2 (`ui-pasaporte-y-contactos`) pendiente de `/spec` + aprobación.
- Pre-Play Store: privacidad/Data Safety, keystore/R8/AAB.
- UI tests Compose, sync online, nav type-safe, CameraScreen (`LocalLifecycleOwner`), clustering, detekt/ktlint.
