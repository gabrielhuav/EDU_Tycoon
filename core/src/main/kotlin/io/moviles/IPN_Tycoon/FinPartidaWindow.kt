package io.moviles.IPN_Tycoon

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.widget.VisWindow
import io.moviles.IPN_Tycoon.engine.Resultado
import ktx.actors.onChange
import ktx.assets.toInternalFile
import ktx.scene2d.*

/** Dialogo de fin de partida contra el Instituto Rival. */
class FinPartidaWindow(
    resultado: Resultado,
    cuotaJugadorPct: Int,
    private val onGoToMainMenu: () -> Unit
) : VisWindow(if (resultado == Resultado.VICTORIA) "¡Victoria!" else "Derrota") {

    private var titleFont: BitmapFont? = null

    init {
        isModal = true
        aplicarFuenteDelTitulo()

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

    /**
     * La fuente bitmap de VisUI no incluye U+00A1, asi que el titulo pierde el
     * signo de apertura. Se genera una fuente desde assets/font.ttf solo para
     * este titulo y se libera al cerrar la ventana.
     */
    private fun aplicarFuenteDelTitulo() {
        val fontFile = "font.ttf".toInternalFile()
        if (!fontFile.exists()) return

        val generator = FreeTypeFontGenerator(fontFile)
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = 15 // mismo tamano que el titulo por defecto de VisUI (skin x1)
            color = Color.WHITE
            minFilter = TextureFilter.Nearest
            magFilter = TextureFilter.Nearest
            characters = FreeTypeFontGenerator.DEFAULT_CHARS + "¡"
        }
        val font = generator.generateFont(parameter)
        generator.dispose()

        titleFont = font
        getTitleLabel().setStyle(Label.LabelStyle(font, Color.WHITE))
    }

    override fun remove(): Boolean {
        titleFont?.dispose()
        titleFont = null
        return super.remove()
    }
}
