---
name: verify
description: Verificar una feature de TravelDiaryBis (build, tests, dispositivo, evidencia) y marcar criterios de la SPEC con honestidad.
---
# /verify <feature>

1. `scripts/verify.sh --device --evidence docs/specs/<feature>/evidence_<n>.png` (nunca `gradlew` con salida completa).
2. Ejecuta el flujo manual de la SPEC con adb (input tap/screencap) y comprueba cada captura leyendo la imagen.
3. En la SPEC marca `[x]` solo lo realmente observado; lo demas queda `[ ]` con el motivo.
4. Si algo falla, resume en <10 lineas y para.
