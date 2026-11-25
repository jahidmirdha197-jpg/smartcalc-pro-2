package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.CurrencyRateService
import com.example.domain.tools.CurrencyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CurrencyUiState(
    val amountInput: String = "100",
    val fromCode: String = "USD",
    val toCode: String = "EUR",
    val ratesPerUsd: Map<String, Double> = CurrencyData.defaultRatesMap,
    val isRefreshing: Boolean = false,
    val isLiveRates: Boolean = false,
    val rateStatusLabel: String = "Reference Rates (Offline Ready)",
    val refreshMessage: String? = null
)

class CurrencyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amountInput = value) }
    }

    fun onFromCodeChange(code: String) {
        _uiState.update { it.copy(fromCode = code) }
    }

    fun onToCodeChange(code: String) {
        _uiState.update { it.copy(toCode = code) }
    }

    fun swapCurrencies() {
        _uiState.update { it.copy(fromCode = it.toCode, toCode = it.fromCode) }
    }

    fun refreshLiveRates() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true, refreshMessage = null) }
        viewModelScope.launch {
            val res = CurrencyRateService.fetchLatestRates()
            res.onSuccess { snapshot ->
                _uiState.update {
                    it.copy(
                        ratesPerUsd = snapshot.ratesPerUsd,
                        isRefreshing = false,
                        isLiveRates = true,
                        rateStatusLabel = snapshot.lastUpdatedLabel,
                        refreshMessage = "Exchange rates updated"
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        refreshMessage = "Offline mode: using built-in reference rates"
                    )
                }
            }
        }
    }

    fun clearRefreshMessage() {
        _uiState.update { it.copy(refreshMessage = null) }
    }
}
