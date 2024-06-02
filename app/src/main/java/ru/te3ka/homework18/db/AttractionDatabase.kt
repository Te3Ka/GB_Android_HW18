package ru.te3ka.homework18.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.te3ka.homework18.model.Attraction
import ru.te3ka.homework18.model.AttractionDao

@Database(entities = [Attraction::class], version = 1, exportSchema = false)
abstract class AttractionDatabase : RoomDatabase() {
    abstract fun attractionDao(): AttractionDao

    companion object {
        @Volatile
        private var INSTANCE: AttractionDatabase? = null

        fun getDatabase(context: Context): AttractionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AttractionDatabase::class.java,
                    "attraction_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}