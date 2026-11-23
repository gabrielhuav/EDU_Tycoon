package io.moviles.IPN_Tycoon.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CycleTimeControllerTest {

    @Test
    fun `inicia en velocidad normal 1x con progreso cero`() {
        val controller = CycleTimeController(duracionCicloSegundos = 30f)

        assertEquals(VelocidadCiclo.NORMAL, controller.velocidadActual)
        assertFalse(controller.estaPausado)
        assertEquals(0f, controller.progreso(), 0.001f)
        assertEquals(0, controller.porcentajeProgreso())
        assertEquals(30, controller.segundosRestantes())
    }

    @Test
    fun `alternarVelocidad recorre secuencia 1x a 2x a PAUSA y regresa a 1x`() {
        val controller = CycleTimeController()

        assertEquals(VelocidadCiclo.RAPIDO, controller.alternarVelocidad())
        assertFalse(controller.estaPausado)

        assertEquals(VelocidadCiclo.PAUSA, controller.alternarVelocidad())
        assertTrue(controller.estaPausado)

        assertEquals(VelocidadCiclo.NORMAL, controller.alternarVelocidad())
        assertFalse(controller.estaPausado)
    }

    @Test
    fun `avanzar en velocidad 1x incrementa progreso proporcionalmente`() {
        val controller = CycleTimeController(duracionCicloSegundos = 30f)

        val ciclos = controller.avanzar(15f)

        assertEquals(0, ciclos)
        assertEquals(0.5f, controller.progreso(), 0.001f)
        assertEquals(50, controller.porcentajeProgreso())
        assertEquals(15, controller.segundosRestantes())
    }

    @Test
    fun `avanzar en velocidad 2x duplica el avance y completa el ciclo en la mitad del tiempo`() {
        val controller = CycleTimeController(duracionCicloSegundos = 30f)
        controller.cambiarVelocidad(VelocidadCiclo.RAPIDO)

        val ciclosMitad = controller.avanzar(7.5f)
        assertEquals(0, ciclosMitad)
        assertEquals(50, controller.porcentajeProgreso())

        val ciclosFin = controller.avanzar(7.5f)
        assertEquals(1, ciclosFin)
        assertEquals(0, controller.porcentajeProgreso())
        assertEquals(30, controller.segundosRestantes())
    }

    @Test
    fun `avanzar en estado PAUSA o bloqueado no modifica el tiempo transcurrido`() {
        val controller = CycleTimeController(duracionCicloSegundos = 30f)
        controller.avanzar(10f)

        controller.cambiarVelocidad(VelocidadCiclo.PAUSA)
        val ciclosEnPausa = controller.avanzar(25f)
        assertEquals(0, ciclosEnPausa)
        assertEquals(10f, controller.tiempoTranscurrido, 0.001f)

        controller.cambiarVelocidad(VelocidadCiclo.NORMAL)
        val ciclosBloqueado = controller.avanzar(25f, bloqueado = true)
        assertEquals(0, ciclosBloqueado)
        assertEquals(10f, controller.tiempoTranscurrido, 0.001f)
    }

    @Test
    fun `reiniciar restablece el tiempo en cero y velocidad normal`() {
        val controller = CycleTimeController(duracionCicloSegundos = 30f)
        controller.cambiarVelocidad(VelocidadCiclo.RAPIDO)
        controller.avanzar(10f)

        controller.reiniciar()

        assertEquals(VelocidadCiclo.NORMAL, controller.velocidadActual)
        assertEquals(0f, controller.tiempoTranscurrido, 0.001f)
        assertEquals(0, controller.porcentajeProgreso())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `duracion de ciclo menor o igual a cero lanza excepcion`() {
        CycleTimeController(duracionCicloSegundos = 0f)
    }
}
