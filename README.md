# IPN Tycoon (EDU_Tycoon)

IPN Tycoon es un juego de gestión estilo tycoon ambientado en el Instituto Politécnico Nacional (IPN). El jugador toma el rol de Director General y administra escuelas, presupuesto, docentes y reputación.

Este repositorio es un fork de [EmmanuelJuarez14/EDU_Tycoon](https://github.com/EmmanuelJuarez14/EDU_Tycoon).

---

## 🚀 Historial de Versiones y Lanzamientos (Google Play)

* **Versión 1.1.0 (versionCode 2) - Actualización Entrega 2:**
  - **Mecánica Tycoon de Recaudación Interactiva:** Cada edificio adquirido cuenta con un temporizador de ciclo de producción independiente.
  - **Burbujas Flotantes Táctiles:** Al concluir el ciclo de un edificio, aparece una burbuja interactiva oscilatoria sobre el plantel para recolectar las ganancias manualmente.
  - **Retroalimentación Multisensorial:** Integración de efecto sonoro de monedas/caja registradora (`coin.wav`), animación de texto ascendente con desvanecimiento (`+$X`) y actualización inmediata del HUD.
  - **Equilibrio Económico Progresivo:** Edificios económicos iniciales (Mac & Cheese, Cafetería) tienen ciclos más lentos (45s - 50s); escuelas universitarias y Dirección General entregan mayores ganancias en ciclos más veloces (15s - 25s).
  - **Ficha Informativa Enriquecida:** Desglose del tiempo de ciclo por plantel, segundos restantes y alerta visual de fondos disponibles para cobro.

* **Versión 1.0.0 (versionCode 1) - Lanzamiento Inicial Entrega 1:**
  - Publicación inicial en canal de Prueba Cerrada de Google Play Console.
  - Mapa isométrico interactivo del campus politécnico.
  - Sistema de compra y mejora de edificios escolares (`GameState`, `ReglaDeCompra`).
  - Música ambiental de fondo y control reactivo de audio en menú de pausa (`AudioManager`).

---

## 📸 Galería Visual de la Actualización (Entrega 2 - Mecánica Tycoon)

A continuación se muestra la evidencia gráfica de la nueva funcionalidad implementada para la actualización de Google Play:

### 1. Comparativa de Comportamiento: Antes vs. Después

| Antes: Sin Burbujas (Generación Pasiva e Incierta) | Después: Burbujas Interactivas (Recaudación Táctil Tycoon) |
| :---: | :---: |
| ![Captura Antes de Burbujas](docs/evidencia/entrega-2/captura_antes_de_burbujas_interactivas.jpg) | ![Burbujas Interactivas Activas](docs/evidencia/entrega-2/burbujas_interactivas.jpg) |
| <sub>**Antes:** Los edificios generaban saldo automáticamente sin retroalimentación visual ni aviso de tiempo.</sub> | <sub>**Después:** Burbujas doradas flotantes oscilan sobre cada plantel al completarse su ciclo de ingresos.</sub> |

---

### 2. Flujo Completo de la Nueva Mecánica en Juego

#### A. Definición y Transparencia de Ciclos de Producción por Edificio
Al pulsar sobre cualquier plantel escolar, la ficha de gestión (`BuildingInfoWindow`) ahora transparenta la duración del ciclo de ingresos y desglosa el tiempo restante en segundos, así como la ganancia que generará cada ciclo.

| Definición de Tiempo por Ciclo | Tiempo de Ciclo de Ingresos Añadido |
| :---: | :---: |
| ![Definición de Tiempo por Ciclo](docs/evidencia/entrega-2/definicion_de_tiempo_por_ciclo_de_ingreso.jpg) | ![Tiempo de Ciclo de Ingresos Añadido](docs/evidencia/entrega-2/tiempo_de_ciclo_ingresos_anadido.jpg) |
| <sub>Visualización de estadísticas base y duración configurada del ciclo (ej. 35s en Escuela de Computación).</sub> | <sub>Desglose en tiempo real del tiempo restante para el cobro o alerta de dinero listo para recolectar.</sub> |

#### B. Generación y Notificación Emergente de Fondos
Al finalizar el ciclo de producción, el edificio deja de estar en espera pasiva y despliega la burbuja interactiva con el indicador de saldo listo para cobro sobre el tejado del inmueble.

<p align="center">
  <img src="docs/evidencia/entrega-2/burbuja_dinero_emergente.jpg" width="600" alt="Burbuja de Dinero Emergente" /><br>
  <sub><b>Burbuja emergente sobre el plantel:</b> Notificación gráfica que señala al jugador que los ingresos están listos para cobro manual.</sub>
</p>

#### C. Animación de Recolección Táctil y Actualización en el HUD
Al pulsar la burbuja con el dedo, se dispara el tintineo de monedas (`coin.wav`), una animación de texto ascendente (`+$X`) que se desvanece suavemente hacia arriba, y el saldo en el HUD superior izquierdo se incrementa al instante.

| Animación de Recolección al Tocar la Burbuja | Saldo del HUD Actualizado de Inmediato |
| :---: | :---: |
| ![Animación de Recolección](docs/evidencia/entrega-2/animacion_recoleccion_dinero.jpg) | ![Saldo Actualizado](docs/evidencia/entrega-2/saldo_actualizado_dinero_recolectado.jpg) |
| <sub>Animación flotante del monto recaudado elevándose sobre el edificio con desvanecimiento alfa.</sub> | <sub>Reflejo inmediato del dinero recolectado en el saldo general del campus escolar.</sub> |

---

## 👥 Instrucciones para Personas Probadoras (Testers)

1. **Acceso al Programa de Prueba Cerrada:**
   - Inicia sesión con la cuenta de Google registrada en la lista de evaluadores autorizados.
   - Accede al enlace de invitación de la Prueba Cerrada en Google Play Console y selecciona **"Aceptar invitación / Convertirse en evaluador"**.
2. **Descarga de la Versión Inicial (v1.0.0):**
   - Descarga e instala EduTycoon desde Google Play Store.
   - Ejecuta la aplicación, completa el tutorial con el Ing. Lázaro Cárdenas y adquiere uno de los primeros edificios accesibles (ej. Cafetería o Mac & Cheese).
3. **Validación de la Actualización OTA (v1.1.0):**
   - Abre Google Play Store sin desinstalar la app previa.
   - Presiona el botón **"Actualizar"** para recibir la versión 1.1.0.
   - Comprueba que tus datos y saldo previo se conserven intactos.
   - Observa la aparición de la burbuja interactiva dorada sobre tus edificios al concluir el ciclo y tócala para escuchar el tintineo de monedas y recolectar las ganancias.

---

## 📁 Documentación Oficial de la Entrega

* 📋 **[docs/entrega-2.md](docs/entrega-2.md):** Índice oficial de verificación (tablero Scrum, enlaces a los 3 PRs fusionados, comparativa técnica y justificación psicológica de diseño).
* 🧪 **[docs/pruebas.md](docs/pruebas.md):** Matriz exhaustiva de QA con los 6 casos de prueba reales obligatorios (instalación, actualización OTA, ciclos, accesibilidad y ciclo de vida).
* 🏷️ **Etiqueta Git Final:** `parcial-2`

---

## Tecnologías

- [libGDX](https://libgdx.com/) 1.14.0 (motor del juego), generado con [gdx-liftoff](https://github.com/libgdx/gdx-liftoff)
- Kotlin 2.2.10 con las extensiones [KTX](https://libktx.github.io/)
- AndroidX Room (base de datos SQLite local para las partidas guardadas)
- [Gradle](https://gradle.org/) con el Gradle Wrapper incluido

## Módulos

- `core`: lógica del juego compartida por todas las plataformas. Clase de entrada: `io.moviles.IPN_Tycoon.Main`.
- `android`: lanzador de Android (`AndroidLauncher`), manifiesto, recursos de Android y base de datos Room. Requiere el Android SDK.
- `assets`: recursos del juego, incluido el mapa isométrico de Tiled `Mapa/Mapa_General.tmx`, que se empaqueta en el APK.
- `docs`: documentación del proyecto.

## Arquitectura

El proyecto se divide en dos módulos de Gradle. `android` es la capa de plataforma: inicia la app, construye la base de datos Room e inyecta el sistema de guardado en el juego. `core` contiene el juego, organizado en tres capas: presentación (pantallas y ventanas), lógica (`engine/`) y datos (`data/`). Todo se ejecuta en el dispositivo; la app no usa la red.

### Vista general

![Vista general de la arquitectura de IPN Tycoon](docs/img/arquitectura-general.png)

### Arranque de la app

`AndroidLauncher.onCreate()` crea la base de datos, el repositorio y `AndroidGameSaveManager`, y se lo pasa a `Main`. Así `core` nunca depende de Android (inyección de dependencias).

![Arranque de la app](docs/img/arquitectura-arranque.png)

### Ciclo del juego

Cada 30 segundos, `GameScreen` llama a `GameCycleEngine.advanceCycle()`. El ciclo notifica a sus listeners (`EconomyEngine`, `EstudiantesEngine`, `EventEngine`, `DecisionEngine`), que actualizan `GameState` (patrón Observador).

![Ciclo del juego](docs/img/arquitectura-ciclo-juego.png)

### Sistema de guardado

Las pantallas guardan y cargan mediante la interfaz `GameSaveManager`. En Android, `AndroidGameSaveManager` usa los repositorios, DAOs y entidades para guardar los datos en una base SQLite local con Room.

![Sistema de guardado](docs/img/arquitectura-persistencia.png)

## Entorno probado

| Herramienta | Versión |
|---|---|
| Sistema operativo | Windows 11 Pro, 64 bits (x64) |
| Android Studio | Quail 3 2026.1.3 (Build #AI-261.26222.65.2613.15948027) |
| JDK de Gradle | JDK 21 (Gradle JVM criteria en Android Studio) |
| Distribución de Gradle | Gradle Wrapper (incluido en el repositorio) |
| Plataforma del Android SDK | API 35 (compileSdk / targetSdk) |
| Android Gradle Plugin | 8.9.3 |
| Kotlin | 2.2.10 |
| Dispositivo de prueba | Xiaomi 2511FPC34G (celular físico, depuración USB), Android 16 (API 36) |

## Ejecutar desde un clon limpio

### Requisitos previos

1. Instalar **Git**: https://git-scm.com/
2. Instalar **Android Studio** (una versión compatible con Android Gradle Plugin 8.9.3). Incluye un JDK, así que no hace falta instalar Java aparte. El proyecto requiere **JDK 17 o superior**.
3. En Android Studio, abrir **Tools > SDK Manager** e instalar **Android SDK Platform 35** (Android 15).

### Pasos

1. Clonar el repositorio:

   ```
   git clone https://github.com/JulioCesarCaballero/EDU_Tycoon.git
   ```

2. Abrir Android Studio, elegir **File > Open** y seleccionar la carpeta raíz `EDU_Tycoon` (no `android` ni `core`).
3. Esperar a que termine el **Gradle Sync**. La primera sincronización descarga todas las dependencias y puede tardar varios minutos. Android Studio crea `local.properties` con la ruta de tu SDK automáticamente.
4. Si la sincronización falla por el JDK, ir a **File > Settings > Build, Execution, Deployment > Build Tools > Gradle** y poner el JDK de Gradle en versión 17 o superior (el JetBrains Runtime incluido funciona).
5. Preparar un dispositivo:
    - **Emulador:** abrir **Device Manager**, crear un dispositivo virtual con API 21 o superior e iniciarlo.
    - **Celular físico:** seguir [Ejecutar en un celular físico](#ejecutar-en-un-celular-físico) y conectarlo por USB.
6. En el selector de configuración de ejecución (barra superior), elegir **`android`** y el dispositivo.
7. Presionar **Run ▶**. El juego se abre en modo horizontal.

### Alternativa por línea de comandos

Desde la raíz del repositorio (Windows):

```
gradlew.bat android:installDebug
gradlew.bat android:run
```

En macOS/Linux usar `./gradlew` en lugar de `gradlew.bat`. La tarea `run` necesita `local.properties` con `sdk.dir` o la variable de entorno `ANDROID_SDK_ROOT`, además de un dispositivo conectado o un emulador en ejecución.

Nota: `gradle.properties` pone el nivel de log de Gradle en `quiet`, así que la consola muestra muy poca información cuando la compilación sale bien.

### Ejecutar en un celular físico

Para instalar y ejecutar el juego en un celular Android físico, primero hay que activar estas opciones:

1. **Opciones de desarrollador:** ir a **Ajustes > Acerca del teléfono** y tocar siete veces **Número de compilación** (en Xiaomi: **Versión del SO**).
2. **Depuración USB:** en **Ajustes > Opciones de desarrollador**, activar **Depuración USB**.
3. **Autorizar la computadora:** conectar el celular por USB y aceptar el aviso **"¿Permitir la depuración USB?"** en el teléfono.
4. **Instalar vía USB (solo Xiaomi / HyperOS / MIUI):** en **Opciones de desarrollador**, activar **Instalar vía USB**. Sin esto, el teléfono bloquea la instalación desde Android Studio.

Después de esto, el celular aparece en el selector de dispositivos de Android Studio.

## Solución de problemas

No se encontraron errores ni complicaciones durante la primera ejecución del proyecto. El Gradle Sync y la compilación terminaron sin cambios en el código ni en la configuración.

| Problema | Causa | Solución |
|---|---|---|
| Ningún problema en la primera ejecución | No aplica | No aplica |

## Tareas útiles de Gradle

Se ejecutan con `gradlew.bat` (Windows) o `./gradlew` (macOS/Linux):

- `android:installDebug`: compila el APK de depuración y lo instala en el dispositivo conectado.
- `android:lint`: valida el proyecto de Android.
- `build`: compila el código y los archivos de todos los módulos.
- `clean`: borra las carpetas `build`, que guardan clases compiladas y archivos generados.
- `test`: ejecuta las pruebas unitarias (si las hay).
- `--offline`: usa las dependencias guardadas en caché.
- `--refresh-dependencies`: fuerza la validación de todas las dependencias.

La mayoría de las tareas se pueden limitar a un módulo con el prefijo `nombre:`. Por ejemplo, `core:clean` borra solo la carpeta `build` del módulo `core`.

## Archivos que no se versionan (.gitignore)

El archivo `.gitignore` evita subir al repositorio archivos que se generan solos o que son propios de cada computadora:

| Categoría | Ejemplos | Por qué no se versiona |
|---|---|---|
| Salidas de compilación | `.gradle/`, `build/`, `*.class` | Gradle las regenera en cada compilación. Subirlas llena el repositorio y causa conflictos. |
| Configuración local | `local.properties` | Guarda la ruta del Android SDK de cada computadora, que es distinta para cada integrante. Android Studio la crea al abrir el proyecto. |
| Archivos del IDE | `.idea/`, `*.iml` | Preferencias personales de Android Studio. |
| Librerías nativas | `android/libs/arm64-v8a/`, `x86/`, etc. | La tarea `copyAndroidNatives` las extrae de las dependencias en cada build. |
| Generados del proyecto | `assets/assets.txt`, `.kotlin/` | Los crean la tarea `generateAssetList` y el compilador de Kotlin. |
| Archivos del sistema | `.DS_Store`, `Thumbs.db` | Los genera el sistema operativo. |

Por eso, después de clonar, el primer Gradle Sync tarda unos minutos: descarga y regenera todo lo que no está en el repositorio.

## Licencia

Licencia MIT. Ver [`LICENSE`](LICENSE) y [`docs/licencias.md`](docs/licencias.md) para las dependencias de terceros y la procedencia de los recursos.
