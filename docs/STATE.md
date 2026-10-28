# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-26 | **Rama:** `develop` | **Commit:** merge fast-forward de `feature/ui-pasaporte-y-contactos` (`287d00f`, 137 tests JVM verdes, verificado en Xiaomi)

## Hecho en esta sesión
- **Spec 2 del plan Stitch implementada, verificada y mergeada a `develop`** (`ui-pasaporte-y-contactos`):
  - `domain/model/GeoFormat.kt`: `haversineKm`, `totalDistanceKm(entries) -> (km, tramos)`, `formatKm` (miles `.` es, determinista sin locale).
  - `domain/model/CountryCode.kt`: `countryCodeOf` con tabla es/en (~150 alias) + fallback 3 letras; normalización compartida con `Continent.kt`.
  - `domain/model/Passport.kt`: `PassportData{kmTotal,legs,stamps}` + `RouteStamp` + `buildRouteStamps` (1 sello/país, primera visita, rotación determinista ±6°, cap 8).
  - `domain/usecase/GetPassportUseCase.kt` + `ProfileViewModel` con `combine` → `ProfileUiState.Success(stats, passport)`. Tests: HaversineTest, CountryCodeTest, RouteStampTest, GetPassportUseCaseTest, ProfileViewModelTest (137 verdes).
  - `ui/components/PassportStamp.kt` (doble borde, rotación, alpha entintado) y `FilmStrip.kt` (`Modifier.filmStrip`, carrete `#0E0D12` + perforaciones `Canvas`, `FilmAmber`).
  - `ProfileScreen` = Pasaporte de Expedición: cubierta (avatar AL + stats mono `VIAJES··FAV··PAÍSES··CONT`), tira `KM RECORRIDOS` (612 km / 01 tramo en el diario del dispositivo), grid "Visados y Sellos de Ruta", Exportar/Importar intactos.
  - `MemoriesScreen` = hoja de contactos 35mm sobre `filmStrip` con numeración `01A` ámbar; idéntica en claro/oscuro.
  - `MapScreen` = `Polyline` discontinua `primary` (bermillón claro / ámbar oscuro) uniendo markers por fecha asc.
  - `Type.kt`: `MonoDisplay`; `TicketDivider` gana flag `vertical=false`.
- Verificado en Xiaomi (`2412DPC0AG`): pasaporte y carrete en claro y oscuro, hilo de ruta, marker→InfoWindow→Detalle, fotograma→Detalle. 9 capturas en `docs/specs/ui-pasaporte-y-contactos/` y SPEC con criterios marcados.

## Siguiente paso (al abrir sesión con "Lee AGENTS.md y docs/STATE.md y continúa")
- Decidir siguiente unidad: ¿Spec 3 del plan Stitch (mirar `docs/specs/ui-polish-concepts/`)? o atacar deuda: bug `exportRequested` pegajoso.
- La rama `feature/ui-pasaporte-y-contactos` sigue existiendo (mergeada); no borrar sin confirmación.
- Criterio SPEC no observado en dispositivo (diario tiene sellos): placeholder "COMPLETA TU PRIMER VISADO" cuando hay 0 países — implementado pero sin evidencia.

## Decisiones vigentes
- Dualidad visual (sin dynamic color): Claro = Cuaderno de Campo (`#F4EFE6`/`#141B24`/`#C83E2B`); Oscuro = Atlas Nocturno (`#12121D`/`#FFDCA1`/`#B4C9DF`).
- Metáfora física: billete + cuadrícula + telemetría mono + sellos/carrete, todo Compose puro sin dependencias.
- Exportar/Importar ZIP (`formatVersion=1` con `uuid`) y migraciones Room (`v3`) activos.
- `local.properties` no se commitea. Sin push. Commits `feat|fix|test|docs|chore:`.

## Bloqueos
- Ninguno. adb inalámbrico del Xiaomi se desconectó a mitad de sesión; reconectado por el usuario (`adb-MVVSE6A6S4IVQ8MJ._adb-tls-connect._tcp`).

## Deuda técnica
- **Bug preexistente:** `exportRequested` es argumento de nav pegajoso — volver a Home re-activa el modo selección (`LaunchedEffect` se re-dispara). Fix candidato: consumir el arg una vez.
- Placeholder de 0 sellos y estados Empty/Loading de Recuerdos: implementados, sin evidencia en dispositivo (diario con datos).
- Pre-Play Store: privacidad/Data Safety, keystore/R8/AAB.
- UI tests Compose, sync online, nav type-safe, CameraScreen (`LocalLifecycleOwner`), clustering, detekt/ktlint.
