package com.makeforge.forgefit.data.local.dao

import androidx.room.*
import com.makeforge.forgefit.data.local.entity.JogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JogDao {

    @Query("SELECT * FROM jogs ORDER BY dateMs DESC")
    fun getAllJogs(): Flow<List<JogEntity>>

    @Query("SELECT COALESCE(SUM(distanceKm), 0) FROM jogs")
    fun getTotalDistance(): Flow<Float>

    @Query("SELECT COUNT(*) FROM jogs")
    fun getJogCount(): Flow<Int>

    @Query("SELECT MIN(paceSecondsPerKm) FROM jogs WHERE paceSecondsPerKm > 0")
    fun getBestPace(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jog: JogEntity): Long

    @Delete
    suspend fun delete(jog: JogEntity)
}
