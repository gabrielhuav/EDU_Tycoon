package io.moviles.IPN_Tycoon

/**
 * Guardado automático de la partida en su slot actual.
 *
 * Se guarda:
 *  - al terminar cada [cadaCiclos] ciclos de juego,
 *  - cuando la app pasa a segundo plano (pause) o se sale de la partida (hide).
 *
 * Solo guarda si la partida ya tiene slot (1, 2 o 3) y nunca lanza dos
 * guardados a la vez. No depende de libGDX, así que se puede probar con
 * pruebas unitarias usando un [GameSaveManager] falso.
 */
class AutoGuardado(
    private val saveManager: GameSaveManager,
    private val cadaCiclos: Int = 1,
    private val onResultado: (Boolean) -> Unit = {}
) {
    /** true mientras hay un guardado en curso. */
    var guardando: Boolean = false
        private set

    private var ciclosDesdeUltimo = 0

    fun puedeGuardar(): Boolean = GameState.slotActual != 0

    /** Llamar al final de cada ciclo de juego. */
    fun alTerminarCiclo() {
        ciclosDesdeUltimo++
        if (ciclosDesdeUltimo >= cadaCiclos) guardarAhora()
    }

    /**
     * Guarda inmediatamente en el slot actual.
     * @return false si no hay slot o ya hay un guardado en curso.
     */
    fun guardarAhora(): Boolean {
        if (!puedeGuardar() || guardando) return false
        guardando = true
        ciclosDesdeUltimo = 0
        saveManager.guardar(GameState.slotActual) { ok ->
            guardando = false
            onResultado(ok)
        }
        return true
    }
}
