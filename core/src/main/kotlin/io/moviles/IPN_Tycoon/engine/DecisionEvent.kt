package io.moviles.IPN_Tycoon.engine

/**
 * Una de las dos respuestas posibles de un evento de decisión.
 *
 * @param texto      Texto del botón (lo que el jugador decide).
 * @param dinero     Cambio en el presupuesto: positivo = ingreso, negativo = gasto.
 * @param reputacion Puntos de reputación que se suman (o restan si es negativo).
 */
data class OpcionDecision(
    val texto: String,
    val dinero: Long,
    val reputacion: Int
)

/**
 * Evento que pausa la partida y obliga al jugador a elegir entre dos opciones.
 * A diferencia de [GameEvent], su efecto no se aplica solo: depende de la decisión.
 *
 * Regla de diseño: [opcionB] nunca cuesta dinero, para que el jugador
 * siempre tenga una salida aunque no le alcance el presupuesto.
 */
data class DecisionEvent(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val opcionA: OpcionDecision,
    val opcionB: OpcionDecision,
    val requiereEdificios: Int = 1
) {
    val opciones: List<OpcionDecision> get() = listOf(opcionA, opcionB)
}
