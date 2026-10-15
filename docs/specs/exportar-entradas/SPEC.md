# SPEC: Exportar entradas del diario

## Qué construimos
El usuario selecciona recuerdos en Home (modo selección con checkboxes) y genera un
ZIP con `entries.json` (datos serializados) + `photos/` (fotos copiadas). El ZIP se
puede **guardar** donde elija el usuario (SAF `CreateDocument`) o **compartir**
(`ACTION_SEND` vía `FileProvider`). Accesible desde un icono en la top bar de Home y
desde la opción "Exportar diario" en Perfil, que abre Home ya en modo selección.
No hay importación: es exportación de solo escritura hacia fuera. El documento JSON
es un sobre versionado diseñado para ser también el DTO de una futura sincronización
online (sin tocar el esquema de Room en esta feature).

## Criterios de aceptación (testeables, sin ambigüedad)
- [ ] Icono de exportar visible en la top bar de Home solo cuando el diario tiene >= 1 entrada.
- [ ] Al activar modo selección: cada tarjeta muestra checkbox, tap alterna su selección,
      la top bar cambia a "N seleccionadas" con acciones "seleccionar todo" y "cerrar" (sale del modo).
- [ ] El botón/acción de exportar está deshabilitado con 0 entradas seleccionadas.
- [ ] Al exportar, el usuario elige entre **Guardar** (lanza `CreateDocument` con mime
      `application/zip` y nombre sugerido `traveldiary-export-<yyyyMMdd-HHmm>.zip`) y
      **Compartir** (lanza share sheet con `EXTRA_STREAM`, `FLAG_GRANT_READ_URI_PERMISSION`).
- [ ] El ZIP contiene `diary.json`, documento versionado:
      `{formatVersion: 1, app: "TravelDiaryBis", appVersion, exportedAt (ISO-8601),
      entryCount, entries: [...]}`; cada entrada con campos
      `id, title, location, country, date (ISO-8601), tag, description, latitude, longitude,
      isFavourite, imageUrl (valor original), photoFile (nombre dentro del ZIP o null)`.
- [ ] Las fotos se copian a `photos/entry-<id>-<n>.<ext>` resolviendo `imageUrl`:
      `content://`/`file://`/path absoluto via ContentResolver/File. URLs `http(s)` NO se
      descargan: `photoFile = null` y `imageUrl` conserva la URL.
- [ ] Si una foto no se puede leer, la exportación continúa y esa entrada queda con
      `photoFile = null` (no falla el ZIP entero).
- [ ] Compartir usa `FileProvider` (authority `${applicationId}.fileprovider`) sobre un ZIP
      temporal en `cacheDir/exports/`.
- [ ] "Exportar diario" en Perfil navega a Home con el modo selección ya activado.
- [ ] Cancelar el picker SAF vuelve al modo selección sin mostrar error.
- [ ] Exportación exitosa: sale del modo selección y muestra snackbar "Diario exportado".

## Fuera de alcance
- Sincronización online, backend, auth, uuid/identidad remota por entrada: su propia
  spec. Aquí solo queda el contrato `formatVersion` listo para reutilizar.
- Cambios de esquema de Room (sin columnas nuevas ni migraciones).
- Importar/restaurar (otra feature/spec).
- Formatos PDF, Markdown o exportación "bonita" para lectura.
- Exportación automática/programada, cifrado, subida a Drive/nube.
- Selección por filtros (chips país/tag/fecha) dentro del modo selección.

## UI states
- Selección (dentro del estado de `HomeViewModel`): `selectionMode: Boolean`,
  `selectedIds: Set<Int>`. Sin modo selección todo se comporta como hoy.
- `sealed interface ExportUiState { Idle, Exporting, ShareReady(fileName: String),
  Success(fileName: String), Error(message: String) }` en `HomeViewModel`.
  `ShareReady` lleva el nombre del ZIP temporal en caché; la screen construye el
  intent con `FileProvider.getUriForFile`.

## Navegación
- Sin pantalla nueva. La ruta `home` acepta query arg opcional `?export=true`
  (default `false`): Home entra en modo selección al arrancar.
- Perfil -> `navigate("home?export=true")`.
- Guardar: `rememberLauncherForActivityResult(CreateDocument("application/zip"))` en la
  screen; el Uri resultante se pasa al ViewModel.
- Compartir: la screen observa `ShareReady` y lanza `ACTION_SEND`.

## Arquitectura
- `domain/model/ExportDocument.kt`: data classes `@Serializable` del sobre
  (`ExportDocument`) y de cada entrada (`ExportedEntry`), Kotlin puro sin
  dependencias Android -> reutilizables como DTO de la futura API de sync.
- `domain/export/DiaryExporter.kt`: interfaz `suspend fun export(entries, photoFor, out)`.
- `domain/usecase/ExportEntriesUseCase.kt`: recibe ids seleccionados, obtiene entradas
  (del flujo ya cargado en el VM o repo), delega en `DiaryExporter`.
- `data/export/DiaryExporterImpl.kt`: `java.util.zip.ZipOutputStream` + kotlinx-serialization;
  resuelve bytes de foto con `ContentResolver`/`File` vía `@ApplicationContext`
  (inyectable con `(imageUrl) -> ByteArray?` para tests JVM).
- `ui/viewmodel/HomeViewModel.kt`: estado de selección + `exportState`; métodos
  `enterSelectionMode/exitSelectionMode/toggleSelection(id)/selectAll()`;
  `exportSelectedTo(uri)` (SAF) y `shareSelected()` (ZIP temporal en caché).
- `ui/screens/TravelDiaryHomeScreen.kt`: top bar con icono exportar, modo selección,
  chooser Guardar/Compartir, launcher SAF, intent de share, snackbar.
- `ui/screens/ProfileScreen.kt`: opción "Exportar diario" -> `home?export=true`.
- `navigation/Screen.kt` + `TravelDiaryNavGraph.kt`: arg `export` en ruta `home`.
- `AndroidManifest.xml` + `res/xml/file_paths.xml`: `FileProvider` (cache-path `exports/`).
- DI: binding de `DiaryExporter` en `di/` (DatabaseModule o nuevo ExportModule).
- `utils/`: nada nuevo salvo helper de nombre de archivo si se reutiliza.

## Permisos, lifecycle y persistencia
- Sin permisos nuevos: SAF no requiere permisos; FileProvider solo manifest + xml.
- Rotación: selección y `exportState` viven en el ViewModel; el launcher SAF y el share
  sheet sobreviven solos (activity result APIs).
- Exportación en `Dispatchers.IO`, streaming al OutputStream (no cargar fotos en memoria).
- ZIP temporal de share en `cacheDir/exports/`; puede sobreescribirse en cada share.
- Solo lectura sobre Room y archivos de fotos; no se modifica ningún dato.

## Plan de tests
- [ ] build: `scripts/verify.sh`
- [ ] unit `DiaryExporterImpl` (JVM, resolver de fotos fake): ZIP válido con
      `entries.json` correcto; foto presente -> `photoFile`; foto fallida/http -> `null`.
- [ ] unit `HomeViewModel`: toggle/selectAll/exit limpia selección; exportar deshabilitado
      con 0 seleccionadas; `exportSelectedTo` produce `Success`; error -> `Error`.
      (patrón `backgroundScope.launch { collect {} }` + `advanceUntilIdle()`)
- [ ] dispositivo: `scripts/verify.sh --device` + flujo manual: seleccionar 2 entradas
      con foto -> Guardar en Descargas -> abrir ZIP y comprobar JSON + fotos;
      Compartir -> share sheet abre con el ZIP adjunto.
- [ ] evidencia: capturas en `docs/specs/exportar-entradas/`

## Edge cases
- Diario vacío: icono de exportar no aparece.
- 0 seleccionadas: acción exportar deshabilitada.
- Entrada sin foto o con `imageUrl` remota: `photoFile = null`, JSON conserva `imageUrl`.
- Foto borrada/permiso revocado: se omite del ZIP, no aborta.
- Nombres de foto duplicados: siempre `entry-<id>-<n>.<ext>` (nunca colisiona).
- Cancelar SAF picker o share sheet: sin error, vuelve al modo selección.
- Selección grande: escritura en streaming en hilo IO; sin límite de tamaño explícito.
- Contrato de formato: `formatVersion` empieza en 1 y todo cambio futuro debe poder
  seguir parseando v1 (condición para la futura importación y el sync online).
