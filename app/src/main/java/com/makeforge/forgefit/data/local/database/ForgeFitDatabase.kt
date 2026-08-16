package com.makeforge.forgefit.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.makeforge.forgefit.data.local.dao.JogDao
import com.makeforge.forgefit.data.local.dao.WorkoutDao
import com.makeforge.forgefit.data.local.entity.JogEntity
import com.makeforge.forgefit.data.local.entity.WorkoutEntity

@Database(
    entities = [WorkoutEntity::class, JogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ForgeFitDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun jogDao(): JogDao

    companion object {
        /** Adds the jogs table without wiping existing workout history. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `jogs` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `dateMs` INTEGER NOT NULL,
                        `durationSeconds` INTEGER NOT NULL,
                        `distanceKm` REAL NOT NULL,
                        `paceSecondsPerKm` INTEGER NOT NULL,
                        `caloriesBurned` INTEGER NOT NULL,
                        `jackedPointsEarned` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
