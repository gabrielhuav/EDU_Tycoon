package io.moviles.IPN_Tycoon.rival

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class RivalEstadoTest {

    private lateinit var agent: RivalAgent

    @Before
    fun setUp() {
        GameState.reset()
        PropiedadRepository.resetProgress()
        RivalState.reset()
        agent = RivalAgent(Random(1))
    }

    @Test
    fun `low money goes to AHORRAR`() {
        RivalState.dinero = 100_000L
        assertEquals(RivalEstado.AHORRAR, agent.siguienteEstado())
    }

    @Test
    fun `enough money with free buildings goes to EXPANDIR`() {
        assertEquals(RivalEstado.EXPANDIR, agent.siguienteEstado())
    }

    @Test
    fun `player share above 0_60 with affordable upgrade goes to COMPETIR`() {
        PropiedadRepository.getPropiedad("escom_hitbox")!!.apply { comprada = true; nivel = 1 } // 600
        RivalState.edificios["Mac_and_cheese"] = 1 // 50 -> cuota 0.92
        RivalState.dinero = 300_000L
        assertEquals(RivalEstado.COMPETIR, agent.siguienteEstado())
    }

    @Test
    fun `AHORRAR has priority over COMPETIR`() {
        PropiedadRepository.getPropiedad("escom_hitbox")!!.apply { comprada = true; nivel = 1 }
        RivalState.edificios["Mac_and_cheese"] = 1
        RivalState.dinero = 100_000L
        assertEquals(RivalEstado.AHORRAR, agent.siguienteEstado())
    }

    @Test
    fun `nothing useful affordable goes to AHORRAR`() {
        PropiedadRepository.propiedades.values.forEach { it.comprada = true; it.nivel = 1 }
        RivalState.dinero = 1_000_000L
        assertEquals(RivalEstado.AHORRAR, agent.siguienteEstado())
    }

    @Test
    fun `tomarTurno records state change and syncs RivalState`() {
        RivalState.dinero = 100_000L
        val acciones = agent.tomarTurno()
        assertTrue(acciones.contains(RivalAccion.CambioEstado(RivalEstado.EXPANDIR, RivalEstado.AHORRAR)))
        assertEquals(RivalEstado.AHORRAR, RivalState.estado)
        assertEquals(RivalEstado.AHORRAR, agent.stateMachine.currentState)
    }

    @Test
    fun `onMessage returns false`() {
        assertEquals(false, RivalEstado.EXPANDIR.onMessage(agent, com.badlogic.gdx.ai.msg.Telegram()))
    }
}
