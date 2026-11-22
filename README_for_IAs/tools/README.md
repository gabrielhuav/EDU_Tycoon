# Herramientas de verificación

Scripts sin dependencias externas, escritos para Python 3.7, la versión de la
laptop vieja; en la PC de escritorio corren igual con 3.10. Se usan desde la raíz
del repo.

Existen porque las herramientas normales no sirven para esto: el manifest dentro
de un AAB va en protobuf y `aapt2` no lo lee, y `bundletool` no está instalado.

## `checkaab.py` — verificar un bundle antes de subirlo

El más útil. Comprueba de un golpe versionCode, versionName, targetSdk,
`appCategory`, si está firmado, y la alineación de 16 KB de las nativas.

```bash
python README_for_IAs/tools/checkaab.py android/release/android-release.aab
```

Salida esperada de un bundle listo para subir:

```
-- Manifest --
   package          io.moviles.IPN_Tycoon
   versionCode      5
   versionName      1.3.1
   minSdkVersion    21
   targetSdkVersion 36
   appCategory      game
-- Firma --
   firmado con META-INF/KEY0.RSA
-- Librerias nativas (paginas de 16 KB) --
   12 de 12 librerias cumplen 16 KB
```

Deja el certificado extraído en una carpeta temporal para que verifiques la
huella. Esto **no pide contraseña**: el certificado es público y viaja dentro del
propio bundle.

```bash
keytool -printcert -file <ruta que imprima el script>
```

La huella SHA-256 debe coincidir con la de
[../04-publicacion-play.md](../04-publicacion-play.md). Si no coincide, el
bundle se firmó con la llave equivocada y Play lo rechazará.

## `elfalign.py` — alineación de páginas de 16 KB

Revisa cualquier carpeta con `.so`. Útil para comprobar las librerías antes de
empaquetar, o después de subir la versión de libGDX.

```bash
python README_for_IAs/tools/elfalign.py android/libs
```

Lee el `p_align` de los segmentos `PT_LOAD` de cada ELF, que debe ser ≥ 16384.
Devuelve código de salida 1 si alguna falla, así que sirve en un script.

## `mksymbols.py` — zip de símbolos para Play Console

```bash
python README_for_IAs/tools/mksymbols.py
```

Toma el `versionCode` de `android/build.gradle` y deja el zip en
`android/release/`. Ver [../03-migracion-android-16.md](../03-migracion-android-16.md)
para por qué esto no se puede resolver desde el build.
