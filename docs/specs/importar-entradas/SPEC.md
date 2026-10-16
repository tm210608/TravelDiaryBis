# SPEC: Importar entradas del diario

## Qué construimos
Desde Perfil, "Importar diario" abre el picker SAF (`OpenDocument`, mime zip), lee un
ZIP del formato exportado (`diary.json` `formatVersion=1` + `photos/`) y **fusiona**
las entradas en Room: se insertan como nuevas (id autoincrement) y sus fotos se copian
a `filesDir/photos` reescribiendo `imageUrl` al path local. Errores de archivo
informan al usuario sin cerrar nada.

## Criterios de aceptación (testeables, sin ambigüedad)
- [ ] Opción "Importar diario" en Perfil (bajo "Exportar diario") lanza `OpenDocument`
      con mimes `application/zip`, `application/x-zip-compressed`, `application/octet-stream`.
- [ ] ZIP válido: se insertan TODAS las entradas del documento con id nuevo (sin dedup;
      reimportar el mismo ZIP duplica). Campos preservados: title, location, country,
      date, tag, description, latitude, longitude, isFavourite.
- [ ] Cada `photoFile` se copia a `filesDir/photos/import-<yyyyMMdd-HHmmss>-<i>.<ext>`
      y `imageUrl` se reescribe a `file://<path>` (formato legible por `PhotoLoader`
      de la exportación -> round-trip funciona).
- [ ] `photoFile = null` o archivo ausente en el ZIP: la entrada se importa conservando
      el `imageUrl` original (http remoto sigue mostrándose).
- [ ] Fichero no-zip, sin `diary.json` o JSON malformado -> `Error` "No es un archivo
      de diario válido".
- [ ] `formatVersion != 1` (incluido ausente) -> `Error` "Versión de diario no soportada".
- [ ] `entries` vacío -> `Error` "El archivo no contiene entradas".
- [ ] Éxito: snackbar "N entradas importadas"; las nuevas aparecen en Home al volver.
- [ ] Error: snackbar con el mensaje; el diario queda intacto (nada parcial salvo fotos
      ya copiadas, aceptado como residuo inocuo).
- [ ] Cancelar el picker -> vuelve a Perfil sin error.
- [ ] Las entradas existentes NO se tocan (merge puro, nada se borra ni modifica).

## Fuera de alcance
- Reemplazar el diario (destructivo), dedup por contenido, uuid por entrada,
  vista previa del contenido antes de importar, importación en segundo plano con
  WorkManager, formatos != ZIP del propio exportador, formatVersion > 1.

## UI states
- `sealed interface ImportUiState { Idle, Importing, Success(count: Int),
  Error(message: String) }` en `ProfileViewModel` (separado de `uiState` de stats).
  `Importing` muestra progreso en la opción; `Success`/`Error` -> snackbar y reset a Idle.

## Navegación
- Sin rutas nuevas. Perfil -> `OpenDocument` (activity result); el Uri se pasa al
  ViewModel. Permiso de lectura one-shot del SAF (sin persist).

## Arquitectura
- `domain/model/ExportDocument.kt`: se REUSA tal cual (contrato v1) + `ImportedEntry`
  no necesario (se mapea a `TravelEntry(id=0, ...)`).
- `domain/export/DiaryImporter.kt`: `suspend fun import(input: InputStream,
  savePhoto: (fileName: String, bytes: ByteArray) -> String?): ImportResult`.
- `domain/export/ImportResult.kt`: sealed `Success(entries: List<TravelEntry>) /
  InvalidFile / UnsupportedVersion / Empty`.
- `domain/export/ImportStorage.kt`: `openInputStream(sourceUri): InputStream?` y
  `savePhoto(fileName, bytes): String?` (devuelve `file://path` nuevo).
- `domain/usecase/ImportEntriesUseCase.kt`: `invoke(sourceUri, ): ImportResult`
  abre stream, importa, inserta cada entrada vía `repository.insertEntry`.
- `data/export/DiaryImporterImpl.kt`: ZipInputStream + `Json.decodeFromString`
  `ignoreUnknownKeys=true` (tolerante a campos futuros); puro, testeable en JVM.
- `data/export/ImportStorageImpl.kt`: ContentResolver openInputStream;
  `filesDir/photos/` + nombre `import-…`; DI en `di/ExportModule.kt`.
- `ui/viewmodel/ProfileViewModel.kt`: `importState` + `importDiary(uri)` +
  `onImportStateConsumed()`.
- `ui/screens/ProfileScreen.kt`: opción "Importar diario" + launcher SAF + snackbar.

## Permisos, lifecycle y persistencia
- Sin permisos nuevos (SAF one-shot). Rotación: `importState` vive en el ViewModel;
  el picker SAF es activity-result y sobrevive.
- Import en `Dispatchers.IO`, streaming (no cargar fotos enteras salvo bytes por foto).
- Escritura Room solo `insertEntry` por entrada; fotos solo se añaden (nada se borra).

## Plan de tests
- [ ] build: `scripts/verify.sh`
- [ ] unit `DiaryImporterImpl` (JVM, savePhoto fake): parseo v1, fotos extraídas y
      `imageUrl` reescrito, `photoFile=null`/ausente conserva `imageUrl`, no-zip ->
      InvalidFile, version!=1 -> UnsupportedVersion, entries vacío -> Empty.
- [ ] unit `ImportEntriesUseCase`: inserta cada entrada (coVerify), cuenta devuelta,
      error del storage -> InvalidFile.
- [ ] unit `ProfileViewModel`: importDiary -> Success/Error; consume -> Idle.
- [ ] dispositivo: `scripts/verify.sh --device` + round-trip: exportar diario ->
      Importar el MISMO zip -> +N entradas duplicadas (merge sin dedup esperado) ->
      fotos visibles offline; zip corrupto -> error; cancelar picker -> sin error.
- [ ] evidencia: capturas en `docs/specs/importar-entradas/`

## Edge cases
- Reimportar el mismo ZIP duplica entradas (decisión: sin dedup esta versión).
- `imageUrl` file:// de OTRO dispositivo sin photoFile -> roto visualmente (aceptado).
- Fotos copiadas aunque la inserción falle a medias (residuo inocuo en photos/).
- ZIP con entradas extra/`diary.json` con campos desconocidos -> se ignoran (tolerante).
- Import durante selección activa en Home: independiente (está en Perfil).
