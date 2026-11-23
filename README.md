<div align="center">

<img src="android/res/drawable/logo_app.png" alt="Logo de EDU Tycoon" width="160">

# EDU Tycoon

**Construye tu propio Politécnico, escuela por escuela, y gánale el campus al Instituto Rival.**

<a href="https://play.google.com/store/apps/details?id=io.moviles.IPN_Tycoon"><img src="https://play.google.com/intl/en_us/badges/static/images/badges/es-419_badge_web_generic.png" alt="Disponible en Google Play" height="80"></a>

![Android](https://img.shields.io/badge/Android-5.0%2B-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2-7F52FF?logo=kotlin&logoColor=white)
![libGDX](https://img.shields.io/badge/libGDX-1.14-E74A45)
![targetSdk](https://img.shields.io/badge/targetSdk-36-blue)
![Sin conexión](https://img.shields.io/badge/juega-sin%20conexi%C3%B3n-success)

</div>

---

EDU Tycoon es un juego de gestión tipo *tycoon* ambientado en el Instituto
Politécnico Nacional. Empiezas con un presupuesto, compras los edificios del
campus y los mejoras para atraer más alumnos, mientras un Instituto Rival
controlado por IA compite contigo por el mismo mercado.

**[Descárgalo gratis en Google Play](https://play.google.com/store/apps/details?id=io.moviles.IPN_Tycoon)**

## Qué hay en el juego

| | |
|---|---|
| 🏫 **Campus isométrico** | Recorre un mapa del Politécnico con escuelas como ESCOM, Arquitectura, Biológicas, Bioquímica, el Museo o la Cafetería |
| 🏗️ **Compra y mejora** | Cada edificio sube de nivel, cambia de aspecto y trae más alumnos |
| 💰 **Burbujas de ingresos** | Cada edificio produce a su ritmo; toca la burbuja de monedas para cobrar |
| ⏩ **Control del tiempo** | Juega a 1x, acelera a 2x o pausa el ciclo escolar desde el HUD |
| ⚖️ **Decisiones** | Donaciones dudosas, patrocinios o una visita de la SEP: cada elección mueve tu dinero y tu reputación |
| 🤖 **Instituto Rival (opcional)** | Una IA que ahorra, compra y compite contigo por los mismos alumnos; se activa en el menú de pausa |
| 🏆 **Victoria o derrota** | Con el rival activo, a partir del ciclo 10 domina el mercado para ganar o te quedas fuera |
| 💾 **Autoguardado** | La partida se guarda sola cada ciclo, al salir y al cambiar de app |
| 🎵 **Música y sonido** | Música de fondo y efecto de monedas, con interruptor en el menú |
| ✈️ **Sin conexión** | Todo corre en el teléfono, sin cuentas ni internet |

## Tecnología

Hecho con [libGDX](https://libgdx.com/) en Kotlin, con
[KTX](https://libktx.github.io/), VisUI y gdx-ai, y Room para las partidas
guardadas. El mapa se edita con [Tiled](https://www.mapeditor.org/).

## Módulos

- `core`: la lógica del juego, común a todas las plataformas (pantallas, mapa,
  economía, ciclos, rival).
- `android`: el lanzador de Android y la base de datos Room. Necesita el SDK de
  Android.
- `assets`: mapas Tiled, texturas, fuentes y sonidos que se empaquetan con la app.

## Compilar y probar

Con el wrapper de Gradle incluido (`gradlew.bat` en Windows, `./gradlew` en
Linux y macOS):

| Tarea | Qué hace |
|---|---|
| `./gradlew :core:test` | Pruebas unitarias del juego |
| `./gradlew :android:assembleDebug` | APK de depuración |
| `./gradlew :android:installDebug` | Instala el APK en el dispositivo o emulador conectado |
| `./gradlew :android:connectedAndroidTest` | Pruebas instrumentadas (Room y migraciones) en un dispositivo |
| `./gradlew :android:lint` | Revisión de Android lint |

Para el entorno, las versiones del build y cómo se publica en Play, ver
[`README_for_IAs/`](README_for_IAs/README.md). Si eres nuevo en el proyecto,
empieza por la
[guía para desarrolladores](README_for_IAs/06-guia-para-desarrolladores.md).

## Documentación

| Archivo | Qué contiene |
|---|---|
| [`README_for_IAs/`](README_for_IAs/README.md) | Entorno, estructura del proyecto, publicación en Play y migración a otra PC |
| [`docs/rival-ia.md`](docs/rival-ia.md) | Diseño del Instituto Rival y del mercado de alumnos |
| [`docs/pruebas.md`](docs/pruebas.md) | Matriz de pruebas automáticas y manuales |
| [`docs/evidencia/`](docs/evidencia/) | Registros de texto de las pruebas |

## Cómo contribuir

- Todo cambio entra a `main` por PR, y el PR necesita la aprobación del
  responsable del repositorio.
- **No subas capturas de pantalla ni otras imágenes que no sean parte del
  juego.** La evidencia de QA (capturas, videos) se adjunta en la descripción o
  en un comentario del PR, arrastrándola al cuadro de texto de GitHub. En el
  repositorio solo van los registros de texto y las imágenes de `assets/` que
  usa la app.
- No subas llaves de firma, contraseñas, `local.properties` ni archivos `.aab` o
  `.apk`: el `.gitignore` ya los excluye.

### Etiquetas de los PRs

| Etiqueta | Significado |
|---|---|
| `Android` | El cambio afecta la versión de Android |
| `qa-pending` | Está en control de calidad |
| `manual-play-upload` | Requiere generar el AAB firmado y subirlo a mano a Play Console (el workflow que lo genera está [pendiente](README_for_IAs/08-workflow-release-pendiente.md)) |
| `Android-Release` | Listo para publicarse en Google Play |

## Créditos y licencia

EDU Tycoon lo desarrollan alumnos del Instituto Politécnico Nacional. La lista
completa de quienes han aportado está en la
[gráfica de contribuidores](https://github.com/gabrielhuav/EDU_Tycoon/graphs/contributors)
y en los [PRs unidos](https://github.com/gabrielhuav/EDU_Tycoon/pulls?q=is%3Apr+is%3Amerged).

Código bajo [licencia MIT](LICENSE).
