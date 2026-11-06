package io.moviles.IPN_Tycoon.rival

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RivalSerializacionTest {

    @Before
    fun setUp() { RivalState.reset() }

    @Test
    fun `roundtrip preserves everything`() {
        RivalState.dinero = 123_456L
        RivalState.estado = RivalEstado.COMPETIR
        RivalState.ciclosVictoria = 2
        RivalState.ciclosDerrota = 1
        RivalState.finPartidaMostrado = true
        RivalState.edificios["escom_hitbox"] = 2
        RivalState.edificios["cafeteria"] = 1
        val s = RivalState.serializar()

        RivalState.reset()
        RivalState.deserializar(s)

        assertEquals(123_456L, RivalState.dinero)
        assertEquals(RivalEstado.COMPETIR, RivalState.estado)
        assertEquals(2, RivalState.ciclosVictoria)
        assertEquals(1, RivalState.ciclosDerrota)
        assertTrue(RivalState.finPartidaMostrado)
        assertEquals(mapOf("escom_hitbox" to 2, "cafeteria" to 1), RivalState.edificios.toMap())
    }

    @Test
    fun `roundtrip with no buildings`() {
        val s = RivalState.serializar()
        RivalState.dinero = 1
        RivalState.deserializar(s)
        assertEquals(RivalState.DINERO_INICIAL, RivalState.dinero)
        assertTrue(RivalState.edificios.isEmpty())
    }

    @Test
    fun `blank string resets`() {
        RivalState.dinero = 5
        RivalState.edificios["x"] = 1
        RivalState.deserializar("")
        assertEquals(RivalState.DINERO_INICIAL, RivalState.dinero)
        assertTrue(RivalState.edificios.isEmpty())
        RivalState.deserializar(null)
        RivalState.deserializar("   ")
    }

    @Test
    fun `corrupt strings reset and never throw`() {
        val malos = listOf("basura", "abc|EXPANDIR|0|0|0|", "100|NOPE|0|0|0|", "100|AHORRAR|x|0|0|",
            "100|AHORRAR|0|0|0|a:b", "100|AHORRAR|0|0|0|a", "-5|AHORRAR|0|0|0|", "1|2|3")
        for (m in malos) {
            RivalState.dinero = 7
            RivalState.edificios["z"] = 3
            RivalState.deserializar(m)
            assertEquals(m, RivalState.DINERO_INICIAL, RivalState.dinero)
            assertTrue(m, RivalState.edificios.isEmpty())
            assertFalse(RivalState.finPartidaMostrado)
        }
    }

    @Test
    fun `posee reflects buildings`() {
        RivalState.edificios["cafeteria"] = 1
        assertTrue(RivalState.posee("cafeteria"))
        assertFalse(RivalState.posee("escom_hitbox"))
    }
}
