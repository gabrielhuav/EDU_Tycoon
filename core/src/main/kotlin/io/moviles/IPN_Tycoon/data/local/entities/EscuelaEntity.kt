package io.moviles.IPN_Tycoon.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una fila = un slot de partida guardada.
 * [slot] es la clave lógica (1 o 2). Slot 3 es solo testing, no se persiste.
 * [edificiosJson] = "escom_hitbox:2,auditorio:1,..."  (solo edificios comprados)
 */
@Entity(tableName = "escuelas")
data class EscuelaEntity(
    @PrimaryKey
    val slot: Int,
    val nombreJugador: String  = "",
    val nombreEscuela: String  = "",
    val dinero: Long           = 1_500_000L,
    val edificiosJson: String  = "",
    val ciclosJugados: Int     = 0,
    val alumnosTotales: Int    = 0,
    val fechaGuardado: Long    = System.currentTimeMillis(),
    /** Estado serializado del Instituto Rival (ver RivalState.serializar). Vacio = partida sin rival. */
    val rivalJson: String      = ""
)
