package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.Propiedad
import io.moviles.IPN_Tycoon.PropiedadRepository

/**
 * Gestor del subsistema de ingresos interactivos y ciclos de producción por edificio (Tycoon).
 *
 * En lugar de acreditar ingresos de forma automática y pasiva cada ciclo global,
 * cada edificio escolar adquirido opera bajo un temporizador de producción independiente:
 * - Edificios iniciales/económicos: ciclos más lentos (40s - 50s).
 * - Edificios avanzados/superiores: ciclos más rápidos (15s - 25s) y mayor ingreso.
 *
 * Al concluir el ciclo de producción de un edificio, se activa una ganancia lista para
 * ser recolectada manualmente por el jugador mediante una burbuja interactiva táctil.
 */
object BuildingIncomeManager {

    const val INGRESO_POR_ALUMNO = 100L

    data class EstadoEdificio(
        val propiedadId: String,
        var tiempoAcumulado: Float = 0f,
        var listoParaRecolectar: Boolean = false,
        var gananciasListas: Long = 0L
    )

    private val estados = mutableMapOf<String, EstadoEdificio>()

    init {
        reiniciar()
    }

    /**
     * Reinicia los estados de producción para todas las propiedades.
     */
    fun reiniciar() {
        estados.clear()
        PropiedadRepository.propiedades.keys.forEach { id ->
            estados[id] = EstadoEdificio(propiedadId = id)
        }
    }

    /**
     * Obtiene el estado actual de un edificio por ID (lo crea si no existiera).
     */
    fun obtenerEstado(propiedadId: String): EstadoEdificio {
        return estados.getOrPut(propiedadId) { EstadoEdificio(propiedadId) }
    }

    /**
     * Calcula la ganancia que generará un edificio por ciclo según su nivel y alumnos base.
     */
    fun calcularIngresoPorCiclo(propiedad: Propiedad, nivel: Int = propiedad.nivel): Long {
        if (nivel <= 0) return 0L
        return propiedad.baseAlumnos.toLong() * nivel.toLong() * INGRESO_POR_ALUMNO
    }

    /**
     * Actualiza el temporizador de los edificios comprados.
     * @param delta Segundos transcurridos desde el último frame.
     * @return Lista de IDs de edificios que acaban de completar su ciclo en esta llamada.
     */
    fun actualizar(delta: Float): List<String> {
        val completados = mutableListOf<String>()

        for (propiedad in PropiedadRepository.propiedades.values) {
            if (!propiedad.comprada || propiedad.nivel <= 0) continue

            val estado = obtenerEstado(propiedad.id)
            if (estado.listoParaRecolectar) {
                // El ciclo está completado; se pausa hasta que el jugador recolecte la ganancia
                continue
            }

            estado.tiempoAcumulado += delta
            val duracion = propiedad.tiempoCicloSegundos.coerceAtLeast(1f)

            if (estado.tiempoAcumulado >= duracion) {
                estado.tiempoAcumulado = duracion
                estado.gananciasListas = calcularIngresoPorCiclo(propiedad)
                estado.listoParaRecolectar = true
                completados.add(propiedad.id)
            }
        }

        return completados
    }

    /**
     * Indica si un edificio tiene una burbuja de recaudación activa.
     */
    fun tieneIngresoListo(propiedadId: String): Boolean {
        return estados[propiedadId]?.listoParaRecolectar == true
    }

    /**
     * Retorna el monto acumulado disponible para cobro en un edificio.
     */
    fun obtenerGananciasListas(propiedadId: String): Long {
        return estados[propiedadId]?.gananciasListas ?: 0L
    }

    /**
     * Retorna el progreso del ciclo actual en un rango de 0.0f a 1.0f.
     */
    fun obtenerProgresoCiclo(propiedad: Propiedad): Float {
        val estado = estados[propiedad.id] ?: return 0f
        if (estado.listoParaRecolectar) return 1.0f
        val duracion = propiedad.tiempoCicloSegundos.coerceAtLeast(1f)
        return (estado.tiempoAcumulado / duracion).coerceIn(0f, 1f)
    }

    /**
     * Retorna el tiempo restante en segundos para que concluya el ciclo de producción.
     */
    fun obtenerTiempoRestante(propiedad: Propiedad): Float {
        val estado = estados[propiedad.id] ?: return propiedad.tiempoCicloSegundos
        if (estado.listoParaRecolectar) return 0f
        val restante = propiedad.tiempoCicloSegundos - estado.tiempoAcumulado
        return restante.coerceAtLeast(0f)
    }

    /**
     * Recolecta manualmente los ingresos de un edificio específico.
     * Acredita el dinero a GameState.dinero y reinicia el ciclo de producción del edificio.
     * @return El monto acreditado, o 0L si no había fondos listos para cobrar.
     */
    fun recolectar(propiedadId: String): Long {
        val estado = estados[propiedadId] ?: return 0L
        if (!estado.listoParaRecolectar || estado.gananciasListas <= 0L) {
            return 0L
        }

        val monto = estado.gananciasListas
        GameState.acreditar(monto)
        estado.gananciasListas = 0L
        estado.listoParaRecolectar = false
        estado.tiempoAcumulado = 0f

        return monto
    }

    /**
     * Recolecta simultáneamente todos los ingresos pendientes listos en el campus.
     * @return Suma total acreditada.
     */
    fun recolectarTodo(): Long {
        var total = 0L
        for (propiedad in PropiedadRepository.propiedades.values) {
            if (tieneIngresoListo(propiedad.id)) {
                total += recolectar(propiedad.id)
            }
        }
        return total
    }
}
