package com.makeforge.forgefit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jogs")
data class JogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateMs: Long,
    val durationSeconds: Int,
    val distanceKm: Float,
    val paceSecondsPerKm: Int,
    val caloriesBurned: Int,
    val jackedPointsEarned: Int
)
