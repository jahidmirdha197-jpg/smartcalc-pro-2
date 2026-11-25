package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.HistoryRepository
import com.example.data.preferences.AngleUnit
import com.example.data.preferences.UserPreferences
import com.example.data.preferences.UserPreferencesRepository
import com.example.domain.math.EvalResult
import com.example.domain.math.MathEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val mainDisplay: String = "0",
    val previewResult: String = "",
    val isError: Boolean = false,
    val justEvaluated: Boolean = false,
    val isScientificExpanded: Boolean = false,
    val isSecondMode: Boolean = false,
    val angleUnit: AngleUnit = AngleUnit.DEG
)

class CalculatorViewModel(
    private val historyRepository: HistoryRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private var initializedDefaults = false

    init {
        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collect { prefs ->
                _uiState.update { current ->
                    val expanded = if (!initializedDefaults) {
                        initializedDefaults = true
                        prefs.scientificByDefault
                    } else {
                        current.isScientificExpanded
                    }
                    current.copy(
                        angleUnit = prefs.angleUnit,
                        isScientificExpanded = expanded
                    )
                }
                recomputePreview()
            }
        }
    }

    fun toggleScientific() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun toggleSecondMode() {
        _uiState.update { it.copy(isSecondMode = !it.isSecondMode) }
    }

    fun toggleAngleUnit() {
        val next = if (_uiState.value.angleUnit == AngleUnit.DEG) AngleUnit.RAD else AngleUnit.DEG
        viewModelScope.launch {
            preferencesRepository.setAngleUnit(next)
        }
        _uiState.update { it.copy(angleUnit = next) }
        recomputePreview()
    }

    fun onDigit(digit: String) {
        _uiState.update { state ->
            val baseExpr = if (state.justEvaluated) "" else state.expression
            // Prevent leading multiple zeros like 00
            val nextExpr = if (baseExpr == "0" && digit == "0") {
                "0"
            } else if (baseExpr == "0" && digit != ".") {
                digit
            } else {
                baseExpr + digit
            }
            state.copy(
                expression = nextExpr,
                justEvaluated = false,
                isError = false
            )
        }
        recomputePreview()
    }

    fun onDecimal() {
        _uiState.update { state ->
            val baseExpr = if (state.justEvaluated) "" else state.expression
            val lastToken = baseExpr.takeLastWhile { it.isDigit() || it == '.' }
            if (lastToken.contains('.')) return@update state
            val prefix = if (baseExpr.isEmpty() || !baseExpr.last().isDigit()) "0." else "."
            state.copy(
                expression = baseExpr + prefix,
                justEvaluated = false,
                isError = false
            )
        }
        recomputePreview()
    }

    fun onOperator(op: String) {
        _uiState.update { state ->
            val baseExpr = if (state.justEvaluated && !state.isError) {
                state.mainDisplay.replace(",", "")
            } else {
                state.expression
            }

            if (baseExpr.isEmpty()) {
                return@update if (op == "−") {
                    state.copy(expression = "−", justEvaluated = false, isError = false)
                } else {
                    state
                }
            }

            val operators = setOf('+', '−', '-', '×', '*', '÷', '/', '^')
            val lastChar = baseExpr.last()
            val nextExpr = when {
                lastChar in operators -> {
                    if (op == "−" && lastChar != '−' && lastChar != '-') {
                        baseExpr + op
                    } else if (baseExpr.length == 1 && lastChar == '−') {
                        baseExpr
                    } else {
                        baseExpr.dropLast(1) + op
                    }
                }
                lastChar == '(' -> {
                    if (op == "−") baseExpr + op else baseExpr
                }
                else -> baseExpr + op
            }
            state.copy(
                expression = nextExpr,
                justEvaluated = false,
                isError = false
            )
        }
        recomputePreview()
    }

    fun onPercent() {
        _uiState.update { state ->
            val baseExpr = if (state.justEvaluated && !state.isError) {
                state.mainDisplay.replace(",", "")
            } else {
                state.expression
            }
            if (baseExpr.isEmpty()) return@update state
            val last = baseExpr.last()
            if (last.isDigit() || last == ')' || last == 'π' || last == 'e') {
                state.copy(expression = "$baseExpr%", justEvaluated = false, isError = false)
            } else {
                state
            }
        }
        recomputePreview()
    }

    fun onSmartBracket() {
        _uiState.update { state ->
            val baseExpr = if (state.justEvaluated && !state.isError) {
                state.mainDisplay.replace(",", "")
            } else if (state.justEvaluated) {
                ""
            } else {
                state.expression
            }

            val openCount = baseExpr.count { it == '(' }
            val closeCount = baseExpr.count { it == ')' }
            val last = baseExpr.lastOrNull()
            val canClose = openCount > closeCount &&
                last != null &&
                (last.isDigit() || last == ')' || last == 'π' || last == 'e' || last == '%' || last == '!')

            val next = if (canClose) "$baseExpr)" else "$baseExpr("
            state.copy(expression = next, justEvaluated = false, isError = false)
        }
        recomputePreview()
    }

    fun onFunction(funcPrefix: String) {
        _uiState.update { state ->
            val baseExpr = if (state.justEvaluated && !state.isError) {
                // Wrap previous result if user taps function right after '='
                val cleanPrev = state.mainDisplay.replace(",", "")
                return@update state.copy(
                    expression = "$funcPrefix$cleanPrev)",
                    justEvaluated = false,
                    isError = false
                )
            } else if (state.justEvaluated) {
                ""
            } else {
                state.expression
            }
            state.copy(
                expression = baseExpr + funcPrefix,
                justEvaluated = false,
                isError = false
            )
        }
        recomputePreview()
    }

    fun onConstant(constant: String) {
        _uiState.update { state ->
            val baseExpr = if (state.justEvaluated) "" else state.expression
            state.copy(
                expression = baseExpr + constant,
                justEvaluated = false,
                isError = false
            )
        }
        recomputePreview()
    }

    fun onPowerTwo() {
        appendSuffix("^2")
    }

    fun onPowerThree() {
        appendSuffix("^3")
    }

    fun onFactorial() {
        appendSuffix("!")
    }

    fun onReciprocal() {
        _uiState.update { state ->
            val baseExpr = if (state.justEvaluated && !state.isError) {
                state.mainDisplay.replace(",", "")
            } else {
                state.expression
            }
            if (baseExpr.isEmpty()) {
                state.copy(expression = "1÷(", justEvaluated = false, isError = false)
            } else {
                state.copy(expression = "1÷($baseExpr)", justEvaluated = false, isError = false)
            }
        }
        recomputePreview()
    }

    fun onToggleSign() {
        _uiState.update { state ->
            val baseExpr = if (state.justEvaluated && !state.isError) {
                state.mainDisplay.replace(",", "")
            } else {
                state.expression
            }
            if (baseExpr.isEmpty()) {
                return@update state.copy(expression = "−", justEvaluated = false, isError = false)
            }
            val next = if (baseExpr.startsWith("−") && baseExpr.drop(1).all { it.isDigit() || it == '.' }) {
                baseExpr.drop(1)
            } else if (baseExpr.startsWith("-") && baseExpr.drop(1).all { it.isDigit() || it == '.' }) {
                baseExpr.drop(1)
            } else if (baseExpr.all { it.isDigit() || it == '.' }) {
                "−$baseExpr"
            } else if (baseExpr.startsWith("−(") && baseExpr.endsWith(")")) {
                baseExpr.removePrefix("−(").removeSuffix(")")
            } else {
                "−($baseExpr)"
            }
            state.copy(expression = next, justEvaluated = false, isError = false)
        }
        recomputePreview()
    }

    private fun appendSuffix(suffix: String) {
        _uiState.update { state ->
            val baseExpr = if (state.justEvaluated && !state.isError) {
                state.mainDisplay.replace(",", "")
            } else {
                state.expression
            }
            if (baseExpr.isEmpty()) return@update state
            val last = baseExpr.last()
            if (last.isDigit() || last == ')' || last == 'π' || last == 'e') {
                state.copy(expression = baseExpr + suffix, justEvaluated = false, isError = false)
            } else {
                state
            }
        }
        recomputePreview()
    }

    fun onBackspace() {
        _uiState.update { state ->
            if (state.justEvaluated) {
                return@update CalculatorUiState(
                    isScientificExpanded = state.isScientificExpanded,
                    isSecondMode = state.isSecondMode,
                    angleUnit = state.angleUnit
                )
            }
            val expr = state.expression
            if (expr.isEmpty()) return@update state
            val functionSuffixes = listOf(
                "sin⁻¹(", "cos⁻¹(", "tan⁻¹(",
                "sinh(", "cosh(", "tanh(",
                "sin(", "cos(", "tan(",
                "cbrt(", "sqrt(", "abs(",
                "log(", "ln(", "√("
            )
            val matched = functionSuffixes.firstOrNull { expr.endsWith(it) }
            val nextExpr = if (matched != null) {
                expr.dropLast(matched.length)
            } else {
                expr.dropLast(1)
            }
            state.copy(expression = nextExpr, isError = false)
        }
        recomputePreview()
    }

    fun onClearAll() {
        _uiState.update { state ->
            CalculatorUiState(
                isScientificExpanded = state.isScientificExpanded,
                isSecondMode = state.isSecondMode,
                angleUnit = state.angleUnit
            )
        }
    }

    fun onEquals() {
        val state = _uiState.value
        val expr = state.expression.trim()
        if (expr.isEmpty()) return

        val prefs = preferences.value
        when (
            val eval = MathEngine.evaluate(
                expression = expr,
                angleUnit = state.angleUnit,
                decimalPrecision = prefs.decimalPrecision,
                useThousandsSeparator = prefs.thousandsSeparator
            )
        ) {
            is EvalResult.Success -> {
                val category = if (isScientificExpression(expr)) "Scientific" else "Standard"
                _uiState.update {
                    it.copy(
                        mainDisplay = eval.formatted,
                        previewResult = "",
                        isError = false,
                        justEvaluated = true
                    )
                }
                viewModelScope.launch {
                    historyRepository.saveCalculation(
                        expression = expr,
                        result = eval.formatted,
                        category = category
                    )
                }
            }
            is EvalResult.Error -> {
                _uiState.update {
                    it.copy(
                        mainDisplay = eval.message,
                        previewResult = "",
                        isError = true,
                        justEvaluated = true
                    )
                }
            }
            EvalResult.Empty -> Unit
        }
    }

    fun loadFromHistory(expression: String, result: String) {
        _uiState.update {
            it.copy(
                expression = expression,
                mainDisplay = result,
                previewResult = "",
                isError = false,
                justEvaluated = false,
                isScientificExpanded = it.isScientificExpanded || isScientificExpression(expression)
            )
        }
        recomputePreview()
    }

    private fun recomputePreview() {
        val state = _uiState.value
        val expr = state.expression
        if (expr.isEmpty() || state.justEvaluated) return

        val prefs = preferences.value
        val result = MathEngine.evaluate(
            expression = expr,
            angleUnit = state.angleUnit,
            decimalPrecision = prefs.decimalPrecision,
            useThousandsSeparator = prefs.thousandsSeparator
        )
        _uiState.update { current ->
            when (result) {
                is EvalResult.Success -> current.copy(
                    mainDisplay = expr,
                    previewResult = "= ${result.formatted}",
                    isError = false
                )
                is EvalResult.Error -> current.copy(
                    mainDisplay = expr,
                    previewResult = "",
                    isError = false
                )
                EvalResult.Empty -> current.copy(
                    mainDisplay = "0",
                    previewResult = "",
                    isError = false
                )
            }
        }
    }

    private fun isScientificExpression(expr: String): Boolean {
        val markers = listOf("sin", "cos", "tan", "ln", "log", "√", "∛", "π", "e", "^", "!", "abs")
        return markers.any { expr.contains(it) }
    }

    class Factory(
        private val historyRepository: HistoryRepository,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalculatorViewModel(historyRepository, preferencesRepository) as T
        }
    }
}
