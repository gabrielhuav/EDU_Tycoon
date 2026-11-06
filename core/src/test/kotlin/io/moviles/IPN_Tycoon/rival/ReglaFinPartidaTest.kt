package io.moviles.IPN_Tycoon.rival

import io.moviles.IPN_Tycoon.engine.Resultado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ReglaFinPartidaTest {

    @Before
    fun setUp() { RivalState.reset() }

    @Test
    fun `nothing happens before cycle 10`() {
        for (c in 1..9) assertNull(ReglaFinPartida.evaluar(c, 0.95))
        assertEquals(0, RivalState.ciclosVictoria)
    }

    @Test
    fun `victory after 3 consecutive cycles`() {
        assertNull(ReglaFinPartida.evaluar(10, 0.8))
        assertNull(ReglaFinPartida.evaluar(11, 0.8))
        assertEquals(Resultado.VICTORIA, ReglaFinPartida.evaluar(12, 0.7))
    }

    @Test
    fun `counter resets when condition breaks`() {
        ReglaFinPartida.evaluar(10, 0.8)
        ReglaFinPartida.evaluar(11, 0.8)
        assertNull(ReglaFinPartida.evaluar(12, 0.5))
        assertEquals(0, RivalState.ciclosVictoria)
        assertNull(ReglaFinPartida.evaluar(13, 0.8))
        assertNull(ReglaFinPartida.evaluar(14, 0.8))
        assertEquals(Resultado.VICTORIA, ReglaFinPartida.evaluar(15, 0.8))
    }

    @Test
    fun `defeat after 3 consecutive cycles`() {
        assertNull(ReglaFinPartida.evaluar(10, 0.2))
        assertNull(ReglaFinPartida.evaluar(11, 0.25))
        assertEquals(Resultado.DERROTA, ReglaFinPartida.evaluar(12, 0.1))
    }

    @Test
    fun `reported only once per game`() {
        repeat(2) { ReglaFinPartida.evaluar(10 + it, 0.9) }
        assertEquals(Resultado.VICTORIA, ReglaFinPartida.evaluar(12, 0.9))
        for (c in 13..20) assertNull(ReglaFinPartida.evaluar(c, 0.9))
    }
}
