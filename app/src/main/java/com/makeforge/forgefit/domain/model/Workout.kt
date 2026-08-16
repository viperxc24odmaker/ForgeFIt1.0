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
) {
    val totalSets: Int get() = exercises.sumOf { it.sets }
}

data class Exercise(
    val name: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val muscleGroup: String,
    val instructions: String = "",
    val isCompleted: Boolean = false
)

data class WorkoutSession(
    val workout: Workout,
    val currentExerciseIndex: Int = 0,
    val currentSet: Int = 1,
    val startTime: Long = System.currentTimeMillis(),
    val isResting: Boolean = false,
    val restSecondsLeft: Int = 0
) {
    val currentExercise: Exercise get() = workout.exercises[currentExerciseIndex]

    /** Sets finished across the whole workout so far. */
    val completedSets: Int
        get() = workout.exercises.take(currentExerciseIndex).sumOf { it.sets } + (currentSet - 1)

    val progress: Float
        get() = if (workout.totalSets == 0) 0f else completedSets.toFloat() / workout.totalSets
}
