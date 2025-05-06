package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "birds")
data class Bird(
    @PrimaryKey val species: String, // Use species as the primary key
    val count: Int // Track the number of times the bird has been detected
)
