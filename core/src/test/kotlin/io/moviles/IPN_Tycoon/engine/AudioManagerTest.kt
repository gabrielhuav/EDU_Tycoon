package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AudioManagerTest {

    @Before
    fun setUp() {
        GameState.musicaActiva = true
        AudioManager.setAudio(true)
    }

    @Test
    fun `audio inicializado por defecto como activo con volumen normal moderado`() {
        assertTrue(AudioManager.audioHabilitado)
        assertTrue(GameState.musicaActiva)
        assertEquals(AudioManager.VOLUMEN_NORMAL, AudioManager.volumenMaster, 0.001f)
        assertEquals("Audio: ACTIVADO", AudioManager.obtenerTextoBoton())
    }

    @Test
    fun `alternarAudio silencia el audio y fija volumen en cero`() {
        val nuevoEstado = AudioManager.alternarAudio()

        assertFalse(nuevoEstado)
        assertFalse(AudioManager.audioHabilitado)
        assertFalse(GameState.musicaActiva)
        assertEquals(0.0f, AudioManager.volumenMaster, 0.001f)
        assertEquals("Audio: SILENCIADO", AudioManager.obtenerTextoBoton())
    }

    @Test
    fun `alternarAudio reactiva el audio y restaura volumen normal`() {
        // Primer toggle: silencia
        AudioManager.alternarAudio()
        assertFalse(AudioManager.audioHabilitado)

        // Segundo toggle: reactiva
        val reactivado = AudioManager.alternarAudio()

        assertTrue(reactivado)
        assertTrue(AudioManager.audioHabilitado)
        assertTrue(GameState.musicaActiva)
        assertEquals(AudioManager.VOLUMEN_NORMAL, AudioManager.volumenMaster, 0.001f)
        assertEquals("Audio: ACTIVADO", AudioManager.obtenerTextoBoton())
    }

    @Test
    fun `setAudio asigna estado explicito correctamente`() {
        AudioManager.setAudio(false)
        assertFalse(AudioManager.audioHabilitado)
        assertEquals(0.0f, AudioManager.volumenMaster, 0.001f)

        AudioManager.setAudio(true)
        assertTrue(AudioManager.audioHabilitado)
        assertEquals(AudioManager.VOLUMEN_NORMAL, AudioManager.volumenMaster, 0.001f)
    }

    @Test
    fun `iniciarMusicaAmbiental maneja entornos headless de prueba sin lanzar excepciones`() {
        // En entorno JUnit headless (sin contexto gráfico libGDX), debe ser seguro
        AudioManager.iniciarMusicaAmbiental("Route 1 Morning Breeze.mp3")
        assertEquals("Route 1 Morning Breeze.mp3", AudioManager.pistaActual)
    }

    @Test
    fun `reproducirSonidoDinero maneja entornos headless sin fallos`() {
        // En headless mode, AudioManager no debe lanzar excepciones al intentar reproducir
        AudioManager.reproducirSonidoDinero()
        AudioManager.setAudio(false)
        AudioManager.reproducirSonidoDinero()
        assertFalse(AudioManager.audioHabilitado)
    }
}
