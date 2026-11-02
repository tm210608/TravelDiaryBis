# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-02 | **Rama:** `develop` (`eefb66d`, pendiente push a origin) | 156 tests JVM verdes

## Hecho el 2026-11-02 (sesión 2)
- **Bug `exportRequested` pegajoso corregido y verificado en Xiaomi físico** (merge `eefb66d`,
  rama `fix/export-requested-sticky`). Causa: el arg nav `export=true` quedaba en el Bundle del
  backStackEntry y `LaunchedEffect(exportRequested)` re-disparaba `enterSelectionMode()` cada vez
  que Home re-entraba en composición. Fix: flag `exportHandled` con `rememberSaveable` en
  `TravelDiaryHomeScreen` (una vez por entry; entry nuevo con export=true vuelve a disparar).
- Verificación adb-WiFi (`adb-MVVSE6A6S4IVQ8MJ`): Perfil > Exportar diario > `seleccionadas`
  FOUND > Cerrar selección > Rutas > Volver > ABSENT; y Exportar > Cerrar > abrir entrada
  (Detalle) > Atrás > ABSENT. Antes del fix ese retorno re-activaba la selección.
- Notas operativas: adb-WiFi del Xiaomi rota el serial entre conexiones (apareció como
  `(2)._adb-tls-connect`); el patrón de bloqueo requiere al usuario; los emuladores 5554/5556
  tenían otras apps del usuario en primer plano.
- Helper adb útil: `export MSYS_NO_PATHCONV=1` (Git Bash rompe rutas /sdcard), `uiautomator dump`
  + `exec-out cat`, tap por centro de `bounds`.

## Siguiente paso
- ROADMAP pendiente: nav type-safe, sync backend, Play Store release, PDF export, detekt/ktlint.
- Ramas viejas existentes (`feature/cuadricula-visible`, `feature/ui-pasaporte-y-contactos`,
  `feature/filter-chips`, `feature/home-filter-chips`): borrar SOLO con confirmación.
- `fix/export-requested-sticky` ya mergeada: borrarla SOLO con confirmación.

## Decisiones vigentes
- Dualidad visual (sin dynamic color): Claro = Cuaderno de Campo; Oscuro = Atlas Nocturno.
- Metáfora física: billete + cuadrícula + telemetría mono + sellos/carrete + nav metafórico.
- Meteo: Open-Meteo gratis sin key; aditivo opcional, nunca rompe offline-first.
- `local.properties` no se commitea. Push solo con confirmación (salvo mantener
  `origin/develop` al día tras merges verificados). Commits `feat|fix|test|docs|chore:`.
- Args nav de un solo uso: consumir con flag `rememberSaveable` (patrón `exportHandled`).
- Usuario pidió: al ofrecer opciones, incluir siempre recomendación propia.

## Bloqueos
- Ninguno. Para pruebas offline: script `nohup` on-device que desconecte radios, capture a
  /sdcard y reconecte WiFi (`svc wifi disable` mata adb-WiFi y la depuración inalámbrica).

## Deuda técnica
- Descripción en FeaturedCard implementada pero no observada (entradas sin descripción).
- Placeholder de 0 sellos y Empty/Loading de Recuerdos: implementados, sin evidencia.
- Pre-Play Store: privacidad/Data Safety, keystore/R8/AAB.
- UI tests Compose, sync online, nav type-safe, CameraScreen (`LocalLifecycleOwner`),
  clustering, detekt/ktlint.
