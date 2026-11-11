# SPEC: Exportar diario a PDF

> SDMD — spec tras preguntas de alcance (contenido: portada + página por entrada;
> flujo: mismo que el ZIP; con fotos; guardar y compartir). Complementa al ZIP
> técnico con una variante legible para humanos, 100% offline.

## Qué construimos

El menú "Exportar" del modo selección gana el formato **PDF**: mismo flujo que
el ZIP (seleccionar entradas -> Guardar en dispositivo / Compartir). El PDF se
genera con `android.graphics.pdf.PdfDocument` (API del framework, sin
dependencias nuevas): **portada** estilo registro expedicionario con resumen de
la selección (viajes, favoritos, países, continentes, KM Haversine) y **una
página por entrada** con foto grande, fecha, ubicación, tag y descripción.
Orden cronológico ascendente, como se lee un diario.

## Criterios de aceptación (testeables, sin ambigüedad)

- [x] El dropdown "Exportar" del modo selección ofrece 4 opciones:
      "Guardar como ZIP", "Guardar como PDF", "Compartir ZIP", "Compartir PDF".
      Las dos de ZIP conservan exactamente el comportamiento actual.
- [x] "Guardar como PDF" abre el picker SAF con MIME `application/pdf` y nombre
      sugerido `traveldiary-diario-<yyyyMMdd-HHmm>.pdf`; al elegir destino se
      escribe el PDF y aparece el snackbar "Diario exportado".
- [x] "Compartir PDF" genera el PDF en `cacheDir/exports`, lo comparte vía
      `FileProvider` con `Intent.ACTION_SEND` y MIME `application/pdf` (el ZIP
      sigue con `application/zip`).
- [x] Portada (página 1): eyebrow "REGISTRO EXPEDICIONARIO", título
      "DIARIO DE VIAJE", línea de stats `VIAJES NN · FAV NN · PAÍSES NN ·
      CONT NN`, "KM RECORRIDOS" con `NN KM · NN TRAMOS` y fecha
      `GENERADO OFFLINE · dd MMM yyyy`. Las stats se calculan SOLO sobre las
      entradas seleccionadas (no todo el diario).
- [x] Una página por entrada, orden `date` ASC: foto en la mitad superior
      (escalada recortando al centro), eyebrow `ENTRADA NN · dd MMM yyyy ·
      PAÍS` (omite `· PAÍS` si `country` está en blanco), título, línea
      `UBICACIÓN · TAG`, descripción multilínea recortada con "…" si excede el
      espacio, y footer `PÁGINA NN DE NN`.
- [x] Entrada sin foto, foto `http(s)` o bytes no decodificables -> rectángulo
      gris con texto "SIN FOTO" centrado (no bloquea el PDF).
- [x] `PdfExporter` escribe un PDF válido: test instrumentado lo reabre con
      `android.graphics.pdf.PdfRenderer` y afirma `pageCount == entradas + 1`.
- [x] `ExportUiState.ShareReady` lleva `mimeType` para que el intent use el tipo
      correcto (derivado de `shareMimeType(fileName)` en domain).
- [x] Sin dependencias nuevas en `build.gradle.kts`; generación en
      `Dispatchers.IO`; el `LinearProgressIndicator` existente cubre el progreso.

## Fuera de alcance

- Índice/página de contenidos, mapa de la ruta, datos meteorológicos.
- Elegir portada, autor o tipografías; paginación de entradas largas en varias
  páginas (la descripción se recorta).
- PDF/A, firma digital, contraseña/cifrado, impresión vía `PrintManager`.
- Selector de orientación/tamaño de página (fijo A4 vertical 595x842 pt).
- Previsualización dentro de la app.
- Compartir como imágenes en vez de PDF.

## UI states

- `ExportUiState` existente se reutiliza: `Idle`, `Exporting`,
  `ShareReady(fileName, mimeType)` (campo nuevo), `Success(fileName)`,
  `Error(message)`. Sin estados nuevos ni navegación nueva.

## Navegación

Sin rutas nuevas. Todo ocurre en `Home` (modo selección) usando los launchers
SAF existentes + uno nuevo `CreateDocument("application/pdf")`.

## Arquitectura

- `domain/model/ExportDocument.kt` — añade `exportPdfFileName(now): String`
  (`traveldiary-diario-<ts>.pdf`) y `shareMimeType(fileName): String`
  (`.pdf`->pdf, `.zip`->zip, resto->`application/octet-stream`).
- `domain/model/PdfCoverData.kt` — `data class` con las stats de portada +
  `buildPdfCover(entries): PdfCoverData` usando `continentOf`,
  `totalDistanceKm` y `daysWithEntries` (domain puro, testeable en JVM).
- `domain/export/PdfExporter.kt` — interfaz análoga a `DiaryExporter`:
  `suspend fun export(entries, cover, photoFor, out, exportedAt)`.
- `domain/usecase/ExportPdfUseCase.kt` — `saveToUri(ids, destinationUri)` y
  `toTempPdf(ids)`; ordena por `date` asc antes de llamar al exporter.
- `data/export/PdfDocumentExporter.kt` — impl con `PdfDocument`/`Canvas`/
  `Paint`/`StaticLayout`/`BitmapFactory` (decode con `inSampleSize` para no
  reventar memoria; `Typeface.MONOSPACE` para las líneas mono de marca).
- `di/ExportModule.kt` — `providePdfExporter()` + `ExportPdfUseCase` por @Inject.
- `ui/viewmodel/HomeViewModel.kt` — `exportPdfSelectedTo(uri, suggestedName)` y
  `sharePdfSelected()`; `ShareReady` con `mimeType`.
- `ui/screens/TravelDiaryHomeScreen.kt` — launcher PDF SAF, dropdown con 4
  opciones, `intent.type = state.mimeType`.

## Permisos, lifecycle y persistencia

- Sin permisos nuevos: SAF y FileProvider ya configurados (`file_paths.xml`
  cubre `cacheDir/exports`).
- Generación en `Dispatchers.IO`; estado sobrevive rotación via ViewModel.
- Solo lectura sobre Room; no escribe datos de la app.

## Plan de tests

- [x] build: `scripts/verify.sh` — BUILD+TESTS OK (169 tests unitarios)
- [x] unit `ExportPdfFileNameTest`: formato `traveldiary-diario-<ts>.pdf`,
      fecha fija determinista.
- [x] unit `ShareMimeTypeTest`: `.pdf`->`application/pdf`,
      `.zip`->`application/zip`, otro->`application/octet-stream`.
- [x] unit `PdfCoverTest`: stats calculadas solo sobre la selección (2 países,
      km entre tramos con GPS consecutivos, días únicos).
- [x] unit `ExportPdfUseCaseTest`: exporter fake captura orden -> `date` asc y
      solo ids seleccionados.
- [x] instrumentado `PdfDocumentExporterTest`: genera PDF con 2 entradas (1 sin
      foto) -> `PdfRenderer.pageCount == 3`, página 1 renderiza bitmap no vacío.
- [x] dispositivo (Xiaomi 2412DPC0AG): 2 entradas -> Guardar como PDF ->
      `traveldiary-diario-20261111-2000.pdf` (37 KB, `%PDF-1.4`) en Descargas;
      abierto en visor: portada (VIAJES 02 · FAV 00 · PAÍSES 02 · CONT 01,
      612 KM, GENERADO OFFLINE), ENTRADA 01 "Prueba Mapa" pág 2, ENTRADA 02
      "Con GPS" pág 3 (orden asc), ambas con bloque SIN FOTO (fotos
      content:// no legibles -> fallback correcto). Compartir PDF -> chooser
      con `traveldiary-diario-20261111-2014.pdf`; ZIP sigue funcionando
      (`traveldiary-export-20261111-2017.zip`, regresión OK).
- [x] evidencia: capturas en `docs/specs/exportar-pdf/` (evidencia-01..09 +
      `diario-evidencia.pdf`).

## Edge cases

- 0 entradas seleccionadas -> botón "Exportar" deshabilitado (ya existente).
- Descripción muy larga -> se recorta con "…" en la página (una página por
  entrada, nunca desborda).
- Foto enorme (12MP+) -> `BitmapFactory` con `inSampleSize` (~1600px máx);
  bitmaps reciclados por página.
- `content://` revocado o bytes corruptos -> bloque "SIN FOTO", PDF no falla.
- Nombre de archivo ya existente en SAF -> Android añade sufijo `(1)`.
- Entrada sin `country`/`location`/`tag`/`description` -> esas líneas se
  omiten en la página.
- 1 sola entrada seleccionada -> portada + 1 página (pageCount 2).
- Rotación con el picker abierto o durante la generación -> estado en VM.
