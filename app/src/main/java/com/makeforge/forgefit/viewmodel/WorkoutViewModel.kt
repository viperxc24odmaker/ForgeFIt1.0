package com.makeforge.forgefit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makeforge.forgefit.data.repository.UserPreferencesRepository
import com.makeforge.forgefit.data.repository.WorkoutRepository
import com.makeforge.forgefit.domain.ActiveWorkoutHolder
import com.makeforge.forgefit.domain.model.Workout
import com.makeforge.forgefit.domain.model.WorkoutSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    activeWorkoutHolder: ActiveWorkoutHolder
) : ViewModel() {

    private val _session = MutableStateFlow<WorkoutSession?>(null)
    val session: StateFlow<WorkoutSession?> = _session.asStateFlow()

    private val _summary = MutableStateFlow<WorkoutSummary?>(null)
    val summary: StateFlow<WorkoutSummary?> = _summary.asStateFlow()

    private var restTimerJob: Job? = null
    private var savedWorkoutId: Long = -1L

    init {
        // Pick up the workout handed over by the Home screen.
        activeWorkoutHolder.consume()?.let { startWorkout(it) }
    }

    private fun startWorkout(workout: Workout) {
        _session.value = WorkoutSession(workout = workout)
        _summary.value = null
        viewModelScope.launch {
            savedWorkoutId = workoutRepository.saveWorkout(workout)
        }
    }

    /** Called when the user finishes the current SET (not the whole exercise). */
    fun completeSet() {
        val current = _session.value ?: return
        val exercise = current.currentExercise
        val isLastSet = current.currentSet >= exercise.sets
        val isLastExercise = current.currentExerciseIndex >= current.workout.exercises.size - 1

        if (isLastSet && isLastExercise) {
            finishWorkout()
        } else {
            startRestTimer(exercise.restSeconds)
        }
    }

    fun skipRest() {
        restTimerJob?.cancel()
        advance()
    }

    private fun advance() {
        val current = _session.value ?: return
        val exercise = current.currentExercise

        if (current.currentSet < exercise.sets) {
            // Next set of the same exercise
            _session.value = current.copy(
                currentSet = current.currentSet + 1,
                isResting = false,
                restSecondsLeft = 0
            )
        } else {
            val nextIndex = current.currentExerciseIndex + 1
            if (nextIndex < current.workout.exercises.size) {
                _session.value = current.copy(
                    currentExerciseIndex = nextIndex,
                    currentSet = 1,
                    isResting = false,
                    restSecondsLeft = 0
                )
            } else {
                finishWorkout()
            }
        }
    }

    private fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _session.value = _session.value?.copy(isResting = true, restSecondsLeft = seconds)
        restTimerJob = viewModelScope.launch {
            for (i in seconds downTo 1) {
                _session.value = _session.value?.copy(restSecondsLeft = i)
                delay(1000)
            }
            advance()
        }
    }

    fun quitWorkout() {
        restTimerJob?.cancel()
        _session.value = null
    }

    private fun finishWorkout() {
        restTimerJob?.cancel()
        val current = _session.value ?: return

        // Capture the totals BEFORE clearing the session, otherwise the
        // summary screen reads a null session and reports zeros.
        val exerciseCount = current.workout.exercises.size
        val totalSets = current.workout.totalSets
        val elapsedMinutes = ((System.currentTimeMillis() - current.startTime) / 60000L).toInt()
        val points = totalSets * 5

        viewModelScope.launch {
            if (savedWorkoutId != -1L) {
                workoutRepository.markCompleted(savedWorkoutId, points)
            }
            userPreferencesRepository.recordCompletedSession(points)
            _session.value = null
            _summary.value = WorkoutSummary(
                exerciseCount = exerciseCount,
                totalSets = totalSets,
                jackedPoints = points,
                elapsedMinutes = elapsedMinutes
            )
        }
    }
}

data class WorkoutSummary(
    val exerciseCount: Int,
    val totalSets: Int,
    val jackedPoints: Int,
    val elapsedMinutes: Int
)
