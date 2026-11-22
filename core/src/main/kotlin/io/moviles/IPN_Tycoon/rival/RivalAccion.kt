package io.moviles.IPN_Tycoon.rival

/** Acciones que el rival realiza en un ciclo. */
sealed class RivalAccion {
    data class Compra(val propId: String, val nombre: String, val precio: Long) : RivalAccion()
    data class Mejora(val propId: String, val nombre: String, val nivel: Int, val costo: Long) : RivalAccion()
    data class CambioEstado(val de: RivalEstado, val a: RivalEstado) : RivalAccion()
}
