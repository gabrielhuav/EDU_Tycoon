package io.moviles.IPN_Tycoon.rival

import io.moviles.IPN_Tycoon.PropiedadRepository
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Modelo de cuota de mercado de alumnos entre el jugador y el Instituto Rival.
 *
 * El "poder" de una escuela es la suma de baseAlumnos * nivel de sus edificios.
 * La cuota del jugador es su proporcion del poder total y se traduce en un factor
 * de atraccion (0.75 - 1.25) que multiplica alumnos e ingresos.
 *
 * Nota de balance: si el rival no tiene edificios y el jugador si, la cuota es 1.0
 * y el factor 1.25 (bonus de monopolio). Si ninguno tiene edificios la cuota es 0.5.
 */
object MercadoAlumnos {

    const val INGRESO_POR_ALUMNO = 100L
    const val FACTOR_MIN = 0.75
    const val FACTOR_MAX = 1.25

    /** Poder de una lista de edificios dados como (baseAlumnos, nivel). */
    fun poder(edificios: Iterable<Pair<Int, Int>>): Long =
        edificios.sumOf { (base, nivel) -> base.toLong() * nivel }

    fun poderJugador(): Long = poder(
        PropiedadRepository.propiedades.values
            .filter { it.comprada }
            .map { it.baseAlumnos to it.nivel }
    )

    fun poderRival(): Long = poder(
        RivalState.edificios.mapNotNull { (id, nivel) ->
            PropiedadRepository.getPropiedad(id)?.let { it.baseAlumnos to nivel }
        }
    )

    /** Cuota del jugador: poderJ / (poderJ + poderR), 0.5 si ambos son 0. */
    fun cuota(poderJugador: Long, poderRival: Long): Double {
        val total = poderJugador + poderRival
        return if (total <= 0L) 0.5 else poderJugador.toDouble() / total
    }

    fun cuotaJugador(): Double = cuota(poderJugador(), poderRival())

    /** Factor de atraccion = 0.75 + 0.5 * cuota, acotado a [0.75, 1.25]. */
    fun factorAtraccion(cuota: Double): Double =
        (FACTOR_MIN + 0.5 * cuota).coerceIn(FACTOR_MIN, FACTOR_MAX)

    /** Alumnos del jugador (con factor de atraccion aplicado). */
    fun alumnosJugador(): Int {
        val base = PropiedadRepository.propiedades.values
            .filter { it.comprada }
            .sumOf { it.baseAlumnos * it.nivel }
        return (base * factorAtraccion(cuotaJugador())).roundToInt()
    }

    /** Ingreso por ciclo del jugador sin factor de atraccion. */
    fun ingresoBaseJugador(): Long = PropiedadRepository.propiedades.values
        .filter { it.comprada }
        .sumOf { it.baseAlumnos * it.nivel * INGRESO_POR_ALUMNO }

    /** Ingreso por ciclo del jugador con factor de atraccion. */
    fun ingresoJugador(): Long =
        (ingresoBaseJugador() * factorAtraccion(cuotaJugador())).roundToLong()

    /** Ingreso por ciclo del rival con su factor (1 - cuotaJugador). */
    fun ingresoRival(): Long {
        val base = RivalState.edificios.entries.sumOf { (id, nivel) ->
            (PropiedadRepository.getPropiedad(id)?.baseAlumnos ?: 0) * nivel * INGRESO_POR_ALUMNO
        }
        return (base * factorAtraccion(1.0 - cuotaJugador())).roundToLong()
    }
}
