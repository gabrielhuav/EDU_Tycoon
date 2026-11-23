### 2.3 Recorrido del código: Compra y Mejora de una Propiedad

Elegimos la funcionalidad de "Comprar / Mejorar un edificio". El recorrido desde que el usuario interactúa hasta que el estado del juego cambia es el siguiente:

**1. Acción del usuario y Capa de Interfaz (`BuildingInfoWindow.kt`):**
El flujo inicia en el archivo `BuildingInfoWindow.kt`. Aquí se dibuja la ventana emergente y se definen los elementos de interfaz mediante Scene2D y VisUI. La vista consulta de forma reactiva y preventiva las reglas de compra mediante `ReglaDeCompra.evaluar()` al inicializarse o cada vez que cambia el saldo del jugador en `act()`:

```kotlin
// Actualización reactiva y evaluación preventiva de fondos
private fun actualizarEstado() {
    saldoMostrado = GameState.dinero
    saldoLabel.setText("Tu saldo: \$${fmt(saldoMostrado)}")

    when (val resultado = ReglaDeCompra.evaluar(saldoMostrado, ReglaDeCompra.costoSiguiente(data))) {
        is ResultadoCompra.Permitida -> {
            estadoLabel.setText("Saldo después: \$${fmt(resultado.saldoDespues)}")
            estadoLabel.color = Color.LIGHT_GRAY
            botonAccion.setText(textoAccion(resultado.costo))
            botonAccion.isDisabled = false
            botonAccion.color = Color.WHITE
        }
        is ResultadoCompra.SaldoInsuficiente -> {
            estadoLabel.setText("Te faltan \$${fmt(resultado.faltante)}")
            estadoLabel.color = Color.SALMON
            botonAccion.setText("SALDO INSUFICIENTE")
            botonAccion.isDisabled = true
            botonAccion.color = Color.GRAY
        }
        ResultadoCompra.NivelMaximo -> {
            estadoLabel.setText("")
            botonAccion.setText("NIVEL MÁXIMO")
            botonAccion.isDisabled = true
            botonAccion.color = Color.WHITE
        }
    }
}
```

Al presionar el botón de compra o mejora, se ejecuta `intentarCompra()`, la cual delega la transacción a `ReglaDeCompra.comprar(data)`. Si la operación es exitosa (`ResultadoCompra.Permitida`), se notifica el cambio al mapa y se cierra la ventana.

**2. Delegación a la Lógica de Dominio (`ReglaDeCompra.kt`):**
La lógica de negocio reside aislada de la interfaz en `io.moviles.IPN_Tycoon.engine.ReglaDeCompra`. Esta clase asegura transacciones atómicas e impide que el dinero caiga en números negativos:

```kotlin
fun comprar(propiedad: Propiedad): ResultadoCompra {
    val costo = costoSiguiente(propiedad) ?: return ResultadoCompra.NivelMaximo
    val saldo = GameState.dinero
    if (saldo < costo) {
        return ResultadoCompra.SaldoInsuficiente(costo = costo, faltante = costo - saldo)
    }

    GameState.dinero -= costo
    if (!propiedad.comprada) {
        propiedad.comprada = true
        propiedad.nivel = 1
    } else {
        propiedad.nivel++
    }
    return ResultadoCompra.Permitida(costo = costo, saldoDespues = GameState.dinero)
}
```

**3. Modificación del Modelo de Datos (`Propiedad.kt`):**
Los atributos de la instalación residen en el archivo `Propiedad.kt`. Al procesar la compra en la lógica de negocio, se actualiza el nivel y la bandera `comprada` de la instancia en memoria:

```kotlin
data class Propiedad(
    val id: String,
    val nombre: String,
    val precio: Long,
    val descripcion: String,
    val baseAlumnos: Int,
    val capacidad: Int,
    var nivel: Int = 0,
    var comprada: Boolean = false,
    val mejoraMax: Int = 3
)
```

**4. Efecto en la Lógica de Negocio (`EconomyEngine.kt`):**
Una vez que `Propiedad.comprada` es `true` y se incrementa su nivel, el motor económico en `EconomyEngine.kt` lo detecta durante el siguiente ciclo de renderizado y comienza a generar ingresos pasivos automáticamente utilizando la fórmula basada en alumnos y nivel:
```kotlin
val ingresoCiclo = propiedad.baseAlumnos * propiedad.nivel * 10L
```

---

### Declaración de uso de Inteligencia Artificial

En cumplimiento con los lineamientos de entrega de la práctica:
* **Herramientas utilizadas:** Asistentes de IA basados en Modelos de Lenguaje (LLM).
* **Propósito y alcance:** La asistencia de IA fue empleada como herramienta de consulta para la redacción técnica, análisis estático preliminar de código y estructuración de la matriz de pruebas.
* **Validación humana:** Todo el código, casos de prueba, resultados en dispositivos físicos (Samsung SM-A566E) y flujos arquitectónicos fueron revisados, verificados y validados manualmente por los integrantes del equipo.
