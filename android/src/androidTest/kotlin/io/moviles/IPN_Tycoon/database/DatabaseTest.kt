package io.moviles.IPN_Tycoon.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.moviles.IPN_Tycoon.android.database.AppDatabase
import io.moviles.IPN_Tycoon.data.local.entities.EscuelaEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        // Base en memoria: se descarta al terminar cada prueba.
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun guardarYLeerPorSlot() = runBlocking {
        db.escuelaDao().guardar(
            EscuelaEntity(slot = 1, nombreEscuela = "ESCOM", alumnosTotales = 100, rivalJson = "500000|EXPANDIR|0|0|0|")
        )

        val leida = db.escuelaDao().getBySlot(1)
        assertNotNull(leida)
        assertEquals("ESCOM", leida!!.nombreEscuela)
        assertEquals(100, leida.alumnosTotales)
        assertEquals("500000|EXPANDIR|0|0|0|", leida.rivalJson)
    }

    @Test
    fun slotInexistenteDevuelveNull() = runBlocking {
        assertNull(db.escuelaDao().getBySlot(2))
    }

    @Test
    fun guardarMismoSlotReemplazaLaFila() = runBlocking {
        val dao = db.escuelaDao()
        dao.guardar(EscuelaEntity(slot = 1, nombreEscuela = "ESCOM", dinero = 10L))
        dao.guardar(EscuelaEntity(slot = 1, nombreEscuela = "ESIME", dinero = 20L))

        val leida = dao.getBySlot(1)!!
        assertEquals("ESIME", leida.nombreEscuela)
        assertEquals(20L, leida.dinero)
    }

    @Test
    fun eliminarSlotSoloBorraEseSlot() = runBlocking {
        val dao = db.escuelaDao()
        dao.guardar(EscuelaEntity(slot = 1, nombreEscuela = "ESCOM"))
        dao.guardar(EscuelaEntity(slot = 2, nombreEscuela = "ESIME"))

        dao.eliminarSlot(1)

        assertNull(dao.getBySlot(1))
        assertNotNull(dao.getBySlot(2))
    }
}
