package com.calsnap.app.domain.usecase

import com.calsnap.app.domain.model.ActivityLevel
import com.calsnap.app.domain.model.Gender
import com.calsnap.app.domain.model.Goal
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

class CalculateTdeeUseCase @Inject constructor() {

    /**
     * Mifflin-St Jeor формула
     * Мужчины:  BMR = 10*вес + 6.25*рост - 5*возраст + 5
     * Женщины:  BMR = 10*вес + 6.25*рост - 5*возраст - 161
     */
    fun calculate(
        gender: Gender,
        dateOfBirth: String,    // "1990-05-20"
        heightCm: Float,
        weightKg: Float,
        activityLevel: ActivityLevel,
        goal: Goal
    ): TdeeResult {
        val age = calculateAge(dateOfBirth)

        val bmr = when (gender) {
            Gender.MALE   -> 10 * weightKg + 6.25 * heightCm - 5 * age + 5
            Gender.FEMALE -> 10 * weightKg + 6.25 * heightCm - 5 * age - 161
        }

        val tdee = (bmr * activityLevel.multiplier).toInt()
        val targetKcal = (tdee + goal.kcalDelta).coerceAtLeast(1200)

        // Целевой объём воды: 35 мл на кг веса
        val targetWaterMl = (weightKg * 35).toInt()

        return TdeeResult(
            bmr = bmr.toInt(),
            tdee = tdee,
            targetKcal = targetKcal,
            targetWaterMl = targetWaterMl,
            bmi = calculateBmi(weightKg, heightCm)
        )
    }

    private fun calculateAge(dob: String): Int {
        return try {
            val birth = LocalDate.parse(dob)
            Period.between(birth, LocalDate.now()).years
        } catch (e: Exception) {
            25 // дефолт если не удалось распарсить
        }
    }

    private fun calculateBmi(weightKg: Float, heightCm: Float): Float {
        val heightM = heightCm / 100f
        return weightKg / (heightM * heightM)
    }
}

data class TdeeResult(
    val bmr: Int,
    val tdee: Int,
    val targetKcal: Int,
    val targetWaterMl: Int,
    val bmi: Float
)
