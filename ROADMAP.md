# 🗺️ Roadmap de TravelDiary

Este documento define la visión, los criterios técnicos y los pasos a seguir para convertir TravelDiary en una aplicación premium de diario de viajes.

## 1. Visión del Producto
**TravelDiary** es una aplicación "offline-first" diseñada para que los viajeros capturen sus recuerdos de forma rápida y visual. El enfoque principal es la **estética premium**, la **facilidad de uso** y el **funcionamiento sin conexión**.

### Objetivos de Usuario:
- **Captura Inmediata:** Poder registrar un momento en segundos.
- **Narrativa Visual:** Priorizar las fotos y mapas sobre el texto denso.
- **Privacidad:** Los datos pertenecen al usuario y se guardan localmente.

---

## 2. Criterios Técnicos (Tech Stack)
Para asegurar que la app sea moderna y mantenible, utilizaremos:

| Componente | Tecnología | Razón |
| :--- | :--- | :--- |
| **Lenguaje** | Kotlin 2.0 | Últimos estándares y rendimiento. |
| **UI Framework** | Jetpack Compose | Desarrollo de UI declarativo y moderno. |
| **Diseño** | Material 3 | Look & Feel nativo y actualizado. |
| **Arquitectura** | MVVM | Separación clara de responsabilidades. |
| **Persistencia** | Room | Base de datos local robusta. |
| **Multimedia** | CameraX | Integración estable con la cámara. |
| **Navegación** | Navigation Compose | Navegación fluida entre pantallas. |
| **Asincronía** | Coroutines & Flow | Manejo eficiente de tareas en segundo plano. |

---

## 3. Hoja de Ruta de Desarrollo

### 📍 Fase 1: Cimientos y Navegación (Estado Actual)
- [x] Prototipo de UI de Inicio.
- [ ] Definir Grafo de Navegación (Home -> Detail -> Camera).
- [ ] Crear Pantalla de Detalle (UI base).
- [ ] Implementar ViewModel base para la gestión de estados.

### 🗄️ Fase 2: Persistencia y Datos
- [ ] Configurar Room (Entity, DAO, Database).
- [ ] Implementar Repositorios para abstraer la fuente de datos.
- [ ] Migrar los datos de "ejemplo" a datos reales de la base de datos.
- [ ] Funcionalidad de "Añadir Viaje" y "Añadir Entrada" persistente.

### 📸 Fase 3: Multimedia y Cámara
- [ ] Integración completa con CameraX.
- [ ] Galería básica para seleccionar fotos existentes.
- [ ] Gestión de permisos en tiempo real.
- [ ] Almacenamiento local de archivos de imagen vinculados a las entradas.

### 🗺️ Fase 4: Enriquecimiento de Datos
- [ ] Integración con Google Maps API o MapLibre.
- [ ] Geoetiquetado automático de entradas.
- [ ] Vista de mapa con "pins" de los viajes realizados.

### ✨ Fase 5: Pulido Premium (WOW Factor)
- [ ] Animaciones de transición (Shared Element Transitions).
- [ ] Soporte completo para Dark Mode.
- [ ] Micro-interacciones (Haptic feedback, animaciones de botones).
- [ ] Generación de resumen de viaje (Exportar a PDF o Imagen estéticamente bella).

---

## 4. Criterios de Aceptación (QA)
1. **Performance:** La app no debe tener "jank" al hacer scroll, incluso con muchas fotos.
2. **Offline:** El 100% de la funcionalidad core debe funcionar sin internet.
3. **Resiliencia:** Si la cámara falla o no hay permisos, la app debe informar amigablemente sin cerrarse.
