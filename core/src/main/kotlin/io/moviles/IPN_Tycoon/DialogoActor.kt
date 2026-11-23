package io.moviles.IPN_Tycoon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import ktx.scene2d.Scene2DSkin

class DialogoActor(
    private val font: BitmapFont,
    private val textureManager: (String) -> TextureRegion
) : Group() {

    private val labelTexto: Label
    private val labelNombre: Label
    private val labelPromptInput: Label // Etiqueta superior para guiar el input
    private val imagenPersonaje = Image()
    private val fondoBordeGuinda: Image
    private val fondoBlancoInterno: Image
    private val inputNombre: TextField
    private val fondoInput: Image
    private val overlayOscuro: Image // Para oscurecer el fondo al escribir

    var alTerminarNombre: ((String) -> Unit)? = null
    var alTerminarEscuela: ((String) -> Unit)? = null
    val variables = mutableMapOf<String, String>()

    private var listaDialogos = listOf<Dialogo>()
    private var indiceActual = 0
    private var contadorInputs = 0
    private var textoCompleto = ""
    private var caracteresVisibles = 0
    private var tiempoAcumulado = 0f
    private val velocidadTexto = 0.03f

    private val whiteRegion: TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        TextureRegion(texture)
    }

    init {
        val guindaIPN = Color.valueOf("660000")
        val blancoPuro = Color.WHITE
        val negroPuro = Color.BLACK

        // --- OVERLAY ---
        overlayOscuro = Image(TextureRegionDrawable(whiteRegion)).apply {
            color = Color(0f, 0f, 0f, 0.6f)
            isVisible = false
            setFillParent(true)
        }

        // --- STYLES ---
        val estiloTexto = Label.LabelStyle(font, negroPuro)
        labelTexto = Label("", estiloTexto).apply {
            wrap = true
            setAlignment(Align.topLeft)
            setFontScale(1.2f)
        }

        val estiloNombre = Label.LabelStyle(font, negroPuro)
        labelNombre = Label("", estiloNombre).apply {
            setAlignment(Align.left)
            setFontScale(1.3f)
        }

        labelPromptInput = Label("", Label.LabelStyle(font, Color.WHITE)).apply {
            setAlignment(Align.center)
            setFontScale(1.5f)
            isVisible = false
        }

        // --- BACKGROUNDS ---
        fondoBordeGuinda = Image(TextureRegionDrawable(whiteRegion)).apply { color = guindaIPN }
        fondoBlancoInterno = Image(TextureRegionDrawable(whiteRegion)).apply { color = blancoPuro }
        fondoInput = Image(TextureRegionDrawable(whiteRegion)).apply {
            color = Color.valueOf("F5F5F5")
            isVisible = false
        }

        // --- LAYOUT ---
        fondoBordeGuinda.setSize(760f, 160f)
        fondoBordeGuinda.setPosition(20f, 20f)
        fondoBlancoInterno.setSize(752f, 152f)
        fondoBlancoInterno.setPosition(24f, 24f)
        labelNombre.setPosition(50f, 135f)
        labelTexto.setSize(700f, 90f)
        labelTexto.setPosition(50f, 40f)

        inputNombre = TextField("", Scene2DSkin.defaultSkin).apply {
            isVisible = false
            setSize(500f, 80f)
            setAlignment(Align.center)
            messageText = "Escribe aquí..."

            val style = TextField.TextFieldStyle(this.style)
            style.font = font
            style.fontColor = blancoPuro
            style.focusedFontColor = blancoPuro
            style.messageFontColor = Color.LIGHT_GRAY
            style.cursor = Scene2DSkin.defaultSkin.newDrawable("white", Color.GOLD).apply { minWidth = 3f }
            this.style = style

            addListener(object : com.badlogic.gdx.scenes.scene2d.InputListener() {
                override fun keyDown(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, keycode: Int): Boolean {
                    if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
                        avanzar()
                        return true
                    }
                    return false
                }
            })
        }

        imagenPersonaje.setSize(280f, 280f)
        imagenPersonaje.setPosition(500f, 170f)

        // --- HIERARCHY ---
        addActor(overlayOscuro)
        addActor(imagenPersonaje)
        addActor(fondoBordeGuinda)
        addActor(fondoBlancoInterno)
        addActor(labelNombre)
        addActor(labelTexto)
        addActor(fondoInput)
        addActor(inputNombre)
        addActor(labelPromptInput)

        this.isVisible = false

        // --- INTERACTION ---
        addListener(object : com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                if (isVisible) {
                    avanzar()
                    event?.handle()
                }
            }
        })
    }

    override fun sizeChanged() {
        super.sizeChanged()
        actualizarPosicionesLayout()
    }

    private fun actualizarPosicionesLayout() {
        val currentW = if (width > 0f) width else (stage?.width ?: 800f)
        val currentH = if (height > 0f) height else (stage?.height ?: 480f)
        val offsetX = ((currentW - 800f) / 2f).coerceAtLeast(0f)

        overlayOscuro.setBounds(0f, 0f, currentW, currentH)
        fondoBordeGuinda.setPosition(offsetX + 20f, 20f)
        fondoBlancoInterno.setPosition(offsetX + 24f, 24f)
        labelNombre.setPosition(offsetX + 50f, 135f)
        labelTexto.setPosition(offsetX + 50f, 40f)
        imagenPersonaje.setPosition(offsetX + 500f, 170f)

        if (inputNombre.isVisible) {
            val centroX = currentW / 2f
            val posYInput = 320f
            labelPromptInput.pack()
            labelPromptInput.setPosition(centroX - labelPromptInput.width / 2f, posYInput + 100f)
            inputNombre.setPosition(centroX - inputNombre.width / 2f, posYInput)
            fondoInput.setBounds(
                inputNombre.x - 10f,
                inputNombre.y - 10f,
                inputNombre.width + 20f,
                inputNombre.height + 20f
            )
        }
    }

    fun mostrarConversacion(dialogos: List<Dialogo>) {
        this.listaDialogos = dialogos
        this.indiceActual = 0
        this.contadorInputs = 0
        this.isVisible = true
        actualizarPosicionesLayout()
        actualizarContenido()
    }

    private fun actualizarContenido() {
        if (indiceActual >= listaDialogos.size) return
        val actual = listaDialogos[indiceActual]

        actual.alMostrar?.invoke()

        try {
            val region = textureManager(actual.spritePath)
            imagenPersonaje.drawable = TextureRegionDrawable(region)
        } catch (e: Exception) {}

        labelNombre.setText(actual.nombre)

        var textoFinal = actual.texto
        variables.forEach { (clave, valor) -> textoFinal = textoFinal.replace("{$clave}", valor) }

        textoCompleto = textoFinal
        caracteresVisibles = 0
        tiempoAcumulado = 0f
        labelTexto.setText("")

        if (actual.tipo == TipoDialogo.INPUT) {
            prepararInput(textoFinal)
        } else {
            ocultarInput()
        }
    }

    private fun prepararInput(pregunta: String) {
        overlayOscuro.isVisible = true
        inputNombre.isVisible = true
        fondoInput.isVisible = true
        labelPromptInput.isVisible = true
        inputNombre.text = ""

        // Texto guía basado en lo que se pregunta
        labelPromptInput.setText(pregunta)
        actualizarPosicionesLayout()

        stage?.keyboardFocus = inputNombre
        Gdx.input.setOnscreenKeyboardVisible(true)
    }

    private fun ocultarInput() {
        overlayOscuro.isVisible = false
        inputNombre.isVisible = false
        fondoInput.isVisible = false
        labelPromptInput.isVisible = false
        // Ocultar teclado físicamente en Android
        Gdx.input.setOnscreenKeyboardVisible(false)
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (!isVisible) return

        if (caracteresVisibles < textoCompleto.length) {
            tiempoAcumulado += delta
            if (tiempoAcumulado >= velocidadTexto) {
                caracteresVisibles++
                labelTexto.setText(textoCompleto.substring(0, caracteresVisibles))
                tiempoAcumulado = 0f
            }
        }
    }

    fun avanzar() {
        if (caracteresVisibles < textoCompleto.length) {
            caracteresVisibles = textoCompleto.length
            labelTexto.setText(textoCompleto)
        } else {
            val actual = listaDialogos.getOrNull(indiceActual)
            if (actual?.tipo == TipoDialogo.INPUT) {
                if (inputNombre.text.isNotBlank()) {
                    if (contadorInputs == 0) {
                        alTerminarNombre?.invoke(inputNombre.text)
                    } else {
                        alTerminarEscuela?.invoke(inputNombre.text)
                    }
                    contadorInputs++
                    ocultarInput()
                    stage?.keyboardFocus = null
                } else {
                    return
                }
            }

            indiceActual++
            if (indiceActual < listaDialogos.size) {
                actualizarContenido()
            } else {
                this.isVisible = false
                // Asegurar que se oculte el teclado al cerrar el diálogo
                Gdx.input.setOnscreenKeyboardVisible(false)
            }
        }
    }

    fun retroceder(): Boolean {
        if (indiceActual > 0) {
            indiceActual--
            actualizarContenido()
            caracteresVisibles = textoCompleto.length
            labelTexto.setText(textoCompleto)
            return true
        }
        return false
    }

    fun dispose() {
        try { whiteRegion.texture.dispose() } catch (_: Exception) {}
    }
}
