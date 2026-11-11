# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-11 | **Rama:** `develop` (pendiente push a origin, ~9 commits por delante) | 156 tests JVM verdes + instrumentados verdes en 2 emuladores

## Hecho el 2026-11-11
- **Navegación type-safe mergeada a `develop`** (`feature/nav-type-safe`, merge `22afc9b`):
  rutas string -> `@Serializable` en `navigation/Destinations.kt` (8 destinos; args en
  `Home(export)`, `Detail(entryId)`, `EditEntry(entryId)`). Navigation 2.7.7 -> 2.8.9.
  `Screen.kt` eliminado. Mismo comportamiento: `savedStateHandle`, tabs `popUpTo`/`saveState`,
  Profile -> `Home(export = true)`.
- Verificado en emulador `SocialChef_Test` (API 36): flujo completo Add>Camera>Guardar>
  Detail>Edit(refresh)>tabs>marker>export>rotación. 18 capturas en `docs/specs/nav-type-safe/`.
  `connectedDebugAndroidTest` verde en 2 emuladores (HomeAddEntryFlowTest + MigrationTest).
- `AGENTS.md`: preferencias del usuario (explicaciones no técnicas, opción recomendada en
  cuestionarios, explicar pasos que requieren aprobación). Rama `feature/ui-tests-compose` borrada.

## Siguiente paso
- ROADMAP: `ui-pasaporte-y-contactos` (pulido Fase 2), sync backend, Play Store release,
  PDF export, detekt/ktlint.
- Opcional: repetir `connectedDebugAndroidTest` en Xiaomi tras aprobar instalación en el teléfono.

## Decisiones vigentes
- Navigation fijada en **2.8.9** (no subir a 2.9+/2.10+ sin pedirlo: cambian requisitos de SDK).
- Destinos type-safe en `navigation/Destinations.kt` (`data object` sin args, `data class` con args).
- Emulador `SocialChef_Test`: IME stylus intercepta campos la 1ª vez (cancelar tutorial);
  es AVD de otro proyecto: comprobar foreground antes de enviar taps.
- Xiaomi/MIUI: `adb install` requiere "Instalar por USB" aprobado en el teléfono
  (`INSTALL_FAILED_USER_RESTRICTED` si no).
- Resto: sesiones anteriores (Cuaderno/Atlas, Open-Meteo, Medium_Phone inestable -> Xiaomi).

## Bloqueos
- Ninguno.

## Deuda técnica
- `popBackStack<Home>` (tab Cuaderno), `onEntryNotFound`, `capturedImageUri`->EditEntry:
  compilados/verificados en código, sin ejercicio manual.
- Descripción FeaturedCard sin observar; placeholder 0 sellos/Empty Recuerdos sin evidencia.
- Pre-Play Store: privacidad/Data Safety, keystore/R8/AAB.
- UI tests adicionales, CameraScreen, clustering, detekt/ktlint.
