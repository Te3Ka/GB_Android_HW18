package ru.te3ka.homework18.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attractions")
data class Attraction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val photoPath: String,
    val dateTaken: String
)
