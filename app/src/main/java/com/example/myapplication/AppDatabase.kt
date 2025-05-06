package com.example.myapplication

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Bird::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun birdDao(): BirdDao

    fun populateWithSampleData() {
        CoroutineScope(Dispatchers.IO).launch {
            val birdDao = birdDao()
            birdDao.insertOrUpdateBird(Bird(species = "Sparrow", count = 1))
            birdDao.insertOrUpdateBird(Bird(species = "Robin", count = 1))
            birdDao.insertOrUpdateBird(Bird(species = "Blue Jay", count = 1))
            birdDao.insertOrUpdateBird(Bird(species = "Cardinal", count = 1))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.populateWithSampleData()
                android.util.Log.d("AppDatabase", "Database created and sample data populated.")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bird_database"
                )
                .addCallback(roomCallback)
                .build().also { INSTANCE = it }
            }
        }
    }
}
