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
- [x] Editar entrada: formulario reutilizado, Room actualiza por id, confirmación GPS — `docs/specs/edit-entry/SPEC.md` (GPS/cámara completos pendientes de verificar en dispositivo)
- [x] Tema oscuro global Atlas nocturno, seguimiento del sistema y barras sincronizadas — `docs/specs/dark-mode/SPEC.md` (CameraScreen y cobertura clara total pendientes de dispositivo)
- [x] Reskin Material 3 Expressive (tokens Stitch navy/ámbar, Epilogue/Manrope) mergeado a develop — `docs/specs/m3-expressive-redesign/`
- [x] Exportar diario a ZIP (diary.json versionado `formatVersion=1` + fotos) con Guardar (SAF) y Compartir (FileProvider), verificado en emulador — `docs/specs/exportar-entradas/SPEC.md`
- [x] Importar diario desde ZIP exportado (merge sin dedup, fotos copiadas, parseo tolerante), verificado en emulador y Xiaomi físico — `docs/specs/importar-entradas/SPEC.md`
- [x] Migraciones Room reales: `exportSchema` + `MIGRATION_2_3` con uuid por entrada (identidad estable para futura sync) + `MigrationTestHelper` en androidTest, verificado en Xiaomi — `docs/specs/migraciones-room/SPEC.md`

## En curso
- [ ] UI tests Compose (Home -> AddEntry -> guardar -> Home); hay `CONTEXT.md` viejo en el stash

## Siguiente (una spec SDMD cada uno)
- [ ] Pulido visual Fase 1 (`ui-cuaderno-campo-home`): Tema Light de marca "Cuaderno de Campo Cartográfico" + tarjetas billete troqueladas con talón de fecha y telemetría GPS en monoespaciada — ref `docs/specs/ui-polish-concepts/stitch-01-atlas-campo-home.png`
- [ ] Pulido visual Fase 2 (`ui-pasaporte-y-contactos`): Perfil "Pasaporte de Expedición" con sellos entintados dinámicos y KM Haversine + Recuerdos como "Hoja de Contactos 35mm" + hilo de ruta en Mapa — ref `docs/specs/ui-polish-concepts/stitch-02-pasaporte-y-contactos-dark.png`
- [ ] Navegación type-safe (exige Navigation 2.8.x; hoy 2.7.7)
- [ ] Sync online / backend (el sobre `ExportDocument` ya sirve de DTO; uuid por entrada listo)
- [ ] Release Play Store: política de privacidad + Data Safety (fotos/ubicación),
  keystore, R8, AAB (migraciones Room ya resueltas)
- [ ] Exportar variante PDF/imagen (legible para humanos)
- [ ] detekt + ktlint

## Criterios de calidad
Sin jank al hacer scroll; 100% del core offline; fallos de cámara/permisos informan sin cerrar la app.
