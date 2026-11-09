package io.moviles.IPN_Tycoon

object GameState {

    // ── Identidad de sesión ───────────────────────────────────────────
    var saveId: Int       = 0   // 0 = sin guardar aún
    var slotActual: Int   = 0   // 1, 2, 3 = slots reales
    var nombreJugador: String = ""
    var nombreEscuela: String = ""
    var ciclosJugados: Int    = 0
    var musicaActiva: Boolean = true

    // ── Economía ──────────────────────────────────────────────────────
    const val DINERO_INICIAL_REAL: Long = 500_000L
    const val DINERO_INICIAL_TEST: Long = 100_000_000L

    var dinero: Long = DINERO_INICIAL_REAL
    var alumnosTotales: Int = 0

    // ── Reputación por decisiones ─────────────────────────────────────
    // Puntos ganados o perdidos en los eventos de decisión. Se suman al
    // progreso de edificios para obtener la reputación que muestra el HUD.
    const val REPUTACION_BONUS_MIN = -100
    const val REPUTACION_BONUS_MAX = 100

    var reputacionBonus: Int = 0

    // ── API de dinero ─────────────────────────────────────────────────
    fun puedeComprar(costo: Long) = dinero >= costo

    fun gastar(cantidad: Long): Boolean {
        if (dinero < cantidad) return false
        dinero -= cantidad
        return true
    }

    fun acreditar(cantidad: Long) { dinero += cantidad }

    fun ajustarReputacion(puntos: Int) {
        reputacionBonus = (reputacionBonus + puntos)
            .coerceIn(REPUTACION_BONUS_MIN, REPUTACION_BONUS_MAX)
    }

    /** Reputación final (0–100 %): progreso de edificios + puntos de decisiones. */
    fun reputacionTotal(progresoEdificios: Int): Int =
        (progresoEdificios + reputacionBonus).coerceIn(0, 100)

    fun costoMejora(propiedad: Propiedad): Long =
        propiedad.precio * propiedad.nivel

    // ── Reset completo para nueva partida ─────────────────────────────
    fun reset(presupuestoInicial: Long = DINERO_INICIAL_REAL) {
        saveId         = 0
        slotActual     = 0
        nombreJugador  = ""
        nombreEscuela  = ""
        ciclosJugados  = 0
        dinero         = presupuestoInicial
        alumnosTotales = 0
        reputacionBonus = 0
        io.moviles.IPN_Tycoon.engine.BuildingIncomeManager.reiniciar()
    }
}
