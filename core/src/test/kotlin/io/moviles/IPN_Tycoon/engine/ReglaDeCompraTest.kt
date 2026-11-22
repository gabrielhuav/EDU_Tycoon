package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.Configuracion
import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.Propiedad
import io.moviles.IPN_Tycoon.rival.RivalState
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglaDeCompraTest {

    private fun propiedad(
        precio: Long = 150L,
        mejoraMax: Int = 2,
        nivel: Int = 0,
        comprada: Boolean = false
    ) = Propiedad(
        id = "test",
        nombre = "Plantel de prueba",
        precio = precio,
        descripcion = "",
        capacidad = 100,
        baseAlumnos = 10,
        mejoraMax = mejoraMax,
        nivel = nivel,
        comprada = comprada
    )

    @After
    fun limpiar() = GameState.reset()

    // ── evaluar ───────────────────────────────────────────────────────

    @Test
    fun `saldo menor al costo indica cuanto falta`() {
        // Criterio de aceptación 1: saldo $100, costo $150 → faltan $50
        assertEquals(ResultadoCompra.SaldoInsuficiente(costo = 150L, faltante = 50L),
            ReglaDeCompra.evaluar(saldo = 100L, costo = 150L))
    }

    @Test
    fun `saldo mayor al costo indica el saldo despues`() {
        assertEquals(ResultadoCompra.Permitida(costo = 150L, saldoDespues = 50L),
            ReglaDeCompra.evaluar(saldo = 200L, costo = 150L))
    }

    @Test
    fun `saldo exacto permite comprar y deja cero`() {
        assertEquals(ResultadoCompra.Permitida(costo = 150L, saldoDespues = 0L),
            ReglaDeCompra.evaluar(saldo = 150L, costo = 150L))
    }

    @Test
    fun `sin costo la propiedad esta en nivel maximo`() {
        assertEquals(ResultadoCompra.NivelMaximo, ReglaDeCompra.evaluar(saldo = 1_000L, costo = null))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `costo negativo es invalido`() {
        ReglaDeCompra.evaluar(saldo = 100L, costo = -1L)
    }

    // ── costoSiguiente ────────────────────────────────────────────────

    @Test
    fun `costo siguiente usa precio de compra o de mejora`() {
        assertEquals(150L, ReglaDeCompra.costoSiguiente(propiedad()))
        assertEquals(300L, ReglaDeCompra.costoSiguiente(propiedad(nivel = 2, mejoraMax = 3, comprada = true)))
        assertNull(ReglaDeCompra.costoSiguiente(propiedad(nivel = 2, mejoraMax = 2, comprada = true)))
    }

    // ── comprar ───────────────────────────────────────────────────────

    @Test
    fun `comprar con saldo insuficiente no descuenta ni cambia la propiedad`() {
        GameState.dinero = 100L
        val p = propiedad()

        val resultado = ReglaDeCompra.comprar(p)

        assertTrue(resultado is ResultadoCompra.SaldoInsuficiente)
        assertEquals(100L, GameState.dinero)
        assertFalse(p.comprada)
        assertEquals(0, p.nivel)
    }

    @Test
    fun `comprar con saldo suficiente descuenta y compra la propiedad`() {
        // Criterio de aceptación 2: saldo $200, costo $150 → saldo $50
        GameState.dinero = 200L
        val p = propiedad()

        val resultado = ReglaDeCompra.comprar(p)

        assertTrue(resultado is ResultadoCompra.Permitida)
        assertEquals(50L, GameState.dinero)
        assertTrue(p.comprada)
        assertEquals(1, p.nivel)
    }

    @Test
    fun `mejorar sin saldo suficiente no sube de nivel`() {
        GameState.dinero = 100L
        val p = propiedad(nivel = 1, comprada = true)   // mejora a nivel 2 cuesta 150

        ReglaDeCompra.comprar(p)

        assertEquals(100L, GameState.dinero)
        assertEquals(1, p.nivel)
    }

    @Test
    fun `el saldo nunca queda negativo`() {
        GameState.dinero = 149L
        ReglaDeCompra.comprar(propiedad())
        assertTrue(GameState.dinero >= 0)
    }

    @Test
    fun `no se puede comprar un edificio del rival activo`() {
        RivalState.reset()
        Configuracion.rivalActivo = true
        RivalState.edificios["test"] = 1
        GameState.dinero = 1_000L
        val p = propiedad()

        assertEquals(ResultadoCompra.PropiedadDelRival, ReglaDeCompra.comprar(p))
        assertFalse(p.comprada)
        assertEquals(1_000L, GameState.dinero)
        RivalState.reset()
        Configuracion.rivalActivo = false
    }

    @Test
    fun `con el rival apagado su edificio se compra y deja de ser suyo`() {
        RivalState.reset()
        Configuracion.rivalActivo = false
        RivalState.edificios["test"] = 1
        GameState.dinero = 1_000L
        val p = propiedad()

        assertTrue(ReglaDeCompra.comprar(p) is ResultadoCompra.Permitida)
        assertTrue(p.comprada)
        assertFalse(RivalState.edificios.containsKey("test"))
        RivalState.reset()
    }
}