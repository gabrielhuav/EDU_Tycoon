package io.moviles.IPN_Tycoon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

/**
 * Opciones del jugador que se conservan entre partidas y entre sesiones
 * (Preferences de libGDX, no la base de datos de partidas).
 */
object Configuracion {

    private const val ARCHIVO = "edu_tycoon_config"
    private const val CLAVE_RIVAL = "rival_activo"

    private var rival = false

    /**
     * Instituto Rival controlado por IA. Apagado por defecto: sin él, el juego se
     * comporta como antes de la IA (sin cuota de mercado, sin factor de atracción
     * y sin fin de partida). Lo que el rival ya tenga se conserva en la partida y
     * vuelve a contar al encenderlo.
     */
    var rivalActivo: Boolean
        get() = rival
        set(valor) {
            rival = valor
            prefs()?.apply {
                putBoolean(CLAVE_RIVAL, valor)
                flush()
            }
        }

    /** Lee las opciones guardadas. Se llama una vez en Main.create(). */
    fun cargar() {
        prefs()?.let { rival = it.getBoolean(CLAVE_RIVAL, false) }
    }

    // Gdx.app es null en las pruebas unitarias: ahí las opciones solo viven en memoria.
    private fun prefs(): Preferences? = Gdx.app?.getPreferences(ARCHIVO)
}
