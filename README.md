# 🌍 TravelDiaryBis

**TravelDiaryBis** es una aplicación de Android moderna construida con **Jetpack Compose**, **Material 3** y **Room**, diseñada para capturar y preservar tus recuerdos de viaje de forma segura y elegante.

## 🚀 Características principales
- **📸 Captura de Fotos persistente**: Las imágenes se guardan de forma segura en el almacenamiento interno privado de la aplicación (`filesDir`).
- **📍 Geolocalización Automática**: Cada entrada guarda tus coordenadas GPS para que nunca olvides dónde estuviste.
- **📅 Persistencia con Room**: Base de datos local robusta con soporte para tipos de datos modernos (`java.time.Instant`).
- **🎨 UI Moderna**: Interfaz fluida y reactiva siguiendo las guías de Material Design 3.

## 🛠️ Arquitectura y Tecnologías
- **Patrón de diseño**: MVVM (Model-View-ViewModel).
- **Jetpack Compose**: Para una UI declarativa y moderna.
- **Room Persistence Library**: Para el almacenamiento local de datos.
- **CameraX**: Para una experiencia de captura de fotos integrada y eficiente.
- **Hilt**: Inyección de dependencias.
- **Google Maps Compose**: Mapa de viajes (requiere `MAPS_API_KEY` en `local.properties`).
- **Coil**: Carga de imágenes eficiente.

---

## 📅 Roadmap de Desarrollo
Consulta [docs/ROADMAP.md](./docs/ROADMAP.md) para el progreso y [docs/STATE.md](./docs/STATE.md) para el estado actual. Contexto para asistentes de IA: [AGENTS.md](./AGENTS.md).

---

## 👨‍💻 Instalación y Uso
1. Clona este repositorio:
   ```sh
   git clone https://github.com/Papa/TravelDiaryBis.git
   ```
2. Abre el proyecto en **Android Studio Koala** o superior.
3. Sincroniza Gradle y ejecuta la aplicación en un emulador o dispositivo físico.

---
*Este proyecto está bajo la licencia MIT.*
