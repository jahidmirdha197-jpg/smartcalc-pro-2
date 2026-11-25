package com.example.data.local

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: CalculationHistoryDao) {
    val allHistory: Flow<List<CalculationHistoryEntity>> = dao.getAllHistory()

    fun searchHistory(query: String): Flow<List<CalculationHistoryEntity>> =
        dao.searchHistory(query)

    suspend fun saveCalculation(
        expression: String,
        result: String,
        category: String = "Standard"
    ) {
        if (expression.isBlank() || result.isBlank()) return
        dao.insert(
            CalculationHistoryEntity(
                expression = expression.trim(),
                result = result.trim(),
                category = category
            )
        )
    }

    suspend fun toggleFavorite(id: Long, currentlyFavorite: Boolean) {
        dao.updateFavorite(id, !currentlyFavorite)
    }

    suspend fun deleteEntry(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAllHistory() {
        dao.clearAll()
    }
}
