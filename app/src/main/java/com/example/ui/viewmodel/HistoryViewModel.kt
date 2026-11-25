package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalculationHistoryEntity
import com.example.data.local.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class HistoryFilter(val label: String) {
    ALL("All"),
    FAVORITES("Favorites"),
    CALCULATOR("Calculator"),
    TOOLS("Tools")
}

data class HistoryUiState(
    val items: List<CalculationHistoryEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val totalCount: Int = 0
)

class HistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(HistoryFilter.ALL)

    val uiState: StateFlow<HistoryUiState> = combine(
        historyRepository.allHistory,
        _searchQuery,
        _selectedFilter
    ) { allItems, query, filter ->
        val filtered = allItems.filter { item ->
            val matchesFilter = when (filter) {
                HistoryFilter.ALL -> true
                HistoryFilter.FAVORITES -> item.isFavorite
                HistoryFilter.CALCULATOR -> item.category == "Standard" || item.category == "Scientific"
                HistoryFilter.TOOLS -> item.category != "Standard" && item.category != "Scientific"
            }
            val q = query.trim()
            val matchesQuery = q.isEmpty() ||
                item.expression.contains(q, ignoreCase = true) ||
                item.result.contains(q, ignoreCase = true) ||
                item.category.contains(q, ignoreCase = true)
            matchesFilter && matchesQuery
        }
        HistoryUiState(
            items = filtered,
            searchQuery = query,
            selectedFilter = filter,
            totalCount = allItems.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: HistoryFilter) {
        _selectedFilter.value = filter
    }

    fun toggleFavorite(item: CalculationHistoryEntity) {
        viewModelScope.launch {
            historyRepository.toggleFavorite(item.id, item.isFavorite)
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteEntry(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            historyRepository.clearAllHistory()
        }
    }

    fun saveToolResult(expression: String, result: String, category: String) {
        viewModelScope.launch {
            historyRepository.saveCalculation(expression, result, category)
        }
    }

    class Factory(
        private val historyRepository: HistoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(historyRepository) as T
        }
    }
}
