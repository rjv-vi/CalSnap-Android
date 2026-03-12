package com.calsnap.app.data.repository

import com.calsnap.app.data.datastore.UserPreferences
import com.calsnap.app.data.local.dao.FoodLogDao
import com.calsnap.app.data.local.entity.FoodLogEntity
import com.calsnap.app.domain.model.*
import com.calsnap.app.domain.repository.FoodRepository
import com.calsnap.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val prefs: UserPreferences
) : UserRepository {
    override fun getUser() = prefs.user
    override suspend fun saveUser(user: User) = prefs.saveUser(user)
    override suspend fun isOnboardingDone() = prefs.isOnboardingDone()
}

@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val dao: FoodLogDao
) : FoodRepository {

    override fun getEntriesForDate(dateStr: String): Flow<List<FoodEntry>> =
        dao.getEntriesForDate(dateStr).map { list -> list.map { it.toDomain() } }

    override fun getNutritionForDate(dateStr: String): Flow<NutritionSummary> =
        dao.getEntriesForDate(dateStr).map { list ->
            val entries = list.map { it.toDomain() }
            NutritionSummary(
                kcal      = entries.sumOf { it.kcal },
                proteinG  = entries.sumOf { it.proteinG.toDouble() }.toFloat(),
                carbsG    = entries.sumOf { it.carbsG.toDouble() }.toFloat(),
                fatG      = entries.sumOf { it.fatG.toDouble() }.toFloat(),
                entries   = entries
            )
        }

    override suspend fun addEntry(entry: FoodEntry): Long =
        dao.insert(entry.toEntity())

    override suspend fun deleteEntry(id: Long) = dao.deleteById(id)

    override suspend fun updateEntry(entry: FoodEntry) = dao.update(entry.toEntity())

    override fun getFavourites(): Flow<List<FoodEntry>> =
        dao.getFavourites().map { list -> list.map { it.toDomain() } }

    override suspend fun setFavourite(id: Long, isFav: Boolean) =
        dao.setFavourite(id, isFav)
}

// ── Mappers ─────────────────────────────────────────────────────────────────

fun FoodLogEntity.toDomain() = FoodEntry(
    id         = id,
    name       = name,
    kcal       = kcal,
    proteinG   = proteinG,
    carbsG     = carbsG,
    fatG       = fatG,
    quantity   = quantity,
    unit       = unit,
    mealType   = MealType.valueOf(mealType),
    dateStr    = dateStr,
    timeStr    = timeStr,
    isFavourite = isFavourite,
    source     = EntrySource.valueOf(source),
)

fun FoodEntry.toEntity() = FoodLogEntity(
    id          = id,
    name        = name,
    kcal        = kcal,
    proteinG    = proteinG,
    carbsG      = carbsG,
    fatG        = fatG,
    quantity    = quantity,
    unit        = unit,
    mealType    = mealType.name,
    dateStr     = dateStr,
    timeStr     = timeStr,
    isFavourite = isFavourite,
    source      = source.name,
)
