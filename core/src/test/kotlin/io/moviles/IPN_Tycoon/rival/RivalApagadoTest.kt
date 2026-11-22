package io.moviles.IPN_Tycoon.rival

import io.moviles.IPN_Tycoon.Configuracion
import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository
import io.moviles.IPN_Tycoon.engine.RivalEngine
import io.moviles.IPN_Tycoon.engine.Resultado
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Con el Instituto Rival apagado en la configuración el juego vuelve a ser el de antes de la IA. */
class RivalApagadoTest {

    @Before
    fun setUp() {
        GameState.reset()
        PropiedadRepository.resetProgress()
        RivalState.reset()
        Configuracion.rivalActivo = false
    }

    @After
    fun tearDown() {
        Configuracion.rivalActivo = false
    }

    private fun comprar(id: String, nivel: Int = 1) {
        val p = PropiedadRepository.getPropiedad(id)!!
        p.comprada = true
        p.nivel = nivel
    }

    @Test
    fun `apagado, el ingreso no lleva factor de mercado`() {
        comprar("escom_hitbox")
        assertEquals(MercadoAlumnos.ingresoBaseJugador(), MercadoAlumnos.ingresoJugador())
        val p = PropiedadRepository.getPropiedad("escom_hitbox")!!
        assertEquals(p.baseAlumnos * p.nivel, MercadoAlumnos.alumnosJugador())
        assertEquals(
            p.baseAlumnos.toLong() * p.nivel * MercadoAlumnos.INGRESO_POR_ALUMNO,
            MercadoAlumnos.ingresoEdificio(p.baseAlumnos, p.nivel)
        )
    }

    @Test
    fun `apagado, el motor del rival no cobra, no juega ni termina la partida`() {
        comprar("escom_hitbox", nivel = 3)
        RivalState.edificios["Mac_and_cheese"] = 1
        val dinero = RivalState.dinero
        val acciones = mutableListOf<RivalAccion>()
        val finales = mutableListOf<Resultado>()
        val engine = RivalEngine(onAccion = { acciones += it }, onFinPartida = { finales += it })

        repeat(40) { engine.onResolveCycle(it + 1) }

        assertEquals(dinero, RivalState.dinero)
        assertTrue(acciones.isEmpty())
        assertTrue(finales.isEmpty())
        assertEquals(mapOf("Mac_and_cheese" to 1), RivalState.edificios.toMap())
    }

    @Test
    fun `apagado, sus edificios no cuentan pero se conservan al encenderlo`() {
        RivalState.edificios["Mac_and_cheese"] = 2
        assertFalse(RivalState.posee("Mac_and_cheese"))
        assertEquals(0, RivalState.nivel("Mac_and_cheese"))
        assertEquals(0L, MercadoAlumnos.poderRival())

        Configuracion.rivalActivo = true
        assertTrue(RivalState.posee("Mac_and_cheese"))
        assertEquals(2, RivalState.nivel("Mac_and_cheese"))
        assertTrue(MercadoAlumnos.poderRival() > 0L)
    }
}
