package io.moviles.IPN_Tycoon.rival

import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import io.moviles.IPN_Tycoon.Propiedad
import io.moviles.IPN_Tycoon.PropiedadRepository
import kotlin.random.Random

/**
 * Agente del Instituto Rival. Su maquina de estados (gdx-ai) decide una accion por ciclo.
 * Opera sobre [RivalState] y sobre [PropiedadRepository] (edificios del jugador).
 */
class RivalAgent(private val random: Random = Random.Default) {

    companion object {
        const val RESERVA_MINIMA = 150_000L
        const val UMBRAL_COMPETIR = 0.60
        const val NIVEL_INICIAL = 1
        /** Ciclos iniciales en los que el rival no compra ni mejora. */
        const val CICLOS_GRACIA = 3
        /** Edificios que el rival nunca compra (la escuela inicial del jugador). */
        val EDIFICIOS_PROTEGIDOS: Set<String> = setOf("escom_hitbox")
    }

    val stateMachine = DefaultStateMachine<RivalAgent, RivalEstado>(this, RivalState.estado)

    private val acciones = mutableListOf<RivalAccion>()

    /** Ejecuta un turno (un update de la FSM) y devuelve las acciones de este ciclo. */
    fun tomarTurno(ciclo: Int = Int.MAX_VALUE): List<RivalAccion> {
        acciones.clear()
        // La partida pudo cargarse despues de crear el agente: sincroniza con RivalState.
        if (stateMachine.currentState != RivalState.estado) stateMachine.setInitialState(RivalState.estado)
        if (ciclo <= CICLOS_GRACIA) return emptyList()
        stateMachine.update()
        RivalState.estado = stateMachine.currentState
        return acciones.toList()
    }

    internal fun registrarCambio(de: RivalEstado, a: RivalEstado) {
        acciones.add(RivalAccion.CambioEstado(de, a))
    }

    // ── Consultas ─────────────────────────────────────────────────────

    fun edificiosLibres(): List<Propiedad> = PropiedadRepository.propiedades.values
        .filter { !it.comprada && !RivalState.posee(it.id) && it.id !in EDIFICIOS_PROTEGIDOS }

    private fun libresAsequibles() = edificiosLibres().filter { it.precio <= RivalState.dinero }

    private fun costoMejora(p: Propiedad, nivel: Int): Long = p.precio * nivel

    /** Edificios propios mejorables y asequibles: (propiedad, nivel actual). */
    private fun mejorasAsequibles(): List<Pair<Propiedad, Int>> =
        RivalState.edificios.mapNotNull { (id, nivel) ->
            val p = PropiedadRepository.getPropiedad(id) ?: return@mapNotNull null
            if (nivel < p.mejoraMax && costoMejora(p, nivel) <= RivalState.dinero) p to nivel else null
        }

    private fun factorRival(): Double =
        MercadoAlumnos.factorAtraccion(1.0 - MercadoAlumnos.cuotaJugador())

    /** Decide a que estado debe pasar segun prioridad AHORRAR > COMPETIR > EXPANDIR. */
    fun siguienteEstado(): RivalEstado {
        val compras = libresAsequibles()
        val mejoras = mejorasAsequibles()
        if (RivalState.dinero < RESERVA_MINIMA || (compras.isEmpty() && mejoras.isEmpty())) {
            return RivalEstado.AHORRAR
        }
        if (MercadoAlumnos.cuotaJugador() > UMBRAL_COMPETIR && mejoras.isNotEmpty()) {
            return RivalEstado.COMPETIR
        }
        if (compras.isNotEmpty()) return RivalEstado.EXPANDIR
        // Solo hay mejoras asequibles: competir aunque la cuota sea baja.
        return RivalEstado.COMPETIR
    }

    // ── Acciones ──────────────────────────────────────────────────────

    internal fun comprarMejorEdificio() {
        val factor = factorRival()
        val elegido = libresAsequibles().maxByOrNull {
            it.baseAlumnos * factor / it.precio * (1.0 + random.nextDouble() * 0.02)
        } ?: return
        if (elegido.precio > RivalState.dinero) return
        RivalState.dinero -= elegido.precio
        RivalState.edificios[elegido.id] = NIVEL_INICIAL
        acciones.add(RivalAccion.Compra(elegido.id, elegido.nombre, elegido.precio))
    }

    internal fun mejorarMejorEdificio() {
        val (prop, nivel) = mejorasAsequibles().maxByOrNull { (p, n) ->
            p.baseAlumnos.toDouble() / costoMejora(p, n) * (1.0 + random.nextDouble() * 0.02)
        } ?: return
        val costo = costoMejora(prop, nivel)
        if (costo > RivalState.dinero) return
        RivalState.dinero -= costo
        RivalState.edificios[prop.id] = nivel + 1
        acciones.add(RivalAccion.Mejora(prop.id, prop.nombre, nivel + 1, costo))
    }
}
