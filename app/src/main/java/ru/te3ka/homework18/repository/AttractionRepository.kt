package ru.te3ka.homework18.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import ru.te3ka.homework18.model.Attraction
import ru.te3ka.homework18.model.AttractionDao

class AttractionRepository(private val attractionDao: AttractionDao) {
    val allAttractions: LiveData<List<Attraction>> = attractionDao.getAllAttractions()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(attraction: Attraction) {
        attractionDao.insertAttraction(attraction)
    }
}