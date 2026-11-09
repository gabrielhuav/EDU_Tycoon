# Ficha de idea: Cafetería ESCOM Tycoon (ruta EduTycoon)

> **Institución:** Instituto Politécnico Nacional (IPN)  
> **Unidad Académica:** Escuela Superior de Cómputo (ESCOM)  
> **Programa Académico:** Ingeniería en Sistemas Computacionales (Plan 2020)  
> **Unidad de Aprendizaje:** Desarrollo de aplicaciones móviles nativas  
> **Semestre / Periodo:** 2027-1  
> **Profesor Titular:** M. en C. Gabriel Hurtado Avila  
> **Equipo:** Equipo EduTycoon ESCOM  
> **Integrantes:**  
> - Velazquez Beltrán Brandon  
> - Caballero Pérez Julio César  
> - Hernandez Alvirde Maria Guadalupe  
> - Aragon Martinez Manuel Alejandro  
> **Repositorio:** https://github.com/JulioCesarCaballero/EDU_Tycoon  
> **Fecha:** 5 de octubre de 2026  
> **Versión de la ficha:** 2.0 (Entrega 1 - Puntos 1.1 y 1.3)  

---

## 1.1 Ficha de idea

### 1. Ruta elegida y motivo

**Ruta:** EduTycoon (Kotlin + libGDX, Android).

**Motivo:** EduTycoon ya tiene el motor base que necesita un juego de gestión y simulación de negocio: saldo persistente (`GameState`), regla estricta de compra (`puedeComprar` / `gastar`), simulación por ciclos (`GameCycleEngine`), resolución de ingresos (`EconomyEngine`), generador de eventos aleatorios (`EventEngine`) y persistencia local mediante Room.

En lugar de administrar la macroeconomía de todo el IPN de forma abstracta, adaptamos esa arquitectura hacia una vivencia estudiantil directa y tangible: **gestionar un puesto de comida dentro de la ESCOM**. De esta forma, el juego enseña conceptos fundamentales de microeconomía (costo unitario, margen de ganancia, fijación de precios, caducidad e inventario perecedero) dentro de un contexto escolar que los alumnos viven diariamente.

---

### 2. Usuario y contexto

* **¿Quién es?:** Un estudiante de la ESCOM o del IPN, de 18 a 24 años, que vende o ha pensado vender comida en la escuela (tortas, café, burritos, dulces), o que busca comprender de manera práctica cómo opera un pequeño emprendimiento estudiantil.
* **¿Dónde y cuándo usaría la app?:** En partidas cortas de 5 a 10 minutos: en el transporte público (Metro o camión) camino a la escuela, en pausas entre clases o en la fila de la cafetería escolar. Juega en orientación horizontal, en sesiones susceptibles a interrupciones rápidas, esperando poder pausar la partida y retomarla posteriormente sin pérdida de datos.

---

### 3. Problema observable en una sola frase

> **"Los estudiantes que quieren vender comida en la escuela compran insumos sin calcular costo, precio y demanda, y se dan cuenta de que perdieron dinero hasta que ya lo gastaron."**

---

### 4. Alternativa actual

Hoy, quien busca aprender a administrar un puesto escolar:
- Aprende a prueba y error **con su propio dinero**: compra insumos de más, sufre mermas por alimentos que se echan a perder o fija precios que no cubren sus costos operativos.
- Usa notas en el celular o una hoja de cálculo estática, herramientas que no simulan la variabilidad de la demanda ni imprevistos como lluvia, suspensión de clases o semanas de evaluación.
- Juega simuladores *tycoon* genéricos comerciales (restaurantes, cadenas de café) desvinculados de la realidad escolar: horarios de receso, días de examen, puentes oficiales y presupuestos limitados de estudiante.

---

### 5. Tarea principal

> **Preparar el puesto para un día de clases (comprar insumos con el saldo disponible y fijar precios de venta) y completar el día sabiendo con exactitud si ganó o perdió dinero y por qué.**

---

### 6. Criterio de éxito

La propuesta se considera exitosa si, en una prueba de usabilidad con al menos 5 compañeros de ESCOM que no hayan jugado previamente:
1. Al menos **4 de 5** completan el ciclo completo de un día (comprar insumos, fijar precios, abrir el puesto y revisar el resumen financiero) en **menos de 3 minutos**, de manera autónoma y sin ayuda.
2. Al menos **4 de 5** pueden explicar, al consultar el resumen final, **si tuvieron ganancia o pérdida neta y cuál fue la causa principal** (por ejemplo: *"se me quedaron 8 cafés sin vender"* o *"el precio asignado fue menor al costo del insumo"*).
3. En el **100% de los intentos**, el sistema impide realizar compras de insumos que superen el saldo disponible, garantizando que el dinero jamás resulte negativo.

---

### 7. Delimitación del alcance: Primera versión (MVP - Entrega 1)

Lo que **entra** en la primera versión:
- **Un solo puesto operativo:** La Cafetería de la ESCOM (inmueble ya modelado en `PropiedadRepository`).
- **4 productos emblemáticos:** Torta, café, burrito y agua embotellada. Cada uno con su costo unitario de insumo, precio de venta al público y factor de caducidad (el alimento no vendido al final del día se pierde como merma, excepto el agua).
- **Módulo de compra de insumos:** Selección de cantidad por producto, desglose del costo total y visualización clara del **saldo restante proyectado** antes de confirmar.
- **Regla estricta de compra:** Validación que bloquea la transacción si el costo total excede el saldo actual. El botón se desactiva e indica explícitamente cuánto dinero falta.
- **Fijación de precios:** El jugador establece el precio de venta de cada producto dentro de un rango comercial permitido.
- **Ciclo dinámico (Un día = Un ciclo):** Llegada de clientes simulada por intervalos horarios (alta afluencia en recesos y salida de clases), respondiendo al precio fijado y al inventario disponible.
- **Eventos aleatorios contextuales (`EventEngine`):** Situaciones típicas como semana de exámenes departamentales (mayor demanda de café), día lluvioso (menor afluencia general) o viernes de puente (plantel semivacío).
- **Pantalla de resumen del día:** Unidades vendidas, mermas/sobrantes, ingresos brutos, gastos de compra y utilidad neta final.
- **Persistencia local:** Guardado y carga del progreso mediante base de datos Room.
- **Manejo riguroso de estados alternos:**
  - **Carga:** Durante la simulación del ciclo del día o lectura de partidas guardadas.
  - **Vacío:** Intento de abrir el puesto sin inventario, o ausencia de partidas previas.
  - **Error:** Falla en la persistencia o lectura de datos locales.
  - **Dato inválido:** Compra que excede fondos, cantidades en cero o precios fuera de rango.

---

### 8. Funciones aplazadas (Deliberadamente fuera del MVP)

Para garantizar un alcance verificable, estable y enfocado en la Entrega 1:
- Gestión de múltiples locales o expansión a otras escuelas de Zacatenco (mapa completo del juego base).
- Contratación de ayudantes, turnos de trabajo y salarios.
- Adquisición de mejoras de infraestructura (refrigerador industrial, cafetera exprés, mobiliario adicional).
- Creación de recetas combinadas o ensamblado artesanal de ingredientes.
- Negociación con múltiples proveedores o solicitudes de crédito/deuda.
- Tablas de clasificación en línea, logros globales y sincronización en la nube.

---

### 9. Evidencia técnica e hipótesis pendiente de validar

#### Base técnica existente en el repositorio (Factibilidad)

| Fecha | Método | Resultado observado |
| :--- | :--- | :--- |
| **04/10/2026** | Inspección de arquitectura | `GameState` ya implementa control de saldo con `puedeComprar()` y `gastar()`, proporcionando el soporte directo para la regla de compra de insumos. |
| **04/10/2026** | Análisis de motores | `GameCycleEngine`, `EconomyEngine` y `EventEngine` resuelven la simulación por turnos y contingencias; un ciclo modela perfectamente un día escolar de venta. |
| **04/10/2026** | Catálogo de datos y Room | `PropiedadRepository` ya contiene la "Cafetería" y "Mac and Cheese", y la persistencia local con Room se encuentra operativa en la capa Android. |

*Nota técnica:* Esta tabla certifica la factibilidad de desarrollo sobre el código base; no sustituye la validación empírica con usuarios reales.

#### Hipótesis pendientes de validar

⚠️ **Declaración formal:** A la fecha de entrega no se han ejecutado encuestas cuantitativas ni pruebas de campo formales. Se plantean formalmente las siguientes hipótesis de trabajo:

> 1. Los estudiantes que comercializan alimentos en el plantel habitualmente no realizan un cálculo formal de costo unitario, precio de venta ni merma antes de surtirse de insumos.
> 2. Una simulación móvil ágil que transparente el costo de insumos, el saldo restante proyectado y el balance de cierre permite a los alumnos entender con mayor rapidez por qué un puesto genera utilidad o quiebra.

**Plan de validación empírica (a ejecutar previo a la Entrega 2):**

| Pregunta de investigación | Método | Muestra | Criterio de validación |
| :--- | :--- | :--- | :--- |
| **¿Calculan costos y precios antes de comprar?** | Entrevista estructurada (5 preguntas) | 5 estudiantes que vendan comida en la ESCOM | Al menos 3 de 5 declaran que no calculan con anticipación o que lo hacen solo de forma estimada ("a ojo"). |
| **¿Es intuitiva la tarea principal?** | Prueba de usabilidad con la v1 | 5 compañeros de clase | Al menos 4 de 5 completan la jornada de un día en menos de 3 minutos sin requerir orientación externa. |
| **¿Comprenden la causa de sus resultados?** | Cuestionario posterior a la partida | Los mismos 5 usuarios | Al menos 4 de 5 explican correctamente a partir del resumen si su balance fue positivo o negativo y qué insumo provocó el resultado. |

---

## 1.3 Historias de usuario y criterios de aceptación

### Historia de Usuario Principal (Core Loop del Puesto de Comida)

```gherkin
Como estudiante emprendedor y jugador de Cafetería ESCOM Tycoon,
quiero comprar insumos para mi puesto conociendo el costo total y el saldo restante antes de confirmar,
para abastecer mi inventario del día sin gastar más dinero del disponible ni provocar la insolvencia de mi negocio.
```

---

### Criterio de Aceptación Principal (Ruta Feliz: Compra Válida de Insumos)

```gherkin
Escenario: Compra exitosa de insumos dentro del presupuesto disponible
  Dado que el jugador dispone de un saldo de $500.00 MXN en GameState,
    y se encuentra en la pantalla de abastecimiento del puesto "Cafetería ESCOM",
    y selecciona 10 tortas (costo unitario: $20.00 MXN, subtotal: $200.00 MXN) 
    y 10 cafés (costo unitario: $10.00 MXN, subtotal: $100.00 MXN),
  Cuando visualiza el desglose con un costo total de $300.00 MXN y presiona el botón "Confirmar Compra",
  Entonces el sistema debita exitosamente los $300.00 MXN reflejando un saldo restante de $200.00 MXN,
    y el inventario del puesto se actualiza sumando 10 tortas y 10 cafés,
    y la interfaz habilita el botón "Abrir Puesto" para iniciar el día de clases.
```

*Verificabilidad:* **Sí / No**. Un evaluador externo puede ingresar las cantidades de insumos, comprobar que el saldo resultante coincida con la resta aritmética exacta ($500.00 - $300.00 = $200.00), presionar el botón de confirmación y constatar que el inventario se actualice en pantalla sin ambigüedades.

---

### Criterios de Aceptación para Estados Alternos y Casos Límite

#### Escenario Alterno 1: Bloqueo de Compra de Insumos por Saldo Insuficiente (Dato Inválido / Regla de Compra)
```gherkin
Escenario: El jugador intenta comprar insumos cuyo costo total excede su saldo
  Dado que el jugador cuenta con un saldo de $150.00 MXN,
    y selecciona insumos por un costo total de $400.00 MXN,
  Cuando el sistema calcula el importe de la orden,
  Entonces el botón "Confirmar Compra" se muestra deshabilitado (isDisabled = true),
    y el texto del botón se despliega en color rojo advirtiendo: "¡Saldo insuficiente! (Faltan $250.00)",
    y el saldo del jugador permanece intacto en $150.00 MXN sin registrar cargos ni entrega de productos.
```

#### Escenario Alterno 2: Fin de Jornada Escolar con Mermas y Balance Diario (Resumen del Día)
```gherkin
Escenario: Conclusión de las ventas del día y registro de productos perecederos
  Dado que el jugador abrió el puesto con 15 tortas y 20 cafés,
    y durante el ciclo del día se vendieron 10 tortas y 18 cafés,
  Cuando finaliza el horario escolar (cierre del ciclo),
  Entonces el sistema calcula que las 5 tortas y 2 cafés no vendidos se registran como merma (caducidad),
    y se despliega la pantalla de resumen diario detallando ingresos por venta, costo inicial de insumos y ganancia neta,
    y el inventario perecedero se reinicia en cero para el día siguiente.
```

#### Escenario Alterno 3: Manejo de Estado Vacío (Puesto sin Inventario)
```gherkin
Escenario: Intento de iniciar ventas sin haber adquirido insumos
  Dado que el jugador cuenta con 0 unidades en todos los productos de su inventario,
  Cuando pulsa el botón "Abrir Puesto",
  Entonces el sistema bloquea el avance del ciclo,
    y presenta una advertencia visual indicando: "Inventario vacío: Debes surtir insumos antes de abrir la cafetería",
    y mantiene la pantalla en el módulo de compras sin avanzar el reloj del día.
```

#### Escenario Alterno 4: Validación de Fijación de Precios fuera de Rango (Dato Inválido)
```gherkin
Escenario: El usuario intenta fijar un precio inferior al costo o excesivamente elevado
  Dado que una torta tiene un costo de insumo de $20.00 MXN (rango de venta válido: $22.00 a $45.00 MXN),
  Cuando el usuario intenta ingresar un precio de venta de $15.00 MXN o de $100.00 MXN,
  Entonces la interfaz rechaza el valor ingresado,
    y colorea el campo en advertencia mostrando el mensaje: "Precio fuera de rango comercial ($22 - $45)",
    y restablece el campo al precio sugerido por defecto.
```

#### Escenario Alterno 5: Restablecimiento y Consistencia de Partida (*Reset State*)
```gherkin
Escenario: Reinicio del emprendimiento tras una quiebra o desde el menú
  Dado que el jugador decide iniciar una nueva partida,
  Cuando confirma la acción "Reiniciar Cafetería",
  Entonces el saldo vuelve al capital inicial base ($500.00 MXN),
    y el inventario se reinicia en 0,
    y el contador de días se restablece en "Día 1 / Semana 1" sin dejar estados residuales en Room ni en memoria.
```

---

## 10. Declaración de uso de herramientas de Inteligencia Artificial

* En estricto apego a los lineamientos éticos de la materia, se declara de forma transparente que:
  * El primer borrador de la ficha y la delimitación conceptual del negocio escolar fueron estructurados con el apoyo de **Claude (Anthropic)**.
  * La articulación técnica de la arquitectura de software, la especificación de motores de libGDX (`GameState`, `EconomyEngine`, `EventEngine`), y la formulación rigurosa de las historias de usuario y criterios de aceptación en sintaxis formal Gherkin verificable fueron desarrolladas y refinadas con **Gemini (Google DeepMind)**.
  * Todos los textos, supuestos de negocio y reglas fueron revisados, verificados y aprobados por los integrantes del equipo.
