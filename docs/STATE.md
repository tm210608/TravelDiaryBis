# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-13 | **Rama:** `feature/extension-postal` (NO pusheada; `main` al día en origin con rediseno-postal mergeado) | 167 tests JVM verdes | verify.sh OK | SPEC verificada con evidencia

## Hecho el 2026-11-13 — feature "extension-postal" implementada y verificada
SPEC: `docs/specs/extension-postal/SPEC.md` (21 criterios `[x]`). Evidencia: `docs/specs/extension-postal/evidence/` (12 capturas).
- **Detalle**: foto hero 400dp (o degradado+icono si no hay foto), título serif, "Ciudad, País · fecha", "Ver mapa" discreto (mismo geo intent a GMaps), galería LazyRow, "La Historia" serif, tarjeta compartir. Fuera: TALÓN DE EXPEDICIÓN, GPS crudo, chip de tag, fondo cuadriculado, TicketDivider.
- **Perfil**: `PostalHeader` ("Perfil" + avatar→editar) reutilizable en `CommonComponents.kt` (extraído del Home); tarjeta stats cálida (nombre serif + "N viajes · favoritos · países · continentes" + km/días/tramos sin mono); Visados y Sellos de Ruta **conservados** tal cual (encajan con postal); export/import igual.
- **Recuerdos**: rejilla 2 cols de `MemoryPostalCard` (postal foto-led 1:1 + título + fecha). Borrado `FilmStrip.kt` (film strip + números de fotograma) — código muerto.
- **Formularios** (Nueva entrada, Editar entrada, Editar perfil): títulos TopAppBar en `SerifCardTitle`; campos ya eran cálidos, sin más cambios.
- **Type.kt**: nuevo `SerifScreenTitle` (28sp) entre `SerifAppTitle` y `SerifCardTitle`.
- Commits: `2f264cc` spec, `4d012a8` implementación, `632e4e7` spec verificada.
- Verificado en `emulator-5554` (Medium_Phone_API_36.1) con 8 entradas mock y fotos reales de Wikimedia inyectadas por `run-as`+base64 (ver comando en decisión). Home/Recuerdos/Perfil/Detalle/Detalle-sin-foto/Nueva-entrada/Editar-perfil capturados; oscuro y landscape en Recuerdos y formulario.

## Siguiente paso
1. **Usuario revisa** el diseño completo en el emulador (app instalada en emulator-5554 con datos mock) — si le gusta: merge a `main` + push con su aprobación.
2. **Instalar en su móvil** cuando lo libere (el build con postal-1 ya quedó instalado por accidente cuando installDebug pilló el WiFi adb).
3. Play Store (sin cambios): backup keystore, cuenta dev 25 USD, GitHub Pages (`main` + `/docs` → `privacy.html`), AAB en `app/build/outputs/bundle/release/` (recopilar si se publica con postal).

## Decisiones vigentes
- `applicationId`=`com.traveldiary.app`; launcher `com.example.traveldiary.MainActivity`.
- Mock data en AVD: fotos → `run-as com.traveldiary.app sh -c 'base64 -d > files/x.jpg'` (stdin); DB sqlite → `cat db | run-as ... 'cat > databases/travel_database'` + borrar wal/shm; identity_hash de `TravelDatabase_Impl.kt` generado.
- `screenshots-review/` en `.gitignore` (scripts de mock + capturas scratch; `prep.py`/`build_db.py` reutilizables).
- adb con múltiples dispositivos: usar `-s` siempre; el móvil se reconecta solo por WiFi; `uiautomator dump` para coordenadas exactas (los taps a ciegas fallan); navegación tarda ~2s — esperar antes de screencap.
- Emulator-5556 (Samsung) murió por RAM al tener 2 AVDs arriba; el 5554 se reinició una vez pero sobrevive.

## Deuda técnica
- `develop`, `feature/play-store-release` sin push; `chore/lint-detekt-ktlint` sin borrar.
- ANR en emulador-5556 (2026-11-13, arranque en frío): vigilar en físico.
- Detalle mantiene `geo:` intent hacia Google Maps (comportamiento heredado) — evaluar abrir el mapa interno (OSM) en el futuro.
- `kotlinOptions.jvmTarget` deprecated (warning).
