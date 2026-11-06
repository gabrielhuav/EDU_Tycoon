package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.rival.MercadoAlumnos
import io.moviles.IPN_Tycoon.rival.RivalAccion
import io.moviles.IPN_Tycoon.rival.RivalAgent
import io.moviles.IPN_Tycoon.rival.RivalState

/** Resultado de la partida contra el rival (lo usa la etapa de fin de partida). */
enum class Resultado { VICTORIA, DERROTA }

/**
 * Acredita los ingresos del rival y le da un turno a su IA cada ciclo.
 * Corre despues de STUDENTS (ResolutionOrder.RIVAL).
 */
class RivalEngine(
    private val agent: RivalAgent = RivalAgent(),
    private val onAccion: (RivalAccion) -> Unit = {},
    @Suppress("unused") private val onFinPartida: (Resultado) -> Unit = {}
) : GameCycleListener {

    override val resolutionOrder = ResolutionOrder.RIVAL

    override fun onResolveCycle(cycle: Int) {
        RivalState.dinero += MercadoAlumnos.ingresoRival()
        agent.tomarTurno().forEach(onAccion)
    }
}
