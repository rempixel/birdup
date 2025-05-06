package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BirdDao {
    @Insert
    suspend fun insertBird(bird: Bird)

    @Query("SELECT * FROM birds")
    suspend fun getAllBirds(): List<Bird>
}
