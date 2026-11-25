package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.AngleUnit
import com.example.data.preferences.UserPreferences
import com.example.ui.components.CalculatorKeyButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowingEqualsButton
import com.example.ui.components.KeyStyle
import com.example.ui.components.SmartCalcLogo
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalSmartCalcColors
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.util.ResultActions
import com.example.ui.viewmodel.CalculatorUiState
import com.example.ui.viewmodel.CalculatorViewModel

@Composable
fun HomeScreen(
    uiState: CalculatorUiState,
    preferences: UserPreferences,
    viewModel: CalculatorViewModel,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ext = LocalSmartCalcColors.current
    val context = LocalContext.current
    val exprScroll = rememberScrollState()
    val mainScroll = rememberScrollState()

    LaunchedEffect(uiState.expression) {
        exprScroll.animateScrollTo(exprScroll.maxValue)
    }
    LaunchedEffect(uiState.mainDisplay) {
        mainScroll.animateScrollTo(mainScroll.maxValue)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 580.dp)
                .padding(horizontal = 14.dp)
        ) {
            // Top Brand Bar with SmartCalc Pro Logo + History & Settings Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SmartCalcLogo()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("home_history_button"),
                        shape = CircleShape,
                        color = ext.glassSurface,
                        border = BorderStroke(1.dp, ext.glassBorderStart)
                    ) {
                        IconButton(onClick = onOpenHistory) {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = "Calculation History",
                                tint = ext.accentCyan,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("home_settings_button"),
                        shape = CircleShape,
                        color = ext.glassSurface,
                        border = BorderStroke(1.dp, ext.glassBorderEnd)
                    ) {
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                    }
                }
            }

            // Glassmorphic Calculation Display Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (uiState.isScientificExpanded) 0.25f else 0.30f),
                cornerRadius = 26.dp,
                strongSurface = true,
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Display Top Utility Bar: Mode badges + Copy & Share actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.toggleAngleUnit() }
                                    .testTag("angle_unit_chip"),
                                color = ext.accentBlue.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, ext.accentBlue.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = if (uiState.angleUnit == AngleUnit.DEG) "DEG" else "RAD",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = ext.accentCyan,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            if (uiState.justEvaluated && !uiState.isError) {
                                Surface(
                                    color = ext.successGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "RESULT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.6.sp
                                        ),
                                        color = ext.successGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            IconButton(
                                onClick = {
                                    val copyText = if (uiState.justEvaluated && !uiState.isError) {
                                        uiState.mainDisplay
                                    } else {
                                        uiState.previewResult.removePrefix("= ").ifEmpty { uiState.mainDisplay }
                                    }
                                    ResultActions.copyToClipboard(context, "SmartCalc Result", copyText)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("display_copy_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = "Copy result",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val shareBody = if (uiState.expression.isNotBlank() && uiState.justEvaluated) {
                                        "${uiState.expression} = ${uiState.mainDisplay}"
                                    } else if (uiState.previewResult.isNotBlank()) {
                                        "${uiState.expression} ${uiState.previewResult}"
                                    } else {
                                        uiState.mainDisplay
                                    }
                                    ResultActions.shareResult(context, "SmartCalc Pro Calculation", shareBody)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("display_share_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = "Share result",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Small Expression Display above the Main Result + Large Display
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        val topExpressionText = when {
                            uiState.justEvaluated -> uiState.expression
                            uiState.expression.isNotEmpty() -> uiState.expression
                            else -> "Ready"
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(exprScroll),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = topExpressionText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = if (uiState.expression.isEmpty()) 0.55f else 0.9f
                                ),
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.testTag("expression_display")
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        val displayText = when {
                            uiState.isError -> uiState.mainDisplay
                            uiState.justEvaluated -> uiState.mainDisplay
                            uiState.previewResult.isNotEmpty() -> uiState.previewResult.removePrefix("= ")
                            else -> uiState.mainDisplay
                        }

                        val dynamicFontSize = when {
                            uiState.isError -> 24.sp
                            displayText.length > 16 -> 26.sp
                            displayText.length > 11 -> 34.sp
                            displayText.length > 8 -> 40.sp
                            else -> 46.sp
                        }

                        val displayColor = when {
                            uiState.isError -> ext.errorRose
                            uiState.justEvaluated -> MaterialTheme.colorScheme.onSurface
                            uiState.previewResult.isNotEmpty() -> ext.accentCyan
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(mainScroll),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = displayText,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = dynamicFontSize,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = displayColor,
                                maxLines = 1,
                                softWrap = false,
                                textAlign = TextAlign.End,
                                modifier = Modifier.testTag("main_result_display")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scientific Calculator Toggle + Quick Sign Toggle Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.toggleScientific() }
                        .testTag("scientific_toggle"),
                    shape = RoundedCornerShape(14.dp),
                    color = if (uiState.isScientificExpanded) {
                        ext.accentPurple.copy(alpha = 0.24f)
                    } else {
                        ext.glassSurface
                    },
                    border = BorderStroke(
                        1.dp,
                        if (uiState.isScientificExpanded) ext.accentPurple else ext.glassBorderStart
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Functions,
                            contentDescription = "Toggle scientific keypad",
                            tint = if (uiState.isScientificExpanded) ext.accentCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (uiState.isScientificExpanded) "Scientific On" else "Scientific",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = SpaceGroteskFontFamily
                            ),
                            color = if (uiState.isScientificExpanded) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = if (uiState.isScientificExpanded) {
                                Icons.Rounded.KeyboardArrowUp
                            } else {
                                Icons.Rounded.KeyboardArrowDown
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuickActionPill(
                        label = "±",
                        onClick = { viewModel.onToggleSign() },
                        testTag = "key_sign"
                    )
                    QuickActionPill(
                        label = "(",
                        onClick = { viewModel.onFunction("(") },
                        testTag = "key_open_paren"
                    )
                    QuickActionPill(
                        label = ")",
                        onClick = { viewModel.onConstant(")") },
                        testTag = "key_close_paren"
                    )
                }
            }

            // Expandable Scientific Keypad Drawer
            AnimatedVisibility(
                visible = uiState.isScientificExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ScientificPanel(
                    isSecondMode = uiState.isSecondMode,
                    hapticEnabled = preferences.hapticFeedback,
                    onToggleSecond = { viewModel.toggleSecondMode() },
                    onFunction = { viewModel.onFunction(it) },
                    onConstant = { viewModel.onConstant(it) },
                    onOperator = { viewModel.onOperator(it) },
                    onPowerTwo = { viewModel.onPowerTwo() },
                    onPowerThree = { viewModel.onPowerThree() },
                    onFactorial = { viewModel.onFactorial() },
                    onReciprocal = { viewModel.onReciprocal() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary 4x5 Calculator Keypad
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (uiState.isScientificExpanded) 0.50f else 0.70f)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                // Row 1: AC, ( ), %, ÷
                KeypadRow(modifier = Modifier.weight(1f)) {
                    CalculatorKeyButton(
                        label = "AC",
                        onClick = { viewModel.onClearAll() },
                        keyStyle = KeyStyle.CLEAR,
                        hapticEnabled = preferences.hapticFeedback,
                        fontSize = 19.sp,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        testTag = "key_ac"
                    )
                    CalculatorKeyButton(
                        label = "( )",
                        onClick = { viewModel.onSmartBracket() },
                        keyStyle = KeyStyle.OPERATOR,
                        hapticEnabled = preferences.hapticFeedback,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        testTag = "key_brackets"
                    )
                    CalculatorKeyButton(
                        label = "%",
                        onClick = { viewModel.onPercent() },
                        keyStyle = KeyStyle.OPERATOR,
                        hapticEnabled = preferences.hapticFeedback,
                        fontSize = 22.sp,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        testTag = "key_percent"
                    )
                    CalculatorKeyButton(
                        label = "÷",
                        onClick = { viewModel.onOperator("÷") },
                        keyStyle = KeyStyle.OPERATOR,
                        hapticEnabled = preferences.hapticFeedback,
                        fontSize = 26.sp,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        testTag = "key_divide"
                    )
                }

                // Row 2: 7, 8, 9, ×
                KeypadRow(modifier = Modifier.weight(1f)) {
                    CalculatorKeyButton(
                        label = "7",
                        onClick = { viewModel.onDigit("7") },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorKeyButton(
                        label = "8",
                        onClick = { viewModel.onDigit("8") },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorKeyButton(
                        label = "9",
                        onClick = { viewModel.onDigit("9") },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorKeyButton(
                        label = "×",
                        onClick = { viewModel.onOperator("×") },
                        keyStyle = KeyStyle.OPERATOR,
                        hapticEnabled = preferences.hapticFeedback,
                        fontSize = 25.sp,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        testTag = "key_multiply"
                    )
                }

                // Row 3: 4, 5, 6, −
                KeypadRow(modifier = Modifier.weight(1f)) {
                    CalculatorKeyButton(
                        label = "4",
                        onClick = { viewModel.onDigit("4") },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorKeyButton(
                        label = "5",
                        onClick = { viewModel.onDigit("5") },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorKeyButton(
                        label = "6",
                        onClick = { viewModel.onDigit("6") },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorKeyButton(
                        label = "−",
                        onClick = { viewModel.onOperator("−") },
                        keyStyle = KeyStyle.OPERATOR,
                        hapticEnabled = preferences.hapticFeedback,
                        fontSize = 26.sp,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        testTag = "key_minus"
                    )
                }

                // Row 4: 1, 2, 3, +
                KeypadRow(modifier = Modifier.weight(1f)) {
                    CalculatorKeyButton(
                        label = "1",
                        onClick = { viewModel.onDigit("1") },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorKeyButton(
                        label = "2",
                        onClick = { viewModel.onDigit("2") },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorKeyButton(
                        label = "3",
                        onClick = { viewModel.onDigit("3") },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorKeyButton(
                        label = "+",
                        onClick = { viewModel.onOperator("+") },
                        keyStyle = KeyStyle.OPERATOR,
                        hapticEnabled = preferences.hapticFeedback,
                        fontSize = 26.sp,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        testTag = "key_plus"
                    )
                }

                // Row 5: 0, ., ⌫, = (Special Glowing Equals)
                KeypadRow(modifier = Modifier.weight(1f)) {
                    CalculatorKeyButton(
                        label = "0",
                        onClick = { viewModel.onDigit("0") },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorKeyButton(
                        label = ".",
                        onClick = { viewModel.onDecimal() },
                        hapticEnabled = preferences.hapticFeedback,
                        fontSize = 26.sp,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        testTag = "key_decimal"
                    )
                    CalculatorKeyButton(
                        label = "DEL",
                        onClick = { viewModel.onBackspace() },
                        keyStyle = KeyStyle.FUNCTION,
                        hapticEnabled = preferences.hapticFeedback,
                        icon = Icons.AutoMirrored.Rounded.Backspace,
                        contentDescription = "Delete last character",
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        testTag = "key_backspace"
                    )
                    GlowingEqualsButton(
                        onClick = { viewModel.onEquals() },
                        hapticEnabled = preferences.hapticFeedback,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadRow(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun QuickActionPill(
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    val ext = LocalSmartCalcColors.current
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = ext.glassSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ext.glassBorderEnd)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = ext.keyFunctionText,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ScientificPanel(
    isSecondMode: Boolean,
    hapticEnabled: Boolean,
    onToggleSecond: () -> Unit,
    onFunction: (String) -> Unit,
    onConstant: (String) -> Unit,
    onOperator: (String) -> Unit,
    onPowerTwo: () -> Unit,
    onPowerThree: () -> Unit,
    onFactorial: () -> Unit,
    onReciprocal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Scientific Row 1
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorKeyButton(
                label = if (isSecondMode) "1st" else "2nd",
                onClick = onToggleSecond,
                keyStyle = if (isSecondMode) KeyStyle.OPERATOR else KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 13.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "sci_2nd"
            )
            CalculatorKeyButton(
                label = if (isSecondMode) "sin⁻¹" else "sin",
                onClick = { onFunction(if (isSecondMode) "sin⁻¹(" else "sin(") },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 13.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorKeyButton(
                label = if (isSecondMode) "cos⁻¹" else "cos",
                onClick = { onFunction(if (isSecondMode) "cos⁻¹(" else "cos(") },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 13.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorKeyButton(
                label = if (isSecondMode) "tan⁻¹" else "tan",
                onClick = { onFunction(if (isSecondMode) "tan⁻¹(" else "tan(") },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 13.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorKeyButton(
                label = if (isSecondMode) "eˣ" else "ln",
                onClick = {
                    if (isSecondMode) onFunction("e^(") else onFunction("ln(")
                },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 13.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorKeyButton(
                label = if (isSecondMode) "10ˣ" else "log",
                onClick = {
                    if (isSecondMode) onFunction("10^(") else onFunction("log(")
                },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 13.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // Scientific Row 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorKeyButton(
                label = if (isSecondMode) "∛" else "√",
                onClick = { onFunction(if (isSecondMode) "∛(" else "√(") },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 16.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorKeyButton(
                label = if (isSecondMode) "x³" else "x²",
                onClick = { if (isSecondMode) onPowerThree() else onPowerTwo() },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 14.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorKeyButton(
                label = "xʸ",
                onClick = { onOperator("^") },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 14.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorKeyButton(
                label = "π",
                onClick = { onConstant("π") },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 16.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorKeyButton(
                label = "e",
                onClick = { onConstant("e") },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 15.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorKeyButton(
                label = if (isSecondMode) "|x|" else "n!",
                onClick = {
                    if (isSecondMode) onFunction("abs(") else onFactorial()
                },
                keyStyle = KeyStyle.FUNCTION,
                hapticEnabled = hapticEnabled,
                fontSize = 13.sp,
                cornerRadius = 14.dp,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // Scientific Row 3: Extra hyperbolics + 1/x
        if (isSecondMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CalculatorKeyButton(
                    label = "sinh",
                    onClick = { onFunction("sinh(") },
                    keyStyle = KeyStyle.FUNCTION,
                    hapticEnabled = hapticEnabled,
                    fontSize = 12.sp,
                    cornerRadius = 12.dp,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                CalculatorKeyButton(
                    label = "cosh",
                    onClick = { onFunction("cosh(") },
                    keyStyle = KeyStyle.FUNCTION,
                    hapticEnabled = hapticEnabled,
                    fontSize = 12.sp,
                    cornerRadius = 12.dp,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                CalculatorKeyButton(
                    label = "tanh",
                    onClick = { onFunction("tanh(") },
                    keyStyle = KeyStyle.FUNCTION,
                    hapticEnabled = hapticEnabled,
                    fontSize = 12.sp,
                    cornerRadius = 12.dp,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                CalculatorKeyButton(
                    label = "1/x",
                    onClick = onReciprocal,
                    keyStyle = KeyStyle.FUNCTION,
                    hapticEnabled = hapticEnabled,
                    fontSize = 13.sp,
                    cornerRadius = 12.dp,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }
    }
}
