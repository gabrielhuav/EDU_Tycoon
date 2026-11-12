package io.moviles.IPN_Tycoon.rival

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram

/**
 * Estados de la IA del rival (gdx-ai). Prioridad de transicion:
 * AHORRAR > COMPETIR > EXPANDIR (ver [RivalAgent.siguienteEstado]).
 */
enum class RivalEstado : State<RivalAgent> {

    EXPANDIR {
        override fun actuar(agent: RivalAgent) { agent.comprarMejorEdificio() }
    },
    AHORRAR {
        override fun actuar(agent: RivalAgent) { /* no gasta */ }
    },
    COMPETIR {
        override fun actuar(agent: RivalAgent) { agent.mejorarMejorEdificio() }
    };

    /** Accion del estado durante el ciclo. */
    abstract fun actuar(agent: RivalAgent)

    override fun enter(entity: RivalAgent) {}

    override fun update(entity: RivalAgent) {
        val destino = entity.siguienteEstado()
        if (destino != this) {
            entity.registrarCambio(this, destino)
            entity.stateMachine.changeState(destino)
        }
        destino.actuar(entity)
    }

    override fun exit(entity: RivalAgent) {}

    override fun onMessage(entity: RivalAgent, telegram: Telegram): Boolean = false
}
