package com.example.finalprojectgroup4.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finalprojectgroup4.data.model.Checkin
import androidx.room.TypeConverters

/**
 * The app's Room database (file "spot.db"). Lists every table (entity) and hands out the DAOs.
 * Room generates the real implementation (AppDatabase_Impl) at build time.
 *
 * Kind: abstract class extending RoomDatabase. Owner: Track B (Ana).
 */
@Database(
    entities = [Checkin::class],
    version = 1,
    exportSchema = false // no schema JSON files needed for this project
)
@TypeConverters(Converters::class) // stores Category safely (see Converters.kt)

abstract class AppDatabase : RoomDatabase() {

    abstract fun checkinDao(): CheckinDao

    companion object {
        const val DATABASE_NAME = "spot.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    // While the schema is still changing during the course, rebuild instead of crashing.
                    // Remove this and write a Migration before real users have data.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instance = it }
            }
    }
}