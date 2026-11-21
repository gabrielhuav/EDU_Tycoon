# Migración a otra PC

Para la IA que abre este repositorio en la **PC nueva**. Léelo completo antes de
actuar. Las rutas de [01-entorno-local.md](01-entorno-local.md) son de la
**laptop vieja** (Windows, usuario `gabri`) y no existen en la nueva hasta que
las verifiques y actualices.

Preparado el 21 de noviembre de 2026.

## 1. Lo que NO viaja con `git clone`

| Qué | Dónde estaba en la laptop vieja | En la PC nueva |
|---|---|---|
| **Llave de subida de EDU Tycoon** (PKCS12, alias `KEY0`, 2,628 bytes, sin extensión) | `C:\Users\gabri\Downloads\Apps Google Play Store\EduTycoon PlayStore\Edu_Tycoon` | Recomendado: `C:\Users\<usuario>\.android\keystores\edutycoon-upload.p12`, **fuera del repo** |
| Contraseña de la llave | La tiene Gabriel. **No está en ningún archivo del repo** | Gestor de contraseñas, nunca junto a la llave |
| Evidencias y prompts privados | `...\EduTycoon PlayStore\` (`capturas\`, `logs\`, `artefactos\`) | Donde Gabriel decida, fuera del repo |
| `local.properties` | Raíz del repo (ignorado por git) | Android Studio lo genera al abrir el proyecto |
| Emulador `EduTycoon_API36_16KB` | `D:\Android\avd` | No se copia: se recrea (sección 4) |
| bundletool 1.18.3 | `C:\Users\gabri\.autech\herramientas\` | Descargar de <https://github.com/google/bundletool/releases> |

**La llave se copia por USB o unidad cifrada. Nunca por el repositorio, un chat,
correo o una nube sin cifrar.** Renombrarla no le cambia nada: la identidad está
en su contenido. Sin esta llave la app no se puede volver a actualizar en Play,
así que conserva además una copia cifrada fuera de las dos PCs.

El `.gitignore` ya ignora `*.jks`, `*.keystore`, `*.p12`, el nombre `Edu_Tycoon` y
las carpetas `EduTycoon PlayStore/` y `Apps Google Play Store/`, por si alguna
termina dentro del repo por error.

## 2. Instalar en la PC nueva

- **Git for Windows** (incluye Git Bash) y **GitHub CLI**. `gh auth login` lo hace
  Gabriel con su cuenta.
- **JDK 21** (Amazon Corretto 21) con `JAVA_HOME`. El proyecto compila bytecode
  Java 17; cualquier JDK 17 o posterior sirve.
- **Android Studio 2026.2 o posterior.** En el SDK Manager:
  - Android 16 (API 36) SDK Platform, Build-Tools 36.0.0, Platform-Tools,
    Emulator y Command-line Tools (latest).
  - Imagen de sistema **Google Play 16 KB Page Size**, API 36, x86_64.
  - Opcional: Android 17 (API 37), para el requisito de agosto de 2027.
- **Python 3.** Los scripts de [tools/](tools/) solo usan la biblioteca estándar.

**Variables de entorno de la laptop vieja.** Allí el SDK, los emuladores y la caché
de Gradle vivían en `D:` mediante variables de usuario. En la PC nueva no hacen
falta salvo que también quieras sacarlos de `C:`:

| Variable | Valor en la laptop vieja |
|---|---|
| `ANDROID_HOME` | `D:\Android\Sdk` |
| `ANDROID_AVD_HOME` | `D:\Android\avd` |
| `GRADLE_USER_HOME` | `D:\GradleUserHome` |
| `JAVA_HOME` | `C:\Program Files\Amazon Corretto\jdk21.0.4_7` |

**La ruta del proyecto no puede tener acentos.** El Android Gradle Plugin falla con
caracteres no ASCII. Si el usuario de Windows los tiene (`C:\Users\José`),
clona en `C:\dev\EDU_Tycoon`.

## 3. Clonar y compilar

```bash
git clone https://github.com/gabrielhuav/EDU_Tycoon.git
```

```bash
cd EDU_Tycoon && git remote add upstream https://github.com/EmmanuelJuarez14/EDU_Tycoon.git
```

```bash
./gradlew :android:bundleRelease
```

```bash
python README_for_IAs/tools/checkaab.py android/build/outputs/bundle/release/android-release.aab
```

Esperado: `targetSdkVersion 36`, `12 de 12 librerias cumplen 16 KB` y
`SIN FIRMAR`, que es normal: la firma se hace en Android Studio.

## 4. Verificaciones

1. **La llave.** Gabriel corre en su propia terminal (pide la contraseña):
   ```bash
   keytool -list -v -keystore "<ruta nueva de la llave>" -alias KEY0
   ```
   El SHA-256 debe ser
   `C3:0C:C5:09:E8:75:69:C2:8B:04:45:3D:CA:A1:DD:B8:88:B0:DD:B0:7D:93:F6:29:A1:FE:22:B4:80:17:2B:38`.
2. **GitHub.** `gh repo view gabrielhuav/EDU_Tycoon --json viewerPermission`
   debe decir `ADMIN`.
3. **Emulador de 16 KB.** Créalo y comprueba que `getconf PAGE_SIZE` dé `16384`:
   ```bash
   avdmanager create avd -n EduTycoon_API36_16KB -k "system-images;android-36;google_apis_playstore_ps16k;x86_64" -d pixel_5
   ```
4. **Actualiza [01-entorno-local.md](01-entorno-local.md)** con las rutas reales de
   la PC nueva, y la ubicación nueva de la llave en
   [04-publicacion-play.md](04-publicacion-play.md).

## 5. Estado del proyecto al migrar

- **Producción:** versión 5 (1.3.1), `targetSdk 36`. La siguiente que se suba
  debe usar **`versionCode 6`**. Confírmalo en Play Console.
- **Rama `main` protegida:** todo cambio entra por PR con aprobación de
  `@gabrielhuav` (`CODEOWNERS`), sin force-push ni borrado. Gabriel es admin y
  puede fusionar lo suyo.
- **PRs abiertos:**

  | PR | Qué trae | Estado |
  |---|---|---|
  | **#8** Instituto Rival (jesusGoliat) | IA rival, migración de Room 4→5 y la documentación de mantenimiento, incluido este archivo | **Integrar primero.** Verificado: 48 tests, 5/5 instrumentados con la migración, release con R8 en Android 16 con 16 KB |
  | **#6** Entrega 2 (JulioCesarCaballero) | Eventos, autosave, música, control de ciclos, CI | Segundo. Tras el #8 debe actualizar su rama: choca en `GameScreen`, `EconomyEngine`, `BuildingInfoWindow` y `pruebas.md` |
  | **#7** Campus completo (FiniteMusic) | Campus, niveles, interiores, audio | **Bloqueado:** +341 MB de imágenes y Play topa en 200 MB de descarga. No permite edición por maintainers |

- **Pendientes:** medir FPS en un teléfono real con la cámara alejada al
  máximo; firmar y verificar la siguiente versión con `checkaab.py`; subir los
  símbolos nativos con `tools/mksymbols.py`; requisito de API 37 en agosto de
  2027 (ver [03](03-migracion-android-16.md)).

## 6. Reglas

- Nada de commits, push, merge ni cierre de PRs sin autorización de Gabriel.
- Las contraseñas las escribe Gabriel. No abras archivos de credenciales.
- Antes de `connectedAndroidTest` con varios emuladores abiertos, fija
  `ANDROID_SERIAL`.
