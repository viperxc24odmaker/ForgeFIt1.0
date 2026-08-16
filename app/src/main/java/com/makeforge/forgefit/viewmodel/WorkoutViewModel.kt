package com.makeforge.forgefit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class WorkoutViewModel @Inject constructor() : ViewModel() {

    private val _session = MutableStateFlow<WorkoutSession?>(null)
    val session: StateFlow<WorkoutSession?> = _session.asStateFlow()

    private var restTimerJob: Job? = null

    fun startWorkout(workout: Workout) {
        _session.value = WorkoutSession(workout = workout)
    }

    fun completeExercise() {
        val current = _session.value ?: return
        val exercise = current.workout.exercises[current.currentExerciseIndex]
        startRestTimer(exercise.restSeconds)
    }

    fun nextExercise() {
        val current = _session.value ?: return
        restTimerJob?.cancel()
        val nextIndex = current.currentExerciseIndex + 1
        if (nextIndex < current.workout.exercises.size) {
            _session.value = current.copy(
                currentExerciseIndex = nextIndex,
                isResting = false,
                restSecondsLeft = 0
            )
        } else {
            finishWorkout()
        }
    }

    fun skipRest() {
        restTimerJob?.cancel()
        nextExercise()
    }

    private fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _session.value = _session.value?.copy(isResting = true, restSecondsLeft = seconds)
        restTimerJob = viewModelScope.launch {
            for (i in seconds downTo 1) {
                _session.value = _session.value?.copy(restSecondsLeft = i)
                delay(1000)
            }
            nextExercise()
        }
    }

    private fun finishWorkout() {
        _session.value = null
        // TODO: save to Room DB, award jacked points
    }
}
