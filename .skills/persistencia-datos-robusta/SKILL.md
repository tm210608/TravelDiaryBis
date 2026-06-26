# Secure-Data-Flow

## Descripción
Implementa persistencia robusta usando Room con TypeConverters, manejo de migraciones, y almacenamiento persistente de imágenes.

## Contexto recomendado
- Proyectos con datos locales que deben sobrevivir a reinicios.
- Almacenamiento temporal de imágenes (evitar cacheDir).

## Pasos
1. Revisar que las entidades Room usen `TypeConverters` para `Instant`, `Double?`, etc.
2. Migrar almacenamiento de imágenes de `cacheDir` a `filesDir`.
3. Agregar `fallbackToDestructiveMigration()` o migraciones versionadas.
4. Verificar que las consultas en DAO usen `Flow` para reactividad.
