package io.moviles.IPN_Tycoon.engine

import io.moviles.IPN_Tycoon.GameState
import io.moviles.IPN_Tycoon.PropiedadRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BuildingIncomeManagerTest {

    @Before
    fun setUp() {
        GameState.reset(500_000L)
        PropiedadRepository.resetProgress()
        BuildingIncomeManager.reiniciar()
    }

    @Test
    fun `ciclo no avanza para propiedades no compradas`() {
        val escom = PropiedadRepository.getPropiedad("escom_hitbox")!!
        assertFalse(escom.comprada)

        val completados = BuildingIncomeManager.actualizar(100f)

        assertFalse(BuildingIncomeManager.tieneIngresoListo("escom_hitbox"))
        assertEquals(0L, BuildingIncomeManager.obtenerGananciasListas("escom_hitbox"))
        assertFalse(completados.contains("escom_hitbox"))
    }

    @Test
    fun `ciclo acumula tiempo y marca listo para recolectar al completarse`() {
        val cafeteria = PropiedadRepository.getPropiedad("cafeteria")!!
        cafeteria.comprada = true
        cafeteria.nivel = 1

        val duracion = cafeteria.tiempoCicloSegundos
        assertTrue(duracion > 0f)

        // Avanzar la mitad del tiempo
        BuildingIncomeManager.actualizar(duracion * 0.5f)
        assertFalse(BuildingIncomeManager.tieneIngresoListo("cafeteria"))
        assertEquals(0.5f, BuildingIncomeManager.obtenerProgresoCiclo(cafeteria), 0.05f)

        // Completar el tiempo restante
        val completados = BuildingIncomeManager.actualizar(duracion * 0.5f + 1f)
        assertTrue(BuildingIncomeManager.tieneIngresoListo("cafeteria"))
        assertTrue(completados.contains("cafeteria"))

        val esperado = BuildingIncomeManager.calcularIngresoPorCiclo(cafeteria)
        assertEquals(esperado, BuildingIncomeManager.obtenerGananciasListas("cafeteria"))
        assertEquals(1.0f, BuildingIncomeManager.obtenerProgresoCiclo(cafeteria), 0.001f)
    }

    @Test
    fun `recolectar acredita saldo en GameState y reinicia ciclo`() {
        val escom = PropiedadRepository.getPropiedad("escom_hitbox")!!
        escom.comprada = true
        escom.nivel = 1
        val saldoInicial = GameState.dinero

        BuildingIncomeManager.actualizar(escom.tiempoCicloSegundos + 1f)
        assertTrue(BuildingIncomeManager.tieneIngresoListo("escom_hitbox"))

        val gananciasEsperadas = escom.baseAlumnos * escom.nivel * BuildingIncomeManager.INGRESO_POR_ALUMNO
        val recaudado = BuildingIncomeManager.recolectar("escom_hitbox")

        assertEquals(gananciasEsperadas, recaudado)
        assertEquals(saldoInicial + gananciasEsperadas, GameState.dinero)
        assertFalse(BuildingIncomeManager.tieneIngresoListo("escom_hitbox"))
        assertEquals(0L, BuildingIncomeManager.obtenerGananciasListas("escom_hitbox"))
    }

    @Test
    fun `edificios iniciales tienen ciclos mas lentos que edificios avanzados`() {
        val mac = PropiedadRepository.getPropiedad("Mac_and_cheese")!!
        val cafeteria = PropiedadRepository.getPropiedad("cafeteria")!!
        val escom = PropiedadRepository.getPropiedad("escom_hitbox")!!
        val auditorio = PropiedadRepository.getPropiedad("auditorio")!!
        val direccion = PropiedadRepository.getPropiedad("Direccion")!!

        assertTrue(mac.tiempoCicloSegundos > escom.tiempoCicloSegundos)
        assertTrue(cafeteria.tiempoCicloSegundos > auditorio.tiempoCicloSegundos)
        assertTrue(auditorio.tiempoCicloSegundos > direccion.tiempoCicloSegundos)
    }

    @Test
    fun `recolectar sin ingresos listos devuelve cero y no altera saldo`() {
        val saldoInicial = GameState.dinero
        val recaudado = BuildingIncomeManager.recolectar("Mac_and_cheese")

        assertEquals(0L, recaudado)
        assertEquals(saldoInicial, GameState.dinero)
    }

    @Test
    fun `recolectarTodo procesa todos los edificios con ciclo completado`() {
        val mac = PropiedadRepository.getPropiedad("Mac_and_cheese")!!
        val cafeteria = PropiedadRepository.getPropiedad("cafeteria")!!
        mac.comprada = true; mac.nivel = 1
        cafeteria.comprada = true; cafeteria.nivel = 1

        val saldoInicial = GameState.dinero
        // Avanzar suficiente para que ambos terminen
        BuildingIncomeManager.actualizar(60f)

        assertTrue(BuildingIncomeManager.tieneIngresoListo("Mac_and_cheese"))
        assertTrue(BuildingIncomeManager.tieneIngresoListo("cafeteria"))

        val totalEsperado = BuildingIncomeManager.calcularIngresoPorCiclo(mac) +
            BuildingIncomeManager.calcularIngresoPorCiclo(cafeteria)

        val totalRecolectado = BuildingIncomeManager.recolectarTodo()

        assertEquals(totalEsperado, totalRecolectado)
        assertEquals(saldoInicial + totalEsperado, GameState.dinero)
        assertFalse(BuildingIncomeManager.tieneIngresoListo("Mac_and_cheese"))
        assertFalse(BuildingIncomeManager.tieneIngresoListo("cafeteria"))
    }
}
