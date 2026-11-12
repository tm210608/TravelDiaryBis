# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-12 | **Rama:** `feature/play-store-release` (3 commits locales, sin push) | 169 tests JVM verdes

## Hecho el 2026-11-12
- **Material gráfico de Play Store COMPLETO** en `docs/play-store/`:
  - `icon-512.png` (icono alta resolución) y `feature-graphic-1024x500.png` (banner).
  - `screenshots/` con 5 capturas reales del emulador (1080x2400): 01-home, 02-detalle,
    04-recuerdos, 05-perfil, 06-nueva-entrada.
- `PRIVACY_POLICY.md`: email de contacto real (`juanantoniogp92@gmail.com`). Falta publicarla en una URL.
- Datos de demo inyectados en el emulador vía `run-as` (6 entradas con fotos en
  `files/photos/` + BD `travel_database` semilla con el esquema Room v3). Técnica reutilizable.

## Bloqueo encontrado
- **El mapa no carga en `com.traveldiary.app`**: la MAPS_API_KEY restringida rechaza el nuevo
  applicationId ("Authorization failure / INVALID_ARGUMENT" en logcat). Hay que añadir en
  Google Cloud Console: paquete `com.traveldiary.app` + SHA-1 del upload-keystore (prod)
  y + SHA-1 del cert debug (pruebas). `screenshots/03-mapa.png` salió en blanco, repetir tras el arreglo.

## Siguiente paso
Pasos manuales del usuario en Play Console / Google Cloud:
1. Backup de `upload-keystore.jks` + `keystore.properties` (CRÍTICO, sin backup no hay updates).
2. Cuenta de dev (25 USD). Cuentas nuevas: ~12-20 testers x 14 días en prueba cerrada.
3. Restricción de la MAPS_API_KEY (ver bloqueo).
4. Publicar `PRIVACY_POLICY.md` en URL pública (GitHub Pages recomendado).
Luego subir AAB (`app/build/outputs/bundle/release/`) + gráficos a la ficha.

## Decisiones vigentes
- `applicationId` = `com.traveldiary.app`; namespace sigue `com.example.traveldiary`.
- En el emulador ("SocialChef_Test") convive otra app del usuario (`com.aistudio.socialchef.*`
  y un paquete `com.example.traveldiary` que NO es esta app). Instalar con `./gradlew installDebug`.
- Release APK tiene otra firma: probarlo en el móvil exige desinstalar debug = pierde datos locales.
- Data Safety honesto: lat/lon SÍ se envían a api.open-meteo.com y Maps SDK a Google.

## Deuda técnica
- `develop` y `feature/play-store-release` sin push. Rama `chore/lint-detekt-ktlint` sin borrar.
- `kotlinOptions.jvmTarget` deprecated (warning, migrar a compilerOptions DSL algún día).
- PDF: descripción larga con "…" y foto real sin evidencia visual; `popBackStack<Home>` etc. sin prueba manual.
- Captura del mapa pendiente (ver bloqueo).
