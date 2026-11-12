package io.moviles.IPN_Tycoon.rival

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class RivalAgentTest {

    @Before
    fun setUp() {
        GameState.reset()
        PropiedadRepository.resetProgress()
        RivalState.reset()
    }

    private fun simular(seed: Int, ciclos: Int, alCiclo: (List<RivalAccion>) -> Unit = {}): List<RivalAccion> {
        val agent = RivalAgent(Random(seed))
        val todas = mutableListOf<RivalAccion>()
        repeat(ciclos) {
            RivalState.dinero += MercadoAlumnos.ingresoRival()
            val acc = agent.tomarTurno()
            todas.addAll(acc)
            alCiclo(acc)
        }
        return todas
    }

    @Test
    fun `money never negative over 50 cycles`() {
        simular(42, 50) { assertTrue(RivalState.dinero >= 0) }
        assertTrue(RivalState.dinero >= 0)
    }

    @Test
    fun `never buys player-owned buildings`() {
        PropiedadRepository.getPropiedad("escom_hitbox")!!.apply { comprada = true; nivel = 1 }
        PropiedadRepository.getPropiedad("cafeteria")!!.apply { comprada = true; nivel = 1 }
        simular(7, 50)
        assertFalse(RivalState.posee("escom_hitbox"))
        assertFalse(RivalState.posee("cafeteria"))
    }

    @Test
    fun `never buys the protected starting school even with lots of money`() {
        RivalState.dinero = 1_000_000_000L
        simular(3, 60)
        assertFalse(RivalState.posee("escom_hitbox"))
        assertTrue("escom_hitbox" in RivalAgent.EDIFICIOS_PROTEGIDOS)
    }

    @Test
    fun `grace period blocks actions`() {
        val agent = RivalAgent(Random(1))
        for (c in 1..RivalAgent.CICLOS_GRACIA) assertTrue(agent.tomarTurno(c).isEmpty())
        assertTrue(RivalState.edificios.isEmpty())
        assertTrue(agent.tomarTurno(RivalAgent.CICLOS_GRACIA + 1).any { it is RivalAccion.Compra })
    }

    @Test
    fun `respects mejoraMax`() {
        RivalState.dinero = 500_000_000L
        simular(3, 60)
        for ((id, nivel) in RivalState.edificios) {
            assertTrue(nivel in 1..PropiedadRepository.getPropiedad(id)!!.mejoraMax)
        }
    }

    @Test
    fun `at most one purchase or upgrade per cycle and buys at level 1`() {
        val agent = RivalAgent(Random(5))
        repeat(30) {
            RivalState.dinero += 400_000L
            val acc = agent.tomarTurno()
            assertTrue(acc.count { it !is RivalAccion.CambioEstado } <= 1)
            acc.filterIsInstance<RivalAccion.Compra>().forEach {
                assertEquals(1, RivalState.edificios[it.propId])
            }
        }
    }

    @Test
    fun `first turn with initial money buys a building`() {
        val acc = RivalAgent(Random(1)).tomarTurno()
        assertTrue(acc.any { it is RivalAccion.Compra })
    }

    @Test
    fun `same seed gives same result`() {
        val a = simular(99, 50)
        val estadoA = RivalState.serializar()
        setUp()
        val b = simular(99, 50)
        assertEquals(a, b)
        assertEquals(estadoA, RivalState.serializar())
    }
}
