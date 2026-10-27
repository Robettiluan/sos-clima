package com.sosclima.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Ocorrencia::class], version = 1, exportSchema = false)
abstract class SosClimaDatabase : RoomDatabase() {

    abstract fun ocorrenciaDao(): OcorrenciaDao

    companion object {
        @Volatile
        private var instancia: SosClimaDatabase? = null

        fun getInstance(context: Context): SosClimaDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    SosClimaDatabase::class.java,
                    "sosclima.db",
                ).build().also { instancia = it }
            }
    }
}
