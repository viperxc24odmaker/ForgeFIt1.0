package com.makeforge.forgefit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateMs: Long,
    val title: String,
    val exercisesJson: String, // Gson serialized List<Exercise>
    val durationMinutes: Int,
    val isCompleted: Boolean,
    val jackedPointsEarned: Int
)
