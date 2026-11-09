# Migración a Android 16 / API 36 — agosto 2026

Registro de la migración hecha el 14–15 de agosto de 2026, con lo que se
verificó y cómo.

## El requisito

Play Console reportaba:

> La aplicación debe orientarse a Android 16 (nivel de API 36) o versiones
> posteriores. A partir del 30 ago 2026, el nivel de API objetivo deberá tener
> 1 año de antigüedad o menos respecto de la versión más reciente de Android.

Fecha límite: **31 de agosto de 2026**. Cumplido con la versión 1.3.1 el 14 de
agosto.

## Lo que realmente cambió

Un solo archivo, cuatro líneas, en `android/build.gradle`:

```diff
- compileSdk = 35
+ compileSdk = 36

- targetSdkVersion 35
- versionCode 3
- versionName "1.2"
+ targetSdkVersion 36
+ versionCode 5
+ versionName "1.3.1"
```

Eso fue todo. Los demás frentes del checklist estándar de Android 16 no
aplicaron, y comprobarlo fue el grueso del trabajo.

## Los cinco frentes y por qué ninguno aplicó

### 1. Páginas de 16 KB — ya cumplido

Era el que se esperaba más pesado, porque libGDX empaqueta sus propias `.so`. No
hizo falta tocar nada: **libGDX 1.14.0 ya compila sus nativas con alineación de
16 KB.**

Verificado leyendo las cabeceras de programa ELF de las 12 librerías, tanto en
`android/libs/` como extraídas del AAB final:

```
arm64-v8a/libgdx.so           p_align=16384   OK-16KB
...                                           (12/12)
```

Herramienta: [`tools/elfalign.py`](tools/elfalign.py).

**No subas libGDX por este motivo.** Si alguna vez baja de 1.13.x, vuelve a
correr el script antes de asumir nada.

### 2. Predictive back — resuelto por libGDX

No hay ni un override de `onBackPressed()` en el repo. Y libGDX 1.14.0 ya maneja
predictive back solo: en `DefaultAndroidInput`, cuando llamas
`setCatchKey(Keys.BACK, true)` registra un `OnBackInvokedCallback` que llama
`processor.keyDown(Keys.BACK)`, con lo que los listeners del `Stage` siguen
recibiendo el evento.

Detalle que suele confundir: **esto ya estaba activo antes de la migración.**
Predictive back se activa por defecto desde `targetSdk 35`, que es donde ya
estaba la app. Pasar a 36 no cambió nada aquí.

### 3. Orientación en pantallas grandes — exento

El manifest ya declaraba `android:appCategory="game"` (y `isGame="true"`), y los
juegos están exentos de que Android 16 ignore las restricciones de orientación.
`screenOrientation="landscape"` se quedó como estaba.

Confirmado por la propia consola de Play al subir: los dispositivos admitidos no
cambiaron entre la versión 4 y la 5 — 13,982 teléfonos, 7,046 tablets, 72
Chromebooks, 16 automóviles, antes y después.

### 4. Borde a borde — sin nada que quitar

Cero ocurrencias de `statusBarColor` o `navigationBarColor` en todo el repo. El
tema es `android:Theme.Material.Light.NoActionBar.Fullscreen` sobre una sola
`GLSurfaceView` con `useImmersiveMode = true`. Nada que migrar.

### 5. AGP y Gradle

Al empezar: AGP 8.9.3, Gradle 9.4.0. **A mitad de la sesión, el Upgrade
Assistant de Android Studio los subió por su cuenta** a AGP 8.13.2 y Gradle
9.5.0, y añadió diez flags de compatibilidad a `gradle.properties`
(`android.newDsl=false`, `android.builtInKotlin=false`, etc.).

No fue parte de la migración, pero es un cambio bueno: 8.13.2 soporta
`compileSdk 36` oficialmente. Quedó en el árbol de trabajo.

## Callejón sin salida: los símbolos de depuración nativos

Al subir el bundle, Play avisó:

> Este App Bundle contiene código nativo y no subiste símbolos de depuración.

Es una **advertencia, no un error**: no bloquea la publicación.

Se intentó resolver con lo que dice la documentación:

```groovy
buildTypes { release { ndk { debugSymbolLevel 'SYMBOL_TABLE' } } }
```

**No funciona en este proyecto.** El build pasa pero no produce nada:

```
> Task :android:mergeReleaseNativeDebugMetadata NO-SOURCE
  native_symbol_tables/release → 0 archivos
```

Motivo: AGP solo extrae símbolos del código nativo que **compila el propio
proyecto** vía `externalNativeBuild` (CMake o ndk-build). Las `.so` de libGDX
llegan precompiladas por `jniLibs` y la tarea las ignora. El cambio se revirtió
porque dejaba el build dependiendo del NDK a cambio de nada.

Y aunque funcionara, ganarías poco: las librerías de libGDX vienen despojadas,
solo conservan `.dynsym`, sin `.symtab` ni secciones `.debug_*`.

**La vía que sí funciona es manual**, y no requiere recompilar: zipear las `.so`
con estructura `<abi>/<lib>.so` y subirlas en Play Console → Explorador de
bundles → la versión → Descargas → símbolos de depuración nativos. Ya hay uno
generado en `android/release/native-debug-symbols-v5.zip` (2.3 MB, ignorado por
git). Para regenerarlo en otra versión, ver [04](04-publicacion-play.md).

## Pendiente sin cerrar

### `useLegacyPackaging`

El manifest fusionado sale con `android:extractNativeLibs="true"`, porque AGP usa
empaquetado legacy por defecto cuando `minSdk < 23` (aquí es 21). Las `.so` van
comprimidas y se extraen al instalar.

**Esto no incumple el requisito de 16 KB**, que se mide por la alineación ELF y
ya está bien. Pero la configuración que Google documenta como la correcta es sin
comprimir. Sería una línea dentro del bloque `packagingOptions` existente:

```groovy
jniLibs { useLegacyPackaging = false }
```

Con entrega por AAB no rompe Android 5.x: bundletool comprime las libs en los
APK que genera para dispositivos anteriores a API 23. Se dejó sin aplicar para no
cambiar el artefacto en pleno release.

### Prueba en hardware real

**Nunca se conectó un dispositivo** durante la migración (`adb devices` siempre
vacío). Todo lo verificado fue análisis estático del bundle. Los tres puntos que
conviene probar en un teléfono son los que un build verde no detecta:

1. Que el gesto o botón Atrás cierre diálogos y vuelva desde el mapa.
2. Que la imagen llene la pantalla sin barras ni recortes (edge-to-edge).
3. Que carguen las partidas guardadas — el release lleva minificación con
   `proguard-android-optimize.txt` y Room usa clases generadas que R8 podría
   recortar. La variante `debug` **no** valida esto porque no minifica.

## Próximo requisito de nivel de API

Según el blog oficial de Android Developers (febrero de 2026), Google Play exigirá
**targetSdk 37 (Android 17) a partir de agosto de 2027** para apps nuevas y
actualizaciones. Al 9 de noviembre de 2026 Google aún no publica el día exacto en
sus páginas de políticas; históricamente ha sido el 31 de agosto.

La plataforma `android-37.0` ya está instalada en `D:\Android\Sdk`. Cuando toque
migrar, revisa primero los cambios de redimensionamiento y orientación de
Android 17 descritos en ese blog: en Android 16 los juegos (`appCategory="game"`)
quedaron exentos, y hay que confirmar si la exención sigue en Android 17 antes de
asumirlo.

Fuente: <https://android-developers.googleblog.com/2026/02/prepare-your-app-for-resizability-and.html>
