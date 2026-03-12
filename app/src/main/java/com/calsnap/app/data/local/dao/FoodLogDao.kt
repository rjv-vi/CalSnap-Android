package com.calsnap.app.data.local.dao

import androidx.room.*
import com.calsnap.app.data.local.entity.FoodLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodLogDao {

    @Query("SELECT * FROM food_log WHERE dateStr = :date ORDER BY timeStr ASC")
    fun getEntriesForDate(date: String): Flow<List<FoodLogEntity>>

    @Query("SELECT * FROM food_log WHERE isFavourite = 1 ORDER BY name ASC")
    fun getFavourites(): Flow<List<FoodLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FoodLogEntity): Long

    @Update
    suspend fun update(entry: FoodLogEntity)

    @Query("DELETE FROM food_log WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE food_log SET isFavourite = :isFav WHERE id = :id")
    suspend fun setFavourite(id: Long, isFav: Boolean)

    @Query("""
        SELECT COALESCE(SUM(kcal),0) as kcal,
               COALESCE(SUM(proteinG),0) as protein,
               COALESCE(SUM(carbsG),0) as carbs,
               COALESCE(SUM(fatG),0) as fat
        FROM food_log WHERE dateStr = :date
    """)
    fun getDailyTotals(date: String): Flow<DailyTotals>
}

data class DailyTotals(
    val kcal: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)
