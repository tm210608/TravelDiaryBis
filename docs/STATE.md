# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-11 | **Rama:** `develop` (`6c90801`, pendiente push a origin, ~11 commits por delante) | 169 tests JVM verdes + instrumentados verdes en Xiaomi

## Hecho el 2026-11-11
- **Exportar a PDF implementado y verificado en Xiaomi** (`feature/exportar-pdf`, spec
  `docs/specs/exportar-pdf/SPEC.md` con todos los criterios marcados):
  - Menú Exportar: Guardar como ZIP / Guardar como PDF / Compartir ZIP / Compartir PDF.
  - PDF nativo (`android.graphics.pdf.PdfDocument`, sin deps nuevas): portada
    "REGISTRO EXPEDICIONARIO / DIARIO DE VIAJE" con stats de la selección
    (VIAJES·FAV·PAÍSES·CONT, KM Haversine, TRAMOS, DÍAS EN RUTA, GENERADO OFFLINE)
    + una página por entrada (foto mitad superior, eyebrow `ENTRADA NN · fecha · PAÍS`,
    meta `UBICACIÓN · TAG`, descripción recortada, footer `PÁGINA NN DE NN`).
    Orden `date` ASC. Foto ilegible/http -> bloque "SIN FOTO", el PDF no falla.
  - Piezas: `PdfCoverData`+`buildPdfCover` (domain puro), `PdfExporter` (interfaz),
    `ExportPdfUseCase` (saveToUri/toTempPdf), `PdfDocumentExporter` (Canvas/Paint/
    StaticLayout/inSampleSize), `exportPdfFileName`+`shareMimeType` en
    `ExportDocument`, `ShareReady(fileName, mimeType)`, launcher `CreateDocument("application/pdf")`.
  - Evidencia en `docs/specs/exportar-pdf/` (evidencia-01..09 + `diario-evidencia.pdf`
    real, `%PDF-1.4`, 37 KB). Compartir PDF -> chooser con nombre .pdf; ZIP sigue OK
    (regresión verificada guardando `traveldiary-export-20261111-2017.zip`).
- **Placeholder pasaporte verificado y mergeado** (`125eb05` -> develop):
  `ProfilePassportPlaceholderTest` instrumentado + evidencia-10 en
  `docs/specs/ui-pasaporte-y-contactos/`; ROADMAP fase 2 cerrada.

## Siguiente paso
- `feature/exportar-pdf` mergeada a `develop` (ff `6c90801`). Rama sin borrar por si hace falta referencia.
- ROADMAP pendiente: sync backend, Play Store release, detekt/ktlint.

## Decisiones vigentes
- Navigation fijada en **2.8.9** (no subir sin pedirlo: cambian requisitos de SDK).
- PDF sin librerías externas: `PdfDocument` del framework; páginas A4 595x842pt;
  una página por entrada (descripción larga se recorta, no desborda).
- Xiaomi/MIUI: `adb install` requiere "Instalar por USB" en el teléfono; los popups
  Compose no aceptan `input tap`, sí `input swipe` corto (~120ms); el móvil aparece
  duplicado en adb wireless (usar `-s <serial>`; desconectar el duplicado si molesta);
  se suspende rápido durante verificación manual (`stay_on_while_plugged_in` ayuda,
  ya restaurado a 0).
- En modo selección, las tarjetas del carrusel "Registro Destacado" NAVEGAN al
  detalle; la selección se hace en las filas de "Todos los Viajes" o con "Todo"
  (que solo responde cuando el StateFlow ya emitió).

## Bloqueos
- Ninguno.

## Deuda técnica
- Descripción larga con "…" y foto real renderizada: cubiertos en código/test
  instrumentado, sin evidencia visual (las 2 entradas reales no tienen descripción
  y sus fotos content:// no son legibles -> SIN FOTO correcto).
- `popBackStack<Home>` (tab Cuaderno), `onEntryNotFound`, `capturedImageUri`->EditEntry:
  compilados/verificados en código, sin ejercicio manual.
- Pre-Play Store: privacidad/Data Safety, keystore/R8/AAB.
- UI tests adicionales, CameraScreen, clustering, detekt/ktlint.
