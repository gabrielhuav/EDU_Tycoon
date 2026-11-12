package io.moviles.IPN_Tycoon.rival

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MercadoAlumnosTest {

    @Before
    fun setUp() {
        GameState.reset()
        PropiedadRepository.resetProgress()
        RivalState.reset()
    }

    @Test
    fun `cuota is 0_5 when both powers are 0`() {
        assertEquals(0.5, MercadoAlumnos.cuota(0, 0), 1e-9)
        assertEquals(0.5, MercadoAlumnos.cuotaJugador(), 1e-9)
    }

    @Test
    fun `poder sums base times level`() {
        assertEquals(600L * 2 + 50L, MercadoAlumnos.poder(listOf(600 to 2, 50 to 1)))
    }

    @Test
    fun `factor respects bounds`() {
        assertEquals(0.75, MercadoAlumnos.factorAtraccion(0.0), 1e-9)
        assertEquals(1.25, MercadoAlumnos.factorAtraccion(1.0), 1e-9)
        assertEquals(1.0, MercadoAlumnos.factorAtraccion(0.5), 1e-9)
        assertEquals(0.75, MercadoAlumnos.factorAtraccion(-3.0), 1e-9)
        assertEquals(1.25, MercadoAlumnos.factorAtraccion(9.0), 1e-9)
    }

    @Test
    fun `cuota is monotonic in player power`() {
        var prev = -1.0
        for (p in 0L..100L step 10) {
            val c = MercadoAlumnos.cuota(p, 50)
            assertTrue(c >= prev)
            prev = c
        }
    }

    @Test
    fun `cuotaJugador reads global state`() {
        PropiedadRepository.getPropiedad("escom_hitbox")!!.apply { comprada = true; nivel = 1 } // 600
        RivalState.edificios["Mac_and_cheese"] = 1 // 50
        assertEquals(600.0 / 650.0, MercadoAlumnos.cuotaJugador(), 1e-9)
    }

    @Test
    fun `alumnos and ingreso use attraction factor`() {
        PropiedadRepository.getPropiedad("escom_hitbox")!!.apply { comprada = true; nivel = 1 }
        // rival vacio: cuota 1.0 -> factor 1.25
        assertEquals(750, MercadoAlumnos.alumnosJugador())
        assertEquals(60_000L, MercadoAlumnos.ingresoBaseJugador())
        assertEquals(75_000L, MercadoAlumnos.ingresoJugador())
    }

    @Test
    fun `nothing owned yields zero students and income`() {
        assertEquals(0, MercadoAlumnos.alumnosJugador())
        assertEquals(0L, MercadoAlumnos.ingresoJugador())
        assertEquals(0L, MercadoAlumnos.ingresoRival())
    }
}
