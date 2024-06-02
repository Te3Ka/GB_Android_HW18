package ru.te3ka.homework18.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AttractionDao {
    @Query("SELECT * FROM attractions")
    fun getAllAttractions(): LiveData<List<Attraction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttraction(attraction: Attraction)

    @Delete
    suspend fun deleteAttraction(attraction: Attraction)
}