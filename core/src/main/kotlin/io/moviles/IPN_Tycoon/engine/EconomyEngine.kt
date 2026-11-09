package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository

/**
 * Registra estadísticas económicas por ciclo global y avance de periodos.
 * La recaudación directa de ingresos ha sido transformada en un sistema interactivo
 * por edificio gestionado por [BuildingIncomeManager], donde el jugador recolecta
 * activamente las ganancias al completarse la producción en cada plantel.
 */
class EconomyEngine : GameCycleListener {

    override val resolutionOrder = ResolutionOrder.ECONOMY

    private val INGRESO_POR_ALUMNO = 100L

    data class CycleResult(val ingresos: Long)

    var lastResult: CycleResult? = null
        private set

    override fun onResolveCycle(cycle: Int) {
        val ingresosTotales = PropiedadRepository.propiedades.values
            .filter { it.comprada }
            .sumOf { it.baseAlumnos * it.nivel * INGRESO_POR_ALUMNO }

        lastResult = CycleResult(ingresosTotales)
        GameState.ciclosJugados++
    }
}
