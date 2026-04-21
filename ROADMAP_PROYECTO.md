# 🗺️ Hoja de Ruta: TravelDiaryBis - De Prototipo a Producción

Este documento define la estrategia de evolución del proyecto para garantizar escalabilidad, mantenibilidad y robustez, bajo principios de desarrollo profesional (Senior).

---

## 📅 Hitos del Proyecto

### Hito 1: Estabilización de Capa de Datos (Cimientos)
- [x] Implementar `TypeConverters` para fechas (usando `java.time`) y coordenadas.
- [x] Migrar el almacenamiento de imágenes de `cacheDir` a `filesDir` (almacenamiento persistente).
- [x] Implementar una estrategia de gestión de errores en `Repository` (Arquitectura de Interfaz).

### Hito 2: Arquitectura y Reactividad
- [ ] Integrar **Hilt** para inyección de dependencias (reemplazando factorías manuales).
- [ ] Refactorizar estados de UI a `sealed class` (Manejo de estados: `Loading`, `Success`, `Error`).

### Hito 3: UX y Resiliencia
- [ ] Implementar manejo robusto de permisos (Camera/Location/Rationale).
- [ ] Añadir `Snackbar` para feedback al usuario ante errores o acciones exitosas.

### Hito 4: Calidad (Testing)
- [ ] Unit Testing para `ViewModel` (usando JUnit 5 y MockK).
- [ ] UI Testing para flujos críticos (Camera -> AddEntry).

---

## 🤖 Estructura de Subagentes (Roles)

*   **Architect**: Diseñador de estructuras (MVVM, DI, Clean Architecture).
*   **DataSpec**: Especialista en persistencia (Room, SQLite, Migraciones).
*   **UIGuardian**: Experto en Compose/UX (Material 3, Estados, Accesibilidad).
*   **QA-Droid**: Analista de calidad (Testing, Debugging, Logcat).

---

## 🛠️ Skills Disponibles

1.  **`Apply-Clean-Architecture`**: Refactorización de código siguiendo principios SOLID.
2.  **`Integrate-Dependency-Injection`**: Aplicación de Hilt.
3.  **`Secure-Data-Flow`**: Manejo seguro de permisos y persistencia.
4.  **`Write-Unit-Test`**: Generación de tests unitarios (JUnit/MockK).
5.  **`UI-State-Refactor`**: Implementación de `sealed classes` para estados de UI.

---
*Documento creado para el seguimiento del equipo de desarrollo.*
