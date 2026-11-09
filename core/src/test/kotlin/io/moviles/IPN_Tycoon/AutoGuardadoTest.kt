package io.moviles.IPN_Tycoon

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoGuardadoTest {

    /** GameSaveManager falso: registra los slots guardados y deja el callback pendiente. */
    private class FakeSaveManager : GameSaveManager {
        val guardados = mutableListOf<Int>()
        var pendiente: ((Boolean) -> Unit)? = null

        override fun guardar(slot: Int, onDone: (success: Boolean) -> Unit) {
            guardados += slot
            pendiente = onDone
        }
        fun terminar(ok: Boolean = true) { pendiente?.invoke(ok); pendiente = null }

        override fun cargarSlots(onResult: (GameSaveData?, GameSaveData?, GameSaveData?) -> Unit) {}
        override fun cargarPartida(slot: Int, onDone: (success: Boolean) -> Unit) {}
        override fun eliminarSlot(slot: Int, onDone: () -> Unit) {}
    }

    @After
    fun limpiar() = GameState.reset()

    @Test
    fun `sin slot asignado no guarda`() {
        val fake = FakeSaveManager()
        GameState.slotActual = 0
        assertFalse(AutoGuardado(fake).guardarAhora())
        assertTrue(fake.guardados.isEmpty())
    }

    @Test
    fun `guarda en el slot actual`() {
        val fake = FakeSaveManager()
        GameState.slotActual = 2
        assertTrue(AutoGuardado(fake).guardarAhora())
        assertEquals(listOf(2), fake.guardados)
    }

    @Test
    fun `no lanza dos guardados a la vez`() {
        val fake = FakeSaveManager()
        GameState.slotActual = 1
        val auto = AutoGuardado(fake)

        assertTrue(auto.guardarAhora())
        assertFalse(auto.guardarAhora())      // el primero sigue en curso
        fake.terminar()
        assertTrue(auto.guardarAhora())       // ya terminó: se puede volver a guardar
        assertEquals(2, fake.guardados.size)
    }

    @Test
    fun `guarda cada N ciclos`() {
        val fake = FakeSaveManager()
        GameState.slotActual = 1
        val auto = AutoGuardado(fake, cadaCiclos = 2)

        auto.alTerminarCiclo()
        assertTrue(fake.guardados.isEmpty())
        auto.alTerminarCiclo()
        assertEquals(1, fake.guardados.size)
    }

    @Test
    fun `informa el resultado del guardado`() {
        val fake = FakeSaveManager()
        GameState.slotActual = 1
        var resultado: Boolean? = null
        AutoGuardado(fake) { resultado = it }.guardarAhora()

        fake.terminar(ok = false)
        assertEquals(false, resultado)
    }
}
