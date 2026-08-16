package com.makeforge.forgefit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makeforge.forgefit.domain.model.*
import com.makeforge.forgefit.network.WorkoutAiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutAiService: WorkoutAiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // TODO: Load from DataStore
    private val mockProfile = UserProfile(
        name = "Divine",
        age = 17,
        weightKg = 70f,
        goal = FitnessGoal.BUILD_MUSCLE,
        fitnessLevel = FitnessLevel.BEGINNER,
        availableEquipment = listOf(Equipment.NONE),
        jackedScore = 420,
        totalSessions = 12,
        currentStreak = 3
    )

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val motResult = workoutAiService.getMotivationalMessage(mockProfile)
            _uiState.value = _uiState.value.copy(
                profile = mockProfile,
                motivationalMessage = motResult.getOrNull() ?: "TIME TO GET JACKED.",
                isLoading = false
            )
        }
    }

    fun generateTodayWorkout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingWorkout = true)
            val result = workoutAiService.generateWorkout(mockProfile)
            _uiState.value = _uiState.value.copy(
                todayWorkout = result.getOrNull(),
                isGeneratingWorkout = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }
}

data class HomeUiState(
    val profile: UserProfile = UserProfile(),
    val todayWorkout: Workout? = null,
    val motivationalMessage: String = "",
    val isLoading: Boolean = false,
    val isGeneratingWorkout: Boolean = false,
    val error: String? = null
)
