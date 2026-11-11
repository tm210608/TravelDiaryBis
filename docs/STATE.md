# Estado del proyecto (se reescribe en cada /handoff)

**Fecha:** 2026-11-11 | **Rama:** `develop` (`701dcb3`, pendiente push a origin) | 156 tests JVM verdes + 2 instrumentados verdes

## Hecho el 2026-11-11
- **Infra de UI tests Compose creada y verificada en Xiaomi 2412DPC0AG (adb-WiFi `MVVSE6A6S4IVQ8MJ`)**:
  `HomeAddEntryFlowTest` (Home > FAB "Nueva entrada" > formulario > Guardar > Home muestra entrada).
- Nuevos archivos: `androidTest/.../TravelDiaryTestRunner.kt` (runner propio, devuelve
  `dagger.hilt.android.testing.HiltTestApplication` — `HiltTestRunner` ya no existe en Hilt 2.57),
  `di/TestDatabaseModule.kt` (`@TestInstallIn` reemplaza `DatabaseModule` con Room in-memory),
  `ui/HomeAddEntryFlowTest.kt`.
- Deps: `hilt-android-testing` (2.57.2), `ui-test-junit4-android` (BOM 1.11.4), `espresso-core 3.7.0`
  explícito (el 3.5.0 transitivo crashea en API 36 por `InputManager.getInstance`),
  `androidTestImplementation(platform(libs.compose.bom))`, `kspAndroidTest(hilt-compiler)`.

## Siguiente paso
- `feature/ui-tests-compose` ya mergeada a `develop`: borrarla SOLO con confirmación.
- ROADMAP pendiente: nav type-safe, sync backend, Play Store release, PDF export, detekt/ktlint.

## Decisiones vigentes
- androidTest corre con `HiltTestApplication` vía runner propio; NO poner un `@HiltAndroidApp`
  propio en `androidTest` (error "test roots and app roots in the same compilation unit").
- Emulador `Medium_Phone_API_36.1` inestable en este host (se ha cerrado solo); preferir Xiaomi.
- Diálogo de permiso del sistema cubre la app y vacía la jerarquía Compose -> conceder permisos
  con `uiAutomation.grantRuntimePermission` antes de la acción que los pide.
- Resto de decisiones: ver sesión anterior (dualidad Cuaderno/Atlas, Open-Meteo, rememberSaveable,
  ofrecer opciones con recomendación propia).

## Bloqueos
- Ninguno.

## Deuda técnica
- Descripción en FeaturedCard implementada pero no observada.
- Placeholder de 0 sellos y Empty/Loading de Recuerdos: implementados, sin evidencia.
- Pre-Play Store: privacidad/Data Safety, keystore/R8/AAB.
- UI tests adicionales (Detalle, Editar, selección/exportar), nav type-safe, CameraScreen,
  clustering, detekt/ktlint.
