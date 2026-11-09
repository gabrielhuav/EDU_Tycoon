package io.moviles.IPN_Tycoon.engine

import kotlin.math.ceil

/**
 * Velocidades de simulación disponibles para el ciclo escolar.
 */
enum class VelocidadCiclo(val multiplicador: Float, val etiqueta: String) {
    PAUSA(0f, "PAUSA"),
    NORMAL(1f, "1x"),
    RAPIDO(2f, "2x");

    fun siguiente(): VelocidadCiclo = when (this) {
        NORMAL -> RAPIDO
        RAPIDO -> PAUSA
        PAUSA  -> NORMAL
    }
}

/**
 * Controla el progreso temporal de cada ciclo escolar (30s base) y permite
 * pausar o duplicar la velocidad de simulación desde el HUD.
 */
class CycleTimeController(
    val duracionCicloSegundos: Float = 30f
) {
    init {
        require(duracionCicloSegundos > 0f) { "La duración del ciclo debe ser mayor a 0." }
    }

    var velocidadActual: VelocidadCiclo = VelocidadCiclo.NORMAL
        private set

    var tiempoTranscurrido: Float = 0f
        private set

    val estaPausado: Boolean
        get() = velocidadActual == VelocidadCiclo.PAUSA

    /**
     * Alterna secuencialmente entre 1x -> 2x -> PAUSA -> 1x.
     */
    fun alternarVelocidad(): VelocidadCiclo {
        velocidadActual = velocidadActual.siguiente()
        return velocidadActual
    }

    /**
     * Fija explícitamente una velocidad de simulación.
     */
    fun cambiarVelocidad(nueva: VelocidadCiclo) {
        velocidadActual = nueva
    }

    /**
     * Avanza el reloj del ciclo según el delta recibido y la velocidad actual.
     * @param deltaSegundos Tiempo real transcurrido en el frame.
     * @param bloqueado Si hay un modal bloqueante (ej. ventana de decisión), no avanza el tiempo.
     * @return Número de ciclos completos alcanzados en este paso.
     */
    fun avanzar(deltaSegundos: Float, bloqueado: Boolean = false): Int {
        if (bloqueado || estaPausado || deltaSegundos <= 0f) return 0

        tiempoTranscurrido += deltaSegundos * velocidadActual.multiplicador
        var ciclosCompletados = 0
        while (tiempoTranscurrido >= duracionCicloSegundos) {
            tiempoTranscurrido -= duracionCicloSegundos
            ciclosCompletados++
        }
        return ciclosCompletados
    }

    /**
     * Fracción de progreso del ciclo actual en el rango [0.0, 1.0].
     */
    fun progreso(): Float =
        (tiempoTranscurrido / duracionCicloSegundos).coerceIn(0f, 1f)

    /**
     * Porcentaje entero de avance del ciclo actual [0..100].
     */
    fun porcentajeProgreso(): Int =
        (progreso() * 100f).toInt().coerceIn(0, 100)

    /**
     * Segundos restantes (en tiempo base del ciclo) para concluir el ciclo actual.
     */
    fun segundosRestantes(): Int =
        ceil((duracionCicloSegundos - tiempoTranscurrido).coerceAtLeast(0f)).toInt()

    /**
     * Reinicia el progreso del ciclo actual y restablece velocidad normal.
     */
    fun reiniciar() {
        tiempoTranscurrido = 0f
        velocidadActual = VelocidadCiclo.NORMAL
    }
}
