# Publicación en Google Play

## La app

| | |
|---|---|
| Nombre en Play | **EDU Tycoon** |
| Paquete | `io.moviles.IPN_Tycoon` |
| Estado | Prueba cerrada (Alpha) activa · Producción inactiva |
| Alcance | 18 países o regiones |
| Cuenta | Gabriel tiene acceso a Play Console |

## Llave de subida

**Sin esta llave no se puede publicar una actualización. No hay alternativa.**

| | |
|---|---|
| Archivo | `C:\Users\gabri\Downloads\Apps Google Play Store\EduTycoon PlayStore\Edu_Tycoon` (sin extensión) |
| Formato | PKCS12 |
| Alias | `KEY0` |
| Propietario | `C=55749, ST=Mexico, L=Tecamac, CN=Kevin Zarco Sosa` |
| Válido | 12 jun 2026 → 6 jun 2051 |
| SHA-256 | `C3:0C:C5:09:E8:75:69:C2:8B:04:45:3D:CA:A1:DD:B8:88:B0:DD:B0:7D:93:F6:29:A1:FE:22:B4:80:17:2B:38` |
| SHA-1 | `E7:04:FA:DE:77:FD:00:F5:CE:B6:28:4F:7F:C0:5A:34:37:A0:84:40` |

La creó **Kevin Zarco Sosa** (`kzarcos1900@alumno.ipn.mx`), colaborador del
proyecto, y se la compartió a Gabriel. La contraseña la tiene Gabriel y **no está
documentada en ningún archivo de este repo, a propósito. No la pidas ni la
escribas.**

Para confirmar que un almacén candidato es el correcto, compara su huella con la
de arriba (esto pide la contraseña, córrelo tú, no la IA):

```bash
keytool -list -v -keystore "C:\Users\gabri\Downloads\Apps Google Play Store\EduTycoon PlayStore\Edu_Tycoon" -alias KEY0
```

Para verificar un AAB **ya firmado** no hace falta contraseña: el certificado
viaja dentro del bundle. Usa `tools/checkaab.py`.

Para verificar que un AAB ya firmado lleva la llave correcta, sin necesidad de
contraseña, extrae el certificado del bundle y compáralo. Ver
[tools/](tools/).

## Historial de versiones

| versionName | versionCode | Fecha | targetSdk |
|---|---|---|---|
| 1.0 | 1 | 12 jun 2026 | — |
| 1.1 | 2 | 13 jun 2026 | — |
| 1.2 | 3 | 13 jun 2026 | 35 |
| 1.3 | 4 | 22 jun 2026 | 35 |
| 1.3.1 | 5 | 14 ago 2026 (Alpha), luego Producción | 36 |

**La siguiente versión que se suba debe usar `versionCode 6` o mayor.**
Confirma siempre el último código en Play Console antes de subir.

Trampa importante: **el repo y Play se desincronizan.** La versión 1.3 (código 4)
nunca se commiteó — Kevin la construyó en local. Durante la migración, git decía
`versionCode 3 / 1.2` mientras Play ya tenía la 1.3. **Nunca deduzcas el
siguiente versionCode del repo; míralo en Play Console** (Prueba cerrada →
Historial de versiones). Si repites un código, Play rechaza el AAB al instante.

## Cómo se hace un release

No hay `signingConfig` en el build, así que `./gradlew :android:bundleRelease`
produce un AAB **sin firmar**. La firma es manual.

1. **Subir la versión** en `android/build.gradle`: `versionCode` (mayor que el
   último de Play) y `versionName`.
2. **Sincronizar Gradle en Android Studio** (el icono del elefante). Saltarse
   esto ya causó un error real: el bundle salió con el `versionName` viejo
   porque Studio usó una sincronización obsoleta.
3. **Build → Generate Signed App Bundle**, con la llave de arriba. La salida
   suele quedar en `android/release/android-release.aab`.
4. **Verificar el bundle antes de subirlo** con los scripts de `tools/`:
   versionCode, versionName, targetSdk y la huella de la firma.
5. **Subir** en Play Console → Prueba cerrada → Alpha → Crear versión.

### Regenerar el zip de símbolos nativos (opcional)

Silencia la advertencia de Play sobre símbolos de depuración. Ver
[03-migracion-android-16.md](03-migracion-android-16.md) para por qué no se
puede automatizar en el build:

```bash
python README_for_IAs/tools/mksymbols.py
```

Se sube en Play Console → Explorador de bundles → la versión → Descargas.

## Cosas a vigilar

- **Tamaño.** La descarga para instalaciones nuevas iba en **161 MB** en la
  1.3.1. El tope de Play es 200 MB por dispositivo. Quedan ~39 MB de margen y el
  juego sigue creciendo en assets.
- **El AAB pesa ~160 MB y GitHub rechaza archivos de más de 100 MB.**
  `android/release/` está en `.gitignore` justamente por eso.
- **Advertencia de símbolos nativos**: aparecerá en cada subida mientras no se
  suba el zip a mano. No bloquea.

## Acceso a producción

**Solicitud enviada el 15 de agosto de 2026 a las 6:04 p.m. En revisión.**

Las nueve respuestas enviadas, la coherencia entre ellas y qué hacer si rechazan
están en [05-acceso-a-produccion.md](05-acceso-a-produccion.md).
