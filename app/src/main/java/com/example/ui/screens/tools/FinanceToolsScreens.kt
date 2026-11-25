package com.example.ui.screens.tools

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.tools.DiscountCalculator
import com.example.domain.tools.GstCalculator
import com.example.domain.tools.LoanCalculator
import com.example.domain.tools.NumberFormatter
import com.example.domain.tools.PercentageCalculator
import com.example.domain.tools.PercentageMode
import com.example.domain.tools.TipCalculator
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassNumberField
import com.example.ui.components.MetricBreakdownRow
import com.example.ui.components.ToolResultHeroCard
import com.example.ui.components.ToolScreenHeader
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalSmartCalcColors
import com.example.ui.util.ResultActions
import kotlin.math.roundToInt

// 1. Percentage Calculator Screen
@Composable
fun PercentageToolScreen(
    onBack: () -> Unit,
    onSaveHistory: (expression: String, result: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val ext = LocalSmartCalcColors.current
    var mode by remember { mutableStateOf(PercentageMode.PERCENT_OF) }
    var xInput by remember { mutableStateOf("15") }
    var yInput by remember { mutableStateOf("240") }

    val result = PercentageCalculator.calculate(
        mode = mode,
        x = xInput.toDoubleOrNull(),
        y = yInput.toDoubleOrNull()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 620.dp)) {
            ToolScreenHeader(
                title = "Percentage",
                subtitle = "Ratios, shares & percentage change",
                icon = Icons.Rounded.Percent,
                onBack = onBack
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PercentageMode.entries.forEach { item ->
                        FilterChip(
                            selected = mode == item,
                            onClick = { mode = item },
                            label = { Text(item.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ext.accentCyan.copy(alpha = 0.2f),
                                selectedLabelColor = ext.accentCyan
                            )
                        )
                    }
                }

                GlassCard(contentPadding = PaddingValues(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = mode.subtitle,
                            style = MaterialTheme.typography.labelLarge,
                            color = ext.accentCyan
                        )
                        val (xLabel, yLabel) = when (mode) {
                            PercentageMode.PERCENT_OF -> "Percentage (X%)" to "Total Value (Y)"
                            PercentageMode.WHAT_PERCENT -> "Part Value (X)" to "Total Value (Y)"
                            PercentageMode.PERCENT_CHANGE -> "Original Value (X)" to "New Value (Y)"
                        }
                        GlassNumberField(
                            value = xInput,
                            onValueChange = { xInput = it },
                            label = xLabel,
                            suffixText = if (mode == PercentageMode.PERCENT_OF) "%" else null,
                            testTag = "pct_x_input"
                        )
                        GlassNumberField(
                            value = yInput,
                            onValueChange = { yInput = it },
                            label = yLabel,
                            testTag = "pct_y_input"
                        )
                    }
                }

                ToolResultHeroCard(
                    title = "Calculated Result",
                    primaryValue = result?.primaryValue ?: "—",
                    subtitle = result?.formulaSummary ?: "Enter valid numbers above",
                    onCopy = {
                        result?.let { ResultActions.copyToClipboard(context, "Percentage", it.primaryValue) }
                    },
                    onShare = {
                        result?.let {
                            ResultActions.shareResult(
                                context,
                                "SmartCalc Percentage",
                                "${it.formulaSummary}\n${it.secondaryNote}"
                            )
                        }
                    },
                    onSaveHistory = {
                        result?.let {
                            onSaveHistory(it.formulaSummary.substringBefore(" = "), it.primaryValue, "Percentage")
                            Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                        }
                    },
                    extraContent = if (result != null) {
                        {
                            MetricBreakdownRow(
                                label = "Details",
                                value = result.secondaryNote,
                                valueColor = ext.accentCyan
                            )
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// 2. Discount Calculator Screen
@Composable
fun DiscountToolScreen(
    onBack: () -> Unit,
    onSaveHistory: (expression: String, result: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val ext = LocalSmartCalcColors.current
    var priceInput by remember { mutableStateOf("120.00") }
    var discountInput by remember { mutableStateOf("25") }
    var taxInput by remember { mutableStateOf("0") }

    val result = DiscountCalculator.calculate(
        originalPrice = priceInput.toDoubleOrNull(),
        discountPercent = discountInput.toDoubleOrNull(),
        extraTaxPercent = taxInput.toDoubleOrNull() ?: 0.0
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 620.dp)) {
            ToolScreenHeader(
                title = "Discount",
                subtitle = "Final sale price & instant savings",
                icon = Icons.Rounded.LocalOffer,
                onBack = onBack
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GlassCard(contentPadding = PaddingValues(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassNumberField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            label = "Original Price",
                            testTag = "discount_price_input"
                        )
                        GlassNumberField(
                            value = discountInput,
                            onValueChange = { discountInput = it },
                            label = "Discount (%)",
                            suffixText = "%",
                            testTag = "discount_pct_input"
                        )
                        // Quick Discount Presets
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("10", "15", "20", "25", "30", "50").forEach { preset ->
                                FilterChip(
                                    selected = discountInput == preset,
                                    onClick = { discountInput = preset },
                                    label = { Text("$preset%") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ext.accentPurple.copy(alpha = 0.22f),
                                        selectedLabelColor = ext.accentPurple
                                    )
                                )
                            }
                        }
                        GlassNumberField(
                            value = taxInput,
                            onValueChange = { taxInput = it },
                            label = "Additional Sales Tax (%) - Optional",
                            suffixText = "%",
                            testTag = "discount_tax_input"
                        )
                    }
                }

                ToolResultHeroCard(
                    title = "Final Payable Price",
                    primaryValue = result?.let { NumberFormatter.formatFixed(it.finalPrice, 2) } ?: "—",
                    subtitle = result?.let {
                        "You save ${NumberFormatter.formatFixed(it.discountAmount, 2)} (${NumberFormatter.format(it.effectiveSavingsPercent, 1)}% net off)"
                    } ?: "Enter price and discount",
                    onCopy = {
                        result?.let {
                            ResultActions.copyToClipboard(
                                context,
                                "Final Price",
                                NumberFormatter.formatFixed(it.finalPrice, 2)
                            )
                        }
                    },
                    onShare = {
                        result?.let {
                            ResultActions.shareResult(
                                context,
                                "Discount Summary",
                                "Original: $priceInput\nDiscount: $discountInput%\nSaved: ${NumberFormatter.formatFixed(it.discountAmount, 2)}\nFinal Price: ${NumberFormatter.formatFixed(it.finalPrice, 2)}"
                            )
                        }
                    },
                    onSaveHistory = {
                        result?.let {
                            onSaveHistory(
                                "$priceInput − $discountInput% (+$taxInput% tax)",
                                NumberFormatter.formatFixed(it.finalPrice, 2),
                                "Discount"
                            )
                            Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                        }
                    },
                    extraContent = if (result != null) {
                        {
                            val discountRatio = (discountInput.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 100.0) / 100.0
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(ext.accentBlue.copy(alpha = 0.2f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(discountRatio.toFloat())
                                            .fillMaxHeight()
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(ext.successGreen, ext.accentCyan)
                                                )
                                            )
                                    )
                                }
                                MetricBreakdownRow(
                                    label = "Total Savings",
                                    value = NumberFormatter.formatFixed(result.discountAmount, 2),
                                    valueColor = ext.successGreen
                                )
                                if (result.taxAmount > 0.0) {
                                    MetricBreakdownRow(
                                        label = "Sales Tax Added",
                                        value = NumberFormatter.formatFixed(result.taxAmount, 2),
                                        valueColor = ext.warningAmber
                                    )
                                }
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// 3. GST & VAT Calculator Screen
@Composable
fun GstVatToolScreen(
    onBack: () -> Unit,
    onSaveHistory: (expression: String, result: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val ext = LocalSmartCalcColors.current
    var amountInput by remember { mutableStateOf("1000") }
    var rateInput by remember { mutableStateOf("18") }
    var isInclusive by remember { mutableStateOf(false) }

    val result = GstCalculator.calculate(
        amount = amountInput.toDoubleOrNull(),
        ratePercent = rateInput.toDoubleOrNull(),
        isInclusive = isInclusive
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 620.dp)) {
            ToolScreenHeader(
                title = "GST & VAT",
                subtitle = "Inclusive & exclusive tax breakdown",
                icon = Icons.Rounded.ReceiptLong,
                onBack = onBack
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = !isInclusive,
                        onClick = { isInclusive = false },
                        label = { Text("Add Tax (Exclusive)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ext.accentCyan.copy(alpha = 0.2f),
                            selectedLabelColor = ext.accentCyan
                        )
                    )
                    FilterChip(
                        selected = isInclusive,
                        onClick = { isInclusive = true },
                        label = { Text("Extract Tax (Inclusive)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ext.accentPurple.copy(alpha = 0.22f),
                            selectedLabelColor = ext.accentPurple
                        )
                    )
                }

                GlassCard(contentPadding = PaddingValues(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassNumberField(
                            value = amountInput,
                            onValueChange = { amountInput = it },
                            label = if (isInclusive) "Gross Total Amount" else "Base Net Amount",
                            testTag = "gst_amount_input"
                        )
                        GlassNumberField(
                            value = rateInput,
                            onValueChange = { rateInput = it },
                            label = "Tax Rate (%)",
                            suffixText = "%",
                            testTag = "gst_rate_input"
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("5", "10", "12", "15", "18", "20", "28").forEach { slab ->
                                FilterChip(
                                    selected = rateInput == slab,
                                    onClick = { rateInput = slab },
                                    label = { Text("$slab%") }
                                )
                            }
                        }
                    }
                }

                ToolResultHeroCard(
                    title = if (isInclusive) "Pre-Tax Net Amount" else "Gross Total (Inc. Tax)",
                    primaryValue = result?.let {
                        NumberFormatter.formatFixed(if (isInclusive) it.netAmount else it.grossTotal, 2)
                    } ?: "—",
                    subtitle = result?.let {
                        "Tax (${rateInput}%): ${NumberFormatter.formatFixed(it.taxAmount, 2)}"
                    } ?: "Enter amount and tax rate",
                    onCopy = {
                        result?.let {
                            val valCopy = if (isInclusive) it.netAmount else it.grossTotal
                            ResultActions.copyToClipboard(context, "GST/VAT", NumberFormatter.formatFixed(valCopy, 2))
                        }
                    },
                    onShare = {
                        result?.let {
                            ResultActions.shareResult(
                                context,
                                "GST/VAT Breakdown",
                                "Net: ${NumberFormatter.formatFixed(it.netAmount, 2)}\nTax ($rateInput%): ${NumberFormatter.formatFixed(it.taxAmount, 2)}\nGross Total: ${NumberFormatter.formatFixed(it.grossTotal, 2)}"
                            )
                        }
                    },
                    onSaveHistory = {
                        result?.let {
                            val modeLabel = if (isInclusive) "Incl. GST" else "+ GST"
                            val finalVal = if (isInclusive) it.netAmount else it.grossTotal
                            onSaveHistory(
                                "$amountInput ($rateInput% $modeLabel)",
                                NumberFormatter.formatFixed(finalVal, 2),
                                "GST/VAT"
                            )
                            Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                        }
                    },
                    extraContent = if (result != null) {
                        {
                            Column {
                                MetricBreakdownRow(
                                    label = "Net Amount",
                                    value = NumberFormatter.formatFixed(result.netAmount, 2)
                                )
                                MetricBreakdownRow(
                                    label = "Total Tax ($rateInput%)",
                                    value = NumberFormatter.formatFixed(result.taxAmount, 2),
                                    valueColor = ext.accentCyan
                                )
                                MetricBreakdownRow(
                                    label = "CGST / SGST (50% each)",
                                    value = "${NumberFormatter.formatFixed(result.halfTaxAmount, 2)} × 2"
                                )
                                MetricBreakdownRow(
                                    label = "Gross Total",
                                    value = NumberFormatter.formatFixed(result.grossTotal, 2),
                                    valueColor = ext.successGreen
                                )
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// 4. Loan & EMI Calculator Screen
@Composable
fun LoanEmiToolScreen(
    onBack: () -> Unit,
    onSaveHistory: (expression: String, result: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val ext = LocalSmartCalcColors.current
    var principalInput by remember { mutableStateOf("50000") }
    var rateInput by remember { mutableStateOf("7.5") }
    var tenureInput by remember { mutableStateOf("5") }
    var tenureInYears by remember { mutableStateOf(true) }

    val months = ((tenureInput.toDoubleOrNull() ?: 0.0) * if (tenureInYears) 12 else 1).roundToInt()
    val result = LoanCalculator.calculate(
        principal = principalInput.toDoubleOrNull(),
        annualRatePercent = rateInput.toDoubleOrNull(),
        tenureMonths = months
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 620.dp)) {
            ToolScreenHeader(
                title = "Loan & EMI",
                subtitle = "Monthly installment & interest breakdown",
                icon = Icons.Rounded.AccountBalance,
                onBack = onBack
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GlassCard(contentPadding = PaddingValues(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassNumberField(
                            value = principalInput,
                            onValueChange = { principalInput = it },
                            label = "Loan Principal Amount",
                            testTag = "loan_principal_input"
                        )
                        GlassNumberField(
                            value = rateInput,
                            onValueChange = { rateInput = it },
                            label = "Annual Interest Rate (%)",
                            suffixText = "% p.a.",
                            testTag = "loan_rate_input"
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassNumberField(
                                value = tenureInput,
                                onValueChange = { tenureInput = it },
                                label = "Tenure",
                                modifier = Modifier.weight(1f),
                                testTag = "loan_tenure_input"
                            )
                            FilterChip(
                                selected = tenureInYears,
                                onClick = { tenureInYears = true },
                                label = { Text("Years") }
                            )
                            FilterChip(
                                selected = !tenureInYears,
                                onClick = { tenureInYears = false },
                                label = { Text("Months") }
                            )
                        }
                    }
                }

                ToolResultHeroCard(
                    title = "Monthly EMI ($months months)",
                    primaryValue = result?.let { NumberFormatter.formatFixed(it.monthlyEmi, 2) } ?: "—",
                    subtitle = result?.let {
                        "Total Payable: ${NumberFormatter.formatFixed(it.totalPayment, 2)}"
                    } ?: "Enter loan details above",
                    onCopy = {
                        result?.let {
                            ResultActions.copyToClipboard(
                                context,
                                "Monthly EMI",
                                NumberFormatter.formatFixed(it.monthlyEmi, 2)
                            )
                        }
                    },
                    onShare = {
                        result?.let {
                            ResultActions.shareResult(
                                context,
                                "Loan & EMI Summary",
                                "Principal: $principalInput\nRate: $rateInput% for $months mos\nMonthly EMI: ${NumberFormatter.formatFixed(it.monthlyEmi, 2)}\nTotal Interest: ${NumberFormatter.formatFixed(it.totalInterest, 2)}\nTotal Payment: ${NumberFormatter.formatFixed(it.totalPayment, 2)}"
                            )
                        }
                    },
                    onSaveHistory = {
                        result?.let {
                            onSaveHistory(
                                "Loan $principalInput @ $rateInput% ($months mo)",
                                "EMI ${NumberFormatter.formatFixed(it.monthlyEmi, 2)}/mo",
                                "Loan/EMI"
                            )
                            Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                        }
                    },
                    extraContent = if (result != null) {
                        {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Visual Principal vs Interest Bar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(result.principalRatio.coerceAtLeast(0.01f))
                                            .fillMaxHeight()
                                            .background(ext.accentCyan)
                                    )
                                    val interestWeight = (1f - result.principalRatio).coerceAtLeast(0.01f)
                                    Box(
                                        modifier = Modifier
                                            .weight(interestWeight)
                                            .fillMaxHeight()
                                            .background(ext.accentPurple)
                                    )
                                }
                                MetricBreakdownRow(
                                    label = "Principal Amount",
                                    value = NumberFormatter.formatFixed(result.principalAmount, 2),
                                    valueColor = ext.accentCyan
                                )
                                MetricBreakdownRow(
                                    label = "Total Interest",
                                    value = NumberFormatter.formatFixed(result.totalInterest, 2),
                                    valueColor = ext.accentPurple
                                )
                                MetricBreakdownRow(
                                    label = "Total Payment",
                                    value = NumberFormatter.formatFixed(result.totalPayment, 2),
                                    valueColor = ext.successGreen
                                )
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// 5. Tip Calculator Screen
@Composable
fun TipToolScreen(
    onBack: () -> Unit,
    onSaveHistory: (expression: String, result: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val ext = LocalSmartCalcColors.current
    var billInput by remember { mutableStateOf("85.00") }
    var tipPercent by remember { mutableDoubleStateOf(15.0) }
    var splitCount by remember { mutableIntStateOf(2) }

    val result = TipCalculator.calculate(
        billAmount = billInput.toDoubleOrNull(),
        tipPercent = tipPercent,
        splitPeople = splitCount
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 620.dp)) {
            ToolScreenHeader(
                title = "Tip & Split",
                subtitle = "Calculate gratuity & split the bill fairly",
                icon = Icons.Rounded.Restaurant,
                onBack = onBack
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GlassCard(contentPadding = PaddingValues(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        GlassNumberField(
                            value = billInput,
                            onValueChange = { billInput = it },
                            label = "Bill Subtotal",
                            testTag = "tip_bill_input"
                        )

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tip Percentage", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "${tipPercent.roundToInt()}%",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = ext.accentCyan
                                )
                            }
                            Slider(
                                value = tipPercent.toFloat(),
                                onValueChange = { tipPercent = it.roundToInt().toDouble() },
                                valueRange = 0f..35f,
                                colors = SliderDefaults.colors(
                                    thumbColor = ext.accentCyan,
                                    activeTrackColor = ext.accentCyan
                                )
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(10.0, 15.0, 18.0, 20.0, 25.0).forEach { pct ->
                                    FilterChip(
                                        selected = tipPercent == pct,
                                        onClick = { tipPercent = pct },
                                        label = { Text("${pct.toInt()}%") }
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Split Between", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "$splitCount ${if (splitCount == 1) "person" else "people"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = CircleShape,
                                    color = ext.keyFunctionSurface,
                                    border = BorderStroke(1.dp, ext.glassBorderStart)
                                ) {
                                    IconButton(onClick = { if (splitCount > 1) splitCount-- }) {
                                        Icon(Icons.Rounded.Remove, contentDescription = "Decrease people")
                                    }
                                }
                                Text(
                                    text = "$splitCount",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = JetBrainsMonoFontFamily
                                    )
                                )
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = CircleShape,
                                    color = ext.keyOperatorSurface,
                                    border = BorderStroke(1.dp, ext.accentCyan.copy(alpha = 0.5f))
                                ) {
                                    IconButton(onClick = { if (splitCount < 50) splitCount++ }) {
                                        Icon(Icons.Rounded.Add, contentDescription = "Increase people", tint = ext.accentCyan)
                                    }
                                }
                            }
                        }
                    }
                }

                ToolResultHeroCard(
                    title = "Per Person Total",
                    primaryValue = result?.let { NumberFormatter.formatFixed(it.perPersonTotal, 2) } ?: "—",
                    subtitle = result?.let {
                        "Total Bill: ${NumberFormatter.formatFixed(it.totalBill, 2)} (Tip: ${NumberFormatter.formatFixed(it.tipAmount, 2)})"
                    } ?: "Enter bill amount",
                    onCopy = {
                        result?.let {
                            ResultActions.copyToClipboard(
                                context,
                                "Per Person Total",
                                NumberFormatter.formatFixed(it.perPersonTotal, 2)
                            )
                        }
                    },
                    onShare = {
                        result?.let {
                            ResultActions.shareResult(
                                context,
                                "Bill Split",
                                "Bill: $billInput\nTip (${tipPercent.toInt()}%): ${NumberFormatter.formatFixed(it.tipAmount, 2)}\nTotal: ${NumberFormatter.formatFixed(it.totalBill, 2)}\nSplit ($splitCount): ${NumberFormatter.formatFixed(it.perPersonTotal, 2)} each"
                            )
                        }
                    },
                    onSaveHistory = {
                        result?.let {
                            onSaveHistory(
                                "Bill $billInput + ${tipPercent.toInt()}% tip ÷ $splitCount",
                                "${NumberFormatter.formatFixed(it.perPersonTotal, 2)} / person",
                                "Tip"
                            )
                            Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                        }
                    },
                    extraContent = if (result != null) {
                        {
                            Column {
                                MetricBreakdownRow(
                                    label = "Total Tip",
                                    value = NumberFormatter.formatFixed(result.tipAmount, 2),
                                    valueColor = ext.accentCyan
                                )
                                MetricBreakdownRow(
                                    label = "Tip per Person",
                                    value = NumberFormatter.formatFixed(result.perPersonTip, 2)
                                )
                                MetricBreakdownRow(
                                    label = "Grand Total",
                                    value = NumberFormatter.formatFixed(result.totalBill, 2),
                                    valueColor = ext.successGreen
                                )
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
