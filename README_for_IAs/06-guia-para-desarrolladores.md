# Guía para desarrolladores nuevos

Para quien entra al proyecto sin haberlo escrito. Asume que sabes Kotlin pero no
libGDX ni este código.

Si algo aquí no coincide con el repositorio, gana el repositorio: avisa y
corrige este archivo.

## 1. Levantar el proyecto

**Clónalo en una ruta sin acentos ni caracteres raros.** El Android Gradle Plugin
falla si la ruta los tiene, y es un error confuso que parece otra cosa. En esta
máquina el proyecto vive en `C:\Users\gabri\AndroidStudioProjects\EDU_Tycoon`
justamente por eso.

Necesitas JDK 17 o más nuevo (aquí hay Corretto 21), Android Studio, y la
plataforma **SDK 36** instalada. Detalles en [01-entorno-local.md](01-entorno-local.md).

Al abrirlo, Android Studio crea `local.properties` con la ruta de tu SDK. Ese
archivo **no se commitea**, está en `.gitignore`, y es específico de tu máquina.

Para correrlo en un teléfono con depuración USB activada:

```bash
./gradlew :android:installDebug
```

La primera compilación tarda varios minutos: hay kapt de Room y las nativas de
libGDX se extraen a `android/libs/`.

## 2. Cómo está organizado

Dos módulos, y la separación importa:

- **`core/`** — todo el juego, en Kotlin puro. **No puede importar nada de
  Android.** Si escribes `import android.*` aquí, rompes la separación.
- **`android/`** — solo lo que necesita el sistema operativo: el `Activity` de
  arranque, Room y el manifest.

El puente entre los dos es la interfaz `GameSaveManager`, declarada en `core` e
implementada en `android` con Room. `core` pide "guarda esto" sin saber que
detrás hay una base de datos SQLite. Si necesitas algo de Android desde el juego,
ese es el patrón a copiar: interfaz en `core`, implementación en `android`,
inyectada desde `AndroidLauncher`.

### El arranque

`AndroidLauncher.onCreate()` construye la base de datos, el repositorio y el
`AndroidGameSaveManager`, y se los pasa a `Main`. A partir de ahí manda libGDX.

### Las pantallas

`Main` extiende `KtxGame` y registra cuatro pantallas, todas heredando de
`BaseScreen`:

| Pantalla | Qué hace |
|---|---|
| `Bienvenida` | Menú inicial. Es la primera que se muestra. |
| `SeleccionPartida` | Nueva partida o cargar |
| `PartidasGuardadas` | Los tres espacios de guardado |
| `GameScreen` | El juego: mapa, HUD, construcción |

Se cambia de pantalla con `game.setScreen<NombreDePantalla>()`.

`BaseScreen` te da un `Stage` con un `FitViewport(800x480)` y se encarga de
conectar y desconectar el `inputProcessor`. **Las coordenadas siempre son
800x480**, sin importar la pantalla real del teléfono; el viewport escala solo.
No calcules posiciones con `Gdx.graphics.getWidth()`.

### El motor de ciclos

Es la parte mejor diseñada del proyecto y conviene entenderla antes de tocar
nada de la simulación.

El juego avanza por **ciclos** (turnos). `GameCycleEngine.advanceCycle()`
resuelve el ciclo actual, incrementa el contador y arranca el siguiente. Los
sistemas se registran con `addListener()` y se ejecutan **en un orden fijo**
definido por el enum `ResolutionOrder`:

```
ECONOMY → CONSTRUCTION → STUDENTS → REPUTATION → EVENTS
```

Ese orden no es decorativo: la economía se calcula antes que los estudiantes
porque los estudiantes dependen de las escuelas ya pagadas. Si añades un sistema
nuevo, implementa `GameCycleListener`, declara su `resolutionOrder` y el motor lo
inserta en el lugar correcto. **No llames a tu sistema a mano desde `GameScreen`.**

### Rutas de archivos: nunca con `String.format` sin `Locale`

En árabe, persa, maratí, nepalí o bengalí, `"%02d".format(1)` produce dígitos no
latinos (`٠١` en vez de `01`), la ruta no existe y la app se cierra. Para armar
nombres de archivo usa `padStart(2, '0')` o `String.format(Locale.ROOT, ...)`.
Pasó con el código de animaciones del PR #4 (cerrado sin fusionar; su contenido
volvió en el PR #7): revisa que no se repita al integrar.

### El estado

`GameState` es un `object` de Kotlin, o sea un singleton global: dinero, alumnos,
nombre del jugador, ciclos jugados. Es cómodo y es el punto débil del diseño —
cualquier archivo puede modificarlo desde cualquier parte. Al depurar un valor
raro, `GameState` es el primer sospechoso.

Para cambiar dinero usa siempre la API (`gastar`, `acreditar`, `puedeComprar`),
nunca `GameState.dinero = ...` directo, porque `gastar` valida que alcance.

## 3. Dónde tocar para lo típico

| Quiero... | Ve a |
|---|---|
| Cambiar el dinero inicial | `GameState.DINERO_INICIAL_REAL` |
| Ajustar ingresos o costos | `engine/EconomyEngine.kt` |
| Cambiar cómo llegan alumnos | `engine/EstudiantesEngine.kt` |
| Añadir un evento aleatorio | `engine/EventEngine.kt` y `GameEvent.kt` |
| Editar el tutorial | La lista de `Dialogo(...)` en `GameScreen.kt` |
| Tocar tipos de edificio | `Propiedad.kt` |
| Cambiar el guardado | `GameSaveManager` (core) + `AndroidGameSaveManager` (android) |
| Añadir una pantalla | Hereda `BaseScreen` y regístrala en `Main.create()` |
| Añadir un asset | Ponlo en `assets/` de la raíz, no en `android/assets` |

## 4. Trampas conocidas

**`GameScreen.kt` tiene 880 líneas** y concentra el mapa, el HUD, los controles,
el tutorial y buena parte de la lógica de interfaz. Es el archivo donde más fácil
se rompe algo sin querer. Cambios pequeños y probados.

**`DINERO_INICIAL_TEST` = 100 millones existe en `GameState` pero no se usa.**
Los dos sitios que llaman `reset()` pasan `DINERO_INICIAL_REAL` explícitamente.
No lo pongas como valor por defecto ni lo dejes conectado "un ratito para
probar": si eso llega a producción, el juego pierde todo el sentido.

**El botón Atrás no se maneja con `onBackPressed()`.** Desde Android 13 existe el
predictive back y el sistema ya no llama a ese método. libGDX lo resuelve solo:
`GameScreen` llama `Gdx.input.setCatchKey(Input.Keys.BACK, true)` y el backend
registra internamente el callback que hace falta. Escucha `Input.Keys.BACK` en un
listener del `Stage` y ya. **No añadas un `onBackPressed()`**, no se va a ejecutar.

**El release lleva minificación con R8.** La variante `debug` no minifica, así que
puede funcionar perfecto en tu teléfono y fallar en el AAB publicado. Room usa
clases generadas por reflexión que R8 podría recortar. Antes de publicar, prueba
**la variante release**, no la debug. Las reglas están en
`android/proguard-rules.pro`.

**Las `.so` de `android/libs/` no se commitean**, las genera la tarea
`copyAndroidNatives` en cada build. Si desaparecen, compila otra vez.

## 5. Antes de subir cualquier cambio

1. Que compile el release, no solo el debug:
   ```bash
   ./gradlew :android:bundleRelease
   ```
2. Probarlo en un teléfono real. La app usa OpenGL y Room; el emulador no
   siempre revela los mismos problemas.
3. Que no se rompa una partida guardada existente. `DatabaseProvider` usa
   `fallbackToDestructiveMigration()`: si cambias las entidades de Room y subes
   la versión del esquema sin escribir una migración, **Room borra las tablas y
   todos los jugadores que actualicen pierden sus partidas sin ningún aviso.**
   Escribe la migración antes de publicar.

## 6. Lo que no se toca sin entender por qué está ahí

| Cosa | Por qué |
|---|---|
| `versionCode` | Debe ser mayor que el último de Play. Míralo en Play Console, no en el repo. Ver [04](04-publicacion-play.md). |
| `android:appCategory="game"` | Exime al juego del requisito de orientación en pantallas grandes de Android 16. Quitarlo rompe el `screenOrientation="landscape"`. |
| `targetSdk` | Play exige un nivel mínimo con fecha límite anual. Ver [03](03-migracion-android-16.md). |
| `minSdk 21` | Subirlo deja fuera dispositivos que hoy sí pueden instalar. |
| La llave de firma | Sin ella la app **no se puede actualizar nunca más**. Ver [01](01-entorno-local.md). |

## 7. Verificar antes de publicar

Los scripts de [tools/](tools/) revisan el bundle sin necesidad de contraseñas:

```bash
python README_for_IAs/tools/checkaab.py android/release/android-release.aab
```

Comprueba versionCode, versionName, targetSdk, la firma y la alineación de 16 KB.
