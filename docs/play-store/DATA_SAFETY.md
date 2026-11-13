# Respuestas del cuestionario Data Safety (Play Console)

Guía exacta para rellenar el formulario "Seguridad de los datos" en
Play Console. Basado en el código real de la app (actualizado el 13-11-2026,
tras migrar el mapa a OpenStreetMap).

## Pantalla inicial
- ¿La app recopila o comparte datos de usuario? → **Sí** (por la ubicación
  que se envía a Open-Meteo; ver abajo).

## Ubicación
- **Ubicación aproximada / Ubicación precisa** → marcar **Sí**
  - ¿Se recopila o se comparte? → **Se comparte**
  - Finalidad → **Funcionalidad de la app** (clima de la entrada en Open-Meteo)
  - ¿Es obligatoria? → **Opcional** (la app funciona sin permiso de ubicación)
  - ¿Se procesa de forma efímera? → **Sí** (no se almacena en servidores)

## Fotos / archivos
- En Data Safety **no se declara** como "recopilado" lo que el usuario crea y
  se queda solo en el dispositivo, siempre que no se transmita. Como las
  fotos no salen del móvil (salvo exportación manual iniciada por el
  usuario), se puede dejar sin marcar. Si Play insiste, marcar
  "Fotos → recopilada → opcional → no se comparte".

## Resto de categorías
- Datos personales (nombre, email, IDs…) → **No**
- Finanzas → **No**
- Salud → **No**
- Mensajes → **No**
- Archivos y documentos → **No** (mismo criterio que fotos)
- Calendario, contactos → **No**
- Actividad en la app → **No**
- Navegación/búsqueda → **No**
- Audio → **No**
- Identificadores de dispositivo → **No** (ya no se usa el SDK de Google Maps).

## Preguntas finales
- ¿Los datos se cifran en tránsito? → **Sí** (HTTPS en Open-Meteo y OpenStreetMap)
- ¿Hay forma de solicitar eliminación de datos? → **Sí** — explicar:
  "Todos los datos se guardan localmente; desinstalar la app los elimina por
  completo."
- ¿Compromiso con las familias? → No es app dirigida a niños → **No**

## Notas
- `allowBackup=true`: la copia de seguridad la gestiona Android/Google, no
  hace falta declararla como recopilación propia.
- El mapa usa OpenStreetMap (`osmdroid`) sin SDK ni API key de Google Maps.
