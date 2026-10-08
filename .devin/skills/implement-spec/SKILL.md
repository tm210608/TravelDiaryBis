---
name: implement-spec
description: Implementar una SPEC.md aprobada de TravelDiaryBis por capas (domain, viewmodel, UI, navegacion) con tests y commits atomicos.
---
# /implement-spec <feature>

1. Rama: `git checkout -b feature/<feature>` desde `develop` si no existe.
2. Lee SOLO `docs/specs/<feature>/SPEC.md` y `docs/ARCHITECTURE.md`. Copia el patron de archivos vecinos (grep, no lecturas enteras).
3. Orden: dependencias -> domain (+test) -> ViewModel (+test) -> UI -> navegacion. Tras cada capa: `scripts/verify.sh`.
4. Commit por capa (`feat:`/`test:`). No pushear.
5. No marques criterios `[x]` aqui; eso es `/verify`.
