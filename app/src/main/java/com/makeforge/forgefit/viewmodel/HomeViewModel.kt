package com.makeforge.forgefit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makeforge.forgefit.data.repository.UserPreferencesRepository
import com.makeforge.forgefit.domain.ActiveWorkoutHolder
import com.makeforge.forgefit.domain.model.*
import com.makeforge.forgefit.network.WorkoutAiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutAiService: WorkoutAiService,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val activeWorkoutHolder: ActiveWorkoutHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userProfile.collect { profile ->
                _uiState.update { it.copy(profile = profile) }
                if (profile.name.isNotEmpty() && _uiState.value.motivationalMessage.isEmpty()) {
                    loadMotivation(profile)
                }
            }
        }
    }

    private suspend fun loadMotivation(profile: UserProfile) {
        val result = workoutAiService.getMotivationalMessage(profile)
        _uiState.update { it.copy(motivationalMessage = result.getOrNull() ?: "TIME TO GET JACKED.") }
    }

    fun generateTodayWorkout() {
        val profile = _uiState.value.profile
        if (profile.name.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingWorkout = true, error = null) }
            val result = workoutAiService.generateWorkout(profile)
            _uiState.update {
                it.copy(
                    todayWorkout = result.getOrNull(),
                    isGeneratingWorkout = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    /** Stages the generated workout so the active-workout screen can pick it up. */
    fun handOffWorkout(): Boolean {
        val workout = _uiState.value.todayWorkout ?: return false
        activeWorkoutHolder.set(workout)
        return true
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}

data class HomeUiState(
    val profile: UserProfile = UserProfile(),
    val todayWorkout: Workout? = null,
    val motivationalMessage: String = "",
    val isGeneratingWorkout: Boolean = false,
    val error: String? = null
)
