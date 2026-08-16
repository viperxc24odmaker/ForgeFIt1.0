package com.makeforge.forgefit.domain.model

import java.util.Date

data class Jog(
    val id: Long = 0,
    val date: Date = Date(),
    val durationSeconds: Int = 0,
    val distanceKm: Float = 0f,
    val paceSecondsPerKm: Int = 0,
    val caloriesBurned: Int = 0,
    val jackedPointsEarned: Int = 0
) {
    /** "5:42 /km" */
    val paceFormatted: String
        get() = if (paceSecondsPerKm <= 0) "--:--"
        else "${paceSecondsPerKm / 60}:${(paceSecondsPerKm % 60).toString().padStart(2, '0')}"

    val durationFormatted: String
        get() = formatDuration(durationSeconds)
}

fun formatDuration(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "$h:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    else "$m:${s.toString().padStart(2, '0')}"
}

/**
 * Calories from MET values (Compendium of Physical Activities).
 * kcal = MET x bodyweight(kg) x hours
 */
fun estimateJogCalories(distanceKm: Float, durationSeconds: Int, weightKg: Float): Int {
    if (distanceKm <= 0f || durationSeconds <= 0 || weightKg <= 0f) return 0
    val paceMinPerKm = (durationSeconds / 60f) / distanceKm
    val met = when {
        paceMinPerKm < 4f -> 14.0f
        paceMinPerKm < 5f -> 11.5f
        paceMinPerKm < 6f -> 9.8f
        paceMinPerKm < 7f -> 8.3f
        paceMinPerKm < 8f -> 7.0f
        paceMinPerKm < 10f -> 6.0f
        else -> 4.5f
    }
    return (met * weightKg * (durationSeconds / 3600f)).toInt()
}
