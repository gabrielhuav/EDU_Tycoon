package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository
import kotlin.random.Random

/**
 * Lanza eventos de decisión aleatorios (máximo [MAX_POR_DIA] por día de juego).
 *
 * Un "día de juego" equivale a [CICLOS_POR_DIA] ciclos (cada ciclo dura 30 s).
 * Mientras haya una decisión pendiente no se lanza otra; GameScreen además
 * pausa el ciclo hasta que el jugador elige.
 *
 * No depende de libGDX, así que se puede probar con pruebas unitarias.
 */
class DecisionEngine(
    private val random: Random = Random.Default,
    private val edificiosComprados: () -> Int = {
        PropiedadRepository.propiedades.values.count { it.comprada }
    },
    private val onDecision: (DecisionEvent) -> Unit
) : GameCycleListener {

    override val resolutionOrder = ResolutionOrder.EVENTS

    /** Decisión mostrada al jugador que todavía no se responde. */
    var pendiente: DecisionEvent? = null
        private set

    /** Decisiones lanzadas en el día de juego actual. */
    var decisionesHoy: Int = 0
        private set

    private var diaActual = -1

    override fun onResolveCycle(cycle: Int) {
        val dia = (cycle - 1) / CICLOS_POR_DIA
        if (dia != diaActual) {
            diaActual = dia
            decisionesHoy = 0
        }

        if (pendiente != null) return
        if (decisionesHoy >= MAX_POR_DIA) return

        val edificios = edificiosComprados()
        if (edificios == 0) return
        if (random.nextFloat() > PROBABILIDAD) return

        val disponibles = CATALOGO.filter { it.requiereEdificios <= edificios }
        if (disponibles.isEmpty()) return

        val evento = disponibles[random.nextInt(disponibles.size)]
        pendiente = evento
        decisionesHoy++
        onDecision(evento)
    }

    /** true si el presupuesto alcanza para esa opción (las que dan dinero siempre se pueden). */
    fun puedeElegir(opcion: OpcionDecision): Boolean =
        opcion.dinero >= 0 || GameState.puedeComprar(-opcion.dinero)

    /**
     * Aplica la opción elegida a la decisión pendiente.
     * @return false si no hay decisión pendiente, la opción no le pertenece o no alcanza el dinero.
     */
    fun elegir(opcion: OpcionDecision): Boolean {
        val evento = pendiente ?: return false
        if (opcion !in evento.opciones) return false
        if (!puedeElegir(opcion)) return false

        when {
            opcion.dinero > 0 -> GameState.acreditar(opcion.dinero)
            opcion.dinero < 0 -> GameState.gastar(-opcion.dinero)
        }
        GameState.ajustarReputacion(opcion.reputacion)
        pendiente = null
        return true
    }

    /** Descarta la decisión pendiente y reinicia el conteo (al entrar o salir de una partida). */
    fun reset() {
        pendiente = null
        decisionesHoy = 0
        diaActual = -1
    }

    companion object {
        const val MAX_POR_DIA = 3
        const val CICLOS_POR_DIA = 4
        const val PROBABILIDAD = 0.5f

        val CATALOGO = listOf(
            DecisionEvent(
                id          = "donacion_condicionada",
                titulo      = "Donación con condiciones",
                descripcion = "Un padre de familia ofrece una donación si su hijo entra sin presentar examen.",
                opcionA     = OpcionDecision("Aceptar la donación", dinero = 150_000L, reputacion = -10),
                opcionB     = OpcionDecision("Rechazarla", dinero = 0L, reputacion = 5)
            ),
            DecisionEvent(
                id          = "salones_sin_ventilacion",
                titulo      = "Salones sin ventilación",
                descripcion = "Los alumnos se quejan del calor: el sistema de ventilación dejó de funcionar.",
                opcionA     = OpcionDecision("Repararlo ya", dinero = -40_000L, reputacion = 3),
                opcionB     = OpcionDecision("Posponer la reparación", dinero = 0L, reputacion = -6)
            ),
            DecisionEvent(
                id          = "patrocinio_refresquera",
                titulo      = "Patrocinio de refresquera",
                descripcion = "Una marca de refrescos quiere instalar máquinas en todo el campus.",
                opcionA     = OpcionDecision("Aceptar el patrocinio", dinero = 100_000L, reputacion = -4),
                opcionB     = OpcionDecision("Rechazarlo", dinero = 0L, reputacion = 2)
            ),
            DecisionEvent(
                id          = "reunion_egresados",
                titulo      = "Reunión de egresados",
                descripcion = "Los egresados proponen una reunión anual en el campus.",
                opcionA     = OpcionDecision("Organizar el evento", dinero = -30_000L, reputacion = 5),
                opcionB     = OpcionDecision("No organizarlo", dinero = 0L, reputacion = -1)
            ),
            DecisionEvent(
                id                = "sede_hackaton",
                titulo            = "Sede de hackatón",
                descripcion       = "Te invitan a ser sede de un hackatón nacional de estudiantes.",
                opcionA           = OpcionDecision("Ser sede", dinero = -60_000L, reputacion = 8),
                opcionB           = OpcionDecision("Declinar", dinero = 0L, reputacion = 0),
                requiereEdificios = 2
            ),
            DecisionEvent(
                id                = "recorte_presupuesto",
                titulo            = "Recorte de presupuesto",
                descripcion       = "Hacienda pide recortar gastos este mes. Tú decides de dónde sale el dinero.",
                opcionA           = OpcionDecision("Recortar becas", dinero = 80_000L, reputacion = -8),
                opcionB           = OpcionDecision("Recortar eventos culturales", dinero = 30_000L, reputacion = -2),
                requiereEdificios = 2
            ),
            DecisionEvent(
                id                = "investigador_reconocido",
                titulo            = "Investigador reconocido",
                descripcion       = "Un investigador del SNI quiere unirse a tu plantilla si le ofreces un buen sueldo.",
                opcionA           = OpcionDecision("Contratarlo", dinero = -120_000L, reputacion = 10),
                opcionB           = OpcionDecision("No contratarlo", dinero = 0L, reputacion = 0),
                requiereEdificios = 3
            ),
            DecisionEvent(
                id                = "visita_sep",
                titulo            = "Visita de la SEP",
                descripcion       = "Un inspector sugiere que una \"aportación\" agilizaría el trámite de acreditación.",
                opcionA           = OpcionDecision("Pagar la aportación", dinero = -50_000L, reputacion = -12),
                opcionB           = OpcionDecision("Seguir el proceso formal", dinero = 0L, reputacion = 6),
                requiereEdificios = 3
            )
        )
    }
}
