# Entorno local — PC de escritorio de Gabriel

Todo lo de aquí está verificado en esta máquina el 21 de noviembre de 2026, el
día de la migración desde la laptop. Si trabajas en otra PC, nada de esto aplica
tal cual: empieza por [07-migracion-a-otra-pc.md](07-migracion-a-otra-pc.md). Lo
que sigue en la laptop vieja está al final de este archivo.

## Máquina

| | |
|---|---|
| SO | Windows 11 Pro 10.0.26300 |
| Usuario | `gabri` |
| CPU / RAM | AMD Ryzen 5 5600G, 32 GB |
| GPU | NVIDIA GeForce RTX 2060 SUPER (la usa el emulador) |
| Shell disponible | PowerShell (principal) y Git Bash |

**Ojo con `bash` desde PowerShell:** en el PATH resuelve a
`C:\Windows\system32\bash.exe`, que es WSL, no Git Bash. Para los comandos
`./gradlew` de esta documentación usa Git Bash
(`C:\Program Files\Git\bin\bash.exe`), o `.\gradlew.bat` en PowerShell.

## Ruta del proyecto

```
C:\Users\gabri\Documents\GitHub Desktop\EDU_Tycoon
```

Clonado con GitHub Desktop. Remotos:

| Remoto | Repositorio |
|---|---|
| `origin` | `gabrielhuav/EDU_Tycoon`, el que se publica |
| `upstream` | `EmmanuelJuarez14/EDU_Tycoon`, el original del equipo |
| `github-desktop-jesusGoliat` | El fork de Jesús, de donde viene el PR #8 |

GitHub Desktop nombra `pr/<número>` a las ramas de los PRs que vienen de un fork
(por ejemplo `pr/8`).

### ⚠️ Restricción crítica: rutas sin caracteres no ASCII

El Android Gradle Plugin **rechaza rutas con acentos o caracteres no ASCII**. En
esta PC la carpeta de GitHub Desktop no los tiene, así que el repo vive ahí. **Los
espacios sí funcionan**: el `bundleRelease` del 21 nov 2026 compiló sin problemas
con `GitHub Desktop` en la ruta.

En la laptop vieja la carpeta de GitHub Desktop era
`C:\Users\gabri\OneDrive\Imágenes\Documentos\GitHubDesktop\`, con acento, y por
eso el repo vivía en `C:\Users\gabri\AndroidStudioProjects\`. Si alguna vez
mueves el proyecto, tiene que ser a una ruta ASCII pura.

## Variables de entorno

| Variable | Valor | Ámbito |
|---|---|---|
| `JAVA_HOME` | `C:\Program Files\Amazon Corretto\jdk21.0.5_11` | Sistema |
| `ANDROID_HOME` y `ANDROID_SDK_ROOT` | `G:\Android\Sdk` | Usuario |
| `ANDROID_AVD_HOME` | `D:\Android\avd` | Usuario |
| `GRADLE_USER_HOME` | `G:\GradleUserHome` | Usuario |

`local.properties` no existe hasta que Android Studio abre el proyecto; mientras
tanto Gradle toma el SDK de `ANDROID_HOME`. Cuando se cree debe apuntar a
`G:\Android\Sdk`. Está en `.gitignore`: no lo commitees.

## Herramientas instaladas

### JDK

```
JAVA_HOME = C:\Program Files\Amazon Corretto\jdk21.0.5_11
versión   = OpenJDK 21.0.5 LTS (Corretto 21.0.5.11.1)
```

En la misma carpeta también está `jdk17.0.6_10`, que no se usa.

**No hace falta JDK 17.** El proyecto compila bytecode Java 17
(`sourceCompatibility 17`, `jvmTarget JVM_17`), pero eso es independiente del JDK
que ejecuta Gradle. Corretto 21 funciona.

### Android SDK

```
G:\Android\Sdk
```

| Componente | Versiones instaladas (21 nov 2026) |
|---|---|
| Platforms | android-34, 35, **36**, 36.1 |
| Build-tools | 33.0.1, 34.0.0, 35.0.0, **36.0.0**, 36.1.0 |
| NDK | 28.2 |
| platform-tools | `G:\Android\Sdk\platform-tools\adb.exe` (37.0.1) |
| Emulador | `G:\Android\Sdk\emulator\emulator.exe` (37.2.13) |
| cmdline-tools | `latest` = 23.0 |
| Imágenes de sistema | android-35 Google Play x86_64 y **android-36 Google Play 16 KB x86_64** |

`adb`, `emulator` y `avdmanager` **no están en el PATH**: usa las rutas completas.

**`sdkmanager` está obsoleto desde cmdline-tools 23.0.** Imprime un aviso y pasa
todo al nuevo Android CLI (`G:\Android\Sdk\cmdline-tools\latest\bin\android.exe`),
y la opción `--licenses` ya no hace falta. Los paquetes se escriben con barras:

```bash
android sdk install system-images/android-36/google_apis_playstore_ps16k/x86_64
```

`avdmanager` sigue funcionando como antes.

Android 17 (API 37) **no está instalado**. No hace falta para compilar hoy, pero
es la siguiente meta de Play: ver "Próximo requisito de nivel de API" en
[03-migracion-android-16.md](03-migracion-android-16.md).

El NDK está instalado pero **este proyecto no lo usa**: libGDX trae sus `.so` ya
compiladas y no hay `externalNativeBuild`.

### Emuladores

Los AVD viven en `D:\Android\avd` (variable `ANDROID_AVD_HOME`), aunque el SDK
esté en `G:`.

| AVD | Uso |
|---|---|
| **`EduTycoon_API36_16KB`** | **El de este proyecto.** Pixel 5, Android 16 (API 36), x86_64, kernel con páginas de 16 KB. Creado el 21 nov 2026; `getconf PAGE_SIZE` da `16384` |
| `Medium_Phone_API_35`, `Nexus`, `Small_Phone_API_35` | Genéricos, Android 15 |

Se creó con:

```bash
avdmanager create avd -n EduTycoon_API36_16KB -k "system-images;android-36;google_apis_playstore_ps16k;x86_64" -d pixel_5
```

El `config.ini` que deja `avdmanager` dice `hw.gpu.enabled=no`, pero el emulador
usa la RTX 2060 SUPER de todos modos (su log lo confirma en `GPU Renderer`). El
primer arranque en frío tarda un par de minutos. Para arrancarlo:

```bash
G:/Android/Sdk/emulator/emulator.exe -avd EduTycoon_API36_16KB
```

**Los datos del emulador persisten entre arranques.** Para empezar como un
usuario que instala por primera vez, agrega `-wipe-data`.

**Si hay más de un emulador abierto, fija el dispositivo.** Los puertos se asignan
por orden de arranque (`emulator-5554`, `emulator-5556`...), así que no supongas
cuál es el tuyo: compruébalo con `adb -s <serie> emu avd name`. Y antes de correr
`connectedAndroidTest`, exporta `ANDROID_SERIAL=<serie>`, porque Gradle ejecuta
los tests **en todos los dispositivos conectados**. El 15 nov 2026, en la laptop
vieja, se ejecutaron por error en el emulador de otro proyecto; Gradle desinstaló
la app al terminar y no quedó nada, pero no debe repetirse.

Sirve para comprobar en ejecución el requisito de 16 KB: si las nativas no
estuvieran alineadas, la app no arrancaría en este emulador.

### Otras herramientas

| Herramienta | Estado en esta PC |
|---|---|
| Git for Windows | 2.45.2, en `C:\Program Files\Git` |
| GitHub CLI | 2.97.0, con la sesión de `gabrielhuav` (permiso `ADMIN` en el repo) |
| bundletool | **No instalado.** Se baja de <https://github.com/google/bundletool/releases> cuando haga falta |
| scrcpy | **No instalado** |
| Keystore de depuración | `C:\Users\gabri\.android\debug.keystore` |

`bundletool` permite instalar en un emulador exactamente los APK que Play
generaría a partir del AAB; ver [04-publicacion-play.md](04-publicacion-play.md).

### Android Studio

```
C:\Program Files\Android\Android Studio
```

Versión 2026.2.2. Se usa para dos cosas que no se hacen por línea de comandos:
correr la app en un dispositivo desde el IDE y **generar el bundle firmado**
(Build → Generate Signed App Bundle).

### Python

```
Python 3.10.11   (C:\Users\gabri\AppData\Local\Programs\Python\Python310, como `python` en el PATH)
```

Los scripts de `tools/` están escritos para 3.7, sin dependencias externas, y
corren igual con 3.10 (`checkaab.py` verificado el 21 nov 2026).

### Gradle

No hay Gradle instalado globalmente; se usa el wrapper del proyecto
(`./gradlew` en Git Bash, `.\gradlew.bat` en PowerShell). El caché vive en
`G:\GradleUserHome` (variable `GRADLE_USER_HOME`).

No hay scripts de init (`G:\GradleUserHome\init.d` no existe), pero **sí hay un
`G:\GradleUserHome\gradle.properties` global**, que en la laptop vieja no
existía. No se ha revisado su contenido porque podría tener credenciales: antes
de dar por hecho que todo lo que afecta el build está en el repo, que Gabriel
confirme qué contiene.

Un `:android:bundleRelease` desde cero tardó 1 min 48 s en esta PC.

## Material de la Play Store y firma

**La llave de subida todavía no está en esta PC.** Gabriel la copia por USB al
final de la integración de los PRs. Destino recomendado, fuera del repo:

```
C:\Users\gabri\.android\keystores\edutycoon-upload.p12
```

Cuando esté copiada, actualiza esta sección y
[04-publicacion-play.md](04-publicacion-play.md), y que Gabriel verifique la
huella (sección 4 de [07](07-migracion-a-otra-pc.md)). Si no aparece donde debe,
**pregunta a Gabriel antes de buscarla por el disco.**

El resto del material (las evidencias de `capturas\`, `logs\` y `artefactos\`, y
los prompts de traspaso) sigue en la laptop vieja. Nada de eso debe copiarse al
repo ni a un artefacto ni a un chat.

La llave es un almacén **PKCS12** (empieza con `30 82`), 2628 bytes, alias `KEY0`.
Es la llave de subida real de la app publicada — verificado comparando su huella
con la del AAB que ya estaba en Play. Detalles en
[04-publicacion-play.md](04-publicacion-play.md).

**La contraseña no está documentada en ningún archivo de este repo, y así debe
seguir. No la pidas, no la leas, no la escribas.**

El `.gitignore` ignora `*.jks`, `*.keystore`, `*.p12`, el nombre `Edu_Tycoon` y
las carpetas `EduTycoon PlayStore/` y `Apps Google Play Store/`, por si alguna
termina dentro del repo por error. Aun así, guárdala siempre fuera del repo, y
conserva una copia cifrada fuera de las dos PCs.

## Laptop vieja (referencia)

Hasta el 21 nov 2026 se trabajó en una laptop con Windows 11 Pro 10.0.26200,
usuario `gabri`. Ahí sigue lo que no viaja con `git clone`:

| Qué | Ruta en la laptop |
|---|---|
| Proyecto | `C:\Users\gabri\AndroidStudioProjects\EDU_Tycoon` |
| SDK, AVD y caché de Gradle | `D:\Android\Sdk`, `D:\Android\avd`, `D:\GradleUserHome` |
| JDK | `C:\Program Files\Amazon Corretto\jdk21.0.4_7` |
| Material de Play y la llave | `C:\Users\gabri\Downloads\Apps Google Play Store\EduTycoon PlayStore\` |
| bundletool 1.18.3 y scrcpy 5.0.1 | `C:\Users\gabri\.autech\herramientas\` |

En `EduTycoon PlayStore\` están la llave (`Edu_Tycoon`, sin extensión), un `.txt`
con material sensible que **no se abre ni se lee**,
`PROMPT_01_CONTINUAR_EDUTYCOON_1.4.txt` y las carpetas `capturas\`, `logs\` y
`artefactos\`. Antes de noviembre de 2026 la carpeta estaba en
`Downloads\EduTycoon PlayStore\`; si un documento viejo apunta ahí, está
desactualizado.

La carpeta padre `Apps Google Play Store\` guarda además llaves de **otras apps**
(`bitbattles.jks` de BitBattles y `llave_pow.jks` de Politécnico Open World).
**Ninguna sirve para EDU Tycoon**: Play rechaza un AAB firmado con otra llave.

Riesgos abiertos de esa carpeta mientras la llave siga ahí:

1. **La llave y su contraseña están juntas.** Cualquiera con acceso al disco
   obtiene las dos y puede firmar actualizaciones como si fuera el desarrollador.
2. **Está en `Downloads`**, que es donde escribe el navegador y la primera carpeta
   que la gente vacía sin mirar. Un borrado accidental sin respaldo significa que
   la app **nunca más se puede actualizar**.

## Otros proyectos de Gabriel en esta máquina

Útiles como referencia; no toques nada de ahí sin pedirlo. Hay copias tanto en
`C:\Users\gabri\AndroidStudioProjects\` como en
`C:\Users\gabri\Documents\GitHub Desktop\`:

```
BattleNaval_V2         ← BitBattles
PolitecnicoOpenWorld
```

En la laptop vieja, `BattleNaval_V2\docs\ANDROID-16.md` documentaba la misma
migración a API 36 hecha en una app de layouts XML; en esta PC ese archivo no
está en ninguna de las dos copias. Sirve de contraste: ahí los problemas fueron
predictive back, edge-to-edge y orientación en pantallas grandes. En EDU Tycoon
**ninguno de los tres aplicó**, por ser libGDX y estar declarado como juego.
