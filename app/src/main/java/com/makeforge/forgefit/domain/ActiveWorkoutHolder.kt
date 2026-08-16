package com.makeforge.forgefit.domain

import com.makeforge.forgefit.domain.model.Workout
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hands the chosen workout from the Home screen to the active-workout screen.
 *
 * Needed because hiltViewModel() inside a NavHost composable is scoped to that
 * destination's back stack entry - Home and WorkoutActive would otherwise each
 * get their own WorkoutViewModel instance, so the session set on one would be
 * invisible to the other (which rendered a blank screen).
 */
@Singleton
class ActiveWorkoutHolder @Inject constructor() {
    @Volatile
    private var pending: Workout? = null

    fun set(workout: Workout) { pending = workout }

    /** Returns the pending workout exactly once, then clears it. */
    fun consume(): Workout? {
        val w = pending
        pending = null
        return w
    }
}
