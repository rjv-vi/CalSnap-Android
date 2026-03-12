package com.calsnap.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.calsnap.app.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "calsnap_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ONBOARDING_DONE   = booleanPreferencesKey("onboarding_done")
        val NAME              = stringPreferencesKey("name")
        val GENDER            = stringPreferencesKey("gender")
        val DATE_OF_BIRTH     = stringPreferencesKey("dob")
        val HEIGHT_CM         = floatPreferencesKey("height_cm")
        val WEIGHT_KG         = floatPreferencesKey("weight_kg")
        val ACTIVITY_LEVEL    = stringPreferencesKey("activity_level")
        val GOAL              = stringPreferencesKey("goal")
        val PREFERENCES       = stringPreferencesKey("diet_prefs")  // comma-separated
        val TARGET_KCAL       = intPreferencesKey("target_kcal")
        val TARGET_WATER_ML   = intPreferencesKey("target_water_ml")
        val GEMINI_API_KEY    = stringPreferencesKey("gemini_api_key")
        val THEME             = stringPreferencesKey("theme")       // "light" | "dark"
        val STREAK            = intPreferencesKey("streak")
        val LAST_LOG_DATE     = stringPreferencesKey("last_log_date")
    }

    val user: Flow<User?> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            val name = prefs[Keys.NAME] ?: return@map null
            User(
                name           = name,
                gender         = Gender.valueOf(prefs[Keys.GENDER] ?: "MALE"),
                dateOfBirth    = prefs[Keys.DATE_OF_BIRTH] ?: "2000-01-01",
                heightCm       = prefs[Keys.HEIGHT_CM] ?: 170f,
                weightKg       = prefs[Keys.WEIGHT_KG] ?: 70f,
                activityLevel  = ActivityLevel.valueOf(prefs[Keys.ACTIVITY_LEVEL] ?: "MODERATE"),
                goal           = Goal.valueOf(prefs[Keys.GOAL] ?: "MAINTAIN"),
                preferences    = (prefs[Keys.PREFERENCES] ?: "")
                    .split(",")
                    .filter { it.isNotBlank() }
                    .mapNotNull { runCatching { DietPref.valueOf(it) }.getOrNull() }
                    .toSet(),
                targetKcal     = prefs[Keys.TARGET_KCAL] ?: 2000,
                targetWaterMl  = prefs[Keys.TARGET_WATER_ML] ?: 2000,
                geminiApiKey   = prefs[Keys.GEMINI_API_KEY] ?: "",
            )
        }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_DONE]  = true
            prefs[Keys.NAME]             = user.name
            prefs[Keys.GENDER]           = user.gender.name
            prefs[Keys.DATE_OF_BIRTH]    = user.dateOfBirth
            prefs[Keys.HEIGHT_CM]        = user.heightCm
            prefs[Keys.WEIGHT_KG]        = user.weightKg
            prefs[Keys.ACTIVITY_LEVEL]   = user.activityLevel.name
            prefs[Keys.GOAL]             = user.goal.name
            prefs[Keys.PREFERENCES]      = user.preferences.joinToString(",") { it.name }
            prefs[Keys.TARGET_KCAL]      = user.targetKcal
            prefs[Keys.TARGET_WATER_ML]  = user.targetWaterMl
            prefs[Keys.GEMINI_API_KEY]   = user.geminiApiKey
        }
    }

    suspend fun isOnboardingDone(): Boolean {
        return context.dataStore.data.first()[Keys.ONBOARDING_DONE] ?: false
    }

    suspend fun saveGeminiKey(key: String) {
        context.dataStore.edit { it[Keys.GEMINI_API_KEY] = key }
    }

    val theme: Flow<String> = context.dataStore.data
        .map { it[Keys.THEME] ?: "light" }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { it[Keys.THEME] = theme }
    }
}
