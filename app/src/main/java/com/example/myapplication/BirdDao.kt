package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BirdDao {
    @Insert
    suspend fun insertBird(bird: Bird)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBird(bird: Bird)

    @Query("UPDATE birds SET count = count + 1 WHERE species = :species")
    suspend fun incrementBirdCount(species: String)

    @Query("SELECT * FROM birds")
    fun getAllBirds(): List<Bird>
}
