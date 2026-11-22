package io.moviles.IPN_Tycoon.android.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile private var db: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase =
        db ?: synchronized(this) {
            db ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ipn_tycoon_db"
            ).addMigrations(MIGRATION_4_5, MIGRATION_5_6)
                // Las versiones 1 a 3 del esquema solo existieron en desarrollo (a Play
                // llegó la 4) y no tienen migración. De la 4 en adelante cada cambio de
                // esquema necesita su Migration: si falta, la app falla al abrir la base
                // en vez de borrar en silencio las partidas de los jugadores.
                .fallbackToDestructiveMigrationFrom(true, 1, 2, 3)
                // Play no permite instalar una versión anterior; esto solo cubre a
                // quien prueba builds viejos encima de uno nuevo.
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .build()
                .also { db = it }
        }
}
