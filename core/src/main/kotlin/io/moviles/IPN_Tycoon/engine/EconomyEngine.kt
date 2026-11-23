package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.rival.MercadoAlumnos

/**
 * Registra estadísticas económicas por ciclo global y avance de periodos.
 * La recaudación directa de ingresos ha sido transformada en un sistema interactivo
 * por edificio gestionado por [BuildingIncomeManager], donde el jugador recolecta
 * activamente las ganancias al completarse la producción en cada plantel.
 */
class EconomyEngine : GameCycleListener {

    override val resolutionOrder = ResolutionOrder.ECONOMY

    data class CycleResult(val ingresos: Long)

    var lastResult: CycleResult? = null
        private set

    override fun onResolveCycle(cycle: Int) {
        // Ingreso que darían los edificios este ciclo, con el factor de mercado. Ya no se
        // acredita aquí: el jugador lo recoge en las burbujas (BuildingIncomeManager).
        val ingresosTotales = MercadoAlumnos.ingresoJugador()

        lastResult = CycleResult(ingresosTotales)
        GameState.ciclosJugados++
    }
}
