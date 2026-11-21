package io.moviles.IPN_Tycoon.android.database

import com.badlogic.gdx.Gdx
import io.moviles.IPN_Tycoon.GameSaveData
import io.moviles.IPN_Tycoon.GameSaveManager
import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository
import io.moviles.IPN_Tycoon.data.local.entities.EscuelaEntity
import io.moviles.IPN_Tycoon.data.repositories.EscuelaRepository
import io.moviles.IPN_Tycoon.rival.MercadoAlumnos
import io.moviles.IPN_Tycoon.rival.RivalState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * GameState, PropiedadRepository y RivalState solo se leen y se modifican en el
 * hilo de render de libGDX, donde corren los ciclos y la UI. El hilo de IO solo
 * habla con Room: la foto de la partida se toma antes de lanzar la corrutina y lo
 * cargado se aplica de vuelta con Gdx.app.postRunnable.
 */
class AndroidGameSaveManager(
    private val repository: EscuelaRepository
) : GameSaveManager {

    // SupervisorJob: si una operación falla, las siguientes se siguen ejecutando.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ── Guardar ───────────────────────────────────────────────────────
    override fun guardar(slot: Int, onDone: (Boolean) -> Unit) {
        val entity = try {
            fotoDePartida(slot)
        } catch (e: Exception) {
            Gdx.app.error(TAG, "No se pudo preparar el guardado del slot $slot", e)
            onDone(false)
            return
        }
        scope.launch {
            val ok = try {
                repository.guardar(entity)
                true
            } catch (e: Exception) {
                Gdx.app.error(TAG, "No se pudo guardar el slot $slot", e)
                false
            }
            Gdx.app.postRunnable {
                if (ok) {
                    GameState.saveId     = slot
                    GameState.slotActual = slot
                }
                onDone(ok)
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
            val entity = try {
                repository.getBySlot(slot)
            } catch (e: Exception) {
                Gdx.app.error(TAG, "No se pudo leer el slot $slot", e)
                null
            }
            Gdx.app.postRunnable {
                if (entity != null) aplicarPartida(entity)
                onDone(entity != null)
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

    // ── Helpers (hilo de render) ──────────────────────────────────────
    private fun fotoDePartida(slot: Int) = EscuelaEntity(
        slot           = slot,
        nombreJugador  = GameState.nombreJugador,
        nombreEscuela  = GameState.nombreEscuela,
        dinero         = GameState.dinero,
        edificiosJson  = serializarEdificios(),
        ciclosJugados  = GameState.ciclosJugados,
        alumnosTotales = MercadoAlumnos.alumnosJugador(),
        fechaGuardado  = System.currentTimeMillis(),
        rivalJson      = RivalState.serializar()
    )

    private fun aplicarPartida(entity: EscuelaEntity) {
        GameState.saveId         = entity.slot
        GameState.slotActual     = entity.slot
        GameState.dinero         = entity.dinero
        GameState.ciclosJugados  = entity.ciclosJugados
        GameState.nombreJugador  = entity.nombreJugador
        GameState.nombreEscuela  = entity.nombreEscuela
        GameState.alumnosTotales = entity.alumnosTotales
        deserializarEdificios(entity.edificiosJson)
        RivalState.deserializar(entity.rivalJson)
    }

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

    private fun EscuelaEntity.toDto() = GameSaveData(
        slot           = slot,
        nombreJugador  = nombreJugador,
        nombreEscuela  = nombreEscuela,
        dinero         = dinero,
        alumnosTotales = alumnosTotales,
        ciclosJugados  = ciclosJugados,
        fechaGuardado  = fechaGuardado
    )

    private companion object {
        const val TAG = "GUARDADO"
    }
}
