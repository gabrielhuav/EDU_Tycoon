package io.moviles.IPN_Tycoon

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.actors.onChange
import ktx.scene2d.*

class PauseMenuWindow(
    private val game: Main,
    private val onGoToMainMenu: () -> Unit
) : VisWindow("  MENÚ") {

    init {
        addCloseButton()
        closeOnEscape()
        isModal = true

        add(scene2d.table {
            defaults().expandX().fillX().pad(6f)

            val saveBtn = textButton("Guardar partida") {}
            saveBtn.onChange {
                if (GameState.slotActual == 0) {
                    SaveSelectionWindow(game) { ok ->
                        saveBtn.setText(if (ok) "Guardado correctamente" else "Error al guardar")
                    }.show(stage)
                    return@onChange
                }

                saveBtn.isDisabled = true
                saveBtn.setText("Guardando...")
                game.saveManager.guardar(GameState.slotActual) { ok ->
                    saveBtn.setText(if (ok) "Guardado correctamente" else "Error al guardar")
                    saveBtn.isDisabled = false
                }
            }
            row()

            textButton("Estadísticas") {
                onChange { StatsWindow().show(stage) }
            }
            row()

            val audioBtn = textButton(audioLabel()) {}
            audioBtn.onChange {
                GameState.musicaActiva = !GameState.musicaActiva
                audioBtn.setText(audioLabel())
            }
            row()

            textButton("Menu principal") {
                onChange {
                    this@PauseMenuWindow.remove()
                    onGoToMainMenu()
                }
            }
        }).pad(20f).minWidth(280f)

        pack()
        centerWindow()
    }

    private fun audioLabel() =
        if (GameState.musicaActiva) "Audio: ON" else "Audio: OFF"

    fun show(stage: Stage) { stage.addActor(this) }
}

// ── Ventana de estadísticas ───────────────────────────────────────────
class StatsWindow : VisWindow("Estadísticas") {

    init {
        addCloseButton()
        closeOnEscape()
        isModal = false

        val st = StatsEscuela.calcular()

        add(scene2d.table {

            label("Dinero:") { color = Color.GOLD }.cell(padRight = 10f, padBottom = 8f)
            label("\$${fmt(GameState.dinero)}").cell(padBottom = 8f)
            row()

            label("Edificios:") { color = Color.CYAN }.cell(padRight = 10f, padBottom = 8f)
            label("${st.edificios} comprados").cell(padBottom = 8f)
            row()

            label("Alumnos/ciclo:") { color = Color.GREEN }.cell(padRight = 10f, padBottom = 8f)
            label("${st.alumnos}").cell(padBottom = 8f)
            row()

            label("Ingresos/ciclo:") { color = Color.YELLOW }.cell(padRight = 10f, padBottom = 8f)
            label("\$${fmt(st.ingresosCiclo)}").cell(padBottom = 8f)
            row()

            label("Mercado:") { color = Color.ORANGE }.cell(padRight = 10f, padBottom = 8f)
            label("Tú ${st.cuotaJugadorPct}% | Rival ${st.cuotaRivalPct}%").cell(padBottom = 8f)
            row()

            label("Rival:") { color = Color.SALMON }.cell(padRight = 10f, padBottom = 8f)
            label("${st.edificiosRival} edif. | \$${fmt(st.dineroRival)}").cell(padBottom = 8f)
            row()

            label("Ciclos:") { color = Color.LIGHT_GRAY }.cell(padRight = 10f, padBottom = 8f)
            label("${GameState.ciclosJugados}").cell(padBottom = 8f)
            row()

            label("Jugador:") { color = Color.LIGHT_GRAY }.cell(padRight = 10f, padBottom = 8f)
            label(GameState.nombreJugador).cell(padBottom = 8f)
            row()

        }).pad(20f).minWidth(260f)

        pack()
        centerWindow()
    }

    fun show(stage: Stage) { stage.addActor(this) }

    private fun fmt(v: Long) = when {
        v >= 1_000_000L -> "${"%.1f".format(v / 1_000_000.0)}M"
        v >= 1_000L     -> "${"%.0f".format(v / 1_000.0)}K"
        else            -> v.toString()
    }
}
