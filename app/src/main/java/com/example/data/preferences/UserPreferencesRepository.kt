package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smartcalc_prefs")

enum class AngleUnit {
    DEG,
    RAD
}

data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val hapticFeedback: Boolean = true,
    val angleUnit: AngleUnit = AngleUnit.DEG,
    val decimalPrecision: Int = 8,
    val thousandsSeparator: Boolean = true,
    val scientificByDefault: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val ANGLE_UNIT = stringPreferencesKey("angle_unit")
        val DECIMAL_PRECISION = intPreferencesKey("decimal_precision")
        val THOUSANDS_SEPARATOR = booleanPreferencesKey("thousands_separator")
        val SCIENTIFIC_BY_DEFAULT = booleanPreferencesKey("scientific_by_default")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val theme = prefs[Keys.THEME_MODE]?.let {
            runCatching { AppThemeMode.valueOf(it) }.getOrNull()
        } ?: AppThemeMode.DARK

        val angle = prefs[Keys.ANGLE_UNIT]?.let {
            runCatching { AngleUnit.valueOf(it) }.getOrNull()
        } ?: AngleUnit.DEG

        UserPreferences(
            themeMode = theme,
            hapticFeedback = prefs[Keys.HAPTIC_FEEDBACK] ?: true,
            angleUnit = angle,
            decimalPrecision = (prefs[Keys.DECIMAL_PRECISION] ?: 8).coerceIn(2, 10),
            thousandsSeparator = prefs[Keys.THOUSANDS_SEPARATOR] ?: true,
            scientificByDefault = prefs[Keys.SCIENTIFIC_BY_DEFAULT] ?: false
        )
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun setAngleUnit(unit: AngleUnit) {
        context.dataStore.edit { it[Keys.ANGLE_UNIT] = unit.name }
    }

    suspend fun setDecimalPrecision(digits: Int) {
        context.dataStore.edit { it[Keys.DECIMAL_PRECISION] = digits.coerceIn(2, 10) }
    }

    suspend fun setThousandsSeparator(enabled: Boolean) {
        context.dataStore.edit { it[Keys.THOUSANDS_SEPARATOR] = enabled }
    }

    suspend fun setScientificByDefault(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SCIENTIFIC_BY_DEFAULT] = enabled }
    }
}
