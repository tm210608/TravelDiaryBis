# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-12 | **Rama:** `feature/pulido-ux` (6 commits locales, sin push; basada en `feature/play-store-release`) | 175 tests JVM verdes | verify.sh OK

## Hecho el 2026-11-12 — feature "pulido-ux" implementada y verificada
SPEC: `docs/specs/pulido-ux/SPEC.md` (todos los criterios `[x]` salvo 1 `[~]`). Evidencia: `docs/specs/pulido-ux/evidence/` (10 capturas).
- **Splash de marca** (papel cuadriculado + logo + fade ~1s) tras el splash del sistema; icono launcher regenerado desde `docs/play-store/icon-512.png` (OJO: ese PNG es un play-button naranja sobre azul — valorar diseño propio antes de subir a Play).
- **Home**: cabecera en tarjeta separada del fondo, bottom bar mismo estilo, FAB "Nueva entrada", CTA "Crear tu primer viaje" en vacíos; pestaña Satélite→**Recuerdos**; "Trip" residual quitado de Etiqueta.
- **Galería multi-foto**: picker de Android copia a `files/photos/`; 1ª foto = portada (`imageUrl`), resto en `photoUris` (JSON, Room v3→v4 migración no destructiva); detalle muestra portada + rejilla. Export ZIP incluye photoUris (compatible con ZIPs viejos).
- **Perfil editable**: `UserProfile` en DataStore (nombre+foto); avatar grande con inicial por defecto; tocar avatar → `EditProfileScreen`.
- **Mapa → OpenStreetMap (osmdroid)**: `MapScreen` con `MapView`+`AndroidView` como pestaña con bottom bar; marcadores, tap→detalle, línea de ruta, `CopyrightOverlay`, `userAgentValue=packageName` en `TravelDiaryApplication`. Eliminados maps-compose, play-services-maps y `MAPS_API_KEY` del manifest/gradle. `LAYER_TYPE_SOFTWARE` solo en emuladores (bug tiles AVD).
- **Bug real corregido**: el GPS nunca se adjuntaba (permiso pedido async y `hasLocationPermission` stale); además entradas sin coords no podían ganar GPS al editar. Ahora Guardar espera el permiso y usa `getFreshLocation()`.

## Verificado en dispositivo
Móvil físico `2412DPC0AG` por WiFi adb (`adb-MVVSE6A6S4IVQ8MJ-YvIufY...`, IP 192.168.31.99): splash, home, galería 2 fotos→portada+rejilla, marcador→detalle, GPS 39.9640°N 4.8351°W, tiles OSM cargando. Emulador-5556: atribución OSM, entrada con geo fix.
**Pendiente menor**: guardar nombre/foto de perfil a mano en dispositivo (cubierto por unit tests).

## Siguiente paso
Pasos manuales del usuario en Play Console / Google Cloud:
1. Backup de `upload-keystore.jks` + `keystore.properties` (CRÍTICO).
2. Cuenta de dev (25 USD). Cuentas nuevas: ~12-20 testers x 14 días en prueba cerrada.
3. ~~Restricción de la MAPS_API_KEY~~ — YA NO NECESARIO: el mapa es OSM, sin API key.
4. Publicar `PRIVACY_POLICY.md` en URL pública (GitHub Pages recomendado).
5. Rehacer captura `docs/play-store/screenshots/03-mapa.png` con el mapa OSM real.
Luego merge/push de la rama y subir AAB (`app/build/outputs/bundle/release/`) + gráficos.

## Decisiones vigentes
- `applicationId` = `com.traveldiary.app`; namespace `com.example.traveldiary`. Paquete en adb: `com.traveldiary.app`.
- Rama `feature/pulido-ux` basada en `feature/play-store-release` (conserva release config), no en `develop`.
- Otras apps del usuario conviven en sus dispositivos (`com.talaveracity.app` = TalaGo, `com.aistudio.socialchef`); verificar `topResumedActivity` antes de capturas adb.
- Release APK tiene otra firma: probarlo en el móvil exige desinstalar debug = pierde datos locales.
- Data Safety: lat/lon se envían a api.open-meteo.com; ya NO a Google Maps.

## Deuda técnica
- `develop`, `feature/play-store-release`, `feature/pulido-ux` sin push. `chore/lint-detekt-ktlint` sin borrar.
- `kotlinOptions.jvmTarget` deprecated (warning).
- El usuario prefiere trabajar en una app a la vez; capturas adb pueden pillar otra app suya al frente.
- `screenshots-review/` sin commitear (material de revisión UX).
