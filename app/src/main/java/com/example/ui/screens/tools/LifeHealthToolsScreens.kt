package com.example.ui.screens.tools

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.tools.AgeCalculator
import com.example.domain.tools.BmiCalculator
import com.example.domain.tools.BmiCategory
import com.example.domain.tools.NumberFormatter
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassNumberField
import com.example.ui.components.MetricBreakdownRow
import com.example.ui.components.ToolResultHeroCard
import com.example.ui.components.ToolScreenHeader
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalSmartCalcColors
import com.example.ui.util.ResultActions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeToolScreen(
    onBack: () -> Unit,
    onSaveHistory: (expression: String, result: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val ext = LocalSmartCalcColors.current

    val defaultBirthMillis = remember {
        Calendar.getInstance().apply {
            set(2000, Calendar.JANUARY, 15, 0, 0, 0)
        }.timeInMillis
    }
    var birthMillis by remember { mutableLongStateOf(defaultBirthMillis) }
    var targetMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var pickingBirth by remember { mutableStateOf(false) }
    var pickingTarget by remember { mutableStateOf(false) }

    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    }
    val ageResult = AgeCalculator.calculate(birthMillis, targetMillis)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 620.dp)) {
            ToolScreenHeader(
                title = "Age Calculator",
                subtitle = "Exact chronological age & birthday countdown",
                icon = Icons.Rounded.Cake,
                onBack = onBack
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GlassCard(contentPadding = PaddingValues(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DatePickRow(
                            label = "Date of Birth",
                            dateText = dateFormatter.format(Date(birthMillis)),
                            onClick = { pickingBirth = true },
                            testTag = "age_dob_picker"
                        )
                        DatePickRow(
                            label = "Calculate Age On",
                            dateText = dateFormatter.format(Date(targetMillis)),
                            onClick = { pickingTarget = true },
                            testTag = "age_target_picker"
                        )
                    }
                }

                ToolResultHeroCard(
                    title = "Exact Chronological Age",
                    primaryValue = ageResult?.let {
                        "${it.years}y  ${it.months}m  ${it.days}d"
                    } ?: "Invalid Dates",
                    subtitle = ageResult?.let {
                        if (it.daysUntilNextBirthday == 0) "Happy Birthday Today!"
                        else "Next birthday in ${it.daysUntilNextBirthday} days (${it.nextBirthdayDayOfWeek})"
                    } ?: "Birth date must be before target date",
                    onCopy = {
                        ageResult?.let {
                            ResultActions.copyToClipboard(
                                context,
                                "Age",
                                "${it.years} years, ${it.months} months, ${it.days} days"
                            )
                        }
                    },
                    onShare = {
                        ageResult?.let {
                            ResultActions.shareResult(
                                context,
                                "Age Summary",
                                "Born: ${dateFormatter.format(Date(birthMillis))}\nAge: ${it.years} years, ${it.months} months, ${it.days} days\nTotal Days: ${NumberFormatter.format(it.totalDays.toDouble(), 0)}\nNext birthday in ${it.daysUntilNextBirthday} days"
                            )
                        }
                    },
                    onSaveHistory = {
                        ageResult?.let {
                            onSaveHistory(
                                "DOB ${dateFormatter.format(Date(birthMillis))}",
                                "${it.years}y ${it.months}m ${it.days}d",
                                "Age"
                            )
                            Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                        }
                    },
                    extraContent = if (ageResult != null) {
                        {
                            Column {
                                MetricBreakdownRow(
                                    label = "Total Months",
                                    value = NumberFormatter.format(ageResult.totalMonths.toDouble(), 0),
                                    valueColor = ext.accentCyan
                                )
                                MetricBreakdownRow(
                                    label = "Total Weeks",
                                    value = NumberFormatter.format(ageResult.totalWeeks.toDouble(), 0)
                                )
                                MetricBreakdownRow(
                                    label = "Total Days",
                                    value = NumberFormatter.format(ageResult.totalDays.toDouble(), 0)
                                )
                                MetricBreakdownRow(
                                    label = "Total Hours",
                                    value = NumberFormatter.format(ageResult.totalHours.toDouble(), 0),
                                    valueColor = ext.accentPurple
                                )
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (pickingBirth) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = localToUtcMidnight(birthMillis))
        DatePickerDialog(
            onDismissRequest = { pickingBirth = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { birthMillis = utcToLocalMidnight(it) }
                        pickingBirth = false
                    }
                ) { Text("Select") }
            },
            dismissButton = {
                TextButton(onClick = { pickingBirth = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (pickingTarget) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = localToUtcMidnight(targetMillis))
        DatePickerDialog(
            onDismissRequest = { pickingTarget = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { targetMillis = utcToLocalMidnight(it) }
                        pickingTarget = false
                    }
                ) { Text("Select") }
            },
            dismissButton = {
                TextButton(onClick = { pickingTarget = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun DatePickRow(
    label: String,
    dateText: String,
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
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Rounded.CalendarMonth,
                contentDescription = "Pick date",
                tint = ext.accentCyan,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

private fun localToUtcMidnight(localMillis: Long): Long {
    val localCal = Calendar.getInstance().apply { timeInMillis = localMillis }
    val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(
            localCal.get(Calendar.YEAR),
            localCal.get(Calendar.MONTH),
            localCal.get(Calendar.DAY_OF_MONTH)
        )
    }
    return utcCal.timeInMillis
}

private fun utcToLocalMidnight(utcMillis: Long): Long {
    val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
    val localCal = Calendar.getInstance().apply {
        clear()
        set(
            utcCal.get(Calendar.YEAR),
            utcCal.get(Calendar.MONTH),
            utcCal.get(Calendar.DAY_OF_MONTH)
        )
    }
    return localCal.timeInMillis
}

// BMI Calculator Screen
@Composable
fun BmiToolScreen(
    onBack: () -> Unit,
    onSaveHistory: (expression: String, result: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val ext = LocalSmartCalcColors.current
    var isMetric by remember { mutableStateOf(true) }

    var weightKgInput by remember { mutableStateOf("68") }
    var heightCmInput by remember { mutableStateOf("174") }

    var weightLbsInput by remember { mutableStateOf("150") }
    var feetInput by remember { mutableStateOf("5") }
    var inchesInput by remember { mutableStateOf("9") }

    val result = if (isMetric) {
        BmiCalculator.calculateMetric(weightKgInput.toDoubleOrNull(), heightCmInput.toDoubleOrNull())
    } else {
        BmiCalculator.calculateImperial(
            weightLbsInput.toDoubleOrNull(),
            feetInput.toDoubleOrNull(),
            inchesInput.toDoubleOrNull()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 620.dp)) {
            ToolScreenHeader(
                title = "BMI Health",
                subtitle = "Body mass index & healthy weight range",
                icon = Icons.Rounded.MonitorHeart,
                onBack = onBack
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = isMetric,
                        onClick = { isMetric = true },
                        label = { Text("Metric (kg / cm)") }
                    )
                    FilterChip(
                        selected = !isMetric,
                        onClick = { isMetric = false },
                        label = { Text("Imperial (lbs / ft-in)") }
                    )
                }

                GlassCard(contentPadding = PaddingValues(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (isMetric) {
                            GlassNumberField(
                                value = weightKgInput,
                                onValueChange = { weightKgInput = it },
                                label = "Weight",
                                suffixText = "kg",
                                testTag = "bmi_weight_kg"
                            )
                            GlassNumberField(
                                value = heightCmInput,
                                onValueChange = { heightCmInput = it },
                                label = "Height",
                                suffixText = "cm",
                                testTag = "bmi_height_cm"
                            )
                        } else {
                            GlassNumberField(
                                value = weightLbsInput,
                                onValueChange = { weightLbsInput = it },
                                label = "Weight",
                                suffixText = "lbs",
                                testTag = "bmi_weight_lbs"
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GlassNumberField(
                                    value = feetInput,
                                    onValueChange = { feetInput = it },
                                    label = "Height (ft)",
                                    suffixText = "ft",
                                    modifier = Modifier.weight(1f),
                                    testTag = "bmi_height_ft"
                                )
                                GlassNumberField(
                                    value = inchesInput,
                                    onValueChange = { inchesInput = it },
                                    label = "Height (in)",
                                    suffixText = "in",
                                    modifier = Modifier.weight(1f),
                                    testTag = "bmi_height_in"
                                )
                            }
                        }
                    }
                }

                val categoryColor = when (result?.category) {
                    BmiCategory.UNDERWEIGHT -> ext.accentCyan
                    BmiCategory.NORMAL -> ext.successGreen
                    BmiCategory.OVERWEIGHT -> ext.warningAmber
                    BmiCategory.OBESE -> ext.errorRose
                    null -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                ToolResultHeroCard(
                    title = result?.category?.label ?: "Body Mass Index",
                    primaryValue = result?.let { NumberFormatter.formatFixed(it.bmi, 1) } ?: "—",
                    subtitle = result?.category?.advice ?: "Enter valid height and weight",
                    onCopy = {
                        result?.let {
                            ResultActions.copyToClipboard(
                                context,
                                "BMI",
                                "BMI ${NumberFormatter.formatFixed(it.bmi, 1)} (${it.category.label})"
                            )
                        }
                    },
                    onShare = {
                        result?.let {
                            ResultActions.shareResult(
                                context,
                                "BMI Result",
                                "BMI: ${NumberFormatter.formatFixed(it.bmi, 1)} (${it.category.label})\nHealthy weight range: ${NumberFormatter.format(it.minHealthyKg, 1)} – ${NumberFormatter.format(it.maxHealthyKg, 1)} kg"
                            )
                        }
                    },
                    onSaveHistory = {
                        result?.let {
                            val expr = if (isMetric) "${weightKgInput}kg, ${heightCmInput}cm" else "${weightLbsInput}lbs, ${feetInput}ft ${inchesInput}in"
                            onSaveHistory(
                                "BMI ($expr)",
                                "${NumberFormatter.formatFixed(it.bmi, 1)} (${it.category.label})",
                                "BMI"
                            )
                            Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                        }
                    },
                    extraContent = if (result != null) {
                        {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Spectrum bar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                ) {
                                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(ext.accentCyan))
                                    Box(modifier = Modifier.weight(1.4f).fillMaxHeight().background(ext.successGreen))
                                    Box(modifier = Modifier.weight(1.1f).fillMaxHeight().background(ext.warningAmber))
                                    Box(modifier = Modifier.weight(1.2f).fillMaxHeight().background(ext.errorRose))
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = categoryColor.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "Status: ${result.category.label}",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = categoryColor,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }

                                MetricBreakdownRow(
                                    label = "Healthy Weight Range",
                                    value = "${NumberFormatter.format(result.minHealthyKg, 1)} – ${NumberFormatter.format(result.maxHealthyKg, 1)} kg",
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
