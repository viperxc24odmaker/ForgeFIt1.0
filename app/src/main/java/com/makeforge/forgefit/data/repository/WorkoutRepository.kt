package com.makeforge.forgefit.data.repository

import com.google.gson.Gson
import com.makeforge.forgefit.data.local.dao.WorkoutDao
import com.makeforge.forgefit.data.local.entity.WorkoutEntity
import com.makeforge.forgefit.domain.model.Exercise
import com.makeforge.forgefit.domain.model.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val gson: Gson
) {
    val recentWorkouts: Flow<List<Workout>> = workoutDao.getRecentCompleted().map { entities ->
        entities.map { it.toWorkout(gson) }
    }

    suspend fun saveWorkout(workout: Workout): Long {
        return workoutDao.insert(workout.toEntity(gson))
    }

    suspend fun markCompleted(id: Long, jackedPoints: Int) {
        val entity = workoutDao.getById(id) ?: return
        workoutDao.update(entity.copy(isCompleted = true, jackedPointsEarned = jackedPoints))
    }

    suspend fun getTotalCompleted() = workoutDao.getTotalCompleted()
    suspend fun getTotalJackedPoints() = workoutDao.getTotalJackedPoints()
}

private fun WorkoutEntity.toWorkout(gson: Gson): Workout {
    val exercises = gson.fromJson(exercisesJson, Array<Exercise>::class.java).toList()
    return Workout(
        id = id,
        date = Date(dateMs),
        title = title,
        exercises = exercises,
        durationMinutes = durationMinutes,
        isCompleted = isCompleted,
        jackedPointsEarned = jackedPointsEarned
    )
}

private fun Workout.toEntity(gson: Gson): WorkoutEntity {
    return WorkoutEntity(
        id = id,
        dateMs = date.time,
        title = title,
        exercisesJson = gson.toJson(exercises),
        durationMinutes = durationMinutes,
        isCompleted = isCompleted,
        jackedPointsEarned = jackedPointsEarned
    )
}
