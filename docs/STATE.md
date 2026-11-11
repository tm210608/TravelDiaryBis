# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-11 | **Rama:** `feature/play-store-release` (2 commits locales, sin push) | 169 tests JVM verdes

## Hecho el 2026-11-11 (tarde)
- **Release Play Store preparado**: `applicationId` -> `com.traveldiary.app` (Play rechaza com.example.*).
- **Keystore de subida generado**: `upload-keystore.jks` (alias `upload`, RSA 2048, 30 años) + `keystore.properties`
  (contraseñas aleatorias). Ambos en `.gitignore`. **PENDIENTE: copia de seguridad por el usuario.**
- `build.gradle.kts`: signingConfig release desde keystore.properties, `isShrinkResources = true`.
- ProGuard corregido: entidad real `data.local.TravelEntryEntity` + keep RoomDatabase.
- **Verificado**: `assembleRelease` + `bundleRelease` firmados OK (APK 4,5 MB / **AAB 8,1 MB** en
  `app/build/outputs/bundle/release/`). verify.sh verde (169 tests, lint/detekt 0).
- **Docs creados en `docs/play-store/`**: PLAY_STORE_GUIDE.md (guía paso a paso), PRIVACY_POLICY.md
  (falta `[TU EMAIL DE CONTACTO]`), DATA_SAFETY.md (respuestas del cuestionario: ubicación "compartida"
  con Open-Meteo + Maps, resto no), STORE_LISTING.md (textos ficha + gráficos pendientes).

## Siguiente paso
Continuar guía Play Store: tomar capturas en el móvil para la ficha, que el usuario haga backup del
keystore, cuenta de dev (25 USD), restricción de la MAPS_API_KEY, publicar política (GitHub Pages).
Cuentas personales nuevas: ~12-20 testers x 14 días en prueba cerrada antes de producción.

## Decisiones vigentes
- Release APK tiene otra firma: probarlo en el móvil exige desinstalar debug = pierde datos locales.
- Data Safety honesto: lat/lon SÍ se envían a api.open-meteo.com y Maps SDK a Google (funcionalidad).
- detekt/ktlint en verde y en verify.sh; Navigation 2.8.9; Xiaomi popups con `input swipe`.
- En modo selección, tarjetas del carrusel navegan; selección en filas "Todos los Viajes" o "Todo".

## Bloqueos
- Ninguno técnico. Pasos de Play Console son manuales del usuario (cuenta, pago, verificación).

## Deuda técnica
- `develop` y `feature/play-store-release` sin push. Rama `chore/lint-detekt-ktlint` sin borrar.
- `kotlinOptions.jvmTarget` deprecated (warning, migrar a compilerOptions DSL algún día).
- PDF: descripción larga con "…" y foto real sin evidencia visual; `popBackStack<Home>` etc. sin prueba manual.
- Gráficos de tienda sin hacer: icono 512, feature graphic 1024x500, capturas.
