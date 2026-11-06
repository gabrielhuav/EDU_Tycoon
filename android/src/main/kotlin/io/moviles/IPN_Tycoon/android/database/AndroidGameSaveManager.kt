package io.moviles.IPN_Tycoon.android.database

import com.badlogic.gdx.Gdx
import io.moviles.IPN_Tycoon.GameSaveData
import io.moviles.IPN_Tycoon.GameSaveManager
import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository
import io.moviles.IPN_Tycoon.data.local.entities.EscuelaEntity
import io.moviles.IPN_Tycoon.rival.RivalState
import io.moviles.IPN_Tycoon.data.repositories.EscuelaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AndroidGameSaveManager(
    private val repository: EscuelaRepository
) : GameSaveManager {

    private val scope = CoroutineScope(Dispatchers.IO)

    // ── Guardar ───────────────────────────────────────────────────────
    override fun guardar(slot: Int, onDone: (Boolean) -> Unit) {
        scope.launch {
            try {
                val entity = EscuelaEntity(
                    slot           = slot,
                    nombreJugador  = GameState.nombreJugador,
                    nombreEscuela  = GameState.nombreEscuela,
                    dinero         = GameState.dinero,
                    edificiosJson  = serializarEdificios(),
                    ciclosJugados  = GameState.ciclosJugados,
                    alumnosTotales = calcularAlumnos(),
                    fechaGuardado  = System.currentTimeMillis(),
                    rivalJson      = RivalState.serializar()
                )
                repository.guardar(entity)
                GameState.saveId     = slot
                GameState.slotActual = slot
                Gdx.app.postRunnable { onDone(true) }
            } catch (e: Exception) {
                Gdx.app.postRunnable { onDone(false) }
            }
        }
    }

    // ── Cargar slots 1, 2 y 3 ─────────────────────────────────────────
    override fun cargarSlots(onResult: (GameSaveData?, GameSaveData?, GameSaveData?) -> Unit) {
        scope.launch {
            val s1 = repository.getBySlot(1)?.toDto()
            val s2 = repository.getBySlot(2)?.toDto()
            val s3 = repository.getBySlot(3)?.toDto()
            Gdx.app.postRunnable { onResult(s1, s2, s3) }
        }
    }

    // ── Aplicar save al estado global ─────────────────────────────────
    override fun cargarPartida(slot: Int, onDone: (Boolean) -> Unit) {
        scope.launch {
            try {
                val entity = repository.getBySlot(slot)
                if (entity != null) {
                    GameState.saveId         = entity.slot
                    GameState.slotActual     = entity.slot
                    GameState.dinero         = entity.dinero
                    GameState.ciclosJugados  = entity.ciclosJugados
                    GameState.nombreJugador  = entity.nombreJugador
                    GameState.nombreEscuela  = entity.nombreEscuela
                    GameState.alumnosTotales = entity.alumnosTotales
                    deserializarEdificios(entity.edificiosJson)
                    RivalState.deserializar(entity.rivalJson)
                    Gdx.app.postRunnable { onDone(true) }
                } else {
                    Gdx.app.postRunnable { onDone(false) }
                }
            } catch (e: Exception) {
                Gdx.app.postRunnable { onDone(false) }
            }
        }
    }

    // ── Eliminar slot ─────────────────────────────────────────────────
    override fun eliminarSlot(slot: Int, onDone: () -> Unit) {
        scope.launch {
            repository.eliminarSlot(slot)
            Gdx.app.postRunnable { onDone() }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private fun serializarEdificios(): String =
        PropiedadRepository.propiedades.values
            .filter { it.comprada }
            .joinToString(",") { "${it.id}:${it.nivel}" }

    private fun deserializarEdificios(json: String) {
        PropiedadRepository.propiedades.values.forEach { it.comprada = false; it.nivel = 0 }
        if (json.isBlank()) return
        json.split(",").forEach { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) {
                val prop  = PropiedadRepository.getPropiedad(parts[0])
                val nivel = parts[1].toIntOrNull() ?: 0
                if (prop != null && nivel > 0) { prop.comprada = true; prop.nivel = nivel }
            }
        }
    }

    private fun calcularAlumnos(): Int =
        io.moviles.IPN_Tycoon.rival.MercadoAlumnos.alumnosJugador()

    private fun EscuelaEntity.toDto() = GameSaveData(
        slot           = slot,
        nombreJugador  = nombreJugador,
        nombreEscuela  = nombreEscuela,
        dinero         = dinero,
        alumnosTotales = alumnosTotales,
        ciclosJugados  = ciclosJugados,
        fechaGuardado  = fechaGuardado
    )
}
