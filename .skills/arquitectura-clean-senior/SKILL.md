# Apply-Clean-Architecture

## Descripción
Refactoriza el código siguiendo Clean Architecture de Robert C. Martin, separando responsabilidades en capas:
- **Domain**: Modelos de negocio, repositorios (interfaces), casos de uso.
- **Data**: Implementaciones de repositorios, fuentes de datos (Room, API).
- **Presentation**: ViewModels, Composable UI.

## Contexto recomendado
- Proyectos con lógica de negocio mezclada con UI o datos.
- Código que no sigue principios SOLID.

## Pasos
1. Identificar modelos de negocio y moverlos a `domain/model/`.
2. Crear interfaces de repositorio en `domain/repository/`.
3. Mover implementaciones existentes a `data/repository/`.
4. Asegurar que las capas internas no dependen de las externas.
