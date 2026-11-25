package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.HistoryRepository
import com.example.data.preferences.AngleUnit
import com.example.data.preferences.UserPreferences
import com.example.data.preferences.UserPreferencesRepository
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch { preferencesRepository.setThemeMode(mode) }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setHapticFeedback(enabled) }
    }

    fun setAngleUnit(unit: AngleUnit) {
        viewModelScope.launch { preferencesRepository.setAngleUnit(unit) }
    }

    fun setDecimalPrecision(digits: Int) {
        viewModelScope.launch { preferencesRepository.setDecimalPrecision(digits) }
    }

    fun setThousandsSeparator(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setThousandsSeparator(enabled) }
    }

    fun setScientificByDefault(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setScientificByDefault(enabled) }
    }

    fun clearAllHistory() {
        viewModelScope.launch { historyRepository.clearAllHistory() }
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository,
        private val historyRepository: HistoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(preferencesRepository, historyRepository) as T
        }
    }
}
