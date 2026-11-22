package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.rival.MercadoAlumnos

/**
 * Calcula ingresos pasivos de los edificios comprados cada ciclo.
 * Los efectos de eventos se resuelven en el mismo ciclo mediante EventEngine.
 * Payback objetivo: ~10 ciclos por edificio básico.
 */
class EconomyEngine : GameCycleListener {

    override val resolutionOrder = ResolutionOrder.ECONOMY

    data class CycleResult(val ingresos: Long)

    var lastResult: CycleResult? = null
        private set

    override fun onResolveCycle(cycle: Int) {
        // Ingreso base x factor de atraccion (cuota de mercado frente al rival).
        val ingresosTotales = MercadoAlumnos.ingresoJugador()

        lastResult = CycleResult(ingresosTotales)
        GameState.acreditar(ingresosTotales)
        GameState.ciclosJugados++
    }
}
