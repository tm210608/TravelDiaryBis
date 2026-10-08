# SPEC: Editar entrada existente

> Metodología SDMD (Spec Driven Mobile Development) — la spec es la fuente de verdad, el código la sirve.
> "Done" = build + tests + app instalada + flujo ejecutado + evidencia.

---

## Qué construimos

Desde el detalle de un viaje, la persona puede abrir la entrada en modo edición, cambiar sus datos y guardar sobre el mismo registro Room. El formulario permite editar todos los datos de la entrada, incluida la foto (conservar, reemplazar con la cámara o quitar) y el estado de favorito. Antes de guardar una entrada que ya tiene un par de coordenadas GPS, se confirma si siguen siendo correctas; si no lo son, se actualizan con la ubicación actual del dispositivo.

---

## Criterios de aceptación (testeables, sin ambigüedad)

Leyenda: `[x]` verificado; `[ ]` implementado pero no verificado en dispositivo.

- [x] DetailScreen ofrece "Editar" y abre `edit_entry/{entryId}` con los datos precargados (evidencia-formulario.png)
- [x] El formulario expone título, ubicación, país, fecha, etiqueta, descripción, foto y favorito; ViewModel conserva el mismo `id` al actualizar (evidencia-formulario-campos.png + EditEntryViewModelTest)
- [ ] La foto se conserva si no se modifica; se puede reemplazar con cámara o quitar; entrada sin foto deja Recuerdos (quitar probado en ViewModel y dispositivo de forma temporal, luego descartado; ruta de cámara y efecto en Recuerdos pendientes)
- [x] Guardar sin cambios no crea entrada nueva ni altera su id (Room update por fila existente; prueba ViewModel y flujo de retorno al detalle)
- [x] Antes de guardar con coordenadas completas se pregunta si siguen correctas (evidencia-confirmacion-gps.png)
- [x] "Sí, conservar" guarda cambios manteniendo las coordenadas existentes (flujo manual sin alterar el dato, evidencia-detalle-actualizado.png)
- [ ] "No, actualizar ubicación" obtiene la ubicación actual y guarda ambas coordenadas actualizadas (ViewModel probado con lat/lon suministradas; flujo de ubicación real no ejecutado para evitar modificar datos de usuario)
- [ ] Permiso se solicita solo al elegir "Actualizar ubicación"; denegación mantiene edición y coordenadas (lógica ViewModel probada; diálogo del sistema no probado en dispositivo)
- [x] Ubicación nula/no disponible no guarda ni sustituye coordenadas y muestra error recuperable (EditEntryViewModelTest)
- [x] Sin coordenadas completas no se muestra diálogo y se preservan valores null/pareja parcial (EditEntryViewModelTest)
- [x] Validación de título, ubicación y país evita escribir con formulario inválido (EditEntryViewModelTest)
- [x] Al guardar, Room actualiza el mismo id y al volver el detalle observa la entrada (EditEntryViewModelTest + navegación manual)
- [x] Cancelar con cambios solicita confirmación y descarta al confirmar (evidencia-descartar-cambios.png; datos persistidos sin cambios)
- [ ] Entrada borrada mientras la edición está abierta muestra NotFound y vuelve a Home (ViewModel y DAO false probados; ruta visual no probada en dispositivo)
- [x] Build y tests pasan: `scripts/verify.sh` (78 tests, 0 fallos)

---

## Fuera de alcance (no se construye en esta feature)

- Crear/eliminar entradas (la edición no duplica ni borra)
- Cambiar manualmente coordenadas introduciendo latitud/longitud o moviendo un pin
- Geocodificación de texto de ubicación a coordenadas
- Cambiar foto desde galería/importar archivos (la edición de foto usa el flujo CameraX existente)
- Deshacer cambios después de guardar
- Migración Room: `TravelEntry`/`TravelEntryEntity` ya tienen los campos que se editan

---

## UI states

```kotlin
sealed interface EditEntryUiState {
    data object Loading : EditEntryUiState
    data class Editing(val entry: TravelEntry, val form: EntryDetails, val isDirty: Boolean) : EditEntryUiState
    data class Saving(val form: EntryDetails) : EditEntryUiState
    data class Error(val message: String) : EditEntryUiState
    data object NotFound : EditEntryUiState
}
```

- **Loading**: indicador mientras se carga la entrada por id.
- **Editing**: formulario precargado, con validación; diálogo GPS solo al guardar si hay latitud y longitud.
- **Saving**: formulario deshabilitado e indicador.
- **Error**: mensaje recuperable en la pantalla, datos editados preservados.
- **NotFound**: aviso y navegación de vuelta.
- Confirmación GPS: "Sí, conservar" actualiza con coordenadas existentes; "No, actualizar ubicación" ejecuta el flujo de permisos/obtención GPS. Fallo de permiso o ubicación no persiste datos.
- Confirmación de salida con formulario modificado: "Seguir editando" / "Descartar cambios".

---

## Navegación

- DetailScreen → `edit_entry/{entryId}` al tocar "Editar".
- EditEntryScreen → cámara existente; resultado de captura por `savedStateHandle["capturedImageUri"]`.
- Guardar con éxito → back a DetailScreen; la observación de Room refleja los campos actualizados.
- Cancelar → back a DetailScreen, con confirmación solo si el formulario cambió.
- Entry desde Memories/Map → Detail → Edit; al guardar se mantiene ese contexto.

---

## Arquitectura

- `domain/usecase/GetEntryByIdUseCase.kt` — reutilizar para cargar la entrada
- `domain/repository/TravelRepository.kt` + `data/local/TravelDao.kt` + `data/repository/TravelRepositoryImpl.kt` — hacer que `updateEntry` reporte si Room actualizó una fila (`@Update` devuelve filas afectadas), sin migración de esquema
- `domain/usecase/UpdateEntryUseCase.kt` — reutilizar para persistir cambios; devolver éxito/fallo y cubrir ambos con tests
- `ui/viewmodel/EditEntryViewModel.kt` (NUEVO) — carga por id, formulario, validación, dirty state, actualización y estados
- `ui/viewmodel/AddEntryViewModel.kt` / `ui/screens/AddTravelEntryScreen.kt` — extraer/compartir formulario sin cambiar el flujo de creación; mantener ambos modos independientes en ViewModel
- `ui/screens/TravelDetailScreen.kt` — acción Editar y callback de navegación
- `ui/screens/EditTravelEntryScreen.kt` (NUEVO) — pantalla de edición y manejo de estados/dialogs
- `navigation/Screen.kt` — route `edit_entry/{entryId}`
- `navigation/TravelDiaryNavGraph.kt` — route, argumento Int, cámara y retorno a detalle
- `utils/LocationHelper.kt` — reutilizar obtención actual de ubicación y manejo de permisos; solo solicitar al elegir actualizar GPS
- Room — sin cambios de esquema; retorno de filas afectadas para detectar si la entrada fue borrada antes del update

---

## Permisos, lifecycle y persistencia

- Reutiliza el permiso de ubicación existente; se solicita solo por acción explícita "Actualizar ubicación" y no al entrar a editar.
- Imagen capturada conserva el almacenamiento persistente actual (`filesDir`); quitar foto actualiza `imageUrl` a vacío sin borrar otras imágenes/entradas.
- Escritura local mediante `UpdateEntryUseCase`/Room; no hay red ni migración.
- ViewModel conserva formulario, diálogo y estado durante rotación; si el proceso muere, cargar el registro persistido de nuevo.
- No se escribe nada hasta que la validación pasa y se resuelve la confirmación GPS.

---

## Plan de tests

- [x] build: `scripts/verify.sh` — 78 tests, 0 fallos
- [x] unit: `EditEntryViewModelTest` — carga/NotFound, precarga todos los campos, favorito/foto,
      validación, conservar coordenadas, GPS actualizado/denegado/no disponible, éxito mismo id
- [x] unit: `UpdateEntryUseCaseTest` — éxito y ausencia de fila actualizada; repositorio usa el resultado de Room
- [x] unit: validación title/location/country compatible con la validación existente de creación
- [ ] dispositivo: `scripts/verify.sh --device` OK; ejecutados detalle→editar→confirmar/conservar GPS→volver a detalle,
      mostrar/cancelar cambios sin guardar. Actualizar GPS y cambio de foto completos quedan pendientes por no modificar datos del usuario
- [x] evidencia: `evidencia-formulario.png`, `evidencia-formulario-campos.png`,
      `evidencia-confirmacion-gps.png`, `evidencia-detalle-actualizado.png`,
      `evidencia-descartar-cambios.png`, `evidencia-foto-quitada-sin-guardar.png` en `docs/specs/edit-entry/`

---

## Edge cases

- Entrada con foto vacía → no aparece opción de quitar; puede capturar una foto nueva
- Entrada con solo latitud o solo longitud → se trata como sin coordenadas completas; no se muestra diálogo y se preserva la pareja tal como está
- Usuario niega permiso al actualizar GPS → no guarda y mantiene la edición sin cambios
- GPS apagado, timeout o ubicación nula → no guarda coordenadas incorrectas ni sobrescribe el registro
- Captura de cámara cancelada → conserva la foto anterior y sigue editando
- El usuario elige quitar foto y luego cancela la edición → la foto persistida no cambia
- Guardado repetido/tap doble → no duplica registros; controla estado Saving
- Entrada borrada externamente antes de guardar → error recuperable, sin insertar como nueva
- Rotación durante carga, diálogo o edición → mantiene estado del formulario y no pierde los cambios

---

*Spec elaborada siguiendo SDMD — la diferencia no está en usar una IA mejor, está en darle contexto, criterios verificables y una definición clara de cuándo el trabajo está realmente terminado.*
