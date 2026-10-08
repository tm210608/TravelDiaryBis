#!/usr/bin/env bash
# Uso: scripts/verify.sh [--device] [--evidence <ruta.png>]
# Compila y ejecuta tests; imprime un resumen corto. Log completo en build/verify.log.
cd "$(dirname "$0")/.." || exit 1
mkdir -p build
LOG=build/verify.log
DEVICE=0; EVIDENCE=""
while [ $# -gt 0 ]; do
  case "$1" in
    --device) DEVICE=1 ;;
    --evidence) EVIDENCE="$2"; shift ;;
  esac
  shift
done

ADB="${ADB:-$LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe}"
[ -x "$ADB" ] || ADB="$(command -v adb 2>/dev/null)"

./gradlew compileDebugKotlin testDebugUnitTest --console=plain -q > "$LOG" 2>&1
RC=$?

TOTAL=0; FAILED=0
for f in app/build/test-results/testDebugUnitTest/*.xml; do
  [ -f "$f" ] || continue
  t=$(grep -o '<testsuite [^>]*' "$f" | grep -o ' tests="[0-9]*"' | grep -o '[0-9]*')
  x=$(grep -o '<testsuite [^>]*' "$f" | grep -o ' failures="[0-9]*"' | grep -o '[0-9]*')
  e=$(grep -o '<testsuite [^>]*' "$f" | grep -o ' errors="[0-9]*"' | grep -o '[0-9]*')
  TOTAL=$((TOTAL + ${t:-0})); FAILED=$((FAILED + ${x:-0} + ${e:-0}))
done

if [ $RC -eq 0 ]; then echo "BUILD+TESTS: OK  (tests=$TOTAL fallos=$FAILED)"
else
  echo "BUILD+TESTS: FALLO (tests=$TOTAL fallos=$FAILED)"
  grep -E '^e: |FAILED|Unresolved|error:' "$LOG" | head -15
fi
echo "Log completo: build/verify.log"

if [ $DEVICE -eq 1 ] && [ $RC -eq 0 ]; then
  if [ -z "$ADB" ] || [ -z "$("$ADB" devices | sed -n '2p' | tr -d '\r')" ]; then
    echo "DISPOSITIVO: no hay adb o dispositivo conectado"; exit 2
  fi
  ./gradlew installDebug --console=plain -q >> "$LOG" 2>&1 || { echo "INSTALL: FALLO"; exit 1; }
  "$ADB" logcat -c
  "$ADB" shell am start -n com.example.traveldiary/.MainActivity > /dev/null
  sleep 4
  CRASH=$("$ADB" logcat -d -s AndroidRuntime:E | head -8)
  [ -n "$CRASH" ] && { echo "DISPOSITIVO: CRASH"; echo "$CRASH"; } || echo "DISPOSITIVO: instalada y lanzada, sin crash"
  [ -n "$EVIDENCE" ] && "$ADB" exec-out screencap -p > "$EVIDENCE" && echo "Evidencia: $EVIDENCE"
fi
exit $RC
