# SPEC: Navigation type-safe

## Qué construimos
Refactor puro: migrar el grafo de navegación de rutas string a rutas type-safe con
`@Serializable` (Navigation 2.8.x + kotlinx.serialization). El usuario no percibe ningún
cambio funcional; el beneficio es seguridad en tiempo de compilación para los argumentos
(`entryId: Int`, `export: Boolean`) y eliminar la deuda "navegación con strings".

## Criterios de aceptación (testeables, sin ambigüedad)
- [x] `navigation-compose` sube de 2.7.7 al último patch estable de **2.8.x** (fijado:
      2.8.9). `compileSdk`/`targetSdk` sin cambios (siguen en 36), Kotlin en 2.0.0.
- [x] Plugin `org.jetbrains.kotlin.plugin.serialization` aplicado en `app` y dependencia
      `kotlinx-serialization-json` presente (ambos ya estaban aplicados).
- [x] Los 8 destinos declarados como `@Serializable` en `navigation/Destinations.kt`:
      `data object` para AddEntry, Camera, Map, Memories, Profile; `data class` para
      `Home(export: Boolean = false)`, `Detail(entryId: Int)`, `EditEntry(entryId: Int)`.
- [x] `NavHost` usa `startDestination = Home()` tipado y todos los `composable<T>()` +
      `toRoute<T>()` son genéricos: no queda ninguna ruta string en `TravelDiaryNavGraph`.
- [x] Todos los `navigate`, `popBackStack`, `popUpTo` usan overloads type-safe
      (`navigate(Detail(id))`, `popBackStack<Home>()`, `popUpTo<Home>`).
- [x] `Screen.kt` eliminado (renombrado a `Destinations.kt`): ningún consumidor
      sigue importando rutas string.
- [x] Comportamiento idéntico al actual, observado en dispositivo: tabs con
      `popUpTo`/`saveState` (Map, Memories, Profile), Camera devuelve `capturedImageUri`
      por `savedStateHandle` (foto visible en AddEntry), Edit devuelve `refreshVersion`
      (Detail muestra título actualizado "Escapada Valencia II"), Profile navega a
      `Home(export = true)` (Home abre en modo selección "0 seleccionadas").
      Nota: `onEntryNotFound` → `popBackStack<Home>` no se ejercitó (requeriría borrar
      la entrada); el overload es el mismo ya verificado por el tab Cuaderno.
- [x] `scripts/verify.sh` verde; 156 tests JVM verdes y `HomeAddEntryFlowTest` +
      `MigrationTest` verdes en `connectedDebugAndroidTest` (2 emuladores API 36).
- [x] Flujo manual en dispositivo recorre los destinos sin regresión (ver plan de tests).
      Ejecutado en emulador `SocialChef_Test` (API 36); Xiaomi bloqueado por MIUI
      (instalación vía USB requiere aprobación en el teléfono).

## Fuera de alcance
- Cualquier cambio de comportamiento, pantallas o UI (refactor puro).
- Refactorizar `savedStateHandle` a shared ViewModel o nuevas result APIs.
- Corregir iconos `ArrowBack` deprecados u otra deuda de `ARCHITECTURE.md`.
- Subir `compileSdk`/`targetSdk` ni ninguna otra versión del catálogo.
- Deep links (no existen hoy y no se añaden).
- Navigation 2.9.x/2.10.x: exigirían compileSdk 35/36, fuera de alcance.

## UI states
N/A — refactor de navegación, no se introducen estados nuevos ni se tocan los existentes.

## Navegación
- `NavHost(navController, startDestination = Home)`.
- Lectura de args: `val args = backStackEntry.toRoute<Detail>()` → `args.entryId`.
- `onTabNavigate`: tab 0 → `popBackStack<Home>(inclusive = false)`; tabs 1-3 →
  `navigate(Map|Memories|Profile)` con `launchSingleTop` + `popUpTo<Home> { saveState = true }`.
- `onEntryNotFound` en Edit → `popBackStack<Home>(inclusive = false)`.
- `onExportClick` en Profile → `navigate(Home(export = true))` con
  `popUpTo<Home> { inclusive = true }` + `launchSingleTop`.
- `Home` lee `export` con default `false` (equivale al `navArgument` BoolType actual).

## Arquitectura
- `gradle/libs.versions.toml`: `navigationCompose = "2.8.x"` (solo esa línea).
- `app/build.gradle.kts`: `alias(libs.plugins.kotlin.serialization)` +
  `implementation(libs.kotlinx.serialization.json)`.
- `navigation/Screen.kt`: reemplazar la `sealed class` por los 8 `@Serializable`
  (mismo archivo o renombrado a `Destinations.kt`; decidir al implementar).
- `navigation/TravelDiaryNavGraph.kt`: reescrito con API genérica.
- Sin cambios en `domain/`, `data/` ni firmas públicas de las screens (reciben los mismos
  parámetros: `entryId`, `exportRequested`, `refreshKey`, callbacks).

## Permisos, lifecycle y persistencia
Sin cambios. El comportamiento de back stack y `saveState` de los tabs se conserva
explícitamente como criterio de aceptación.

## Plan de tests
- [x] build: `scripts/verify.sh` — OK (tests=156 fallos=0)
- [x] unit: `./gradlew testDebugUnitTest` — 156 tests existentes verdes (no se añaden
      tests unitarios nuevos: los ViewModels no conocen rutas)
- [x] dispositivo: `scripts/verify.sh --device` OK y `connectedDebugAndroidTest` verde
      en emulator-5554 y emulator-5556 (API 36). Xiaomi: pendiente — MIUI cancela la
      instalación por USB sin aprobación en pantalla del teléfono.
- [x] flujo manual en dispositivo (emulator-5556): Home > Add > Camera > capturar >
      Guardar > Detail > Edit > guardar (refreshVersion verificado: título pasó a
      "Escapada Valencia II") > back > tab Rutas > marker > info window > Detail >
      tab Satélite > Detail > tab Índice > Exportar (Home modo selección) >
      rotación en Detail (entryId sobrevive a landscape)
- [x] evidencia: capturas en `docs/specs/nav-type-safe/` (evidence_1..18)

## Edge cases
- `export` ausente: default `false` en el data class (equivale al `defaultValue` actual).
- `entryId` inexistente en Detail/Edit: comportamiento actual se conserva
  (`onEntryNotFound` ya navega a Home); no se altera la lógica.
- Rotación y muerte de proceso: los args se serializan al Bundle vía la propia API
  type-safe; verificar rotación en Detail dentro del flujo manual.
- `savedStateHandle` (`capturedImageUri`, `refreshVersion`) sigue funcionando con rutas
  type-safe: verificar captura de cámara y refresh tras editar.
- `hiltViewModel()` dentro de `composable<T>`: sin cambios, mismo scoping por entry.
