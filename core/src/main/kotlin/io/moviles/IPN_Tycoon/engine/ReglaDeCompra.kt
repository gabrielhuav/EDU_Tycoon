package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.Propiedad

/** Resultado de evaluar si el jugador puede comprar o mejorar una propiedad. */
sealed class ResultadoCompra {

    /** El saldo alcanza: muestra cuánto quedará después de pagar. */
    data class Permitida(val costo: Long, val saldoDespues: Long) : ResultadoCompra()

    /** Estado de dato inválido: el costo supera el saldo; [faltante] es lo que falta. */
    data class SaldoInsuficiente(val costo: Long, val faltante: Long) : ResultadoCompra()

    /** La propiedad ya está en su nivel máximo: no hay nada que comprar. */
    data object NivelMaximo : ResultadoCompra()
}

/**
 * Regla de compra de EduTycoon: el jugador nunca puede gastar más saldo del disponible.
 *
 * No depende de libGDX, así que se puede probar con pruebas unitarias.
 */
object ReglaDeCompra {

    /** Costo de la siguiente acción sobre la propiedad, o null si ya está en su nivel máximo. */
    fun costoSiguiente(propiedad: Propiedad): Long? = when {
        !propiedad.comprada                     -> propiedad.precio
        propiedad.nivel < propiedad.mejoraMax   -> GameState.costoMejora(propiedad)
        else                                    -> null
    }

    /** Compara el [costo] con el [saldo] sin modificar nada. */
    fun evaluar(saldo: Long, costo: Long?): ResultadoCompra {
        if (costo == null) return ResultadoCompra.NivelMaximo
        require(costo >= 0) { "El costo no puede ser negativo: $costo" }

        return if (saldo >= costo) {
            ResultadoCompra.Permitida(costo = costo, saldoDespues = saldo - costo)
        } else {
            ResultadoCompra.SaldoInsuficiente(costo = costo, faltante = costo - saldo)
        }
    }

    /**
     * Compra o mejora la propiedad solo si el saldo alcanza.
     * Si no alcanza, no descuenta dinero ni cambia la propiedad.
     */
    fun comprar(propiedad: Propiedad): ResultadoCompra {
        val resultado = evaluar(GameState.dinero, costoSiguiente(propiedad))

        if (resultado is ResultadoCompra.Permitida && GameState.gastar(resultado.costo)) {
            if (!propiedad.comprada) {
                propiedad.comprada = true
                propiedad.nivel = 1
            } else {
                propiedad.nivel++
            }
        }
        return resultado
    }
}
