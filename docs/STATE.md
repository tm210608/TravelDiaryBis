# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-02 | **Rama:** `develop` (`c96787f`, en sync con origin) | 156 tests JVM verdes

## Hecho el 2026-11-02
- **PR #31 mergeado a `develop`**: cuadrícula cartográfica verificada en Xiaomi físico
  (`2412DPC0AG`, adb-WiFi): Home y Detalle, claro y oscuro — 4/4 capturas bien.
  Merge local `9b98e54` + push (cierra el PR en GitHub).
- 5 PNG de evidencias dark-mode commiteados a develop (`7c08202`).
- **Spec 3 del plan Stitch `ui-polish-concepts` implementada, verificada y mergeada**
  (`c96787f`). Pulido editorial completo sobre datos reales:
  - Cabecera Home: `ARCHIVO CARTOGRÁFICO · NN EXPEDICIONES` + `ESCALA 1:24.000 · FOLIO 001`.
  - Chips con contador real `(NN)` respetando búsqueda; placeholder nuevo.
  - Filas: eyebrow `TAG · PAÍS`; destacada: `GPS LOCK`, descripción y **meteo real
    Open-Meteo** (forecast ≤92d / archive >92d, sin key, cache en memoria, timeout 5s,
    fallo silencioso offline — verificado: `21°C·40%`/`19°C·88%` online, ausente offline).
  - Pie de archivo tipográfico; nav metafórico `Cuaderno/Rutas/Satélite/Índice`.
  - Perfil: `NN DÍAS EN RUTA` + `NN VISADOS REGISTRADOS`.
  - Fixes en ruta: avatar estrangulado por columna ancha (`weight(1f)`), meteo como
    prefijo (el sufijo quedaba ellipsizado).
  - Evidencia en `docs/specs/ui-polish-concepts/` (evidence_01..08).

## Siguiente paso
- Bug `exportRequested` pegajoso (deuda conocida): volver a Home re-activa el modo
  selección. Fix candidato: consumir el arg de nav una vez (`savedStateHandle` remove
  o `LaunchedEffect` con flag).
- Si se quiere otra spec: ROADMAP pendiente = nav type-safe, sync backend, Play Store
  release, PDF export, detekt/ktlint.
- Ramas viejas existentes (`feature/cuadricula-visible`, `feature/ui-pasaporte-y-contactos`,
  `feature/filter-chips`, `feature/home-filter-chips`): borrar SOLO con confirmación.

## Decisiones vigentes
- Dualidad visual (sin dynamic color): Claro = Cuaderno de Campo; Oscuro = Atlas Nocturno.
- Metáfora física: billete + cuadrícula + telemetría mono + sellos/carrete + nav metafórico.
- Meteo: Open-Meteo gratis sin key; aditivo opcional, nunca rompe offline-first.
- `local.properties` no se commitea. Push solo con confirmación (salvo mantener
  `origin/develop` al día tras merges de specs verificadas). Commits `feat|fix|test|docs|chore:`.
- Usuario pidió: al ofrecer opciones, incluir siempre recomendación propia.

## Bloqueos
- Ninguno. Nota operativa: `svc wifi disable` en el Xiaomi mata adb-WiFi Y apaga la
  depuración inalámbrica (hay que reactivarla a mano y rota el puerto — leer IP:puerto
  en Ajustes > Opciones de desarrollo > Depuración inalámbrica). Para pruebas offline:
  script `nohup` on-device que desconecte radios, capture a /sdcard y reconecte WiFi.

## Deuda técnica
- `exportRequested` arg pegajoso (ver arriba).
- Descripción en FeaturedCard implementada pero no observada (entradas sin descripción).
- Modo selección de exportación no re-probado tras el pulido editorial.
- Placeholder de 0 sellos y Empty/Loading de Recuerdos: implementados, sin evidencia.
- Pre-Play Store: privacidad/Data Safety, keystore/R8/AAB.
- UI tests Compose, sync online, nav type-safe, CameraScreen (`LocalLifecycleOwner`),
  clustering, detekt/ktlint.
