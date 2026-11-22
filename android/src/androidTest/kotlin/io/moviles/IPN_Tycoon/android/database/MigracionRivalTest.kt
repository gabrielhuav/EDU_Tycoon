package io.moviles.IPN_Tycoon.android.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Verifica MIGRATION_4_5 sin fallbackToDestructiveMigration ni DatabaseProvider. */
@RunWith(AndroidJUnit4::class)
class MigracionRivalTest {

    private val nombreDb = "migracion-rival-test.db"
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun limpiar() {
        context.deleteDatabase(nombreDb)
    }

    @After
    fun limpiarAlFinal() {
        context.deleteDatabase(nombreDb)
    }

    private fun crearBaseVersion4() {
        val raw = context.openOrCreateDatabase(nombreDb, Context.MODE_PRIVATE, null)
        raw.execSQL(
            "CREATE TABLE IF NOT EXISTS escuelas (" +
                "slot INTEGER NOT NULL, " +
                "nombreJugador TEXT NOT NULL, " +
                "nombreEscuela TEXT NOT NULL, " +
                "dinero INTEGER NOT NULL, " +
                "edificiosJson TEXT NOT NULL, " +
                "ciclosJugados INTEGER NOT NULL, " +
                "alumnosTotales INTEGER NOT NULL, " +
                "fechaGuardado INTEGER NOT NULL, " +
                "PRIMARY KEY(slot))"
        )
        raw.execSQL(
            "INSERT INTO escuelas VALUES (1, 'Javier', 'ESCOM', 1234567, 'escom_hitbox:2', 7, 600, 1700000000000)"
        )
        raw.version = 4
        raw.close()
    }

    @Test
    fun migracion4a5ConservaFilaYAgregaRivalJsonVacio() = runBlocking {
        crearBaseVersion4()

        val db = Room.databaseBuilder(context, AppDatabase::class.java, nombreDb)
            .addMigrations(MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()
        try {
            val fila = db.escuelaDao().getBySlot(1)
            assertNotNull("La fila debe sobrevivir a la migracion", fila)
            assertEquals("Javier", fila!!.nombreJugador)
            assertEquals("ESCOM", fila.nombreEscuela)
            assertEquals(1234567L, fila.dinero)
            assertEquals("escom_hitbox:2", fila.edificiosJson)
            assertEquals(7, fila.ciclosJugados)
            assertEquals(600, fila.alumnosTotales)
            assertEquals("", fila.rivalJson)
        } finally {
            db.close()
        }
    }
}
