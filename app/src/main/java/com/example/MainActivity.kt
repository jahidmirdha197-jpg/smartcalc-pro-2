package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.SmartCalcRoot
import com.example.ui.theme.SmartCalcProTheme
import com.example.ui.viewmodel.CalculatorViewModel
import com.example.ui.viewmodel.CurrencyViewModel
import com.example.ui.viewmodel.HistoryViewModel
import com.example.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as SmartCalcApp

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(
                    preferencesRepository = app.preferencesRepository,
                    historyRepository = app.historyRepository
                )
            )
            val calculatorViewModel: CalculatorViewModel = viewModel(
                factory = CalculatorViewModel.Factory(
                    historyRepository = app.historyRepository,
                    preferencesRepository = app.preferencesRepository
                )
            )
            val historyViewModel: HistoryViewModel = viewModel(
                factory = HistoryViewModel.Factory(
                    historyRepository = app.historyRepository
                )
            )
            val currencyViewModel: CurrencyViewModel = viewModel()

            val preferences by settingsViewModel.preferences.collectAsStateWithLifecycle()

            SmartCalcProTheme(themeMode = preferences.themeMode) {
                SmartCalcRoot(
                    calculatorViewModel = calculatorViewModel,
                    historyViewModel = historyViewModel,
                    settingsViewModel = settingsViewModel,
                    currencyViewModel = currencyViewModel
                )
            }
        }
    }
}
