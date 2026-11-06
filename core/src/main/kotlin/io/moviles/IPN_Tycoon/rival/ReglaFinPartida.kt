package io.moviles.IPN_Tycoon.rival

import io.moviles.IPN_Tycoon.engine.Resultado

/**
 * Condiciones de fin de partida contra el rival (logica pura).
 *
 * Desde el ciclo [CICLO_MINIMO]: cuota >= [UMBRAL_VICTORIA] durante [CICLOS_REQUERIDOS]
 * ciclos seguidos es victoria; cuota <= [UMBRAL_DERROTA] durante el mismo numero es derrota.
 * Los contadores viven en [RivalState] y se reinician si la condicion se rompe.
 * El resultado se informa una sola vez por partida ([RivalState.finPartidaMostrado]).
 */
object ReglaFinPartida {

    const val CICLO_MINIMO = 10
    const val UMBRAL_VICTORIA = 0.70
    const val UMBRAL_DERROTA = 0.25
    const val CICLOS_REQUERIDOS = 3

    /** Devuelve el resultado la primera vez que se cumple una condicion; null en otro caso. */
    fun evaluar(ciclo: Int, cuotaJugador: Double, state: RivalState = RivalState): Resultado? {
        if (state.finPartidaMostrado || ciclo < CICLO_MINIMO) return null

        state.ciclosVictoria = if (cuotaJugador >= UMBRAL_VICTORIA) state.ciclosVictoria + 1 else 0
        state.ciclosDerrota = if (cuotaJugador <= UMBRAL_DERROTA) state.ciclosDerrota + 1 else 0

        val resultado = when {
            state.ciclosVictoria >= CICLOS_REQUERIDOS -> Resultado.VICTORIA
            state.ciclosDerrota >= CICLOS_REQUERIDOS -> Resultado.DERROTA
            else -> null
        }
        if (resultado != null) state.finPartidaMostrado = true
        return resultado
    }
}
