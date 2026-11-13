# Guía de subida a Play Store — Travel Diary

Resumen de lo ya hecho (11-11-2026, rama `feature/play-store-release`):

- applicationId: `com.traveldiary.app`
- Keystore de subida: `upload-keystore.jks` (alias `upload`, validez 30 años)
- Contraseñas: `keystore.properties` (ambos archivos están en .gitignore)
- Release con R8 + shrinkResources. Verificado:
  `app-release.apk` (4,5 MB) y `app-release.aab` (8,1 MB) en
  `app/build/outputs/bundle/release/` y `.../apk/release/`
- verify.sh en verde (169 tests, ktlint/detekt 0 avisos)

---

## PASO 0 — Copia de seguridad del keystore (HAZLO PRIMERO)

Copia `upload-keystore.jks` y `keystore.properties` a un sitio seguro
(gestor de contraseñas, disco externo, nube cifrada). Si activas Play App
Signing (recomendado) Google puede restablecer una clave perdida, pero el
backup igualmente es obligatorio de facto.

## PASO 1 — Cuenta de desarrollador

1. Ir a https://play.google.com/console
2. Crear cuenta Google Play Developer: pago único de **25 USD**
3. Verificación de identidad (DNI/pasaporte; puede tardar 1-2 días)
4. Desde 2024-2025, cuentas personales nuevas deben hacer una
   **prueba cerrada con al menos 12-20 testers durante 14 días** antes de
   publicar en producción. Planifica con tiempo.

## PASO 2 — Crear la app

1. Console → "Crear aplicación"
2. Nombre: **Travel Diary** | Idioma: Español | Tipo: Aplicación | Gratuita
3. Aceptar declaraciones (Política de contenido, leyes de exportación)

## ~~PASO 3 — Restringir la API key de Maps~~ (YA NO ES NECESARIO)

El mapa se migró a **OpenStreetMap (`osmdroid`)**, por lo que no hay API key ni SDK de Google Maps. Puedes saltarte este paso.

## PASO 4 — Política de privacidad

1. Publicar `docs/play-store/PRIVACY_POLICY.md` en una URL pública:
   opción fácil → subirlo al repo en GitHub y activar GitHub Pages, o
   pegarlo en un gist/página. Antes, sustituir `[TU EMAIL DE CONTACTO]`.
2. Poner la URL en Play Console → Política de la app → Política de
   privacidad.

## PASO 5 — Ficha de tienda

Copiar textos de `docs/play-store/STORE_LISTING.md`. Subir:
- Icono 512×512, imagen destacada 1024×500, capturas (mín. 2).

## PASO 6 — Cuestionarios obligatorios

- **Seguridad de los datos:** seguir `docs/play-store/DATA_SAFETY.md`.
- **Clasificación de contenido:** cuestionario IARC → app de viajes, sin
  violencia/juegos → clasificación PEGI 3 / Everyone probable.
- **Público objetivo:** 18+ (o "todos los públicos" si prefieres; evita
  marcar menores de 13 para no entrar en política familiar).
- **Noticias / apps COVID / salud:** No a todo.

## PASO 7 — Firma y primera subida

1. Play Console → tu app → **Pruebas → Prueba cerrada** → Crear versión
   (o directamente Producción si tu cuenta lo permite)
2. Al subir el primer AAB, Google ofrece **Play App Signing** →
   **ACTÍVALO** (Google guarda la clave definitiva; tu upload key solo
   firma las subidas)
3. Subir `app/build/outputs/bundle/release/app-release.aab`
4. Nombre de versión: 1.0 | Notas de versión: "Primera versión."
5. Revisar → Publicar

## PASO 8 — Prueba cerrada (si tu cuenta es personal nueva)

1. Crear lista de testers (correos Gmail, 12-20 personas)
2. Compartir el enlace de prueba; esperar los 14 días requeridos
3. Luego "Promover versión" → Producción

## Regenerar el AAB en el futuro

```
./gradlew bundleRelease
```
(subir `versionCode` y `versionName` en `app/build.gradle.kts` en cada
release nueva)

## Aviso sobre probar el release en tu móvil

El APK release tiene otra firma: para instalarlo hay que **desinstalar la
versión debug primero, y se pierden las entradas locales** (expórtalas
antes si quieres conservarlas). Alternativa sin pérdida: probar el AAB
en la prueba interna de Play Console o en un emulador.
