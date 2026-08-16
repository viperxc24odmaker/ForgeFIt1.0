package com.makeforge.forgefit.domain.model

import java.util.Date

data class Workout(
    val id: Long = 0,
    val date: Date = Date(),
    val title: String = "",
    val exercises: List<Exercise> = emptyList(),
    val durationMinutes: Int = 0,
    val isCompleted: Boolean = false,
    val jackedPointsEarned: Int = 0
)

data class Exercise(
    val name: String,
    val sets: Int,
    val reps: String, // e.g. "8-12" or "15"
    val restSeconds: Int,
    val muscleGroup: String,
    val instructions: String = "",
    val isCompleted: Boolean = false
)

data class WorkoutSession(
    val workout: Workout,
    val currentExerciseIndex: Int = 0,
    val startTime: Long = System.currentTimeMillis(),
    val isResting: Boolean = false,
    val restSecondsLeft: Int = 0
)
