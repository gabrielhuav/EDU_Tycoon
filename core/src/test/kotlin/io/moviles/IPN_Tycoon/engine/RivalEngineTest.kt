package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository
import io.moviles.IPN_Tycoon.rival.RivalAccion
import io.moviles.IPN_Tycoon.rival.RivalAgent
import io.moviles.IPN_Tycoon.rival.RivalState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class RivalEngineTest {

    @Before
    fun setUp() {
        GameState.reset()
        PropiedadRepository.resetProgress()
        RivalState.reset()
    }

    @Test
    fun `RIVAL resolves right after STUDENTS`() {
        assertEquals(ResolutionOrder.STUDENTS.ordinal + 1, ResolutionOrder.RIVAL.ordinal)
    }

    @Test
    fun `engine forwards actions and rival acts on cycle`() {
        val acciones = mutableListOf<RivalAccion>()
        val engine = GameCycleEngine()
        engine.addListener(RivalEngine(RivalAgent(Random(1)), { acciones.add(it) }))
        repeat(RivalAgent.CICLOS_GRACIA + 1) { engine.advanceCycle() }
        assertTrue(acciones.any { it is RivalAccion.Compra })
        assertTrue(RivalState.edificios.isNotEmpty())
    }

    @Test
    fun `rival takes no actions during grace period`() {
        val acciones = mutableListOf<RivalAccion>()
        val engine = GameCycleEngine()
        engine.addListener(RivalEngine(RivalAgent(Random(1)), { acciones.add(it) }))
        repeat(RivalAgent.CICLOS_GRACIA) { engine.advanceCycle() }
        assertTrue(acciones.isEmpty())
        assertTrue(RivalState.edificios.isEmpty())
    }

    @Test
    fun `economy applies attraction factor to player income`() {
        PropiedadRepository.getPropiedad("escom_hitbox")!!.apply { comprada = true; nivel = 1 }
        val antes = GameState.dinero
        EconomyEngine().onResolveCycle(1)
        // rival vacio -> cuota 1.0 -> factor 1.25 : 600*100*1.25
        assertEquals(antes + 75_000L, GameState.dinero)
        EstudiantesEngine().onResolveCycle(1)
        assertEquals(750, GameState.alumnosTotales)
    }
}
