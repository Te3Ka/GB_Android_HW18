package ru.te3ka.homework18.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.te3ka.homework18.db.AttractionDatabase
import ru.te3ka.homework18.model.Attraction
import ru.te3ka.homework18.repository.AttractionRepository

class AttractionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AttractionRepository
    val allAttractions: LiveData<List<Attraction>>

    init {
        val attractionDao = AttractionDatabase.getDatabase(application).attractionDao()
        repository = AttractionRepository(attractionDao)
        allAttractions = repository.allAttractions
    }

    fun insert(attraction: Attraction) = viewModelScope.launch {
        repository.insert(attraction)
    }
}