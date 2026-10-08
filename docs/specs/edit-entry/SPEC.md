# SPEC: Editar entrada existente

> Metodología SDMD (Spec Driven Mobile Development) — la spec es la fuente de verdad, el código la sirve.
> "Done" = build + tests + app instalada + flujo ejecutado + evidencia.

---

## Qué construimos

Desde el detalle de un viaje, la persona puede abrir la entrada en modo edición, cambiar sus datos y guardar sobre el mismo registro Room. El formulario permite editar todos los datos de la entrada, incluida la foto (conservar, reemplazar con la cámara o quitar) y el estado de favorito. Antes de guardar una entrada que ya tiene un par de coordenadas GPS, se confirma si siguen siendo correctas; si no lo son, se actualizan con la ubicación actual del dispositivo.

---

## Criterios de aceptación (testeables, sin ambigüedad)

- [ ] DetailScreen ofrece la acción "Editar" y abre `edit_entry/{entryId}` con los datos de esa entrada precargados
- [ ] El formulario de edición permite cambiar título, ubicación, país, fecha, etiqueta, descripción, foto y favorito; mantiene el mismo `id`
- [ ] La foto se conserva si no se modifica; se puede reemplazar con la cámara o quitar. Entrada sin foto deja de aparecer en Recuerdos
- [ ] Guardar una entrada sin cambios no crea una entrada nueva ni altera su id
- [ ] Antes de guardar una entrada con latitud y longitud no nulas, se pregunta: "¿La ubicación registrada sigue siendo correcta?"
- [ ] "Sí, conservar" guarda los cambios y conserva exactamente las coordenadas existentes
- [ ] "No, actualizar ubicación" obtiene la ubicación actual y guarda ambas coordenadas actualizadas junto con el resto de cambios
- [ ] Si falta permiso de ubicación, el permiso se solicita solo después de elegir "Actualizar ubicación"; si se deniega, se permanece en edición, no se guarda ni se alteran las coordenadas, y se informa cómo reintentar
- [ ] Si ubicación actual no está disponible, no se guarda la edición ni se sustituyen las coordenadas; se muestra un error y se permite reintentar o cancelar
- [ ] Si la entrada no tiene un par completo de coordenadas, no aparece el diálogo GPS y los valores actuales (incluidos null/pareja parcial) se conservan al guardar
- [ ] Los campos requeridos conservan las mismas reglas de validación que crear una entrada; los errores se muestran junto al formulario y no se escribe en Room
- [ ] Al guardar correctamente, Room actualiza la entrada existente; se vuelve al detalle de la misma entrada con valores nuevos
- [ ] Cancelar vuelve al detalle sin persistir los cambios; si hay cambios sin guardar, se pide confirmación antes de descartarlos
- [ ] Si la entrada deja de existir mientras se abre edición, se muestra "Esta entrada ya no existe" y se ofrece volver al detalle/Home sin crash
- [ ] Build `./gradlew compileDebugKotlin` y tests `./gradlew testDebugUnitTest` pasan

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
- `domain/usecase/UpdateEntryUseCase.kt` — reutilizar para persistir cambios; verificar/añadir tests del caso de uso
- `ui/viewmodel/EditEntryViewModel.kt` (NUEVO) — carga por id, formulario, validación, dirty state, actualización y estados
- `ui/viewmodel/AddEntryViewModel.kt` / `ui/screens/AddTravelEntryScreen.kt` — extraer/compartir formulario sin cambiar el flujo de creación; mantener ambos modos independientes en ViewModel
- `ui/screens/TravelDetailScreen.kt` — acción Editar y callback de navegación
- `ui/screens/EditTravelEntryScreen.kt` (NUEVO) — pantalla de edición y manejo de estados/dialogs
- `navigation/Screen.kt` — route `edit_entry/{entryId}`
- `navigation/TravelDiaryNavGraph.kt` — route, argumento Int, cámara y retorno a detalle
- `utils/LocationHelper.kt` — reutilizar obtención actual de ubicación y manejo de permisos; solo solicitar al elegir actualizar GPS
- `data/` / Room — sin cambios de esquema; update existente mediante repositorio

---

## Permisos, lifecycle y persistencia

- Reutiliza el permiso de ubicación existente; se solicita solo por acción explícita "Actualizar ubicación" y no al entrar a editar.
- Imagen capturada conserva el almacenamiento persistente actual (`filesDir`); quitar foto actualiza `imageUrl` a vacío sin borrar otras imágenes/entradas.
- Escritura local mediante `UpdateEntryUseCase`/Room; no hay red ni migración.
- ViewModel conserva formulario, diálogo y estado durante rotación; si el proceso muere, cargar el registro persistido de nuevo.
- No se escribe nada hasta que la validación pasa y se resuelve la confirmación GPS.

---

## Plan de tests

- [ ] build: `scripts/verify.sh`
- [ ] unit: `EditEntryViewModelTest` — carga, NotFound, precarga todos los campos, favorito/foto,
      validación, dirty/cancelar, conservar coordenadas, refresh GPS correcto/denegado/no disponible,
      éxito actualiza id existente
- [ ] unit: `UpdateEntryUseCaseTest` — delega al repositorio y no inserta un nuevo registro
- [ ] unit: validación del formulario compartida con el flujo de creación
- [ ] dispositivo: `scripts/verify.sh --device` + editar una entrada desde detalle, conservar GPS,
      actualizar GPS con permiso concedido, quitar/reemplazar foto, cancelar cambios y abrir detalle
- [ ] evidencia: capturas en `docs/specs/edit-entry/` (form precargado, confirmación GPS, detalle actualizado)

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
