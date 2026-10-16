---
name: ui-polish-stitch
description: Disenar, prototipar con el MCP de Stitch y aplicar pulido visual conceptual (temas de marca, metaforas fisicas, Canvas/Shape custom, tipografia mono/editorial) en TravelDiaryBis sin romper Clean Architecture.
---

# /ui-polish-stitch

Flujo para elevar la UI de TravelDiaryBis de "plantilla Material" a producto de autor digno de Play Store:

## 1. Principios de identidad visual del proyecto
- **Dualidad Dia/Noche con caracter propio**:
  - **Modo Oscuro ("Atlas Nocturno")**: ya definido en `ui/theme/Color.kt` (`#12121D` obsidian + `#FFDCA1` ambar linterna + `#B4C9DF` pizarra).
  - **Modo Claro ("Cuaderno de Campo / Atlas Cartografico")**: NUNCA dejar caer en `dynamicLightColorScheme` por defecto si deslava la personalidad. Usar papel topografico calido (`#F4EFE6` / `#EDE5D8`), tinta marina profunda (`#141B24`), acento bermellon brujula (`#D94E28`) o ambar oxidado (`#B87314`) y lineas de regla sutiles (`#D8CFC0`).
- **Tres voces tipograficas**:
  - **Display/Headline (`Epilogue`)**: titulares editoriales con tracking negativo (`-0.02em`).
  - **Body/UI (`Manrope`)**: lectura limpia y eyebrows en mayusculas (`+0.05em`).
  - **Telemetria/Datos (`FontFamily.Monospace` o fuente mono OFL en `res/font`)**: SIEMPRE para coordenadas GPS (`41.3850°N 02.1734°E`), fechas cortas (`08·OCT·26`), contadores (`EXP #03`) y codigos de pais (`ESP`, `JPN`).
- **Metaforas fisicas del mundo viajero (implementables en Compose puro)**:
  1. **Billete / Boarding Pass troquelado**: `Shape` custom con muescas semicirculares laterales y divisor `Canvas` con `PathEffect.dashPathEffect`.
  2. **Sello de pasaporte entintado**: `Box` con `Modifier.rotate(-6f)`, borde doble circular/rectangular con alpha `0.75f`, texto en caps con tracking ancho (codigo ISO3 + mes/ano).
  3. **Tira de telemetria (Flighty style)**: barra inferior o pill densa con coordenadas reales de `TravelEntry.latitude/longitude` formateadas en N/S/E/W.
  4. **Hoja de contactos 35mm (Halide/Kodak style)**: en `MemoriesScreen`, marco oscuro con numeracion `EXP 01A` en mono ambar bajo cada foto.

## 2. Flujo con Stitch MCP
1. **Explorar/Crear proyecto**: usar `list_projects` o `create_project` en el servidor `stitch`.
2. **Generar pantallas con datos reales**: llamar a `generate_screen_from_text` (`deviceType = "MOBILE"`, `modelId = "GEMINI_3_8_FLASH"`) incluyendo entradas reales del usuario (p. ej. Barcelona `41.3850°N`, Kioto, Paris) para evaluar la densidad real.
3. **Descargar y mostrar**: recuperar el `screenshot.downloadUrl` de la pantalla generada, descargarla a `docs/specs/<feature>/stitch-*.png` y leerla con `read` para mostrarla en la conversacion y criticarla.

## 3. Reglas de implementacion en Compose
- **Cero dependencias pesadas**: muescas de ticket, sellos, lineas discontinuas y marcos de carrete se hacen con `GenericShape`, `Canvas`, `drawWithContent` y `PathEffect`.
- **Respetar Clean Architecture**: los formateadores puramente visuales (ej. `Double.toCardinalLat()`, codigo ISO de pais para el sello) viven como funciones puras en `ui/components/` o `domain/model/` con tests unitarios JVM.
- **No romper estados existentes**: los checkboxes de seleccion multiple de exportacion, el `importState` de Perfil y los flujos de camara/GPS deben seguir funcionando intactos sobre el nuevo cromo visual.
