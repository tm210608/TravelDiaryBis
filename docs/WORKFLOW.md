# Flujo de trabajo y política de ahorro

## Ciclo SDMD por feature
| Sesión | Comando | Modelo | Resultado |
|--------|---------|--------|-----------|
| A | `/spec` | potente | `docs/specs/<f>/SPEC.md` aprobada |
| B | `/implement-spec` | medio/ligero | código + tests por capas, commits atómicos |
| C | `/verify` + `/handoff` | ligero | criterios marcados con evidencia, `STATE.md` actualizado |

Cada sesión nueva empieza con: "Lee AGENTS.md y docs/STATE.md y continúa".

## Reglas de ahorro
- Cerrar la sesión tras cada `/handoff`.
- Si el contexto supera ~60% o hay ~15 llamadas de exploración sin avance: `/handoff` y reiniciar.
- Salidas de Gradle: solo `scripts/verify.sh` o `| tail -20`. El log completo está en `build/verify.log`.
- Leer con grep/offset, no archivos enteros. Exploración amplia -> subagente de solo lectura.
- No leer `build/`, `.idea/`, `.gradle/`, `local.properties`.
- Una spec = una rama `feature/<nombre>` = un PR.

## Definición de hecho
Build OK + tests OK + instalada en dispositivo + flujo ejecutado + evidencia. Un criterio solo se marca `[x]` si se verificó de verdad.
