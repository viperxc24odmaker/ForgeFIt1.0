package com.makeforge.forgefit.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.makeforge.forgefit.data.local.dao.WorkoutDao
import com.makeforge.forgefit.data.local.entity.WorkoutEntity

@Database(
    entities = [WorkoutEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ForgeFitDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}
