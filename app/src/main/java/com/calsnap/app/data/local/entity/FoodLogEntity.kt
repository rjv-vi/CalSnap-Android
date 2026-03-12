package com.calsnap.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_log")
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val kcal: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val quantity: Float,
    val unit: String,
    val mealType: String,
    val dateStr: String,
    val timeStr: String,
    val isFavourite: Boolean = false,
    val source: String = "MANUAL",
)
