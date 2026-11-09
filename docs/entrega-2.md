# Expediente e Índice de Verificación - Entrega 2

> **Institución:** Instituto Politécnico Nacional (IPN)  
> **Unidad Académica:** Escuela Superior de Cómputo (ESCOM)  
> **Programa Académico:** Ingeniería en Sistemas Computacionales (Plan 2020)  
> **Unidad de Aprendizaje:** Desarrollo de aplicaciones móviles nativas  
> **Semestre / Periodo:** 2027-1  
> **Profesor Titular:** M. en C. Gabriel Hurtado Avila  
> **Proyecto:** EduTycoon - Gestión e Infraestructura Politécnica  
> **Equipo:** Equipo EduTycoon ESCOM  
> **Integrantes:**  
> - Aragon Martinez Manuel Alejandro  
> - Caballero Pérez Julio César  
> - Hernandez Alvirde Maria Guadalupe  
> - Velazquez Beltrán Brandon  
> **Repositorio Fork (Equipo):** https://github.com/JulioCesarCaballero/EDU_Tycoon  
> **Repositorio Oficial (Profesor):** https://github.com/gabrielhuav/EDU_Tycoon  
> **Etiqueta de Entrega Final:** `parcial-2`  

---

## 1. Gestión Scrum y Tablero de Control

* **URL del Tablero Scrum (GitHub Projects):**  
  `https://github.com/users/JulioCesarCaballero/projects/5/views/1`
* **Columnas obligatorias configuradas:**  
  1. `Backlog` (Historias de usuario y requerimientos futuros)
  2. `Por hacer` (Tareas asignadas al sprint actual)
  3. `En progreso` (Desarrollo y pruebas locales activas)
  4. `En revisión` (Pull Request abierto, CI ejecutándose, revisión por pares en curso)
  5. `Terminado` (PR aprobado con `qua complete`, fusionado a la rama principal y verificado)

### Resumen del Sprint - Entrega 2
* **Objetivo del Sprint:** Desplegar la aplicación en canal de Prueba Cerrada de Google Play Console (v1.0.0), implementar la mejora interactiva de recaudación Tycoon por ciclos y publicar la actualización funcional OTA (v1.1.0).
* **Fechas del Sprint:** 26 de octubre al 9 de noviembre de 2026.
* **Resultado:** Cumplimiento del 100% de tareas comprometidas, 3 Pull Requests fusionados con CI verde, 2 lanzamientos en Play Console y actualización probada sin desinstalar.

---

## 2. Registro de los Tres Pull Requests Integrados

Conforme a la sección 1.2 del instrumento de evaluación, se presentan los tres Pull Requests integrados con participación comprobable del equipo, descripción en inglés y trazabilidad con sus Issues:

| PR # | Rama de Propósito | Issue Vinculada | Autor | Revisor / QA | Estado CI | Fecha Merge | Cambio Entregado / Impacto |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| **#7** | `feature/purchase-balance-rule` | Closes #6 | Julio César Caballero | Manuel Alejandro Aragón | ✅ Exit 0 | Oct 2026 | Regla estricta de saldo en compras para evitar números rojos y deshabilitar botón cuando los fondos son insuficientes. |
| **#12** | `feature/audio-toggle-pause-menu` | Closes #11 | Manuel Alejandro Aragón | Julio César Caballero | ✅ Exit 0 | Oct 2026 | Gestor centralizado de audio (`AudioManager`), música ambiental en bucle y botón reactivo con contraste visual en menú de pausa. |
| **#17** | `feature/interactive-building-income` | Closes #16 | Manuel Alejandro Aragón | Julio César Caballero | ✅ Exit 0 | Nov 2026 | **Mejora funcional de la actualización en Google Play:** Sistema Tycoon de recolección de dinero con ciclos variables, burbujas flotantes interactivas, audio de caja registradora y popup animado. |

---

## 3. Detalle de la Actualización Publicada en Google Play (Parte 3)

### 3.1 Identificación del Paquete y Lanzamientos en Google Play
* **Identificador de Paquete (applicationId):** `io.moviles.IPN_Tycoon`
* **Canal de Distribución:** Prueba Cerrada (Closed Testing).
* **Versión Línea Base (Lanzamiento 1):**  
  * `versionName`: `1.0.0` | `versionCode`: `1`  
  * Estado: Publicada y probada por los testers autorizados.
* **Versión Actualizada (Lanzamiento 2):**  
  * `versionName`: `1.1.0` | `versionCode`: `2`  
  * Estado: Actualización distribuida mediante la tienda y verificada en dispositivo sin desinstalar.

### 3.2 Transformación Funcional (Antes vs. Después)

| Aspecto | Comportamiento Anterior (v1.0.0) | Comportamiento Nuevo Implementado (v1.1.0) |
| :--- | :--- | :--- |
| **Generación de Ingresos** | Ingresos acreditados automáticamente de forma global cada 30 segundos sin importar el tipo de inmueble. | Cada edificio escolar cuenta con su **temporizador de ciclo independiente** configurado según su nivel y costo. |
| **Velocidad de Ciclos** | Tiempos idénticos e inciertos para todos los planteles. | Edificios iniciales (Mac and Cheese, Cafetería) tienen ciclos más lentos (45s-50s); escuelas avanzadas y Dirección General producen más rápido (15s-25s). |
| **Interacción y Recaudación** | Pasiva: el jugador recibía dinero sin interactuar ni saber cuándo ocurría. | **Activa y táctil:** al completarse el ciclo, aparece una **burbuja flotante interactiva** sobre el edificio para recaudar el dinero manualmente al tocarla. |
| **Retroalimentación Sensorial** | Notificación plana en negro en la parte inferior de la pantalla (`Toast`). | **Efecto de sonido de caja registradora (`coin.wav`)**, animación de oscilación y pulso en la burbuja, y texto ascendente con desvanecimiento (`+$X`). |
| **Transparencia en Ficha** | Ficha informativa mostraba valores estáticos sin indicar duraciones. | `BuildingInfoWindow` desglosa el tiempo exacto del ciclo, el saldo generado y un aviso destacado cuando hay fondos listos para cobrar. |

#### Evidencia Visual de la Transformación:

| Antes: Sin Burbujas (v1.0.0) | Después: Burbujas Interactivas (v1.1.0) |
| :---: | :---: |
| <img src="evidencia/entrega-2/captura_antes_de_burbujas_interactivas.jpg" width="340" /><br><sub>Generación pasiva e incierta sin avisos visuales ni sonoros.</sub> | <img src="evidencia/entrega-2/burbujas_interactivas.jpg" width="340" /><br><sub>Burbujas doradas flotantes sobre los planteles con ciclo concluido.</sub> |

### 3.3 Fundamentación Psicológica y de Diseño de Juego (Game Design)
La transformación de la recaudación pasiva a un bucle de recolección manual activo genera un impacto psicológico sustancial:
1. **Sensación de Agencia y Control:** El usuario deja de ser un espectador pasivo del saldo para convertirse en el gestor activo de sus ganancias.
2. **Ciclo de Recompensa Inmediata (*Gratification Loop*):** El estímulo visual de la burbuja que palpita sobre el edificio crea una llamada a la acción (*call to action*), cuya resolución con el tap ofrece una gratificación instantánea.
3. **Refuerzo Multisensorial:** La combinación de la animación flotante ascendente y el tintineo del sonido de monedas activa el refuerzo positivo operante, aumentando la satisfacción y el apego a la partida.
4. **Claridad del Modelo Mental Económico:** Al visualizar en la ficha que un edificio barato produce con lentitud mientras que un plantel universitario avanzado entrega mayores dividendos en ciclos cortos, el jugador comprende de manera intuitiva el retorno de inversión (ROI) y prioriza expandir su imperio educativo.

---

## 4. Matriz de Pruebas de QA y Verificación

La matriz exhaustiva con los **6 casos de prueba reales** exigidos por la rúbrica oficial se encuentra documentada en detalle en:
📄 [docs/pruebas.md](pruebas.md)

* **CP-E2-01:** Instalación inicial por Google Play y ejecución de partida base.
* **CP-E2-02:** Mecánica de recolección táctil mediante burbuja flotante, animación ascendente y retroalimentación auditiva.
* **CP-E2-03:** Variabilidad de ciclos económicos entre planteles y bloqueo de cobro anticipado.
* **CP-E2-04:** Actualización OTA de versión desde Google Play Store sin desinstalar y preservación de datos.
* **CP-E2-05:** Cumplimiento de accesibilidad, contrastes WCAG y área táctil mínima (48dp).
* **CP-E2-06:** Preservación de temporizadores y estabilidad ante ciclo de vida y suspensión en segundo plano.

---

## 5. Instrucciones para Personas Probadoras (Testers)

1. Abrir el enlace de invitación a la **Prueba Cerrada** de Google Play con la cuenta de Google autorizada.
2. Aceptar unirse al programa de evaluadores y descargar la aplicación **EduTycoon**.
3. En la primera ejecución (v1.0.0), jugar el tutorial, adquirir la Cafetería escolar o Mac & Cheese y salir al menú principal.
4. Al recibir la notificación de actualización de la tienda, presionar **Actualizar** para pasar a la versión 1.1.0 sin borrar datos ni desinstalar.
5. Reabrir EduTycoon y verificar la nueva mecánica de burbujas flotantes, el sonido de monedas al tocar y los nuevos datos de ciclo en la ficha de cada edificio.

---

## 6. Evidencias Multimedia

Las capturas y registros de prueba se organizan en el directorio oficial:
📁 `docs/evidencia/entrega-2/`
* `captura_antes_de_burbujas_interactivas.jpg` - Estado previo (v1.0.0) sin burbujas de recolección.
* `burbujas_interactivas.jpg` - Estado posterior (v1.1.0) con burbujas interactivas en campus.
* `burbuja_dinero_emergente.jpg` - Notificación emergente sobre el plantel escolar.
* `animacion_recoleccion_dinero.jpg` - Animación ascendente al tocar la burbuja.
* `saldo_actualizado_dinero_recolectado.jpg` - Presupuesto acreditado de inmediato en el HUD.
* `definicion_de_tiempo_por_ciclo_de_ingreso.jpg` - Duración del ciclo en ficha técnica de gestión.
* `tiempo_de_ciclo_ingresos_anadido.jpg` - Estado de producción en tiempo real en ficha de edificio.
* `play-cerrada-v1.png` - Ficha de publicación de versión inicial en Play Console.
* `play-actualizacion-v2.png` - Lanzamiento y notas de versión de la actualización en Play Console.
* `scrum.png` - Tablero Kanban/Scrum en GitHub Projects con historial de tarjetas cerradas.
* `pr-01.png`, `pr-02.png`, `pr-03.png` - Evidencia de PRs fusionados con revisiones formales.
