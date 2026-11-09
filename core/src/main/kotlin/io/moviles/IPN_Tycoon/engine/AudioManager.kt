package io.moviles.IPN_Tycoon.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import io.moviles.IPN_Tycoon.GameState
import ktx.assets.toInternalFile

/**
 * Gestor centralizado del subsistema de audio para EduTycoon.
 * Controla la reproducción de música ambiental en bucle, efectos de sonido (dinero/recaudación),
 * el silenciamiento, el volumen maestro y la retroalimentación visual en la interfaz de usuario.
 */
object AudioManager {

    /** Volumen moderado para música de fondo ambiental agradable (no invasiva). */
    const val VOLUMEN_NORMAL: Float = 0.35f
    const val VOLUMEN_SILENCIADO: Float = 0.0f

    private var musicaFondo: Music? = null
    private var sonidoDinero: Sound? = null
    var pistaActual: String = "Route 1 Morning Breeze.mp3"
        private set

    /**
     * Volumen maestro actual aplicado al audio del juego (0.0f a 1.0f).
     */
    var volumenMaster: Float = VOLUMEN_NORMAL
        private set

    /**
     * Indica si el audio general (música y efectos) está habilitado.
     */
    var audioHabilitado: Boolean
        get() = GameState.musicaActiva
        set(valor) {
            GameState.musicaActiva = valor
            volumenMaster = if (valor) VOLUMEN_NORMAL else VOLUMEN_SILENCIADO
            aplicarVolumenAMusica()
        }

    init {
        volumenMaster = if (GameState.musicaActiva) VOLUMEN_NORMAL else VOLUMEN_SILENCIADO
    }

    /**
     * Inicia la reproducción de la música ambiental en bucle (loop) con volumen suave.
     * Si ya se está reproduciendo la misma pista, asegura que mantenga el volumen y estado correcto.
     */
    fun iniciarMusicaAmbiental(rutaArchivo: String = "Route 1 Morning Breeze.mp3") {
        pistaActual = rutaArchivo

        try {
            // Protección ante entornos de pruebas unitarias headless donde Gdx.audio o Gdx.files son nulos
            if (Gdx.audio == null || Gdx.files == null) return

            if (musicaFondo == null) {
                val archivo = rutaArchivo.toInternalFile()
                if (archivo.exists()) {
                    musicaFondo = Gdx.audio.newMusic(archivo).apply {
                        isLooping = true
                    }
                }
            }

            aplicarVolumenAMusica()

            musicaFondo?.let { music ->
                if (audioHabilitado && !music.isPlaying) {
                    music.play()
                }
            }
        } catch (e: Exception) {
            Gdx.app?.log("AudioManager", "No se pudo iniciar la música ambiental: ${e.message}")
        }
    }

    /**
     * Sincroniza el volumen y reproducción de la pista de fondo con el estado del audio.
     */
    private fun aplicarVolumenAMusica() {
        try {
            musicaFondo?.let { music ->
                if (audioHabilitado) {
                    music.volume = volumenMaster
                    if (!music.isPlaying) {
                        music.play()
                    }
                } else {
                    music.volume = VOLUMEN_SILENCIADO
                    music.pause()
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Alterna entre silenciar y activar el audio.
     * @return El nuevo estado del audio (true si quedó habilitado, false si quedó silenciado).
     */
    fun alternarAudio(): Boolean {
        audioHabilitado = !audioHabilitado
        return audioHabilitado
    }

    /**
     * Establece el estado del audio explícitamente.
     */
    fun setAudio(habilitado: Boolean) {
        audioHabilitado = habilitado
    }

    /**
     * Retorna la etiqueta de texto para el botón en la interfaz de usuario.
     */
    fun obtenerTextoBoton(): String =
        if (audioHabilitado) "Audio: ACTIVADO" else "Audio: SILENCIADO"

    /**
     * Retorna el color temático para retroalimentación visual en la interfaz.
     */
    fun obtenerColorBoton(): Color =
        if (audioHabilitado) Color.GREEN else Color.CORAL

    /**
     * Reproduce el efecto de sonido de recaudación de fondos / caja registradora ("coin.wav").
     * Respeta el estado de audio maestro y cuenta con protección ante entornos headless.
     */
    fun reproducirSonidoDinero() {
        try {
            if (!audioHabilitado || volumenMaster <= 0f) return
            if (Gdx.audio == null || Gdx.files == null) return

            if (sonidoDinero == null) {
                val archivo = "coin.wav".toInternalFile()
                if (archivo.exists()) {
                    sonidoDinero = Gdx.audio.newSound(archivo)
                }
            }
            sonidoDinero?.play(volumenMaster.coerceAtLeast(0.6f))
        } catch (e: Exception) {
            Gdx.app?.log("AudioManager", "No se pudo reproducir el sonido de recaudación: ${e.message}")
        }
    }

    /**
     * Libera los recursos de música y efectos al cerrar la aplicación.
     */
    fun dispose() {
        try {
            musicaFondo?.stop()
            musicaFondo?.dispose()
            musicaFondo = null
            sonidoDinero?.dispose()
            sonidoDinero = null
        } catch (_: Exception) {}
    }
}
