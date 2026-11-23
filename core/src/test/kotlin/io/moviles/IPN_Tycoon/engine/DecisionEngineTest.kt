package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class DecisionEngineTest {

    /** Random que siempre devuelve 0: el evento siempre se dispara y se elige el primero. */
    private val siempre = object : Random() {
        override fun nextBits(bitCount: Int) = 0
    }

    private val lanzados = mutableListOf<DecisionEvent>()

    private fun motor(edificios: Int = 1) =
        DecisionEngine(random = siempre, edificiosComprados = { edificios }) { lanzados += it }

    @Before
    fun preparar() = GameState.reset(presupuestoInicial = 500_000L)

    @After
    fun limpiar() = GameState.reset()

    @Test
    fun `sin edificios comprados no hay decisiones`() {
        val engine = motor(edificios = 0)
        engine.onResolveCycle(1)
        assertNull(engine.pendiente)
        assertTrue(lanzados.isEmpty())
    }

    @Test
    fun `no se lanza otra decision mientras haya una pendiente`() {
        val engine = motor()
        engine.onResolveCycle(1)
        engine.onResolveCycle(2)
        assertEquals(1, lanzados.size)
    }

    @Test
    fun `maximo tres decisiones por dia de juego`() {
        val engine = motor()
        for (ciclo in 1..DecisionEngine.CICLOS_POR_DIA) {
            engine.onResolveCycle(ciclo)
            engine.pendiente?.let { engine.elegir(it.opcionB) }
        }
        assertEquals(DecisionEngine.MAX_POR_DIA, lanzados.size)

        // Primer ciclo del día siguiente: se reinicia el conteo
        engine.onResolveCycle(DecisionEngine.CICLOS_POR_DIA + 1)
        assertEquals(DecisionEngine.MAX_POR_DIA + 1, lanzados.size)
    }

    @Test
    fun `elegir aplica dinero y reputacion`() {
        val engine = motor()
        engine.onResolveCycle(1)
        val evento = engine.pendiente
        assertNotNull(evento)

        val opcion = evento!!.opcionA   // Donación: +150K, reputación -10
        assertTrue(engine.elegir(opcion))
        assertEquals(500_000L + opcion.dinero, GameState.dinero)
        assertEquals(opcion.reputacion, GameState.reputacionBonus)
        assertNull(engine.pendiente)
    }

    @Test
    fun `no se puede elegir una opcion sin fondos`() {
        val engine = motor()
        val cara = OpcionDecision("Cara", dinero = -600_000L, reputacion = 5)
        assertFalse(engine.puedeElegir(cara))
        assertTrue(engine.puedeElegir(OpcionDecision("Gratis", 0L, 0)))
    }

    @Test
    fun `no se puede elegir una opcion ajena a la decision pendiente`() {
        val engine = motor()
        engine.onResolveCycle(1)
        assertFalse(engine.elegir(OpcionDecision("Otra", 1_000L, 0)))
        assertNotNull(engine.pendiente)
    }

    @Test
    fun `la reputacion total se mantiene entre 0 y 100`() {
        GameState.ajustarReputacion(-500)
        assertEquals(GameState.REPUTACION_BONUS_MIN, GameState.reputacionBonus)
        assertEquals(0, GameState.reputacionTotal(progresoEdificios = 30))

        GameState.reset()
        GameState.ajustarReputacion(15)
        assertEquals(45, GameState.reputacionTotal(progresoEdificios = 30))
        assertEquals(100, GameState.reputacionTotal(progresoEdificios = 95))
    }

    @Test
    fun `todo evento tiene una opcion que no cuesta dinero`() {
        DecisionEngine.CATALOGO.forEach { evento ->
            assertTrue("${evento.id}: la opción B no debe costar", evento.opcionB.dinero >= 0)
        }
    }
}
