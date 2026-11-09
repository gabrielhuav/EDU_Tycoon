package io.moviles.IPN_Tycoon

import io.moviles.IPN_Tycoon.rival.MercadoAlumnos
import io.moviles.IPN_Tycoon.rival.RivalState
import kotlin.math.roundToInt

/** Cifras por ciclo de la ventana de estadisticas (sin dependencias de UI). */
data class StatsEscuela(
    val edificios: Int,
    val alumnos: Int,
    val ingresosCiclo: Long,
    val cuotaJugadorPct: Int,
    val cuotaRivalPct: Int,
    val edificiosRival: Int,
    val dineroRival: Long
) {
    companion object {
        /** Lee GameState / PropiedadRepository / RivalState y delega en MercadoAlumnos. */
        fun calcular(): StatsEscuela {
            val cuota = MercadoAlumnos.cuotaJugador()
            val jugadorPct = (cuota * 100).roundToInt()
            return StatsEscuela(
                edificios = PropiedadRepository.propiedades.values.count { it.comprada },
                alumnos = MercadoAlumnos.alumnosJugador(),
                ingresosCiclo = MercadoAlumnos.ingresoJugador(),
                cuotaJugadorPct = jugadorPct,
                cuotaRivalPct = 100 - jugadorPct,
                edificiosRival = RivalState.edificios.size,
                dineroRival = RivalState.dinero
            )
        }
    }
}
