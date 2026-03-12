package com.calsnap.app.domain.repository

import com.calsnap.app.domain.model.FoodEntry
import com.calsnap.app.domain.model.NutritionSummary
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getEntriesForDate(dateStr: String): Flow<List<FoodEntry>>
    fun getNutritionForDate(dateStr: String): Flow<NutritionSummary>
    suspend fun addEntry(entry: FoodEntry): Long
    suspend fun deleteEntry(id: Long)
    suspend fun updateEntry(entry: FoodEntry)
    fun getFavourites(): Flow<List<FoodEntry>>
    suspend fun setFavourite(id: Long, isFav: Boolean)
}
