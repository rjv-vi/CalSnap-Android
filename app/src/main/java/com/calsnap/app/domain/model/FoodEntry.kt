package com.calsnap.app.domain.model

data class FoodEntry(
    val id: Long = 0,
    val name: String,
    val kcal: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val quantity: Float = 100f,
    val unit: String = "г",
    val mealType: MealType,
    val dateStr: String,    // "2025-03-13"
    val timeStr: String,    // "13:45"
    val isFavourite: Boolean = false,
    val source: EntrySource = EntrySource.MANUAL,
)

enum class MealType(val label: String, val emoji: String) {
    BREAKFAST("Завтрак", "🌅"),
    LUNCH("Обед", "☀️"),
    DINNER("Ужин", "🌙"),
    SNACK("Перекус", "🍎"),
    OTHER("Другое", "🍽️")
}

enum class EntrySource {
    MANUAL, AI_PHOTO, AI_TEXT, BARCODE, FAVOURITE
}

data class NutritionSummary(
    val kcal: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val entries: List<FoodEntry>
)
