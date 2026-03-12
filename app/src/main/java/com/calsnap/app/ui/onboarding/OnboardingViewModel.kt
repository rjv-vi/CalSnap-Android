package com.calsnap.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calsnap.app.domain.model.*
import com.calsnap.app.domain.repository.UserRepository
import com.calsnap.app.domain.usecase.CalculateTdeeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val step: Int = 0,              // 0..5
    val name: String = "",
    val gender: Gender = Gender.MALE,
    val dateOfBirth: String = "",   // "YYYY-MM-DD"
    val heightCm: Float = 170f,
    val weightKg: Float = 70f,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val goal: Goal = Goal.MAINTAIN,
    val preferences: Set<DietPref> = emptySet(),
    val isFinishing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val calculateTdee: CalculateTdeeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    fun setName(v: String)                  = _state.update { it.copy(name = v, error = null) }
    fun setGender(v: Gender)                = _state.update { it.copy(gender = v) }
    fun setDateOfBirth(v: String)           = _state.update { it.copy(dateOfBirth = v) }
    fun setHeight(v: Float)                 = _state.update { it.copy(heightCm = v) }
    fun setWeight(v: Float)                 = _state.update { it.copy(weightKg = v) }
    fun setActivityLevel(v: ActivityLevel)  = _state.update { it.copy(activityLevel = v) }
    fun setGoal(v: Goal)                    = _state.update { it.copy(goal = v) }

    fun togglePref(pref: DietPref) = _state.update {
        val prefs = it.preferences.toMutableSet()
        if (prefs.contains(pref)) prefs.remove(pref) else prefs.add(pref)
        it.copy(preferences = prefs)
    }

    fun nextStep() {
        val s = _state.value
        when (s.step) {
            0 -> {
                if (s.name.isBlank()) {
                    _state.update { it.copy(error = "Введи имя") }
                    return
                }
            }
            1 -> {
                if (s.dateOfBirth.isBlank()) {
                    _state.update { it.copy(error = "Выбери дату рождения") }
                    return
                }
            }
        }
        _state.update { it.copy(step = it.step + 1, error = null) }
    }

    fun prevStep() = _state.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }

    fun finish(onDone: () -> Unit) {
        val s = _state.value
        _state.update { it.copy(isFinishing = true) }

        viewModelScope.launch {
            val tdee = calculateTdee.calculate(
                gender        = s.gender,
                dateOfBirth   = s.dateOfBirth,
                heightCm      = s.heightCm,
                weightKg      = s.weightKg,
                activityLevel = s.activityLevel,
                goal          = s.goal
            )

            userRepository.saveUser(
                User(
                    name          = s.name,
                    gender        = s.gender,
                    dateOfBirth   = s.dateOfBirth,
                    heightCm      = s.heightCm,
                    weightKg      = s.weightKg,
                    activityLevel = s.activityLevel,
                    goal          = s.goal,
                    preferences   = s.preferences,
                    targetKcal    = tdee.targetKcal,
                    targetWaterMl = tdee.targetWaterMl,
                )
            )
            onDone()
        }
    }
}
