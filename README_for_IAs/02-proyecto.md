# El proyecto

> Si es tu primer día en este código, lee antes
> [06-guia-para-desarrolladores.md](06-guia-para-desarrolladores.md). Este archivo
> es la referencia de versiones y comandos; aquél explica cómo funciona el juego.

## Estructura

Proyecto libGDX multi-módulo con Gradle en **Groovy** (`build.gradle`, no `.kts`).

```
EDU_Tycoon/
├── build.gradle              ← raíz: buildscript, AGP, config común de subproyectos
├── settings.gradle           ← include 'core', 'android'
├── gradle.properties         ← versiones de librerías (gdxVersion, kotlinVersion, ...)
├── local.properties          ← sdk.dir  (IGNORADO por git, no lo commitees)
├── assets/                   ← assets compartidos; el módulo android los toma de aquí
├── core/                     ← lógica del juego, Kotlin puro, multiplataforma
│   └── src/main/kotlin/io/moviles/IPN_Tycoon/
├── android/                  ← módulo Android
│   ├── build.gradle          ← ⭐ aquí viven compileSdk, targetSdk, versionCode
│   ├── AndroidManifest.xml   ← ⭐ en la raíz del módulo, NO en src/main/
│   ├── proguard-rules.pro
│   ├── res/
│   ├── libs/<abi>/*.so       ← generadas por copyAndroidNatives, ignoradas por git
│   ├── release/              ← salida firmada manual (IGNORADA por git)
│   └── src/main/kotlin/io/moviles/IPN_Tycoon/android/
└── PS/                       ← material de la Play Store (capturas, textos)
```

Ojo con dos cosas que no son como en un proyecto Android típico:

- **El `targetSdk` está en `android/build.gradle`**, no en un `app/build.gradle.kts`
  ni en `gradle.properties`.
- **El manifest está en `android/AndroidManifest.xml`**, en la raíz del módulo. Lo
  declara `sourceSets.main.manifest.srcFile`.

## Versiones (15 ago 2026)

| | Versión | Dónde se declara |
|---|---|---|
| AGP | 8.13.2 | `build.gradle` raíz |
| Gradle | 9.5.0 | `gradle/wrapper/gradle-wrapper.properties` |
| Kotlin | 2.2.10 | `gradle.properties` → `kotlinVersion` |
| libGDX | 1.14.0 | `gradle.properties` → `gdxVersion` |
| compileSdk | 36 | `android/build.gradle` |
| targetSdk | 36 | `android/build.gradle` |
| minSdk | 21 | `android/build.gradle` |
| versionCode | 5 | `android/build.gradle` |
| versionName | 1.3.1 | `android/build.gradle` |

Otras librerías relevantes: Room `2.7.0-alpha13` (con **kapt**, no KSP),
Lifecycle `2.8.7`, VisUI `1.5.5`, libKTX `1.13.1-rc1`, Ashley, Box2D, FreeType.

## Manifest

```xml
<application android:appCategory="game" android:isGame="true" ...>
  <activity android:name="...android.AndroidLauncher"
            android:screenOrientation="landscape"
            android:configChanges="keyboard|keyboardHidden|navigation|orientation|screenSize|screenLayout"
            android:exported="true">
```

`appCategory="game"` es importante: **exime a la app del requisito de Android 16
de ignorar las restricciones de orientación en pantallas grandes**. Por eso
`screenOrientation="landscape"` sigue siendo válido. No lo quites.

## Nativas de libGDX

`android/build.gradle` define una configuración `natives` y la tarea
`copyAndroidNatives`, que extrae las `.so` de los jars de libGDX a
`android/libs/<abi>/`. Se engancha automáticamente a las tareas
`merge*JniLibFolders`.

ABIs empaquetadas: `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`.
Librerías: `libgdx.so`, `libgdx-box2d.so`, `libgdx-freetype.so`.

**Estas `.so` vienen ya despojadas de símbolos** (solo tienen `.dynsym`). Eso
tiene consecuencias, ver [03](03-migracion-android-16.md).

## Comandos

Desde la raíz, con Git Bash:

```bash
./gradlew :android:bundleRelease
```

```bash
./gradlew :android:assembleDebug
```

```bash
./gradlew :android:installDebug
```

Notas importantes:

- **El AAB que sale de `bundleRelease` NO va firmado.** No hay `signingConfig` en
  el build. La firma se hace a mano desde Android Studio. Ver
  [04-publicacion-play.md](04-publicacion-play.md).
- `gradle.properties` tiene `org.gradle.logging.level=quiet`, así que los builds
  no imprimen casi nada. Para ver avisos:
  `-Dorg.gradle.logging.level=lifecycle --warning-mode=all`.
- `org.gradle.daemon=false` y `-Xmx1G`. Un `bundleRelease` completo tarda varios
  minutos por el kapt de Room y R8.

## Manejo del botón Atrás

`GameScreen.kt` llama `Gdx.input.setCatchKey(Input.Keys.BACK, true)` y escucha
`Input.Keys.BACK` en listeners del `Stage`. **No hay ningún override de
`onBackPressed()` en todo el repo** y no debe haberlo: libGDX 1.14.0 ya registra
un `OnBackInvokedCallback` internamente (`DefaultAndroidInput.PredictiveBackHandler`)
que reinyecta `processor.keyDown(Keys.BACK)`. Funciona con predictive back sin
que el proyecto haga nada.
