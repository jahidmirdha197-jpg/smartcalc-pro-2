package com.example

import android.app.Application
import com.example.data.local.HistoryRepository
import com.example.data.local.SmartCalcDatabase
import com.example.data.preferences.UserPreferencesRepository

class SmartCalcApp : Application() {
    val database: SmartCalcDatabase by lazy {
        SmartCalcDatabase.getInstance(this)
    }

    val historyRepository: HistoryRepository by lazy {
        HistoryRepository(database.calculationHistoryDao())
    }

    val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(this)
    }
}
