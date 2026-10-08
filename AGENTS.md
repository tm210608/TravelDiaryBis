# TravelDiaryBis

App Android offline-first de diario de viaje (Compose + Material 3, Room, Hilt, CameraX, Google Maps).
Paquete: `com.example.traveldiary`. Idioma de UI y docs: español.

## Empezar una sesión
1. Lee `docs/STATE.md` (estado y siguiente paso). No explores el repo antes.
2. Arquitectura solo si la necesitas: `docs/ARCHITECTURE.md`. Flujo y política de sesiones: `docs/WORKFLOW.md`.

## Stack real
Kotlin 2.0.0, AGP 8.5.0, compileSdk/targetSdk 34, minSdk 26, Compose BOM 2024.06.00,
Room 2.6.1, Hilt 2.51.1, Navigation 2.7.7 (rutas string), maps-compose 4.4.2.
Versiones en `gradle/libs.versions.toml`. No subir versiones sin pedirlo (maps-compose 9.x exige SDK 37).

## Comandos (desde la raíz, Git Bash)
- Verificar todo (resumen corto): `scripts/verify.sh` (`--device` instala y lanza en el móvil)
- Compilar: `./gradlew compileDebugKotlin`
- Tests unitarios: `./gradlew testDebugUnitTest` (`test --tests` NO funciona)
- Instalar: `./gradlew installDebug`
- adb: `$LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe`
- NO hay detekt/ktlint configurados en esta rama.

## Arquitectura (resumen)
- `domain/` (modelo, repositorio interfaz, use cases `@Inject`) <- `data/` (Room, mapper, repo impl) <- `ui/` (screens, `@HiltViewModel`, `sealed interface UiState`).
- DI en `di/DatabaseModule.kt`. Navegación en `navigation/Screen.kt` + `TravelDiaryNavGraph.kt`.
- ViewModel -> use case -> repositorio (nunca el repo directo desde UI).

## Tests
JUnit4 + MockK + Turbine + `TestDispatcherRule` (en `src/test/.../TestDispatcherRule.kt`).
ViewModels con `stateIn(WhileSubscribed)`: usar `backgroundScope.launch { vm.state.collect {} }` + `advanceUntilIdle()`
(con `@OptIn(ExperimentalCoroutinesApi::class)`). Turbine sobre ese StateFlow falla por timeout.

## Reglas
- `local.properties` está ignorado: no leer ni commitear. Contiene `MAPS_API_KEY`.
- No hacer `git push`, borrar ramas/stash ni archivos existentes sin confirmación.
- Commits: `feat|fix|test|docs|chore: mensaje`. Una rama por feature (`feature/...`) desde `develop`.
- No añadir ni quitar comentarios en código salvo que se pida.

## Ahorro de contexto (obligatorio)
- Nunca imprimir logs de Gradle completos: usar `scripts/verify.sh` o `| tail -20`.
- No leer `build/`, `.idea/`, `.gradle/`. Para archivos grandes usar grep o offset/limit.
- Exploración amplia: subagente de solo lectura, no lecturas en serie.
- Al terminar una unidad de trabajo: `/handoff` (reescribe `docs/STATE.md`) y cerrar sesión.

## Flujo SDMD (feature nueva)
`/spec` (docs/specs/<feature>/SPEC.md, aprobación) -> `/implement-spec` -> `/verify` -> `/handoff`.
"Hecho" = build + tests + app en dispositivo + flujo ejecutado + evidencia.
