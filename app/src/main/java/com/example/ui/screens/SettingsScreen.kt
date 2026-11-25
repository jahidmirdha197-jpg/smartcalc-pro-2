package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.AngleUnit
import com.example.data.preferences.UserPreferences
import com.example.ui.components.GlassCard
import com.example.ui.components.SmartCalcLogo
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalSmartCalcColors
import com.example.ui.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    historyCount: Int,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val ext = LocalSmartCalcColors.current
    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 660.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Customize appearance, precision & haptic feel",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Appearance Card (Dark & Light Themes)
            GlassCard(contentPadding = PaddingValues(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle(icon = Icons.Rounded.Brightness4, title = "Appearance Theme")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionChip(
                            label = "Dark Navy",
                            icon = Icons.Rounded.DarkMode,
                            selected = preferences.themeMode == AppThemeMode.DARK,
                            onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
                            modifier = Modifier.weight(1f),
                            testTag = "theme_dark_chip"
                        )
                        ThemeOptionChip(
                            label = "Arctic Light",
                            icon = Icons.Rounded.LightMode,
                            selected = preferences.themeMode == AppThemeMode.LIGHT,
                            onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f),
                            testTag = "theme_light_chip"
                        )
                        ThemeOptionChip(
                            label = "System",
                            icon = Icons.Rounded.Brightness4,
                            selected = preferences.themeMode == AppThemeMode.SYSTEM,
                            onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f),
                            testTag = "theme_system_chip"
                        )
                    }
                }
            }

            // Calculator Precision & Behavior
            GlassCard(contentPadding = PaddingValues(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SectionTitle(icon = Icons.Rounded.Calculate, title = "Calculator Behavior")

                    // Angle Unit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Angle Unit", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Used for trigonometric functions",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = preferences.angleUnit == AngleUnit.DEG,
                                onClick = { viewModel.setAngleUnit(AngleUnit.DEG) },
                                label = { Text("DEG") }
                            )
                            FilterChip(
                                selected = preferences.angleUnit == AngleUnit.RAD,
                                onClick = { viewModel.setAngleUnit(AngleUnit.RAD) },
                                label = { Text("RAD") }
                            )
                        }
                    }

                    // Decimal Precision
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Decimal Precision", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${preferences.decimalPrecision} digits",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ext.accentCyan
                            )
                        }
                        Slider(
                            value = preferences.decimalPrecision.toFloat(),
                            onValueChange = { viewModel.setDecimalPrecision(it.roundToInt()) },
                            valueRange = 2f..10f,
                            steps = 7,
                            colors = SliderDefaults.colors(
                                thumbColor = ext.accentCyan,
                                activeTrackColor = ext.accentCyan
                            )
                        )
                    }

                    SettingSwitchRow(
                        icon = Icons.Rounded.Vibration,
                        title = "Haptic Feedback",
                        subtitle = "Tactile vibration on keypad taps",
                        checked = preferences.hapticFeedback,
                        onCheckedChange = { viewModel.setHapticFeedback(it) },
                        testTag = "setting_haptic_switch"
                    )

                    SettingSwitchRow(
                        icon = Icons.Rounded.Calculate,
                        title = "Thousands Separator",
                        subtitle = "Format large numbers with commas (1,000)",
                        checked = preferences.thousandsSeparator,
                        onCheckedChange = { viewModel.setThousandsSeparator(it) },
                        testTag = "setting_thousands_switch"
                    )

                    SettingSwitchRow(
                        icon = Icons.Rounded.Functions,
                        title = "Scientific Mode on Launch",
                        subtitle = "Keep scientific drawer expanded at startup",
                        checked = preferences.scientificByDefault,
                        onCheckedChange = { viewModel.setScientificByDefault(it) },
                        testTag = "setting_scientific_switch"
                    )
                }
            }

            // Data Management Card
            GlassCard(contentPadding = PaddingValues(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Local Calculation History", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "$historyCount entries stored offline on device",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = historyCount > 0) { showClearDialog = true }
                            .testTag("settings_clear_history"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (historyCount > 0) ext.keyClearSurface else ext.glassSurface,
                        border = BorderStroke(
                            1.dp,
                            if (historyCount > 0) ext.errorRose.copy(alpha = 0.45f) else ext.glassBorderStart.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteSweep,
                                contentDescription = "Clear history",
                                tint = if (historyCount > 0) ext.errorRose else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Clear",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (historyCount > 0) ext.errorRose else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // About Brand Card
            GlassCard(contentPadding = PaddingValues(18.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmartCalcLogo()
                    Text(
                        text = "Version 1.0 • Precision Math, Financial & Converter Suite",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Saved History?") },
            text = { Text("Remove all $historyCount saved calculations from local storage?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                        Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Clear", color = ext.errorRose)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String) {
    val ext = LocalSmartCalcColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ext.accentCyan,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.9.sp),
            color = ext.accentCyan
        )
    }
}

@Composable
private fun ThemeOptionChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val ext = LocalSmartCalcColors.current
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) ext.accentCyan.copy(alpha = 0.18f) else ext.keyDigitSurface,
        border = BorderStroke(
            1.dp,
            if (selected) ext.accentCyan else ext.glassBorderStart.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) ext.accentCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (selected) ext.accentCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val ext = LocalSmartCalcColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ext.accentPurple,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ext.accentCyan,
                checkedTrackColor = ext.accentCyan.copy(alpha = 0.35f)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
