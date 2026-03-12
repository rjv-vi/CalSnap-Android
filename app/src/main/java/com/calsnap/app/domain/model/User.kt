package com.calsnap.app.domain.model

data class User(
    val id: Int = 1,
    val name: String,
    val gender: Gender,
    val dateOfBirth: String,        // "1990-05-20"
    val heightCm: Float,
    val weightKg: Float,
    val activityLevel: ActivityLevel,
    val goal: Goal,
    val preferences: Set<DietPref>,
    val targetKcal: Int,            // рассчитанный TDEE с учётом цели
    val targetWaterMl: Int,         // рассчитанный целевой объём воды
    val geminiApiKey: String = "",
)

enum class Gender { MALE, FEMALE }

enum class ActivityLevel(val multiplier: Float, val label: String) {
    SEDENTARY(1.2f, "Сидячий"),
    LIGHT(1.375f, "Лёгкий"),
    MODERATE(1.55f, "Средний"),
    ACTIVE(1.725f, "Активный"),
    VERY_ACTIVE(1.9f, "Очень активный")
}

enum class Goal(val kcalDelta: Int, val label: String) {
    LOSE(-500, "Похудеть"),
    MAINTAIN(0, "Поддерживать"),
    GAIN(300, "Набрать массу")
}

enum class DietPref(val label: String) {
    VEGETARIAN("Вегетарианец"),
    VEGAN("Веган"),
    GLUTEN_FREE("Без глютена"),
    DAIRY_FREE("Без лактозы"),
    LOW_CARB("Низкоугл."),
    HALAL("Халяль")
}
