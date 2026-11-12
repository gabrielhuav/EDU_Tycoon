package io.moviles.IPN_Tycoon

import io.moviles.IPN_Tycoon.engine.EconomyEngine
import io.moviles.IPN_Tycoon.rival.MercadoAlumnos
import io.moviles.IPN_Tycoon.rival.RivalState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StatsEscuelaTest {

    @Before
    fun setUp() {
        GameState.reset()
        PropiedadRepository.resetProgress()
        RivalState.reset()
    }

    private fun comprar(id: String, nivel: Int = 1) {
        PropiedadRepository.getPropiedad(id)!!.apply { comprada = true; this.nivel = nivel }
    }

    @Test
    fun `sin edificios la cuota es 50-50 y no hay alumnos ni ingreso`() {
        val s = StatsEscuela.calcular()
        assertEquals(50, s.cuotaJugadorPct)
        assertEquals(50, s.cuotaRivalPct)
        assertEquals(0, s.alumnos)
        assertEquals(0L, s.ingresosCiclo)
        assertEquals(0, s.edificios)
        assertEquals(0, s.edificiosRival)
    }

    @Test
    fun `solo jugador da cuota 100-0 y factor maximo`() {
        comprar("escom_hitbox")
        val s = StatsEscuela.calcular()
        assertEquals(100, s.cuotaJugadorPct)
        assertEquals(0, s.cuotaRivalPct)
        assertEquals(750, s.alumnos) // 600 * 1.25
        assertEquals(75_000L, s.ingresosCiclo) // 600 * 100 * 1.25
        assertEquals(1, s.edificios)
    }

    @Test
    fun `ambos con edificios las cuotas suman 100 y siguen la proporcion de poder`() {
        comprar("escom_hitbox") // poder 600
        RivalState.edificios["Direccion"] = 3 // poder 600 -> 50/50
        var s = StatsEscuela.calcular()
        assertEquals(50, s.cuotaJugadorPct)
        assertEquals(100, s.cuotaJugadorPct + s.cuotaRivalPct)
        assertEquals(1, s.edificiosRival)

        RivalState.edificios["Direccion"] = 1 // poder 200 -> 75/25
        s = StatsEscuela.calcular()
        assertEquals(75, s.cuotaJugadorPct)
        assertEquals(25, s.cuotaRivalPct)
        assertEquals(100, s.cuotaJugadorPct + s.cuotaRivalPct)
    }

    @Test
    fun `el ingreso coincide con EconomyEngine en el mismo estado`() {
        comprar("escom_hitbox", 2)
        RivalState.edificios["Direccion"] = 2
        val esperado = StatsEscuela.calcular().ingresosCiclo
        val antes = GameState.dinero
        EconomyEngine().onResolveCycle(1)
        assertEquals(esperado, GameState.dinero - antes)
        assertEquals(MercadoAlumnos.ingresoJugador(), esperado)
    }
}
