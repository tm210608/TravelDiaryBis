# Roadmap

Visión: app offline-first, estética premium, captura rápida, datos locales del usuario.

## Hecho
- [x] Persistencia Room con `Instant`, imágenes en `filesDir`, errores en repositorio
- [x] Hilt, sealed UI states, Clean Architecture (domain/data/ui, use cases)
- [x] Permisos cámara/ubicación, Snackbar de feedback
- [x] Unit tests (ViewModels, use cases, mapper)
- [x] Mapa de viajes verificado en dispositivo: tiles, markers, InfoWindow, detalle, vacío, multi-marker bounds, dark mode (mapa) y landscape — `docs/specs/map-view/SPEC.md`
- [x] Filter chips funcionales (Europa/Asia/América/Favoritos) verificados en dispositivo, combinados con búsqueda — `docs/specs/filter-chips/SPEC.md`
- [x] Tabs Recuerdos/Perfil: galería, estadísticas y navegación verificados en dispositivo — `docs/specs/bottom-tabs/SPEC.md`

## En curso
- [ ] UI tests Compose (Home -> AddEntry -> guardar -> Home); hay `CONTEXT.md` viejo en el stash

## Siguiente (una spec SDMD cada uno)
- [ ] Editar entrada existente
- [ ] Dark mode completo
- [ ] Navegación type-safe
- [ ] Exportar viaje a PDF/imagen
- [ ] Release: R8, signing por variables de entorno, AAB, SDK 35
- [ ] detekt + ktlint

## Criterios de calidad
Sin jank al hacer scroll; 100% del core offline; fallos de cámara/permisos informan sin cerrar la app.
