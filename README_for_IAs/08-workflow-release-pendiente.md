# Pendiente: workflow de release con la etiqueta `manual-play-upload`

Estado al 21 de noviembre de 2026: **no existe**. EDU Tycoon no tiene ningún
workflow en `.github/workflows/`, y `android/build.gradle` no tiene
`signingConfig`: hoy el AAB se firma a mano desde Android Studio (ver
[04](04-publicacion-play.md#cómo-se-hace-un-release)).

La etiqueta `manual-play-upload` ya está creada en el repo, copiada de
Politécnico Open World (POW), pero **todavía no hace nada**: su descripción
("Genera el AAB firmado sin subirlo automaticamente a Play Store") describe lo
que hará cuando exista el workflow.

Se hace **después** de copiar la llave de subida a la PC de escritorio y de unir
los PRs de los alumnos.

## Cómo funciona en POW

El modelo es `gabrielhuav/PolitecnicoOpenWorld`,
`.github/workflows/android-release.yml`:

- **Disparo:** `pull_request_target` con `types: [closed]` sobre `main`, y solo
  si `github.event.pull_request.merged == true`. También `workflow_dispatch` para
  relanzarlo a mano desde Actions. Se usa `pull_request_target` porque un
  `pull_request` que viene de un fork no recibe secrets, y aquí todos los PRs de
  los alumnos vienen de forks. Compila el commit ya unido
  (`merge_commit_sha`), nunca el código del fork sin unir.
- **Jobs:**
  1. `play-compliance`: comprueba que la política de privacidad y la página de
     eliminación de cuenta respondan 200 y traigan el correo correcto, y que
     existan las notas de la versión. Si falla, no se sube nada.
  2. `release`: APK debug publicado como GitHub Release `debug-latest`.
  3. `playstore-closed-testing`: valida los secrets, decodifica el keystore,
     verifica contraseña y alias con `keytool` antes de compilar, calcula el
     `versionCode`, ejecuta `bundleRelease`, revisa el tamaño del AAB y lo
     guarda como artefacto. Después:
     - **sin** `manual-play-upload`: lo sube a la prueba cerrada (`alpha`) con
       `r0adkll/upload-google-play`;
     - **con** `manual-play-upload`: se salta la subida y deja el AAB firmado
       como artefacto del run, para subirlo a mano a Play Console. Con esa
       etiqueta tampoco exige `PLAY_SERVICE_ACCOUNT_JSON`.
     Al final borra el keystore del runner (`if: always()`).
  4. `bump-version`: sube el `versionName` y hace commit a `main`.

## Qué hay que adaptar para EDU Tycoon

- **Firma en Gradle.** Añadir un `signingConfigs.release` en
  `android/build.gradle` que lea la ruta, las contraseñas y el alias de
  variables de entorno (`RELEASE_KEYSTORE_PATH`, `RELEASE_STORE_PASSWORD`,
  `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`), y que no rompa el build local
  cuando no existan.
- **Secrets** (los da de alta Gabriel; nunca en archivos del repo):
  `RELEASE_KEYSTORE_BASE64`, las tres contraseñas o alias de arriba y, solo para
  la subida automática, `PLAY_SERVICE_ACCOUNT_JSON`. La llave de EDU Tycoon es
  un `.p12` (PKCS12), no un `.jks`: `keytool` necesita `-storetype PKCS12` si no
  lo detecta solo.
- **`versionCode`.** POW usa `1000 + run_number`. Aquí el último publicado es
  el 5 y el siguiente debe ser el 6 o mayor. Decidir entre leerlo de
  `build.gradle` o calcularlo. Si se calcula, debe quedar por encima de todos
  los ya usados en Play.
- **Ruta del proyecto.** En POW el proyecto vive en `PolitecnicoOpenWorld/`.
  Aquí está en la raíz: `./gradlew :android:bundleRelease` y la salida en
  `android/build/outputs/bundle/release/`.
- **JDK y Gradle.** Usar la versión de JDK con la que compila este proyecto (ver
  [02](02-proyecto.md)) y `gradle-version: wrapper`.
- **Tamaño.** El AAB pesa unos 160 MB y Play topa la descarga en 200 MB. Ajustar
  los umbrales del paso de tamaño a ese tope, no a los 500 MB de POW. Importa
  sobre todo por los assets del PR #7.
- **16 KB.** Correr `README_for_IAs/tools/checkaab.py` sobre el AAB antes de
  guardarlo o subirlo.
- **Cumplimiento.** Cambiar las URLs del paso `play-compliance` por las de
  EDU Tycoon, o quitarlo si la app no tiene esas páginas.
- **`bump-version` y protección de `main`.** `main` exige PR y aprobación, así
  que el push del bot falla con 403, salvo con un PAT de Gabriel en el bypass o
  con Actions en la lista de bypass. Alternativa: no hacer bump automático y
  subir la versión en el propio PR.
- **Nombre del paquete:** `io.moviles.IPN_Tycoon`.

## Cómo comprobar que funciona

1. Un PR de prueba con la etiqueta `manual-play-upload`. Al unirlo, el run debe
   terminar en verde, saltarse la subida y dejar el AAB como artefacto.
2. Descargar el artefacto y verificar la firma (`keytool -printcert -jarfile`)
   contra el SHA-256 de la llave en [04](04-publicacion-play.md), y
   `checkaab.py`.
3. Solo después, probar la subida automática a la prueba cerrada sin la
   etiqueta.
