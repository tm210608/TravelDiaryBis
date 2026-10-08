# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-10-08 | **Rama:** `develop` | **Commit:** `3164671` mergeado — Atlas nocturno

## Hecho
- Filter chips y tabs Recuerdos/Perfil implementados y mergeados; 66 tests antes de las features siguientes.
- Editar entrada existente: `Detail → Edit`, formulario precargado compartido con creación,
  fecha/favorito editables, foto conservar/reemplazar/quitar, confirmación GPS y actualización
  por id existente (Room reporta filas afectadas). Capturas en `docs/specs/edit-entry/`.
- Tema oscuro global Atlas nocturno: paleta semántica, sigue Android, sincroniza barras del
  sistema. Home/Añadir/Detalle/Mapa/Recuerdos/Perfil y cambio de tema conservando formulario
  verificados en Xiaomi. Evidencias principales en `docs/specs/dark-mode/`.
- Build y tests: `scripts/verify.sh` = 79 tests, 0 fallos; `scripts/verify.sh --device`
  instala y lanza sin crash.
- Pendiente en dispositivo: CameraScreen, actualización GPS/permiso denegado y cobertura clara total;
  tabs: estado vacío de Recuerdos y refresco de estadísticas. No se concedió permiso ni se alteraron coordenadas.

## Siguiente paso
1. Nueva feature SDMD desde `develop`: candidatas navegación type-safe o exportar viaje a PDF/imagen.
   Flujo: `/spec` → aprobación → `/implement-spec` → `/verify` → `/handoff`.
2. Antes de limpiar, revisar los artefactos aún sin seguimiento: `docs/specs/m3-expressive-redesign/`
   y capturas auxiliares no añadidas bajo `docs/specs/dark-mode/`; no borrar sin confirmación.
3. Ramas de features mergeadas pueden borrarse solo con confirmación (`feature/map-view`,
   `feature/filter-chips`, `feature/bottom-tabs`, `feature/edit-entry`, `feature/dark-mode`).

## Decisiones vigentes
- `maps-compose` 4.4.2; no subir a 9.x sin pedirlo (requiere compileSdk 37/AGP 9.1).
- Chips de continente derivados de `country` con tabla offline; país desconocido solo en Todo.
- Recuerdos muestra entradas con foto; Perfil solo lectura; edición de perfil futura.
- Dark mode sigue al sistema; paleta oscura Atlas nocturno; sin selector propio.
- `local.properties` con `MAPS_API_KEY` no se commitea. Commits feature atómicos y merge `--no-ff`.

## Bloqueos
- Ninguno.

## Deuda técnica
UI tests Compose, nav type-safe, exportar viaje, verificación Camera/GPS/pantallas claras/tabs, edición foto/cámara,
fixes de cámara, chips África/Oceanía, clustering, subir AGP/compileSdk, detekt/ktlint, iconos ArrowBack deprecados.
