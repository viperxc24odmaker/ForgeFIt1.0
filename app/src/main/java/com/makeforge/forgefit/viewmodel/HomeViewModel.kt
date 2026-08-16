package com.makeforge.forgefit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makeforge.forgefit.data.repository.UserPreferencesRepository
import com.makeforge.forgefit.domain.model.*
import com.makeforge.forgefit.network.WorkoutAiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutAiService: WorkoutAiService,
    private val userPreferencesRepository: UserPreferencesRepository
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

    fun clearError() = _uiState.update { it.copy(error = null) }
}

data class HomeUiState(
    val profile: UserProfile = UserProfile(),
    val todayWorkout: Workout? = null,
    val motivationalMessage: String = "",
    val isGeneratingWorkout: Boolean = false,
    val error: String? = null
)
