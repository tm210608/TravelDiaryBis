# SPEC: Migraciones Room reales + uuid por entrada (v2 → v3)

## Qué construimos
Sustituir `fallbackToDestructiveMigration()` por migraciones reales: `exportSchema=true`
con JSONs de esquema commiteados, una migración explícita **v2→v3** que añade
`uuid TEXT NOT NULL DEFAULT ''` a `travel_entries` y lo puebla con UUIDs generados
por fila. El uuid pasa al modelo de dominio y al `diary.json` de exportación
(campo aditivo; `formatVersion` sigue en 1). Además se monta infra de
`androidTest` con `MigrationTestHelper` que crea una DB v2 real, ejecuta la
migración y valida que las filas sobreviven con uuid poblado.

## Criterios de aceptación (testeables, sin ambigüedad)
- [x] `TravelDatabase` tiene `version = 3`, `exportSchema = true` y **sin**
      `fallbackToDestructiveMigration` ni variantes: sin migración = crash ruidoso.
- [x] `app/schemas/2.json` y `app/schemas/3.json` existen y están commiteados
      (2.json se generó con exportSchema activado ANTES de subir a v3).
- [x] `MIGRATION_2_3` ejecuta `ALTER TABLE travel_entries ADD COLUMN uuid
      TEXT NOT NULL DEFAULT ''` y luego rellena cada fila existente con un
      UUID v4 distinto (generado en Kotlin, no en SQL).
- [x] `TravelEntryEntity`/`TravelEntry` tienen `uuid: String`; las entradas
      nuevas se crean con uuid generado (default `UUID.randomUUID()`).
- [x] `ExportedEntry` incluye `uuid` (default `""`; serializado por
      `encodeDefaults=true`); `formatVersion` permanece en 1.
- [x] El importador mapea `uuid` del ZIP si viene; si falta, la entrada
      importada recibe uuid nuevo (`ifEmpty { UUID.randomUUID() }`).
      Reimportar conserva el uuid de origen (duplicados comparten uuid).
- [x] Test instrumentado `MigrationTest` (androidTest, `MigrationTestHelper`):
      crea DB v2 con 2 filas → `runMigrationsAndValidate(3, MIGRATION_2_3)` →
      las 2 filas siguen y tienen uuid no vacío y distintos entre sí.
      **PASSED en Xiaomi físico** (1 test, 0 fallos).
- [x] Dispositivo: Xiaomi con DB v2 → instalar build v3 → entradas intactas,
      app abre sin crash, export funciona y `diary.json` lleva 2 uuids
      reales distintos (verificado inspeccionando el ZIP).
- [x] `scripts/verify.sh` verde (JVM, 107 tests) y `connectedDebugAndroidTest`
      verde en Xiaomi.

## Fuera de alcance
- MIGRATION_1_2 (v1 nunca existió en este repo; un hipotético v1 sin migración
  crashearía, fail-loud aceptado), migraciones automáticas (`@AutoMigration`),
  borrar duplicados por uuid, usar uuid en UI, cambios en `TravelDao` (ningún
  query lo necesita), CI para connected tests, Room KSP→KAPT o cambio de
  `TravelDatabase.getDatabase` a provider puro de Hilt.

## UI states
Sin cambios de UI ni de ViewModel. La migración corre transparente al abrir
la DB; si fallara, la app crashea en arranque (fail-loud, nunca borrado
silencioso).

## Navegación
Sin cambios.

## Arquitectura
- `app/build.gradle.kts`: `ksp { arg("room.schemaLocation",
  "$projectDir/schemas") }`, `testInstrumentationRunner =
  "androidx.test.runner.AndroidJUnitRunner"`, `androidTest.assets.srcDirs +=
  files("$projectDir/schemas")`, deps androidTest: `room-testing`,
  `androidx.test.ext:junit`, `androidx.test:runner`.
- `data/local/TravelEntryEntity.kt`: `+ uuid: String = ""`.
- `data/local/TravelDatabase.kt`: `version = 3`, `exportSchema = true`,
  `addMigrations(MIGRATION_2_3)`, quitar `fallbackToDestructiveMigration()`.
- `data/local/Migrations.kt` (nuevo): `val MIGRATION_2_3 = object :
  Migration(2,3) { migrate(db) { ALTER TABLE …; cursor por fila → UPDATE con
  UUID.randomUUID() } }`.
- `domain/model/TravelEntry.kt`: `+ uuid: String = UUID.randomUUID().toString()`
  (los constructores existentes siguen compilando; entradas nuevas llevan uuid).
- `domain/model/ExportDocument.kt`: `ExportedEntry + uuid: String = ""`.
- `data/mapper/TravelEntryMapper.kt`: mapear uuid en ambos sentidos.
- `data/export/DiaryExporterImpl.kt`: `uuid = entry.uuid` al exportar.
- `data/export/DiaryImporterImpl.kt`: `uuid = exp.uuid` (default "" del modelo
  genera uno nuevo si ausente — verificar comportamiento real del default en
  data class: el default del CONSTRUCTOR de TravelEntry genera UUID nuevo, así
  que hay que pasar `exp.uuid` explícitamente solo si no vacío, o
  `ifEmpty { UUID.randomUUID() }`).

## Permisos, lifecycle y persistencia
- Sin permisos nuevos. La migración corre en el hilo de Room al primer acceso.
- Datos existentes se conservan SIEMPRE; si no hay migración declarada para
  una versión → `IllegalStateException` en arranque (nunca borrado).
- androidTest corre con `connectedDebugAndroidTest` en el Xiaomi conectado
  (o emulador). NO borra ni toca la DB de la app real: usa `TEST_DB` propia.

## Plan de tests
- [x] build: `scripts/verify.sh` (107 tests, 0 fallos; schemas 2.json+3.json
      generados y commiteados).
- [x] JVM existentes verdes tras añadir uuid (sin tests rotos; `EntryDetails`
      propagado con uuid para que `isDirty` siga funcionando).
- [x] `MigrationTest` instrumentado: v2→v3 preserva filas + uuid distintos —
      PASSED en Xiaomi (TEST-2412DPC0AG, failures=0).
- [x] Dispositivo: Xiaomi v2 → v3 in-place: entradas intactas (2 viajes), app
      abre, export smoke OK con uuids reales en `diary.json`.
- [x] evidencia: `docs/specs/migraciones-room/evidencia-01-*.png` +
      reporte `app/build/outputs/androidTest-results/` (regenerable).

## Edge cases
- DB v2 con 0 filas: migración altera esquema, nada que poblar — pasa igual.
- Fila v2 → uuid generado distinto por fila (no constante).
- Entrada nueva post-v3: uuid del default del dominio (nunca vacío en UI).
- ZIP sin uuid (export pre-v3): entrada importada recibe uuid nuevo.
- Reimportar ZIP con uuid: uuid compartido entre duplicados (aceptado).
- Dispositivo con DB corrupta/incompatible: crash en arranque, no borrado.
