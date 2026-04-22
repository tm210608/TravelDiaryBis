# Habilidad: Persistencia de Datos Robusta

## Contexto y Grounding
Esta habilidad se basa en las mejores prácticas de Android para la gestión de datos persistentes utilizando Room y el sistema de archivos de Android. Se enfoca en garantizar que la información del usuario (textos e imágenes) sea resiliente a cierres de la app y limpiezas de caché.

## Instrucciones Modulares
### Gestión de Imágenes
1.  **Origen**: Las imágenes capturadas por la cámara residen inicialmente en `cacheDir` (temporal).
2.  **Destino**: Mover las imágenes a `context.filesDir/travel_images/` para almacenamiento permanente.
3.  **Referencia**: Almacenar solo el nombre del archivo o la ruta relativa en la base de datos para evitar problemas con cambios en el ID de usuario de Android.

### Base de Datos (Room)
1.  Utilizar `TypeConverters` para tipos complejos (`Instant`, `Double?`).
2.  Implementar el patrón de Repositorio para mediar entre el DAO y el Dominio.

## Especificación de Tarea
- **Entrada**: Una URI de imagen temporal o datos crudos de una entidad.
- **Salida**: Código que garantice el guardado permanente y la integridad referencial en Room.
- **Patrón**: Usar `Result.success()` o `Result.failure()` para informar del estado de la operación.
