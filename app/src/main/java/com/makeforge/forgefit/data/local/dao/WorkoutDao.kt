package com.makeforge.forgefit.data.local.dao

import androidx.room.*
import com.makeforge.forgefit.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts ORDER BY dateMs DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE isCompleted = 1 ORDER BY dateMs DESC LIMIT 10")
    fun getRecentCompleted(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getById(id: Long): WorkoutEntity?

    @Query("SELECT COUNT(*) FROM workouts WHERE isCompleted = 1")
    suspend fun getTotalCompleted(): Int

    @Query("SELECT SUM(jackedPointsEarned) FROM workouts WHERE isCompleted = 1")
    suspend fun getTotalJackedPoints(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: WorkoutEntity): Long

    @Update
    suspend fun update(workout: WorkoutEntity)

    @Delete
    suspend fun delete(workout: WorkoutEntity)
}
