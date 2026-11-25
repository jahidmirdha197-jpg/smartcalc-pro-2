package com.example.ui.screens.tools

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.tools.CurrencyData
import com.example.domain.tools.CurrencyInfo
import com.example.domain.tools.NumberFormatter
import com.example.domain.tools.UnitCategory
import com.example.domain.tools.UnitConverterLogic
import com.example.domain.tools.UnitDefinition
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassNumberField
import com.example.ui.components.MetricBreakdownRow
import com.example.ui.components.ToolResultHeroCard
import com.example.ui.components.ToolScreenHeader
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalSmartCalcColors
import com.example.ui.util.ResultActions
import com.example.ui.viewmodel.CurrencyViewModel

@Composable
fun CurrencyToolScreen(
    currencyViewModel: CurrencyViewModel,
    onBack: () -> Unit,
    onSaveHistory: (expression: String, result: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val ext = LocalSmartCalcColors.current
    val state by currencyViewModel.uiState.collectAsStateWithLifecycle()

    var pickingFrom by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(state.refreshMessage) {
        val msg = state.refreshMessage
        if (msg != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            currencyViewModel.clearRefreshMessage()
        }
    }

    val fromInfo = remember(state.fromCode) {
        CurrencyData.currencies.firstOrNull { it.code == state.fromCode } ?: CurrencyData.currencies.first()
    }
    val toInfo = remember(state.toCode) {
        CurrencyData.currencies.firstOrNull { it.code == state.toCode } ?: CurrencyData.currencies[1]
    }

    val amount = state.amountInput.toDoubleOrNull()
    val converted = amount?.let {
        CurrencyData.convert(it, state.fromCode, state.toCode, state.ratesPerUsd)
    }
    val unitRate = CurrencyData.convert(1.0, state.fromCode, state.toCode, state.ratesPerUsd)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 620.dp)) {
            ToolScreenHeader(
                title = "Currency",
                subtitle = "24 global currencies • offline & live rates",
                icon = Icons.Rounded.CurrencyExchange,
                onBack = onBack
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Live Rate Status & Refresh Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (state.isLiveRates) ext.successGreen.copy(alpha = 0.16f) else ext.glassSurface,
                        border = BorderStroke(
                            1.dp,
                            if (state.isLiveRates) ext.successGreen.copy(alpha = 0.4f) else ext.glassBorderStart
                        )
                    ) {
                        Text(
                            text = state.rateStatusLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (state.isLiveRates) ext.successGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { currencyViewModel.refreshLiveRates() }
                            .testTag("currency_refresh_button"),
                        shape = RoundedCornerShape(12.dp),
                        color = ext.accentBlue.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, ext.accentCyan.copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (state.isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = ext.accentCyan
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "Refresh live rates",
                                    tint = ext.accentCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Live Rates",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = ext.accentCyan
                            )
                        }
                    }
                }

                GlassCard(contentPadding = PaddingValues(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassNumberField(
                            value = state.amountInput,
                            onValueChange = { currencyViewModel.onAmountChange(it) },
                            label = "Amount (${fromInfo.code})",
                            prefixText = "${fromInfo.symbol} ",
                            testTag = "currency_amount_input"
                        )

                        CurrencySelectorRow(
                            label = "From",
                            currency = fromInfo,
                            onClick = { pickingFrom = true },
                            testTag = "currency_from_selector"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("currency_swap_button"),
                                shape = CircleShape,
                                color = ext.keyOperatorSurface,
                                border = BorderStroke(1.dp, ext.accentCyan.copy(alpha = 0.5f))
                            ) {
                                IconButton(onClick = { currencyViewModel.swapCurrencies() }) {
                                    Icon(
                                        imageVector = Icons.Rounded.SwapVert,
                                        contentDescription = "Swap currencies",
                                        tint = ext.accentCyan
                                    )
                                }
                            }
                        }

                        CurrencySelectorRow(
                            label = "To",
                            currency = toInfo,
                            onClick = { pickingFrom = false },
                            testTag = "currency_to_selector"
                        )
                    }
                }

                ToolResultHeroCard(
                    title = "${fromInfo.code} → ${toInfo.code}",
                    primaryValue = converted?.let { "${toInfo.symbol} ${NumberFormatter.format(it, 2)}" } ?: "—",
                    subtitle = "1 ${fromInfo.code} = ${NumberFormatter.format(unitRate, 4)} ${toInfo.code}",
                    onCopy = {
                        converted?.let {
                            ResultActions.copyToClipboard(
                                context,
                                "Currency",
                                "${toInfo.symbol} ${NumberFormatter.format(it, 2)}"
                            )
                        }
                    },
                    onShare = {
                        converted?.let {
                            ResultActions.shareResult(
                                context,
                                "Currency Conversion",
                                "${state.amountInput} ${fromInfo.code} = ${NumberFormatter.format(it, 2)} ${toInfo.code}"
                            )
                        }
                    },
                    onSaveHistory = {
                        converted?.let {
                            onSaveHistory(
                                "${state.amountInput} ${fromInfo.code} → ${toInfo.code}",
                                "${toInfo.symbol} ${NumberFormatter.format(it, 2)}",
                                "Currency"
                            )
                            Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                        }
                    },
                    extraContent = {
                        val inverseRate = CurrencyData.convert(1.0, state.toCode, state.fromCode, state.ratesPerUsd)
                        MetricBreakdownRow(
                            label = "Inverse Rate",
                            value = "1 ${toInfo.code} = ${NumberFormatter.format(inverseRate, 4)} ${fromInfo.code}",
                            valueColor = ext.accentPurple
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    pickingFrom?.let { isFrom ->
        CurrencyPickerDialog(
            selectedCode = if (isFrom) state.fromCode else state.toCode,
            onSelect = { code ->
                if (isFrom) currencyViewModel.onFromCodeChange(code)
                else currencyViewModel.onToCodeChange(code)
                pickingFrom = null
            },
            onDismiss = { pickingFrom = null }
        )
    }
}

@Composable
private fun CurrencySelectorRow(
    label: String,
    currency: CurrencyInfo,
    onClick: () -> Unit,
    testTag: String
) {
    val ext = LocalSmartCalcColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = ext.keyDigitSurface,
        border = BorderStroke(1.dp, ext.glassBorderStart.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = currency.flag, fontSize = 26.sp)
                Column {
                    Text(
                        text = "$label • ${currency.code}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currency.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Choose currency",
                tint = ext.accentCyan
            )
        }
    }
}

@Composable
private fun CurrencyPickerDialog(
    selectedCode: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val ext = LocalSmartCalcColors.current
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        val q = query.trim()
        if (q.isEmpty()) CurrencyData.currencies
        else CurrencyData.currencies.filter {
            it.code.contains(q, ignoreCase = true) || it.name.contains(q, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 520.dp),
            cornerRadius = 26.dp,
            strongSurface = true,
            contentPadding = PaddingValues(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Currency", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onDismiss) { Text("Close") }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search USD, Euro, INR...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered, key = { it.code }) { item ->
                        val isSelected = item.code == selectedCode
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelect(item.code) },
                            color = if (isSelected) ext.accentCyan.copy(alpha = 0.16f) else ext.glassSurface,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(text = item.flag, fontSize = 22.sp)
                                    Column {
                                        Text(
                                            text = "${item.code} (${item.symbol})",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = "Selected",
                                        tint = ext.accentCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Unit Converter Screen
@Composable
fun UnitToolScreen(
    onBack: () -> Unit,
    onSaveHistory: (expression: String, result: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val ext = LocalSmartCalcColors.current
    var selectedCategory by remember { mutableStateOf(UnitCategory.LENGTH) }
    val units = remember(selectedCategory) { UnitConverterLogic.unitsFor(selectedCategory) }
    var fromUnit by remember(selectedCategory) { mutableStateOf(units.first()) }
    var toUnit by remember(selectedCategory) { mutableStateOf(units.getOrElse(1) { units.first() }) }
    var valueInput by remember { mutableStateOf("1") }

    val numericValue = valueInput.toDoubleOrNull()
    val convertedValue = numericValue?.let {
        UnitConverterLogic.convert(selectedCategory, it, fromUnit, toUnit)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 620.dp)) {
            ToolScreenHeader(
                title = "Unit Converter",
                subtitle = "8 categories • instant multi-unit output",
                icon = Icons.Rounded.Straighten,
                onBack = onBack
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Category Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UnitCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ext.accentCyan.copy(alpha = 0.2f),
                                selectedLabelColor = ext.accentCyan
                            )
                        )
                    }
                }

                GlassCard(contentPadding = PaddingValues(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassNumberField(
                            value = valueInput,
                            onValueChange = { valueInput = it },
                            label = "Input Value (${fromUnit.symbol})",
                            suffixText = fromUnit.symbol,
                            testTag = "unit_value_input"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UnitDropdownButton(
                                label = "From",
                                selected = fromUnit,
                                options = units,
                                onSelect = { fromUnit = it },
                                modifier = Modifier.weight(1f)
                            )

                            Surface(
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("unit_swap_button"),
                                shape = CircleShape,
                                color = ext.keyOperatorSurface,
                                border = BorderStroke(1.dp, ext.accentCyan.copy(alpha = 0.5f))
                            ) {
                                IconButton(
                                    onClick = {
                                        val tmp = fromUnit
                                        fromUnit = toUnit
                                        toUnit = tmp
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SwapVert,
                                        contentDescription = "Swap units",
                                        tint = ext.accentCyan
                                    )
                                }
                            }

                            UnitDropdownButton(
                                label = "To",
                                selected = toUnit,
                                options = units,
                                onSelect = { toUnit = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                ToolResultHeroCard(
                    title = "${fromUnit.name} → ${toUnit.name}",
                    primaryValue = convertedValue?.let { "${NumberFormatter.format(it, 6)} ${toUnit.symbol}" } ?: "—",
                    subtitle = convertedValue?.let {
                        "$valueInput ${fromUnit.symbol} = ${NumberFormatter.format(it, 6)} ${toUnit.symbol}"
                    } ?: "Enter a valid number",
                    onCopy = {
                        convertedValue?.let {
                            ResultActions.copyToClipboard(
                                context,
                                "Unit Conversion",
                                "${NumberFormatter.format(it, 6)} ${toUnit.symbol}"
                            )
                        }
                    },
                    onShare = {
                        convertedValue?.let {
                            ResultActions.shareResult(
                                context,
                                "Unit Conversion",
                                "$valueInput ${fromUnit.symbol} = ${NumberFormatter.format(it, 6)} ${toUnit.symbol}"
                            )
                        }
                    },
                    onSaveHistory = {
                        convertedValue?.let {
                            onSaveHistory(
                                "$valueInput ${fromUnit.symbol} → ${toUnit.symbol}",
                                "${NumberFormatter.format(it, 6)} ${toUnit.symbol}",
                                "Unit (${selectedCategory.label})"
                            )
                            Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Simultaneous Multi-Unit Reference Card
                if (numericValue != null) {
                    GlassCard(contentPadding = PaddingValues(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "ALL ${selectedCategory.label.uppercase()} EQUIVALENTS",
                                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.8.sp),
                                color = ext.accentPurple
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            units.forEach { target ->
                                val outVal = UnitConverterLogic.convert(selectedCategory, numericValue, fromUnit, target)
                                MetricBreakdownRow(
                                    label = "${target.name} (${target.symbol})",
                                    value = NumberFormatter.format(outVal, 5),
                                    valueColor = if (target.id == toUnit.id) ext.accentCyan else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun UnitDropdownButton(
    label: String,
    selected: UnitDefinition,
    options: List<UnitDefinition>,
    onSelect: (UnitDefinition) -> Unit,
    modifier: Modifier = Modifier
) {
    val ext = LocalSmartCalcColors.current
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { expanded = true },
            shape = RoundedCornerShape(16.dp),
            color = ext.keyDigitSurface,
            border = BorderStroke(1.dp, ext.glassBorderStart.copy(alpha = 0.55f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${selected.symbol} • ${selected.name}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = JetBrainsMonoFontFamily
                        ),
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Choose unit",
                    tint = ext.accentCyan
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { unit ->
                DropdownMenuItem(
                    text = { Text("${unit.name} (${unit.symbol})") },
                    onClick = {
                        onSelect(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
