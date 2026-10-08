# Roadmap

Visión: app offline-first, estética premium, captura rápida, datos locales del usuario.

## Hecho
- [x] Persistencia Room con `Instant`, imágenes en `filesDir`, errores en repositorio
- [x] Hilt, sealed UI states, Clean Architecture (domain/data/ui, use cases)
- [x] Permisos cámara/ubicación, Snackbar de feedback
- [x] Unit tests (ViewModels, use cases, mapper)
- [x] Mapa de viajes (código + tests + navegación) — `docs/specs/map-view/SPEC.md`

## En curso
- [ ] Verificar mapa en dispositivo con `MAPS_API_KEY` (markers, info window, vacío)
- [ ] UI tests Compose (Home -> AddEntry -> guardar -> Home); hay `CONTEXT.md` viejo en el stash

## Siguiente (una spec SDMD cada uno)
- [ ] Filtro real de chips (Europa/Asia/América/Favoritos)
- [ ] Editar entrada existente
- [ ] Tabs Recuerdos y Perfil
- [ ] Dark mode completo
- [ ] Navegación type-safe
- [ ] Exportar viaje a PDF/imagen
- [ ] Release: R8, signing por variables de entorno, AAB, SDK 35
- [ ] detekt + ktlint

## Criterios de calidad
Sin jank al hacer scroll; 100% del core offline; fallos de cámara/permisos informan sin cerrar la app.
