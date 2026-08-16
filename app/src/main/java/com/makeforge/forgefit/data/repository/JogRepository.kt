package com.makeforge.forgefit.data.repository

import com.makeforge.forgefit.data.local.dao.JogDao
import com.makeforge.forgefit.data.local.entity.JogEntity
import com.makeforge.forgefit.domain.model.Jog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JogRepository @Inject constructor(
    private val jogDao: JogDao
) {
    val allJogs: Flow<List<Jog>> = jogDao.getAllJogs().map { list -> list.map { it.toJog() } }
    val totalDistanceKm: Flow<Float> = jogDao.getTotalDistance()
    val jogCount: Flow<Int> = jogDao.getJogCount()
    val bestPaceSecondsPerKm: Flow<Int?> = jogDao.getBestPace()

    suspend fun saveJog(jog: Jog): Long = jogDao.insert(jog.toEntity())
}

private fun JogEntity.toJog() = Jog(
    id = id,
    date = Date(dateMs),
    durationSeconds = durationSeconds,
    distanceKm = distanceKm,
    paceSecondsPerKm = paceSecondsPerKm,
    caloriesBurned = caloriesBurned,
    jackedPointsEarned = jackedPointsEarned
)

private fun Jog.toEntity() = JogEntity(
    id = id,
    dateMs = date.time,
    durationSeconds = durationSeconds,
    distanceKm = distanceKm,
    paceSecondsPerKm = paceSecondsPerKm,
    caloriesBurned = caloriesBurned,
    jackedPointsEarned = jackedPointsEarned
)
