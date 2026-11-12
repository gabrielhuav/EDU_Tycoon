# Pruebas de Javier — Edutycoon-IA

| Dato | Valor |
|---|---|
| Autor | Javier Gamez (`Javier-Gamez`) |
| Fecha | 08/11/2026 |
| SHA probado | `7d352bb` (código de la app idéntico al de la rama final; los commits posteriores solo agregan pruebas instrumentadas y evidencia) |
| Dispositivo | Samsung SM-G998U, Android 14, físico por USB (sin emulador) |
| Build | `./gradlew :android:assembleDebug` → `android-debug.apk` |
| Partida | Nueva partida «Javier / ESCOM», presupuesto inicial $500K |

## Pruebas automáticas de mi parte

- `./gradlew :core:test` → verde, incluye `StatsEscuelaTest` (4 casos).
- `./gradlew :android:assembleDebugAndroidTest` → compila.
- `./gradlew :android:connectedAndroidTest` en el celular → **5/5**
  (`DatabaseTest` 4, `MigracionRivalTest` 1). Salida en
  [`connected-android-test.txt`](connected-android-test.txt).

## Casos manuales

### J-CP1 — Estadísticas coherentes con el HUD (regresión de #18)

- **Criterio:** la ventana Estadísticas usa `MercadoAlumnos` y coincide con el HUD y la economía.
- **Pasos:** 1) Nueva partida. 2) Comprar ESCOM ($300K). 3) Pausa → Estadísticas.
  4) Comparar con el HUD.
- **Esperado:** alumnos, cuota Tú/Rival e ingreso iguales a los del HUD y a `EconomyEngine`.
- **Real:** HUD `Alumnos: 690`, `Tú 80% | Rival 20%`; Estadísticas: `Alumnos/ciclo 690`,
  `Ingresos/ciclo $69K` (600 × 100 × 1.15), `Mercado: Tú 80% | Rival 20%`, `Rival: 2 edif. | $243K`,
  etiqueta por ciclo visible.
- **Estado:** ✅ Pasa. Captura: [`qa-javier-cp1.png`](qa-javier-cp1.png).

### J-CP2 — Regresión: comprar, mejorar y propiedad del rival

- **Criterio:** comprar y mejorar siguen funcionando y el rival conserva su propiedad bloqueada.
- **Pasos:** 1) Abrir ESCOM y comprar ($300K). 2) Esperar ciclos hasta tener saldo.
  3) Mejorar a nivel 2 ($300K). 4) Abrir la Cafetería del rival.
- **Esperado:** el HUD se actualiza tras cada acción; la Cafetería no ofrece compra.
- **Real:** tras comprar, `$200.0K` y `690` alumnos; tras mejorar, `1.4K` alumnos y `Tú 83% | Rival 17%`;
  la Cafetería mostró «Propiedad del Instituto Rival (Nivel 2)» sin botón.
- **Estado:** ✅ Pasa. Capturas: [`qa-javier-cp2a-compra.png`](qa-javier-cp2a-compra.png),
  [`qa-javier-cp2b-mejora.png`](qa-javier-cp2b-mejora.png),
  [`qa-javier-cp2c-propiedad-rival.png`](qa-javier-cp2c-propiedad-rival.png).

### J-CP3 — Navegación y estado: guardar, salir y recargar

- **Criterio:** al recargar, el rival conserva sus edificios y el HUD se reconstruye.
- **Pasos:** 1) Pausa → Guardar partida → Slot 1. 2) Menú principal. 3) Cargar partida → slot «ESCOM».
- **Esperado:** mismo dinero, alumnos, cuota y edificio rival.
- **Real:** «Guardado correctamente» con `$354.5K`, `1.4K` y `Tú 80% | Rival 20%`; el slot mostró
  «Javier $354K Ciclos: 9»; al cargar, el HUD volvió idéntico y la Cafetería roja del rival siguió en el mapa.
  Estadísticas tras recargar: `Rival: 2 edif.`, `Alumnos/ciclo 1380`, `Ingresos/ciclo $138K`.
- **Estado:** ✅ Pasa. Capturas: [`qa-javier-cp3a-guardado.png`](qa-javier-cp3a-guardado.png),
  [`qa-javier-cp3b-slot.png`](qa-javier-cp3b-slot.png), [`qa-javier-cp3c-cargado.png`](qa-javier-cp3c-cargado.png).

### J-CP4 — Accesibilidad / compatibilidad: texto del sistema ampliado

- **Criterio:** el juego respeta o tolera `font_scale` del sistema.
- **Pasos:** 1) Con el juego abierto, `adb shell settings put system font_scale 1.45`.
  2) Relanzar el juego, cargar la partida y abrir Estadísticas. 3) Restaurar `font_scale 1.0`.
- **Esperado:** textos más grandes o, si se ignora, limitación ya conocida (#5); sin cierres.
- **Real:** los textos quedaron del mismo tamaño (limitación preexistente #5). Además, **al cambiar
  `font_scale` con el juego abierto la app se cerró**: `GdxRuntimeException: VisUI cannot be loaded twice`
  en `Main.create` (`Main.kt:12`), porque Android recrea la actividad y `VisUI.load()` se ejecuta de nuevo
  en el mismo proceso. Defecto preexistente, ajeno a esta feature; se documenta en el hilo del PR #10.
- **Estado:** ❌ Falla (preexistente #5 y defecto nuevo documentado en el PR #10).
  Captura: [`qa-javier-cp4-font-scale.png`](qa-javier-cp4-font-scale.png).

## Imagen individual

[`gamez-javier-edutycoon-ia.png`](gamez-javier-edutycoon-ia.png): la app corriendo en el celular
(ventana scrcpy) y la terminal con `git config user.name`, `user.email`, rama y commits.
