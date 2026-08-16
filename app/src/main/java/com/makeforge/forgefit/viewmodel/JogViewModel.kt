package com.makeforge.forgefit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makeforge.forgefit.data.repository.JogRepository
import com.makeforge.forgefit.data.repository.UserPreferencesRepository
import com.makeforge.forgefit.domain.model.Jog
import com.makeforge.forgefit.domain.model.estimateJogCalories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.math.roundToInt

enum class JogPhase { IDLE, RUNNING, PAUSED, ENTERING_DISTANCE }

@HiltViewModel
class JogViewModel @Inject constructor(
    private val jogRepository: JogRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(JogUiState())
    val state: StateFlow<JogUiState> = _state.asStateFlow()

    private var tickerJob: Job? = null
    private var weightKg: Float = 70f

    init {
        viewModelScope.launch {
            userPreferencesRepository.userProfile.collect { profile ->
                if (profile.weightKg > 0f) weightKg = profile.weightKg
                _state.update { it.copy(totalJogs = profile.totalJogs, totalDistanceKm = profile.totalDistanceKm) }
            }
        }
        viewModelScope.launch {
            jogRepository.allJogs.collect { jogs -> _state.update { it.copy(history = jogs) } }
        }
        viewModelScope.launch {
            jogRepository.bestPaceSecondsPerKm.collect { best -> _state.update { it.copy(bestPaceSecondsPerKm = best) } }
        }
    }

    fun start() {
        _state.update { it.copy(phase = JogPhase.RUNNING, elapsedSeconds = 0, savedMessage = null) }
        startTicker()
    }

    fun pause() {
        tickerJob?.cancel()
        _state.update { it.copy(phase = JogPhase.PAUSED) }
    }

    fun resume() {
        _state.update { it.copy(phase = JogPhase.RUNNING) }
        startTicker()
    }

    fun finish() {
        tickerJob?.cancel()
        _state.update { it.copy(phase = JogPhase.ENTERING_DISTANCE) }
    }

    fun cancel() {
        tickerJob?.cancel()
        _state.update { it.copy(phase = JogPhase.IDLE, elapsedSeconds = 0, distanceInput = "") }
    }

    fun onDistanceChange(value: String) {
        // Allow only digits and a single decimal point
        val filtered = value.filter { it.isDigit() || it == '.' }
        if (filtered.count { it == '.' } > 1) return
        _state.update { it.copy(distanceInput = filtered) }
    }

    fun saveJog() {
        val s = _state.value
        val distance = s.distanceInput.toFloatOrNull() ?: 0f
        if (distance <= 0f || s.elapsedSeconds <= 0) {
            _state.update { it.copy(savedMessage = "Enter the distance you covered first.") }
            return
        }

        val pace = (s.elapsedSeconds / distance).roundToInt()
        val calories = estimateJogCalories(distance, s.elapsedSeconds, weightKg)
        val points = (distance * 15).roundToInt().coerceAtLeast(10)

        viewModelScope.launch {
            jogRepository.saveJog(
                Jog(
                    date = Date(),
                    durationSeconds = s.elapsedSeconds,
                    distanceKm = distance,
                    paceSecondsPerKm = pace,
                    caloriesBurned = calories,
                    jackedPointsEarned = points
                )
            )
            userPreferencesRepository.recordCompletedJog(distance, points)
            _state.update {
                it.copy(
                    phase = JogPhase.IDLE,
                    elapsedSeconds = 0,
                    distanceInput = "",
                    savedMessage = "Jog saved. +$points Jacked points, $calories kcal burned."
                )
            }
        }
    }

    fun dismissMessage() = _state.update { it.copy(savedMessage = null) }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
    }
}

data class JogUiState(
    val phase: JogPhase = JogPhase.IDLE,
    val elapsedSeconds: Int = 0,
    val distanceInput: String = "",
    val history: List<Jog> = emptyList(),
    val totalJogs: Int = 0,
    val totalDistanceKm: Float = 0f,
    val bestPaceSecondsPerKm: Int? = null,
    val savedMessage: String? = null
) {
    /** Live pace preview while entering distance. */
    val livePaceSeconds: Int?
        get() {
            val d = distanceInput.toFloatOrNull() ?: return null
            if (d <= 0f || elapsedSeconds <= 0) return null
            return (elapsedSeconds / d).roundToInt()
        }
}
