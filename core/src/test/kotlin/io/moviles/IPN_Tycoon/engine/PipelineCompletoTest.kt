package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository
import io.moviles.IPN_Tycoon.rival.MercadoAlumnos
import io.moviles.IPN_Tycoon.rival.RivalAccion
import io.moviles.IPN_Tycoon.rival.RivalAgent
import io.moviles.IPN_Tycoon.rival.RivalState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pipeline completo (Economy -> Students -> Rival) dentro de un mismo
 * [GameCycleEngine]. A diferencia de los tests de cada motor por separado,
 * aqui se comprueba que los tres se afectan como en una partida real.
 */
class PipelineCompletoTest {

    @Before
    fun resetEstado() {
        GameState.reset()
        RivalState.reset()
        PropiedadRepository.propiedades.values.forEach {
            it.comprada = false
            it.nivel = 0
        }
    }

    @Test
    fun `economy students and rival resolve in the same cycle`() {
        val cafeteria = PropiedadRepository.getPropiedad("cafeteria")!!
        cafeteria.comprada = true
        cafeteria.nivel = 1

        val engine = GameCycleEngine()
        // Se registran en desorden a proposito: el motor debe ordenarlos.
        engine.addListener(
            RivalEngine(agent = RivalAgent(), cicloActual = { GameState.ciclosJugados })
        )
        engine.addListener(EstudiantesEngine())
        engine.addListener(EconomyEngine())

        val dineroAntes = GameState.dinero
        val ingresoEsperado = MercadoAlumnos.ingresoJugador()
        val alumnosEsperados = MercadoAlumnos.alumnosJugador()

        engine.advanceCycle()

        assertEquals(dineroAntes + ingresoEsperado, GameState.dinero)
        assertEquals(alumnosEsperados, GameState.alumnosTotales)
        assertEquals(1, GameState.ciclosJugados)
    }

    @Test
    fun `rival respects the grace period and acts afterwards`() {
        val acciones = mutableListOf<RivalAccion>()
        val engine = GameCycleEngine()
        engine.addListener(EconomyEngine())
        engine.addListener(EstudiantesEngine())
        engine.addListener(
            RivalEngine(
                agent = RivalAgent(),
                onAccion = { acciones += it },
                cicloActual = { GameState.ciclosJugados }
            )
        )

        repeat(RivalAgent.CICLOS_GRACIA) { engine.advanceCycle() }
        assertTrue("el rival no debe actuar durante el periodo de gracia", acciones.isEmpty())
        assertTrue(RivalState.edificios.isEmpty())

        engine.advanceCycle()
        assertTrue("el rival debe actuar despues de la gracia", acciones.isNotEmpty())
        assertTrue(RivalState.edificios.isNotEmpty())
    }
}
