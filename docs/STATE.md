# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-11 | **Rama:** `develop` (`094fcdb`, pendiente push: 2 commits nuevos + los ~11 anteriores ya subidos) | 169 tests JVM verdes

## Hecho el 2026-11-11
- **`develop` subido a GitHub** (antes solo existía en local; ahora trackea `origin/develop`).
- **detekt + ktlint configurados y en verde** (rama `chore/lint-detekt-ktlint` mergeada ff -> develop, sin borrar):
  - Plugins en `libs.versions.toml`: detekt 1.23.8, ktlint-gradle 12.3.0. Aplicados en `:app`.
  - Config: `.editorconfig` (ktlint_official, LF, 140 col, `ij_kotlin_packages_to_use_import_on_demand = androidx.compose.**,kotlinx.coroutines.**`,
    excepción max-line-length en `SampleData.kt`) y `config/detekt/detekt.yml` (ver abajo).
  - `verify.sh` ahora corre `ktlintCheck detekt compileDebugKotlin testDebugUnitTest`: el lint bloquea la verificación.
  - ktlintFormat autorrregló ~1800 avisos; resto a mano (wildcards no-Compose, líneas largas, `Weather.kt` -> `WeatherReading.kt` por rule filename).
  - detekt 253 avisos -> 0. Arreglos reales: log `Log.w` en `LocationHelper` (ya no traga excepciones), `writePhoto` extraído de
    `DiaryExporterImpl.export` (NestedBlockDepth), `Instance` -> `instance`, `_allEntries` -> `allEntries`, imports explícitos en
    `CameraScreen`/`TravelDiaryHomeScreen` (ojo: `cartographyGrid` es extensión de Modifier, se perdió al quitar el wildcard).
- ROADMAP pendiente: **Play Store release** (siguiente recomendado) y **sync backend** (grande, requiere /spec).

## Decisiones vigentes (detekt.yml)
- Compose exime: FunctionNaming/LongParameterList `ignoreAnnotated Composable`; LongMethod/CyclomaticComplexMethod excluyen `**/ui/**` y `**/navigation/**` (umbral 100).
- `MagicNumber` desactivado (en UI/dp/sp es ruido); `WildcardImport` excluye `androidx.compose.*`; `ReturnCount` max 5 + guard clauses;
  `TooGenericExceptionCaught` permite `Exception`/`RuntimeException` (sigue prohibiendo `Throwable`/`Error`); `MaxLineLength` 140 menos `SampleData.kt`.
- ktlint: no desactivar `discouraged-comment-location` (rompe `if-else-wrapping` y el worker falla con "failed to parse" engañoso).
- Navigation fijada en **2.8.9**; PDF con `PdfDocument` del framework A4 595x842pt; Xiaomi: popups con `input swipe` corto, `adb -s <serial>`.
- En modo selección, tarjetas del carrusel navegan; selección en filas "Todos los Viajes" o "Todo".

## Bloqueos
- Ninguno.

## Deuda técnica
- Rama `chore/lint-detekt-ktlint` sin borrar por referencia. `develop` sin push de los 2 commits de lint.
- PDF: descripción larga con "…" y foto real sin evidencia visual. `popBackStack<Home>`, `onEntryNotFound`, `capturedImageUri` sin ejercicio manual.
- Pre-Play Store: privacidad/Data Safety, keystore/R8/AAB. UI tests extra, CameraScreen, clustering.
