package com.makeforge.forgefit.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.makeforge.forgefit.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val NAME = stringPreferencesKey("name")
        val AGE = intPreferencesKey("age")
        val WEIGHT_KG = floatPreferencesKey("weight_kg")
        val HEIGHT_CM = floatPreferencesKey("height_cm")
        val GOAL = stringPreferencesKey("goal")
        val FITNESS_LEVEL = stringPreferencesKey("fitness_level")
        val EQUIPMENT = stringPreferencesKey("equipment")
        val JACKED_SCORE = intPreferencesKey("jacked_score")
        val TOTAL_SESSIONS = intPreferencesKey("total_sessions")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LAST_SESSION_DATE = longPreferencesKey("last_session_date")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val TOTAL_JOGS = intPreferencesKey("total_jogs")
        val TOTAL_DISTANCE_KM = floatPreferencesKey("total_distance_km")
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[Keys.NAME] ?: "",
            age = prefs[Keys.AGE] ?: 0,
            weightKg = prefs[Keys.WEIGHT_KG] ?: 0f,
            heightCm = prefs[Keys.HEIGHT_CM] ?: 0f,
            goal = FitnessGoal.valueOf(prefs[Keys.GOAL] ?: FitnessGoal.BUILD_MUSCLE.name),
            fitnessLevel = FitnessLevel.valueOf(prefs[Keys.FITNESS_LEVEL] ?: FitnessLevel.BEGINNER.name),
            availableEquipment = (prefs[Keys.EQUIPMENT] ?: "")
                .split(",")
                .filter { it.isNotEmpty() }
                .mapNotNull { runCatching { Equipment.valueOf(it) }.getOrNull() },
            jackedScore = prefs[Keys.JACKED_SCORE] ?: 0,
            totalSessions = prefs[Keys.TOTAL_SESSIONS] ?: 0,
            currentStreak = prefs[Keys.CURRENT_STREAK] ?: 0,
            totalJogs = prefs[Keys.TOTAL_JOGS] ?: 0,
            totalDistanceKm = prefs[Keys.TOTAL_DISTANCE_KM] ?: 0f
        )
    }

    val isOnboarded: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.IS_ONBOARDED] ?: false
    }

    suspend fun saveProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NAME] = profile.name
            prefs[Keys.AGE] = profile.age
            prefs[Keys.WEIGHT_KG] = profile.weightKg
            prefs[Keys.HEIGHT_CM] = profile.heightCm
            prefs[Keys.GOAL] = profile.goal.name
            prefs[Keys.FITNESS_LEVEL] = profile.fitnessLevel.name
            prefs[Keys.EQUIPMENT] = profile.availableEquipment.joinToString(",") { it.name }
            prefs[Keys.IS_ONBOARDED] = true
        }
    }

    suspend fun recordCompletedJog(distanceKm: Float, jackedPointsEarned: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TOTAL_JOGS] = (prefs[Keys.TOTAL_JOGS] ?: 0) + 1
            prefs[Keys.TOTAL_DISTANCE_KM] = (prefs[Keys.TOTAL_DISTANCE_KM] ?: 0f) + distanceKm
            prefs[Keys.JACKED_SCORE] = (prefs[Keys.JACKED_SCORE] ?: 0) + jackedPointsEarned
            bumpStreak(prefs)
        }
    }

    private fun bumpStreak(prefs: MutablePreferences) {
        val lastSession = prefs[Keys.LAST_SESSION_DATE] ?: 0L
        val now = System.currentTimeMillis()
        val oneDayMs = 86_400_000L
        val currentStreak = prefs[Keys.CURRENT_STREAK] ?: 0
        prefs[Keys.CURRENT_STREAK] = when {
            lastSession == 0L -> 1
            now - lastSession < oneDayMs * 2 -> currentStreak + 1
            else -> 1
        }
        prefs[Keys.LAST_SESSION_DATE] = now
    }

    suspend fun recordCompletedSession(jackedPointsEarned: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TOTAL_SESSIONS] = (prefs[Keys.TOTAL_SESSIONS] ?: 0) + 1
            prefs[Keys.JACKED_SCORE] = (prefs[Keys.JACKED_SCORE] ?: 0) + jackedPointsEarned
            bumpStreak(prefs)
        }
    }
}
