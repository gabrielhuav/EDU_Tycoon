# Matriz de pruebas — Edutycoon-IA (Instituto Rival controlado por IA)

> Plan escrito antes de ejecutar y resultados llenados después de correr los
> casos en celulares físicos (08/11/2026). Equipo: Jesús Ángel González
> Arellano (`jesusGoliat`) y Javier Gamez (`Javier-Gamez`).

## Entorno

| Campo | Valor |
|---|---|
| Dispositivo 1 | Xiaomi 25062PC34G, Android 16 (API 36), físico por USB — probó Jesús |
| Dispositivo 2 | Samsung SM-G998U, Android 14 (API 34), físico por USB — probó Javier |
| App | 1.3.1 (`versionCode 5`), APK debug |
| Compilación | JDK 21, Gradle 9.5.0, AGP 9.3.1 |
| Fecha | 08/11/2026 |

### Sobre el código probado

La QA manual se ejecutó en el fork del equipo
([`jesusGoliat/EDU_Tycoon`](https://github.com/jesusGoliat/EDU_Tycoon), PR
[#10](https://github.com/jesusGoliat/EDU_Tycoon/pull/10)), cuya rama además
contiene la regla de compra de la Entrega 1 del curso. Comparado con esta rama,
el código de la app **solo difiere en `BuildingInfoWindow.kt` y
`ReglaCompra.kt`** (`git diff --stat` sobre `core/src/main` y
`android/src/main`). La lógica del rival, la economía, la persistencia,
`StatsWindow` y el diálogo de fin de partida son idénticos, así que la evidencia
aplica a este PR.

La única diferencia observable es el saldo insuficiente (segunda parte de
CP-R2): en el fork el botón aparece deshabilitado con el aviso «Te faltan $X»;
en este repositorio se conserva el comportamiento original, donde el botón
queda activo y al pulsarlo muestra «¡Saldo insuficiente!» sin cobrar.

## Criterios de aceptación

1. **Dado** una partida en curso, **cuando** pasan al menos 4 ciclos, **entonces** el rival actúa (compra o mejora) y el HUD muestra `Tú X% | Rival Y%` sumando 100 %.
2. **Dado** un edificio comprado por el rival, **cuando** el jugador abre su ventana, **entonces** dice «Propiedad del Instituto Rival (Nivel N)» y no ofrece botón de compra.
3. **Dado** un guardado con rival en curso, **cuando** se guarda, se sale y se vuelve a cargar, **entonces** el rival conserva sus edificios y su dinero.
4. **Dado** un edificio comprado por el jugador, **cuando** se abre su ventana, **entonces** «Ingreso/ciclo» usa el mismo factor ×100 que la economía y ya no muestra 10× menos.
5. **Dado** que el jugador alcanza la victoria, **cuando** se muestra el diálogo, **entonces** el título dice «¡Victoria!» con el signo de apertura.
6. **Dado** el menú de pausa, **cuando** se abren las Estadísticas, **entonces** la cuota Tú/Rival, los alumnos y el ingreso coinciden con el HUD y con `MercadoAlumnos`.

## Riesgos

| # | Riesgo | Impacto | Caso que lo cubre |
|---|---|---|---|
| R1 | La migración Room 4→5 borra partidas existentes | Pérdida de datos del jugador | CP-R4 y `MigracionRivalTest` |
| R2 | El balance del rival hace la partida trivial o imposible | Mala experiencia de juego | CP-R1, CP-R7 y simulación en `docs/rival-ia.md` |
| R3 | El rival bloquea una compra legítima o permite comprar un edificio ajeno | Regla de juego incorrecta | CP-R2 |
| R4 | Las estadísticas contradicen al HUD | Información engañosa al jugador | CP-R8 y `StatsEscuelaTest` |

## Pruebas automáticas

- `./gradlew :core:test` → **48/48** en verde: 42 de las clases de `rival/` y
  `engine/`, 2 de `PipelineCompletoTest` (Economy + Students + Rival en un mismo
  motor) y 4 de `StatsEscuelaTest`.
- `./gradlew :android:assembleDebugAndroidTest` → compila en esta rama.
- `./gradlew :android:connectedAndroidTest` en el celular físico de Javier →
  **5/5**: `DatabaseTest` (4) y `MigracionRivalTest` (1, cubre `MIGRATION_4_5`
  sin migración destructiva). Se ejecutó en el fork; las pruebas y la capa de
  base de datos son idénticas en esta rama. Salida:
  [`connected-android-test.txt`](evidencia/edutycoon-ia/connected-android-test.txt).

## Pruebas manuales

| ID | Caso | Pasos | Resultado esperado | Resultado real | Estado | Evidencia |
|---|---|---|---|---|---|---|
| CP-R1 | Ruta feliz: rival activo | 1. Partida nueva. 2. Jugar hasta el ciclo 4. 3. Revisar HUD y log `RIVAL`. | El rival no actúa en los ciclos 1–3; en el 4 compra o mejora; la cuota suma 100 % | Al ciclo 4 compró la Cafetería por $150.0K; el HUD pasó a `Tú 0% \| Rival 100%` y apareció «Instituto Rival compró: Cafetería». | ✅ Pasa | `qa-cp-r1.png`, `qa-cp-r1-log.txt` |
| CP-R2 | Estado alterno: propiedad del rival | 1. Abrir un edificio que ya compró el rival. | Sin botón de compra y con la etiqueta de propiedad | La Cafetería mostró «Propiedad del Instituto Rival (Nivel 1)» sin botón. | ✅ Pasa | `qa-cp-r2a-propiedad-rival.png` |
| CP-R2b | Estado alterno: saldo insuficiente | 1. Abrir un edificio más caro que el saldo. 2. Pulsar comprar. | No cobra y avisa la falta de saldo | Ver «Sobre el código probado»: la captura muestra la regla de compra del fork («Te faltan $400.0K»). En este repositorio aplica el aviso original «¡Saldo insuficiente!». | ⚠️ Evidencia del fork | `qa-cp-r2b-saldo-insuficiente.png` |
| CP-R3 | Regresión: compra, mejora e ingreso | 1. Comprar ESCOM. 2. Mejorarla. 3. Revisar «Ingreso/ciclo». | La compra y la mejora funcionan; el ingreso usa ×100 | ESCOM quedó en 2/2 con «Ingreso/ciclo: $120.0K» (600 × 2 × 100). | ✅ Pasa | `qa-cp-r3-escuela-nivel2.png` |
| CP-R4 | Navegación y estado: guardar, salir y recargar | 1. Guardar en un slot. 2. Salir al menú. 3. Cargar. 4. Revisar rival y HUD. | El rival conserva edificios y dinero; el HUD reconstruye la cuota | «Guardado correctamente»; al recargar volvió `Tú 80% \| Rival 20%` y la Cafetería siguió como propiedad del rival (Nivel 2). | ✅ Pasa | `qa-cp-r4a-guardado.png`, `qa-cp-r4b-cargado.png`, `qa-cp-r4c-propiedad-rival-nivel2.png` |
| CP-R5 | Accesibilidad: texto ampliado | 1. Tamaño de texto del sistema al máximo. 2. Abrir el juego y una ventana. | Los textos crecen o siguen legibles | Con `font_scale = 1.45` los textos del juego no cambian: limitación preexistente de libGDX, no de esta característica. | ❌ Falla (preexistente) | `qa-cp-r5a-texto-xxl.png`, `qa-cp-r5-font-scale.txt` |
| CP-R6 | Compatibilidad: sin red | 1. Activar modo avión. 2. Jugar y abrir ventanas. | El juego funciona igual (100 % local) | Con `airplane_mode_on = 1` el HUD, los ciclos y las ventanas siguieron funcionando. | ✅ Pasa | `qa-cp-r6-sin-red.png`, `qa-cp-r6-airplane.txt` |
| CP-R7 | Fin de partida: derrota y victoria | 1. Mantener ≤ 25 % o ≥ 70 % de cuota 3 ciclos después del ciclo 10. | El diálogo aparece una vez; «Derrota» o «¡Victoria!» | Derrota al 0 %; victoria al 80 % con «¡Victoria!» y el signo de apertura visible. | ✅ Pasa | `qa-cp-r7-derrota.png`, `qa-cp-r7-victoria.png`, logs |
| CP-R8 | Regresión: Estadísticas | 1. Abrir pausa. 2. Estadísticas. 3. Comparar con el HUD. | Cuota, alumnos e ingreso coinciden con el HUD | HUD: `Alumnos: 690`, `Tú 80% \| Rival 20%`; Estadísticas: 690 alumnos, $69K/ciclo (600 × 100 × 1.15) y la misma cuota. | ✅ Pasa | `qa-cp-r8.png` |

Ficha de QA de Javier (J-CP1 a J-CP4):
[`pruebas-javier.md`](evidencia/edutycoon-ia/pruebas-javier.md).

Evidencia individual de ejecución:
[`gonzalez-jesus-edutycoon-ia.png`](evidencia/edutycoon-ia/gonzalez-jesus-edutycoon-ia.png) y
[`gamez-javier-edutycoon-ia.png`](evidencia/edutycoon-ia/gamez-javier-edutycoon-ia.png).

## Observaciones

- Para CP-R3 y CP-R7 se usó una compilación local temporal que iniciaba con
  100 M (`GameState.DINERO_INICIAL_TEST`). Ese cambio no se commiteó.
- La QA preliminar del rival (06/11/2026) está en
  [`docs/rival-ia.md`](rival-ia.md#qa-manual-en-dispositivo) y
  [`docs/evidencia/rival-ia/`](evidencia/rival-ia/).
- Hallazgos preexistentes, no causados por esta característica: el tamaño de
  texto del sistema se ignora (CP-R5) y cambiar `font_scale` con el juego
  abierto recrea la actividad y provoca `VisUI cannot be loaded twice` en
  `Main.kt` (J-CP4 de Javier). `DatabaseProvider` conserva
  `fallbackToDestructiveMigration()` para saltos de versión distintos de 4→5.

## Seguimiento en el fork del equipo

El repositorio original tiene las issues deshabilitadas, así que el seguimiento
vive en el fork:

| Issue | Tema | Estado |
|---|---|---|
| [#9](https://github.com/jesusGoliat/EDU_Tycoon/issues/9) | Instituto Rival controlado por IA | Cerrada |
| [#3](https://github.com/jesusGoliat/EDU_Tycoon/issues/3) | Ingreso 10× menor en la ventana de edificio | Cerrada |
| [#11](https://github.com/jesusGoliat/EDU_Tycoon/issues/11) | Signo «¡» del diálogo de victoria | Cerrada |
| [#18](https://github.com/jesusGoliat/EDU_Tycoon/issues/18) | Estadísticas con cuota y rival | Cerrada |
| [#19](https://github.com/jesusGoliat/EDU_Tycoon/issues/19) | `DatabaseTest` y migración 4→5 | Cerrada |
