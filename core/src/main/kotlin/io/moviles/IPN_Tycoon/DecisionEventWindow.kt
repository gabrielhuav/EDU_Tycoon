package io.moviles.IPN_Tycoon

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.widget.VisWindow
import io.moviles.IPN_Tycoon.engine.DecisionEvent
import io.moviles.IPN_Tycoon.engine.OpcionDecision
import ktx.actors.onChange
import ktx.scene2d.*

/**
 * Ventana modal de un evento de decisión. No tiene botón de cerrar:
 * el jugador debe elegir una de las dos opciones para continuar.
 */
class DecisionEventWindow(
    evento: DecisionEvent,
    puedeElegir: (OpcionDecision) -> Boolean,
    private val onElegir: (OpcionDecision) -> Unit
) : VisWindow(evento.titulo) {

    init {
        isModal   = true
        isMovable = false

        add(scene2d.table {
            label(evento.descripcion) {
                setWrap(true)
                color = Color.LIGHT_GRAY
            }.cell(width = 320f, padBottom = 14f)
            row()

            evento.opciones.forEach { opcion ->
                val alcanza = puedeElegir(opcion)
                textButton("${opcion.texto}\n${efectos(opcion)}") {
                    isDisabled = !alcanza
                    onChange {
                        if (!isDisabled) {
                            this@DecisionEventWindow.remove()
                            onElegir(opcion)
                        }
                    }
                }.cell(expandX = true, fillX = true, padBottom = 6f)
                row()

                if (!alcanza) {
                    label("Fondos insuficientes") { color = Color.RED }
                        .cell(padBottom = 6f)
                    row()
                }
            }
        }).pad(18f)

        pack()
        centerWindow()
    }

    fun show(stage: Stage) { stage.addActor(this) }

    companion object {
        /** Ej.: "+$150K  ·  Reputación -10". */
        fun efectos(opcion: OpcionDecision): String {
            val partes = mutableListOf<String>()
            if (opcion.dinero != 0L) {
                val signo = if (opcion.dinero > 0) "+" else "-"
                partes += "$signo\$${fmt(kotlin.math.abs(opcion.dinero))}"
            }
            if (opcion.reputacion != 0) {
                val signo = if (opcion.reputacion > 0) "+" else ""
                partes += "Reputación $signo${opcion.reputacion}"
            }
            return if (partes.isEmpty()) "Sin efecto" else partes.joinToString("  ·  ")
        }

        private fun fmt(v: Long) = when {
            v >= 1_000_000L -> "${"%.1f".format(v / 1_000_000.0)}M"
            v >= 1_000L     -> "${"%.0f".format(v / 1_000.0)}K"
            else            -> v.toString()
        }
    }
}
