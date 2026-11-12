package io.moviles.IPN_Tycoon

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.widget.VisWindow
import io.moviles.IPN_Tycoon.rival.MercadoAlumnos
import io.moviles.IPN_Tycoon.rival.RivalState
import ktx.actors.onChange
import ktx.scene2d.*

class BuildingInfoWindow(
    private val data: Propiedad,
    private val onBuildingChanged: () -> Unit
) : VisWindow("Gestión del Plantel") {

    init {
        addCloseButton()
        closeOnEscape()
        isModal = false

        val costo: Long
        val btnTexto: String
        val puedeMejorar: Boolean
        val nivelRival = if (data.comprada) 0 else (RivalState.edificios[data.id] ?: 0)

        when {
            nivelRival > 0 -> {
                costo        = 0L
                btnTexto     = ""
                puedeMejorar = false
            }
            !data.comprada -> {
                costo        = data.precio
                btnTexto     = "COMPRAR  \$${fmt(costo)}"
                puedeMejorar = true
            }
            data.nivel < data.mejoraMax -> {
                costo        = GameState.costoMejora(data)
                btnTexto     = "MEJORAR LVL ${data.nivel + 1}  \$${fmt(costo)}"
                puedeMejorar = true
            }
            else -> {
                costo        = 0L
                btnTexto     = "NIVEL MÁXIMO"
                puedeMejorar = false
            }
        }

        add(scene2d.table {

            // ── Nombre ────────────────────────────────────────────────
            label(data.nombre) {
                color = Color.GOLD
            }.cell(padBottom = 6f)
            row()

            // ── Descripción ───────────────────────────────────────────
            label(data.descripcion) {
                setWrap(true)
                color = Color.LIGHT_GRAY
            }.cell(width = 280f, padBottom = 10f)
            row()

            // ── Stats ─────────────────────────────────────────────────
            label("Capacidad: ${data.capacidad} alumnos"); row()

            if (nivelRival > 0) {
                label("Propiedad del Instituto Rival (Nivel $nivelRival)") {
                    color = Color.SALMON
                }.cell(padTop = 6f)
                row()
            } else if (data.comprada) {
                label("Nivel actual: ${data.nivel} / ${data.mejoraMax}") {
                    color = Color.CYAN
                }.cell(padBottom = 2f)
                row()

                val ingresoCiclo = data.baseAlumnos * data.nivel * MercadoAlumnos.INGRESO_POR_ALUMNO
                label("Ingreso/ciclo: \$${fmt(ingresoCiclo)}") {
                    color = Color.GREEN
                }
                row()

                if (data.nivel < data.mejoraMax) {
                    val ingresoSiguiente = data.baseAlumnos * (data.nivel + 1) * MercadoAlumnos.INGRESO_POR_ALUMNO
                    label("Nivel ${data.nivel + 1}: \$${fmt(ingresoSiguiente)}/ciclo") {
                        color = Color.LIGHT_GRAY
                    }
                    row()
                }
            } else {
                val ingresoNivel1 = data.baseAlumnos * 1 * MercadoAlumnos.INGRESO_POR_ALUMNO
                label("Ingreso al comprar: \$${fmt(ingresoNivel1)}/ciclo") {
                    color = Color.GREEN
                }
                row()
            }

            // ── Saldo ─────────────────────────────────────────────────
            label("Tu saldo: \$${fmt(GameState.dinero)}") {
                color = Color.LIGHT_GRAY
            }.cell(padTop = 6f, padBottom = 4f)
            row()

            // ── Botón acción ──────────────────────────────────────────
            if (nivelRival == 0) textButton(btnTexto) {
                isDisabled = !puedeMejorar

                onChange {
                    if (!puedeMejorar) return@onChange
                    // El rival pudo comprarlo mientras esta ventana estaba abierta.
                    if (!data.comprada && RivalState.posee(data.id)) {
                        setText("Propiedad del rival")
                        isDisabled = true
                        return@onChange
                    }

                    if (!GameState.puedeComprar(costo)) {
                        setText("¡Saldo insuficiente!")
                        color = Color.RED
                        isDisabled = true
                        return@onChange
                    }

                    GameState.gastar(costo)

                    if (!data.comprada) {
                        data.comprada = true
                        data.nivel    = 1
                    } else {
                        data.nivel++
                    }

                    onBuildingChanged()
                    this@BuildingInfoWindow.remove()
                }
            }.cell(padTop = 14f, expandX = true, fillX = true)
        }).pad(16f)

        pack()
        centerWindow()
    }

    fun show(stage: Stage) { stage.addActor(this) }

    private fun fmt(v: Long) = when {
        v >= 1_000_000L -> "${"%.2f".format(v / 1_000_000.0)}M"
        v >= 1_000L     -> "${"%.1f".format(v / 1_000.0)}K"
        else            -> v.toString()
    }
}
