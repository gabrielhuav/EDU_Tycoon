# Matriz de Pruebas de QA - Entrega 1 y Entrega 2

## Proyecto: EduTycoon - Gestión e Infraestructura Politécnica
**Institución:** Instituto Politécnico Nacional (IPN)  
**Unidad Académica:** Escuela Superior de Cómputo (ESCOM)  
**Asignatura:** Desarrollo de aplicaciones móviles nativas (2027-1)  
**Característica Entrega 2:** Sistema interactivo de recolección de ingresos por edificio (Tycoon) y actualización en Google Play  
**Rama:** `feature/interactive-building-income`  
**Versiones evaluadas:**  
- **Línea Base (Primer Lanzamiento Play Store):** Versión `1.0.0` (versionCode `1`)
- **Actualización Publicada (Segundo Lanzamiento Play Store):** Versión `1.1.0` (versionCode `2`)

---

# PARTE I: MATRIZ DE QA OFICIAL - ENTREGA 2 (ACTUALIZACIÓN GOOGLE PLAY)

## 📋 Resumen Ejecutivo de Pruebas (Entrega 2)
Esta matriz documenta la validación integral conforme a las especificaciones oficiales de la **Entrega 2**. Se evalúa el ciclo de vida de distribución en canal de **Prueba Cerrada de Google Play Console**, la preservación de datos ante actualización OTA (sin desinstalar) y la implementación de la nueva mecánica Tycoon: **generación de burbujas flotantes interactivas por ciclo de edificio, recolección manual táctil, retroalimentación auditiva y visual de ganancias**.

* **Dispositivo de prueba 1:** Google Pixel 7 (Emulador Android Studio - API 34).
* **Dispositivo de prueba 2:** Samsung Galaxy S21 / Dispositivo físico (API 34 / Android 14).
* **Dispositivo de prueba 3:** Samsung Galaxy A56 (SM-A566E) / Dispositivo físico (API 36.1 / Android 16).
* **Herramienta de pruebas automatizadas:** JUnit 4 / Gradle (`./gradlew :core:test`).
* **Verificación de construcción móvil:** Gradle (`./gradlew assembleDebug`).

---

## 🧪 Matriz de los 6 Casos de Prueba Oficiales (Entrega 2)

| ID | Caso de Prueba | Categoría / Requisito PDF | Pasos de Ejecución | Resultado Esperado | Resultado Observado | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CP-E2-01** | Instalación inicial desde Google Play y arranque de partida | Instalación Inicial por Google Play (Ruta Feliz) | 1. Ingresar con cuenta de tester al enlace de prueba cerrada en Google Play Store.<br>2. Instalar la versión inicial `1.0.0` (versionCode `1`).<br>3. Abrir la aplicación y crear una nueva partida ingresando nombre y escuela en el tutorial. | La aplicación descarga, instala y abre sin cierres forzados. El tutorial del Ing. Cárdenas se ejecuta correctamente y el mapa isométrico se carga con el presupuesto inicial ($500,000 MXN). | Descarga correcta vía Google Play, ejecución fluida y juego listo para gestión. | ✅ APROBADO |
| **CP-E2-02** | Generación de burbuja flotante y recaudación manual táctil con audio y animación | Funcionamiento de la Mejora (Tarea Principal Tycoon) | 1. En la partida, adquirir el plantel "Mac and Cheese" o "Cafetería".<br>2. Aguardar el cumplimiento del ciclo de producción (45s - 50s).<br>3. Observar la aparición de la burbuja interactiva dorada sobre el edificio.<br>4. Tocar la burbuja para recolectar el dinero. | Aparece la burbuja flotante con animación oscilatoria (*bobbing*) y la etiqueta `+$X`. Al pulsarla, se reproduce el efecto de sonido de caja registradora (`coin.wav`), se despliega la animación flotante con desvanecimiento de saldo acreditado y el saldo del HUD en la esquina superior izquierda se incrementa de inmediato. | Burbuja generada en techo del edificio, efecto sonoro limpio, animación de texto `+$X` ascendente y saldo actualizado. | ✅ APROBADO |
| **CP-E2-03** | Variabilidad de tiempos de producción y bloqueo de cobro anticipado | Entrada / Caso Límite y Regla de Negocio | 1. Poseer dos edificios: uno inicial ("Mac and Cheese", ciclo de 50s) y uno avanzado ("Auditorio", ciclo de 25s).<br>2. Abrir la ventana de gestión del edificio antes de que termine el ciclo.<br>3. Intentar cobrar antes de tiempo tocando el edificio. | La ventana de información muestra con precisión el tiempo de ciclo configurado y los segundos restantes. El sistema no genera ingresos antes de tiempo ni permite cobros prematuros (`recolectar()` devuelve 0L). Los edificios avanzados completan sus ciclos significativamente más rápido que los iniciales. | Tiempos independientes verificados, cobro bloqueado mientras la producción está en curso y ventana informativa veraz. | ✅ APROBADO |
| **CP-E2-04** | Actualización de versión vía Play Store sin desinstalar | Instalación de Actualización (Google Play) | 1. Con la versión `1.0.0` instalada y con progreso guardado (edificios comprados y saldo acumulado), ingresar a Google Play Store.<br>2. Seleccionar "Actualizar" a la versión `1.1.0` (versionCode `2`).<br>3. Abrir la app tras completarse la descarga sin borrar datos. | La aplicación se actualiza correctamente conservando los datos de sesión previa (`GameState` / Room / SharedPreferences). El nuevo motor de recaudación interactiva queda activo en la partida existente sin inconsistencias. | Actualización completada sin desinstalación previa; versión 1.1.0 activa con datos persistidos intactos. | ✅ APROBADO |
| **CP-E2-05** | Accesibilidad visual, contraste WCAG y retroalimentación táctil | Accesibilidad y Usabilidad | 1. Configurar texto grande (130%) en los ajustes del sistema operativo.<br>2. Activar modo silenciado desde el menú de pausa y observar las burbujas en el mapa.<br>3. Verificar área de toque táctil de las burbujas y contraste del texto dorado/verde sobre el mapa. | La burbuja de recaudación posee un área táctil mínima generosa (>48dp en espacio de pantalla), contraste nítido y legible contra cualquier fondo de terreno, y provee retroalimentación visual inmediata aunque el audio esté silenciado. | Alta legibilidad visual, fácil pulsación con una sola mano y contraste conforme a WCAG 2.1 AA. | ✅ APROBADO |
| **CP-E2-06** | Preservación de temporizadores ante pausa, suspensión y minimizado | Ciclo de Vida y Persistencia de Estado | 1. Dejar un edificio en producción al 50% de su ciclo.<br>2. Presionar el botón Home para minimizar la app a segundo plano.<br>3. Abrir otras aplicaciones y regresar a EduTycoon tras varios minutos. | La aplicación recupera el foco sin fallas de memoria (OOM), el temporizador de producción avanza correctamente de acuerdo al tiempo transcurrido o retoma la ejecución, activando la burbuja si el ciclo culminó en segundo plano. | Sin fallas ni desincronización de texturas en libGDX tras retorno de segundo plano. | ✅ APROBADO |

---

## 🤖 Cobertura de Pruebas Unitarias Automatizadas (CI - Entrega 2)

Se crearon y validaron pruebas automatizadas en `io.moviles.IPN_Tycoon.engine.BuildingIncomeManagerTest` y `io.moviles.IPN_Tycoon.engine.AudioManagerTest`:

* `ciclo no avanza para propiedades no compradas`: Garantiza que edificios sin adquirir no consuman ni generen ingresos.
* `ciclo acumula tiempo y marca listo para recolectar al completarse`: Valida la transición temporal y el cálculo aritmético exacto del ingreso.
* `recolectar acredita saldo en GameState y reinicia ciclo`: Valida la acreditación atómica en `GameState.dinero` y el reseteo del temporizador.
* `edificios iniciales tienen ciclos mas lentos que edificios avanzados`: Verifica la curva de progresión y ritmo económico entre planteles baratos vs. caros.
* `recolectar sin ingresos listos devuelve cero y no altera saldo`: Previene condiciones de carrera o sobregiros de recaudación.
* `recolectarTodo procesa todos los edificios con ciclo completado`: Asegura el funcionamiento masivo de recolección en todo el campus.
* `reproducirSonidoDinero maneja entornos headless sin fallos`: Garantiza la robustez de audio en servidores de integración continua sin tarjeta de sonido.

---

## 📸 Evidencias Visuales de Implementación y QA (Entrega 2)

### 1. Comparativa Antes vs. Después (CP-E2-02)

| Antes: Sin Burbujas (Generación Pasiva) | Después: Burbujas Interactivas (Recaudación Táctil) |
| :---: | :---: |
| <img src="evidencia/entrega-2/captura_antes_de_burbujas_interactivas.jpg" width="340" /><br><sub>**Antes:** Generación pasiva e incierta sin avisos visuales ni sonoros.</sub> | <img src="evidencia/entrega-2/burbujas_interactivas.jpg" width="340" /><br><sub>**Después:** Burbujas doradas flotantes sobre los edificios con ciclo concluido.</sub> |

### 2. Transparencia y Tiempos de Ciclo en Ficha de Gestión (CP-E2-03)

| Definición de Tiempo por Ciclo | Tiempo de Ciclo de Ingresos Añadido |
| :---: | :---: |
| <img src="evidencia/entrega-2/definicion_de_tiempo_por_ciclo_de_ingreso.jpg" width="340" /><br><sub>Estadísticas de plantel y duración del ciclo de producción (ej. 35s).</sub> | <img src="evidencia/entrega-2/tiempo_de_ciclo_ingresos_anadido.jpg" width="340" /><br><sub>Desglose de ingresos por ciclo y alerta de fondos listos para cobrar.</sub> |

### 3. Notificación Emergente, Animación y Acreditación de Saldo (CP-E2-02, CP-E2-05)

| Burbuja Emergente sobre Plantel | Animación Ascendente de Recaudación | Saldo Acreditado en el HUD |
| :---: | :---: | :---: |
| <img src="evidencia/entrega-2/burbuja_dinero_emergente.jpg" width="230" /><br><sub>Burbuja dorada emergente sobre el edificio.</sub> | <img src="evidencia/entrega-2/animacion_recoleccion_dinero.jpg" width="230" /><br><sub>Animación flotante del monto (`+$X`) con sonido `coin.wav`.</sub> | <img src="evidencia/entrega-2/saldo_actualizado_dinero_recolectado.jpg" width="230" /><br><sub>Saldo del presupuesto incrementado al instante.</sub> |

---

# PARTE II: HISTORIAL DE PRUEBAS DE LÍNEA BASE - ENTREGA 1

### Módulo 1: Regla de Compra y Saldo (`feature/purchase-balance-rule`)

| ID | Caso de Prueba | Tipo / Categoría | Pasos de Ejecución | Resultado Esperado | Resultado Real | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CP-01** | Compra exitosa de edificio con saldo suficiente | Flujo Principal (Ruta Feliz) | 1. Iniciar partida con saldo inicial ($500,000 MXN).<br>2. Abrir la ventana de gestión del edificio "Escuela de Computación" ($300,000 MXN).<br>3. Verificar que el botón muestre "COMPRAR $300.0K".<br>4. Pulsar el botón. | `ReglaDeCompra.comprar()` devuelve `ResultadoCompra.Permitida`. El saldo se debita a $200,000 MXN, el edificio se marca como comprado con nivel 1 y la ventana se cierra actualizando el mapa. | Saldo debitado a $200,000 MXN, nivel 1 asignado y sprite actualizado correctamente. | ✅ APROBADO |
| **CP-02** | Bloqueo preventivo de compra por saldo insuficiente | Caso Límite / Error de Saldo | 1. Iniciar partida con saldo de $200,000 MXN.<br>2. Abrir ventana de "Dirección General" cuyo costo es de $5,000,000 MXN.<br>3. Observar el estado del botón y del indicador de saldo.<br>4. Intentar interactuar con el botón. | `ReglaDeCompra.evaluar()` devuelve `ResultadoCompra.SaldoInsuficiente`. El botón se inicializa deshabilitado (`isDisabled = true`) mostrando en rojo "¡SALDO INSUFICIENTE! (Faltan $4.80M)", y el saldo permanece intacto en $200,000 MXN. | Botón deshabilitado en color rojo, texto con saldo faltante visible y saldo intacto en $200,000 MXN. | ✅ APROBADO |
| **CP-03** | Mejora de nivel de edificio con fondos insuficientes | Caso Límite / Transacción de Mejora | 1. Poseer un edificio en nivel 1 cuyo costo de mejora a nivel 2 sea de $300,000 MXN.<br>2. Ajustar saldo a $100,000 MXN.<br>3. Abrir ventana de gestión del edificio.<br>4. Verificar validación de saldo. | El sistema detecta que el saldo ($100,000) es menor al costo de mejora ($300,000), devolviendo `ResultadoCompra.SaldoInsuficiente` y bloqueando la transacción sin alterar el nivel. | Transacción rechazada, nivel se mantiene en 1 y saldo permanece en $100,000 MXN. | ✅ APROBADO |
| **CP-04** | Preservación de saldo y estado ante recreación o ciclo de vida | Ciclo de Vida / Recreación | 1. Realizar una compra exitosa de edificio.<br>2. Provocar recreación o cambio de foco de la actividad.<br>3. Volver al estado activo del juego. | El saldo debitado y las propiedades compradas persisten sin duplicación de cobro ni reseteo no deseado. | El saldo y nivel de edificios se conservan intactos en memoria (`GameState`). | ✅ APROBADO |
| **CP-05** | Accesibilidad visual y retroalimentación de contraste | Accesibilidad / UI Feedback | 1. Habilitar modo de texto ampliado / accesibilidad en ajustes del sistema.<br>2. Provocar estado de saldo insuficiente en la ventana de gestión.<br>3. Evaluar legibilidad de textos y contraste del botón. | El texto "¡SALDO INSUFICIENTE!" se visualiza con contraste nítido (Color Rojo / Alerta), sin solapamiento de elementos y con área táctil accesible. | Texto legible con contraste adecuado y dimensiones de ventana adaptativas. | ✅ APROBADO |

### Módulo 2: Control de Audio en Menú de Pausa (`feature/audio-toggle-pause-menu`)

| ID | Caso de Prueba | Tipo / Categoría | Pasos de Ejecución | Resultado Esperado | Resultado Real | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CP-AUDIO-01** | Silenciamiento de audio desde el menú de pausa | Flujo Principal / Mute | 1. Iniciar partida y presionar el botón de Menú.<br>2. Observar el botón de audio (muestra "Audio: ACTIVADO" en verde).<br>3. Pulsar el botón de audio. | El botón actualiza su texto a "Audio: SILENCIADO", cambia de color a coral/rojo, `volumenMaster` pasa a 0.0f y `musicaActiva` pasa a `false`. | Texto actualizado a "Audio: SILENCIADO", color coral aplicado y volumen en 0.0f. | ✅ APROBADO |
| **CP-AUDIO-02** | Reactivación de audio desde el menú de pausa | Flujo Principal / Unmute | 1. Estando en estado silenciado, presionar nuevamente el botón de audio. | El botón actualiza su texto a "Audio: ACTIVADO", cambia de color a verde, `volumenMaster` se restablece a 1.0f y `musicaActiva` pasa a `true`. | Texto actualizado a "Audio: ACTIVADO", color verde y volumen restablecido a 1.0f. | ✅ APROBADO |
| **CP-AUDIO-03** | Persistencia de estado al cerrar y reabrir el menú | Estado de UI / Persistencia | 1. Silenciar el audio en el menú de pausa.<br>2. Cerrar la ventana del menú de pausa regresando al juego.<br>3. Volver a abrir el menú de pausa. | El botón de audio se inicializa mostrando el estado actual persistido ("Audio: SILENCIADO" en color coral) sin revertirse. | Estado conservado correctamente al reabrir la ventana. | ✅ APROBADO |
| **CP-AUDIO-04** | Consistencia de audio ante recreación o ciclo de vida | Ciclo de Vida / Recreación | 1. Silenciar el audio en el juego.<br>2. Forzar pausa o recreación de la actividad en Android.<br>3. Volver a consultar el estado del audio. | El volumen permanece silenciado (0.0f) y `GameState.musicaActiva` conserva su valor `false`. | Estado de audio preservado sin desincronización. | ✅ APROBADO |
| **CP-AUDIO-05** | Accesibilidad visual y feedback de contraste | Accesibilidad / UI Feedback | 1. Observar la legibilidad de la etiqueta del botón tanto en modo activo como silenciado. | El contraste de texto y fondo en ambos estados ("ACTIVADO" verde y "SILENCIADO" coral) es legible y accesible conforme a pautas WCAG. | Alto contraste visual y retroalimentación táctil clara. | ✅ APROBADO |

---

### Módulo 3: Pruebas Funcionales en Dispositivo Físico (Samsung SM-A566E)

#### Caso 1: Flujo principal (Happy Path) - Compra exitosa con saldo suficiente
* **Objetivo:** Verificar que el jugador pueda comprar una instalación cuando cuenta con el dinero necesario en un dispositivo físico.
* **Pasos:**
  1. Iniciar el juego y verificar que el saldo inicial cubra la propiedad "Mac and Cheese" (costo $120,000).
  2. Tocar el edificio "Mac and Cheese" en el mapa.
  3. Pulsar el botón "COMPRAR $120.0K" en la ventana de información.
* **Resultado Esperado:** 
  - El botón ejecuta el cobro y la ventana se cierra.
  - El saldo se reduce en $120,000 de forma correcta.
  - El edificio cambia su estado a comprado (nivel 1) y comienza a generar ingresos en los siguientes ciclos.
* **Resultado Real:** La compra se efectúa correctamente y el saldo disminuye conforme a lo esperado.
* **Estado:** APROBADO (PASS).

#### Caso 2: Datos vacíos o inválidos - Compra con saldo insuficiente (Detección de defecto y validación preventiva)
* **Objetivo:** Comprobar el comportamiento del sistema cuando el saldo disponible es menor al costo de la instalación.
* **Pasos:**
  1. Iniciar la aplicación con un saldo inicial bajo (ej. $10,000).
  2. Tocar el "Auditorio Alejo Peralta" en el mapa (costo base $800,000).
  3. Observar la ventana `BuildingInfoWindow` y pulsar el botón de comprar.
* **Resultado Esperado:** 
  - El sistema debe prevenir la compra desde el inicio.
  - El botón debe permanecer deshabilitado con retroalimentación inmediata de saldo insuficiente.
  - El saldo no debe sufrir modificaciones ni pasar a números negativos.
* **Resultado Real:** En la versión inicial previa a `ReglaDeCompra`, el botón aparecía habilitado visualmente y únicamente tras pulsarlo se actualizaba a "¡Saldo insuficiente!" bloqueándose (sin descontar saldo negativo debido al guard clause en `GameState`). Con la integración de `ReglaDeCompra`, la validación es preventiva: el botón se inicializa deshabilitado (`isDisabled = true`), mostrando el texto en color de alerta y desglosando la cantidad faltante.
* **Estado:** APROBADO (PASS) / DEFECTO PREVIAMENTE DETECTADO Y SUBSANADO.

#### Caso 3: Ciclo de vida y Recreación de actividad (Pausa, minimización y reanudación)
* **Objetivo:** Confirmar que al minimizar la app o pausar la actividad no se reinicie la partida en curso ni se pierda el progreso.
* **Pasos:**
  1. Comprar un edificio y acumular al menos 3 ciclos de ingresos.
  2. Minimizar la aplicación presionando el botón Home o cambiando a la vista de aplicaciones recientes.
  3. Reanudar EduTycoon desde la multitarea.
* **Resultado Esperado:** El motor gráfico (LibGDX) y el estado de la actividad se reanudan correctamente, manteniendo intactos los edificios comprados, su nivel y el saldo actual sin pérdida de progreso.
* **Resultado Real:** Los datos se conservan en memoria dentro de `GameState` y la superficie de renderizado se reanuda sin reiniciar el ciclo de juego.
* **Estado:** APROBADO (PASS).

#### Caso 4: Red no disponible - Modo avión y juego offline
* **Objetivo:** Verificar el funcionamiento de la aplicación sin conexión activa a Internet.
* **Pasos:**
  1. Activar el Modo Avión en el dispositivo.
  2. Abrir la aplicación EduTycoon desde un arranque limpio (Cold start).
  3. Interactuar con las mecánicas (recolectar ingresos, abrir ventanas de edificios).
* **Resultado Esperado:** La aplicación debe abrir sin cerrarse inesperadamente, el motor económico debe seguir calculando ciclos y no deben aparecer pantallas bloqueantes de error de red.
* **Resultado Real:** La aplicación es completamente local. Las operaciones y el renderizado corren con normalidad sin requerir servidores externos.
* **Estado:** APROBADO (PASS).

#### Caso 5: Accesibilidad - Adaptación de Interfaz de Usuario
* **Objetivo:** Evaluar si las ventanas emergentes (`VisWindow`) y textos del juego se mantienen legibles y dentro de la pantalla en distintas densidades de píxeles o resoluciones.
* **Pasos:**
  1. Entrar a los Ajustes de pantalla del dispositivo y subir el tamaño de visualización / fuente al máximo.
  2. Regresar a EduTycoon y abrir la ventana de "Dirección General".
  3. Observar los textos descriptivos y el botón de compra.
* **Resultado Esperado:** Los componentes de la interfaz (`Scene2D`) deben organizarse sin solapar texto ni esconder botones críticos fuera del área táctil.
* **Resultado Real:** La ventana de gestión muestra toda la información (capacidad, ingreso, saldo). Dependiendo de la relación de aspecto del teléfono, el texto descriptivo hace un salto de línea correcto sin desbordar.
* **Estado:** APROBADO (PASS).
