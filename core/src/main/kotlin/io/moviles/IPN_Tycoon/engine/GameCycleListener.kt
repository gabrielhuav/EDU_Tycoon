package io.moviles.IPN_Tycoon.engine

/**
 * Define el orden de resolución para los sistemas del juego.
 */
enum class ResolutionOrder {
    ECONOMY,      // 1. Ingresos y gastos
    CONSTRUCTION, // 2. Finalización de obras
    STUDENTS,     // 3. Generación y asignación
    RIVAL,        // 4. Ingresos y decisiones del Instituto Rival
    REPUTATION,   // 5. Cálculo de prestigio
    EVENTS        // 6. Eventos y consecuencias
}

/**
 * Interfaz para sistemas que reaccionan al ciclo de vida del juego.
 */
interface GameCycleListener {
    val resolutionOrder: ResolutionOrder

    /** Se llama cuando un ciclo comienza (fase de planificación) */
    fun onCycleStarted(cycle: Int) {}

    /** Se llama para procesar las consecuencias del ciclo actual */
    fun onResolveCycle(cycle: Int)
}
