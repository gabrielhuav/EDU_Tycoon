# Entorno local — laptop Windows de Gabriel

Todo lo de aquí está verificado en esta máquina el 15 de agosto de 2026. Si
trabajas en otra PC, nada de esto aplica tal cual.

## Máquina

| | |
|---|---|
| SO | Windows 11 Pro 10.0.26200 |
| Usuario | `gabri` |
| Shell disponible | PowerShell (principal) y Git Bash |

## Rutas del proyecto

```
C:\Users\gabri\AndroidStudioProjects\EDU_Tycoon
```

### ⚠️ Restricción crítica: rutas sin caracteres no ASCII

El Android Gradle Plugin **rechaza rutas con acentos o caracteres no ASCII**. Por
eso este repo NO vive en la carpeta de GitHub Desktop, que es:

```
C:\Users\gabri\OneDrive\Imágenes\Documentos\GitHubDesktop\    ← tiene acento, no sirve
```

Si alguna vez mueves el proyecto, tiene que ser a una ruta ASCII pura. Los demás
proyectos Android de Gabriel viven por la misma razón en
`C:\Users\gabri\AndroidStudioProjects\`.

## Herramientas instaladas

### JDK

```
JAVA_HOME = C:\Program Files\Amazon Corretto\jdk21.0.4_7
java      = C:\Program Files\Amazon Corretto\jdk21.0.4_7\bin\java.exe
versión   = OpenJDK 21.0.4 LTS (Corretto 21.0.4.7.1)
```

**No hace falta JDK 17.** El proyecto compila bytecode Java 17
(`sourceCompatibility 17`, `jvmTarget JVM_17`), pero eso es independiente del JDK
que ejecuta Gradle. Corretto 21 funciona.

### Android SDK

```
D:\Android\Sdk
```

**Se movió aquí en septiembre de 2026.** La ruta vieja
(`C:\Users\gabri\AppData\Local\Android\Sdk`) ya no existe; si una
herramienta la busca ahí, falla con "No such file". La variable de usuario
`ANDROID_HOME` y `local.properties` (que está en `.gitignore`, no lo commitees)
apuntan a la ruta nueva.

| Componente | Versiones instaladas (8 nov 2026) |
|---|---|
| Platforms | android-33, 34, 35, **36**, 36.1, **37.0** |
| Build-tools | 30.0.3, 34.0.0, 35.0.0, **36.0.0** |
| NDK | 25.1, 26.3, 27.0, 28.2, 30.0 |
| CMake | 3.22.1 |
| platform-tools | `D:\Android\Sdk\platform-tools\adb.exe` |
| Emulador | `D:\Android\Sdk\emulator\emulator.exe` (37.2.13) |
| avdmanager | `D:\Android\Sdk\cmdline-tools\latest\bin\avdmanager.bat` |

`android-37.0` es **Android 17**. No hace falta para compilar hoy, pero es la
siguiente meta de Play: ver "Próximo requisito de nivel de API" en
[03-migracion-android-16.md](03-migracion-android-16.md).

El NDK está instalado pero **este proyecto no lo usa**: libGDX trae sus `.so` ya
compiladas y no hay `externalNativeBuild`.

### Emuladores

Los AVD viven en `D:\Android\avd` (variable de usuario `ANDROID_AVD_HOME`). La
nota `D:\Android\avd\LEEME_MOVIMIENTO_AVD.md` documenta el traslado.

| AVD | Uso |
|---|---|
| **`EduTycoon_API36_16KB`** | **El de este proyecto.** Pixel 5, Android 16, kernel con páginas de 16 KB |
| `AUTECH_C4_API36`, `AUTECH_C4_API36_16KB` | Proyecto AUTECH. No usar |
| `FiveNightsESCOM_16KB`, `FiveNightsESCOM_Test` | Proyecto Five Nights. No usar |
| `HolaMundo_Phone`, `Medium_Phone` | Genéricos |

`EduTycoon_API36_16KB` tiene `disk.dataPartition.path=<temp>`: **cada arranque es
un dispositivo limpio**, sin la app ni partidas de la sesión anterior. Es a
propósito, para probar como un usuario que instala por primera vez. Arranque:

```bash
D:/Android/Sdk/emulator/emulator.exe -avd EduTycoon_API36_16KB
```

Sirve para comprobar en ejecución el requisito de 16 KB: si las nativas no
estuvieran alineadas, la app no arrancaría en este emulador.

### Otras herramientas

| Herramienta | Ruta |
|---|---|
| bundletool 1.18.3 | `C:\Users\gabri\.autech\herramientas\bundletool-all-1.18.3.jar` |
| scrcpy 5.0.1 (espejo de teléfono) | `C:\Users\gabri\.autech\herramientas\scrcpy-win64-v5.0.1\` |
| Keystore de depuración | `C:\Users\gabri\.android\debug.keystore` |

Viven en la carpeta de AUTECH porque se instalaron para ese proyecto, pero son
genéricas. `bundletool` permite instalar en un emulador exactamente los APK que
Play generaría a partir del AAB; ver [04-publicacion-play.md](04-publicacion-play.md).

### Android Studio

```
C:\Program Files\Android\Android Studio
```

Versión 2026.2.2. Se usa para dos cosas que no se hacen por línea de comandos:
correr la app en un dispositivo desde el IDE y **generar el bundle firmado**
(Build → Generate Signed App Bundle).

### Python

```
Python 3.7.9   (disponible como `python` en el PATH)
```

Los scripts de `tools/` están escritos para 3.7, sin dependencias externas.

### Gradle

No hay Gradle instalado globalmente; se usa el wrapper del proyecto
(`./gradlew` en Git Bash, `.\gradlew.bat` en PowerShell). El caché vive en
`C:\Users\gabri\.gradle\`.

No hay scripts de init en `~/.gradle/init.d/` ni un `~/.gradle/gradle.properties`
global — se verificó, no existen. Todo lo que afecta el build está en el repo.

## Material de la Play Store y firma

Todo lo importante del proyecto que **no** vive en el repositorio está aquí:

```
C:\Users\gabri\Downloads\Apps Google Play Store\EduTycoon PlayStore\
```

Nada de esa carpeta debe copiarse al repo ni a un artefacto ni a un chat.

| Contenido | Qué es |
|---|---|
| `Edu_Tycoon` | **La llave de subida.** Sin extensión |
| un archivo `.txt` | Material sensible. **No lo abras ni lo leas** |
| `PROMPT_01_CONTINUAR_EDUTYCOON_1.4.txt` | Prompt de traspaso para retomar la publicación de la 1.4 |
| `capturas\` | Evidencias de prueba numeradas, con índice en `capturas.md` |
| `logs\` | Logs de compilación |
| `artefactos\` | Reservada para el AAB firmado y su verificación |

**Historial de ubicaciones.** En agosto de 2026 la carpeta estaba en
`C:\Users\gabri\Downloads\EduTycoon PlayStore\`; en noviembre Gabriel la movió a
`Downloads\Apps Google Play Store\`. Si una herramienta o un documento viejo
apunta a la ruta anterior, está desactualizado. Si la llave no aparece en la ruta
actual, **pregunta a Gabriel antes de buscarla por el disco.**

La carpeta padre `Apps Google Play Store\` también guarda material de **otras
apps**: `APP BitBattles PlayStore\` (con `bitbattles.jks`) y `llave_pow.jks` de
Politécnico Open World. **Ninguna de esas llaves sirve para EDU Tycoon**: cada app
tiene la suya y Play rechaza un AAB firmado con otra.

La llave es un almacén **PKCS12** (empieza con `30 82`), 2628 bytes, alias `KEY0`.
Es la llave de subida real de la app publicada — verificado comparando su huella
con la del AAB que ya estaba en Play. Detalles en
[04-publicacion-play.md](04-publicacion-play.md).

**La contraseña no está documentada en ningún archivo de este repo, y así debe
seguir. No la pidas, no la leas, no la escribas.**

### Riesgos abiertos de esta carpeta

1. **La llave y su contraseña están juntas.** Cualquiera con acceso al disco
   obtiene las dos y puede firmar actualizaciones como si fuera el desarrollador.
   Conviene separarlas.
2. **Está en `Downloads`**, que es donde escribe el navegador y la primera carpeta
   que la gente vacía sin mirar. Un borrado accidental sin respaldo significa que
   la app **nunca más se puede actualizar**: Play rechaza cualquier AAB firmado
   con otra llave.
3. **La llave no tiene extensión**, así que `*.jks` no la atraparía. Desde
   noviembre de 2026 el `.gitignore` la cubre por nombre (`Edu_Tycoon`) y también
   ignora las carpetas `EduTycoon PlayStore/` y `Apps Google Play Store/` si
   alguien las copia dentro del repo. Aun así, guárdala siempre fuera del repo.

Respaldo recomendado: una copia cifrada fuera de este equipo.

## Otros proyectos de Gabriel en esta máquina

Útiles como referencia, no toques nada de ahí sin pedirlo:

```
C:\Users\gabri\AndroidStudioProjects\BattleNaval_V2         ← BitBattles
C:\Users\gabri\AndroidStudioProjects\PolitecnicoOpenWorld
```

`BattleNaval_V2\docs\ANDROID-16.md` documenta la misma migración a API 36 hecha
en una app de layouts XML. Sirve de contraste: ahí los problemas fueron
predictive back, edge-to-edge y orientación en pantallas grandes. En EDU Tycoon
**ninguno de los tres aplicó**, por ser libGDX y estar declarado como juego.
