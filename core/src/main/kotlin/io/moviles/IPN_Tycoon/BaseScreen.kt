package io.moviles.IPN_Tycoon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen

abstract class BaseScreen(
    val game: Main,
    val viewport: Viewport = ExtendViewport(800f, 480f)
) : KtxScreen {

    val stage: Stage = Stage(viewport)

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun dispose() {
        stage.dispose()
    }
}
