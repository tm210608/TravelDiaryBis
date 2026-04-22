# Habilidad: Arquitectura Clean Senior

## Contexto y Grounding
Sigue las guías oficiales de arquitectura de aplicaciones Android (Guide to app architecture). Define la separación estricta entre la lógica de datos, la lógica de negocio (Dominio) y la representación visual (Presentación).

## Instrucciones Modulares
### Capas
1.  **Data**: DAOs, Entidades de Room, Mappers.
2.  **Domain**: Modelos de Dominio (Kotlin puro), Interfaces de Repositorio, UseCases.
3.  **Presentation**: ViewModels, Estados de UI (Sealed Interfaces), Composables.

### Inyección de Dependencias
1.  Utilizar `@HiltViewModel` para ViewModels.
2.  Utilizar `@Module` y `@InstallIn(SingletonComponent::class)` para proveer dependencias globales.

## Especificación de Tarea
- **Tarea**: Refactorizar o crear nuevos componentes asegurando que no haya fugas de lógica entre capas.
- **Regla**: La UI nunca debe conocer la existencia de `TravelEntryEntity`.
