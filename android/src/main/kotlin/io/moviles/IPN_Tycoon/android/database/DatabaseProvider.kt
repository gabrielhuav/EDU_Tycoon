package io.moviles.IPN_Tycoon.android.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var db: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ipn_tycoon_db"
            ).addMigrations(MIGRATION_4_5)
            .fallbackToDestructiveMigration()
            .build()
            db = instance
            instance
        }
    }
}
