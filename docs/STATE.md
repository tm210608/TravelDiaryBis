# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-29 | **Rama:** `feature/cuadricula-visible` (PR #31 abierto contra `develop`, CI verde) | 137 tests JVM verdes

## Hecho el 2026-10-29
- `develop` local (76 commits sin subir) pusheado a `origin/develop`. PR #30 (filtro de chips rehecho en VM sobre base vieja) cerrado sin merge: duplicaba `feature/filter-chips`.
- PR #31 `fix: cuadrícula cartográfica visible en Home y Detalle`:
  - `gridColor` alpha 0.14 claro / 0.12 oscuro (antes 0.05/0.06) en `TravelDiaryHomeScreen` y `TravelDetailScreen`; `cartographyGrid(strokeWidth = 1.dp)` (antes 0.8px fijo).
  - Lint de CI arreglado (fallaba en `develop`): `EntryCards.kt` usaba `uppercase(Locale.getDefault())` en composable (NonObservableLocale) → `uppercase()`.
  - Verificado Home claro/oscuro en emulador Pixel 6 API 34 (VM). Sin ver en Xiaomi; Detalle sin captura en dispositivo.
- (2026-10-26) **Spec 2 del plan Stitch mergeada a `develop`** (`ui-pasaporte-y-contactos`):
  - `GeoFormat.kt` (haversine, km), `CountryCode.kt` (~150 alias es/en), `Passport.kt` + `GetPassportUseCase` + `ProfileViewModel`.
  - `PassportStamp.kt`, `FilmStrip.kt`; `ProfileScreen` = Pasaporte; `MemoriesScreen` = hoja de contactos 35mm; `MapScreen` con `Polyline` por fecha.
  - Verificado en Xiaomi (`2412DPC0AG`); capturas en `docs/specs/ui-pasaporte-y-contactos/`.

## Siguiente paso (al abrir sesión local con "Lee AGENTS.md y docs/STATE.md y continúa")
1. Conectar el Xiaomi por adb, instalar la rama y comprobar la cuadrícula en Home y Detalle (claro/oscuro). Si se ve bien, mergear PR #31 en `develop` y hacer `git pull` en local.
2. Después elegir: Spec 3 del plan Stitch (`docs/specs/ui-polish-concepts/`) o el bug `exportRequested` pegajoso.
- 5 PNG sin commitear en `docs/specs/dark-mode/` (evidencias dark mode): decidir si se commitean.
- Ramas `feature/ui-pasaporte-y-contactos`, `feature/filter-chips` y `feature/home-filter-chips` (la del PR #30 cerrado) siguen existiendo; no borrar sin confirmación.

## Decisiones vigentes
- Dualidad visual (sin dynamic color): Claro = Cuaderno de Campo (`#F4EFE6`/`#141B24`/`#C83E2B`); Oscuro = Atlas Nocturno (`#12121D`/`#FFDCA1`/`#B4C9DF`).
- Metáfora física: billete + cuadrícula + telemetría mono + sellos/carrete, todo Compose puro sin dependencias.
- Exportar/Importar ZIP (`formatVersion=1` con `uuid`) y migraciones Room (`v3`) activos.
- `local.properties` no se commitea. Push solo con confirmación; mantener `origin/develop` al día con el local. Commits `feat|fix|test|docs|chore:`.

## Bloqueos
- Ninguno. El Xiaomi no estaba conectado por adb en la sesión del 2026-10-29.

## Deuda técnica
- **Bug preexistente:** `exportRequested` es argumento de nav pegajoso — volver a Home re-activa el modo selección (`LaunchedEffect` se re-dispara). Fix candidato: consumir el arg una vez.
- Placeholder de 0 sellos y estados Empty/Loading de Recuerdos: implementados, sin evidencia en dispositivo.
- Pre-Play Store: privacidad/Data Safety, keystore/R8/AAB.
- UI tests Compose, sync online, nav type-safe, CameraScreen (`LocalLifecycleOwner`), clustering, detekt/ktlint.
