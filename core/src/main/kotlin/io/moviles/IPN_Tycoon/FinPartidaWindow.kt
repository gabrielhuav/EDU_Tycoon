package io.moviles.IPN_Tycoon

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.widget.VisWindow
import io.moviles.IPN_Tycoon.engine.Resultado
import ktx.actors.onChange
import ktx.scene2d.*

/** Dialogo de fin de partida contra el Instituto Rival. */
class FinPartidaWindow(
    resultado: Resultado,
    cuotaJugadorPct: Int,
    private val onGoToMainMenu: () -> Unit
) : VisWindow(if (resultado == Resultado.VICTORIA) "¡Victoria!" else "Derrota") {

    init {
        isModal = true

        val victoria = resultado == Resultado.VICTORIA
        val texto = if (victoria)
            "Tu escuela domina el mercado: Tú $cuotaJugadorPct% | Rival ${100 - cuotaJugadorPct}%.\nEl Instituto Rival no pudo competir."
        else
            "El Instituto Rival te superó: Tú $cuotaJugadorPct% | Rival ${100 - cuotaJugadorPct}%.\nPuedes seguir jugando para remontar."

        add(scene2d.table {
            defaults().expandX().fillX().pad(6f)
            label(texto) {
                setWrap(true)
                color = if (victoria) Color.GOLD else Color.SALMON
            }.cell(width = 300f)
            row()
            textButton("Continuar jugando") {
                onChange { this@FinPartidaWindow.remove() }
            }
            row()
            textButton("Menú") {
                onChange {
                    this@FinPartidaWindow.remove()
                    onGoToMainMenu()
                }
            }
        }).pad(20f).minWidth(300f)

        pack()
        centerWindow()
    }

    fun show(stage: Stage) { stage.addActor(this) }
}
