package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalSmartCalcColors
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun FuturisticBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val ext = LocalSmartCalcColors.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ext.bgTop, ext.bgMiddle, ext.bgBottom)
                )
            )
            .drawBehind {
                // Top-left cyan ambient nebula
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ext.glowCyan, Color.Transparent),
                        center = Offset(size.width * 0.12f, size.height * 0.08f),
                        radius = size.width * 0.65f
                    ),
                    radius = size.width * 0.65f,
                    center = Offset(size.width * 0.12f, size.height * 0.08f)
                )
                // Mid-right neon purple glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ext.glowPurple, Color.Transparent),
                        center = Offset(size.width * 0.92f, size.height * 0.36f),
                        radius = size.width * 0.6f
                    ),
                    radius = size.width * 0.6f,
                    center = Offset(size.width * 0.92f, size.height * 0.36f)
                )
                // Bottom-left electric blue glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ext.glowBlue, Color.Transparent),
                        center = Offset(size.width * 0.25f, size.height * 0.88f),
                        radius = size.width * 0.7f
                    ),
                    radius = size.width * 0.7f,
                    center = Offset(size.width * 0.25f, size.height * 0.88f)
                )
            },
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    strongSurface: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val ext = LocalSmartCalcColors.current
    val shape = RoundedCornerShape(cornerRadius)
    val bgColor = if (strongSurface) ext.glassSurfaceStrong else ext.glassSurface
    Box(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(ext.glassBorderStart, ext.glassBorderEnd)
                ),
                shape = shape
            )
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun SmartCalcLogo(
    modifier: Modifier = Modifier
) {
    val ext = LocalSmartCalcColors.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Glowing geometric prism emblem
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            ext.accentCyan.copy(alpha = 0.2f),
                            ext.accentPurple.copy(alpha = 0.25f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(ext.accentCyan, ext.accentPurple)
                    ),
                    shape = RoundedCornerShape(11.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(20.dp)) {
                val w = size.width
                val h = size.height
                val stroke = Stroke(
                    width = 2.4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
                val gradient = Brush.linearGradient(
                    colors = listOf(ext.accentCyan, ext.accentBlue, ext.accentPurple)
                )
                // Sleek mathematical prism + plus/equals motif
                val path = Path().apply {
                    moveTo(w * 0.18f, h * 0.35f)
                    lineTo(w * 0.50f, h * 0.12f)
                    lineTo(w * 0.82f, h * 0.35f)
                    lineTo(w * 0.82f, h * 0.65f)
                    lineTo(w * 0.50f, h * 0.88f)
                    lineTo(w * 0.18f, h * 0.65f)
                    close()
                }
                drawPath(path = path, brush = gradient, style = stroke)
                // Inner equals / calculation bar
                drawLine(
                    brush = gradient,
                    start = Offset(w * 0.35f, h * 0.44f),
                    end = Offset(w * 0.65f, h * 0.44f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    brush = gradient,
                    start = Offset(w * 0.35f, h * 0.58f),
                    end = Offset(w * 0.65f, h * 0.58f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "SmartCalc",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ext.accentCyan, ext.accentPurple)
                            )
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "PRO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            fontSize = 9.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

enum class KeyStyle {
    DIGIT,
    OPERATOR,
    FUNCTION,
    CLEAR
}

@Composable
fun CalculatorKeyButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    keyStyle: KeyStyle = KeyStyle.DIGIT,
    hapticEnabled: Boolean = true,
    fontSize: TextUnit = 22.sp,
    cornerRadius: Dp = 20.dp,
    icon: ImageVector? = null,
    contentDescription: String = label,
    testTag: String = "key_$label"
) {
    val ext = LocalSmartCalcColors.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "key_scale"
    )

    val (bgColor, textColor, borderColors) = when (keyStyle) {
        KeyStyle.DIGIT -> Triple(
            ext.keyDigitSurface,
            MaterialTheme.colorScheme.onSurface,
            listOf(
                Color.White.copy(alpha = if (ext.isDark) 0.14f else 0.7f),
                ext.accentBlue.copy(alpha = if (ext.isDark) 0.18f else 0.12f)
            )
        )
        KeyStyle.OPERATOR -> Triple(
            ext.keyOperatorSurface,
            ext.keyOperatorText,
            listOf(
                ext.accentCyan.copy(alpha = 0.45f),
                ext.accentBlue.copy(alpha = 0.25f)
            )
        )
        KeyStyle.FUNCTION -> Triple(
            ext.keyFunctionSurface,
            ext.keyFunctionText,
            listOf(
                ext.accentPurple.copy(alpha = 0.40f),
                ext.accentBlue.copy(alpha = 0.20f)
            )
        )
        KeyStyle.CLEAR -> Triple(
            ext.keyClearSurface,
            ext.keyClearText,
            listOf(
                ext.keyClearText.copy(alpha = 0.50f),
                ext.keyClearText.copy(alpha = 0.18f)
            )
        )
    }

    val shape = RoundedCornerShape(cornerRadius)
    Surface(
        modifier = modifier
            .scale(scale)
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .clip(shape)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(colors = borderColors),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(
                    color = textColor.copy(alpha = 0.25f)
                ),
                onClick = {
                    if (hapticEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    onClick()
                }
            ),
        color = bgColor,
        shape = shape,
        tonalElevation = if (ext.isDark) 0.dp else 2.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = textColor,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = if (keyStyle == KeyStyle.DIGIT) JetBrainsMonoFontFamily else SpaceGroteskFontFamily,
                        fontWeight = if (keyStyle == KeyStyle.DIGIT) FontWeight.SemiBold else FontWeight.Bold,
                        fontSize = fontSize
                    ),
                    color = textColor,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun GlowingEqualsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hapticEnabled: Boolean = true,
    cornerRadius: Dp = 20.dp
) {
    val ext = LocalSmartCalcColors.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "equals_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "equals_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .scale(scale)
            .minimumInteractiveComponentSize()
            .testTag("key_equals")
            .drawBehind {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ext.equalsGradientStart.copy(alpha = glowAlpha),
                            ext.equalsGradientEnd.copy(alpha = glowAlpha)
                        )
                    ),
                    topLeft = Offset(-3.dp.toPx(), 2.dp.toPx()),
                    size = Size(size.width + 6.dp.toPx(), size.height + 4.dp.toPx()),
                    cornerRadius = CornerRadius(cornerRadius.toPx() + 4.dp.toPx())
                )
            }
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ext.equalsGradientStart,
                        ext.equalsGradientMid,
                        ext.equalsGradientEnd
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.45f),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = Color.White.copy(alpha = 0.35f)),
                onClick = {
                    if (hapticEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "=",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            ),
            color = Color.White
        )
    }
}

@Composable
fun ToolScreenHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ext = LocalSmartCalcColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .testTag("tool_back_button"),
            shape = CircleShape,
            color = ext.glassSurface,
            border = BorderStroke(1.dp, ext.glassBorderStart)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(ext.accentCyan.copy(alpha = 0.22f), ext.accentPurple.copy(alpha = 0.26f))
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(ext.accentCyan, ext.accentPurple)),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = ext.accentCyan,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun ToolResultHeroCard(
    title: String,
    primaryValue: String,
    subtitle: String? = null,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSaveHistory: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    extraContent: (@Composable () -> Unit)? = null
) {
    val ext = LocalSmartCalcColors.current
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        strongSurface = true,
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                    color = ext.accentCyan
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (onSaveHistory != null) {
                        IconButton(
                            onClick = onSaveHistory,
                            modifier = Modifier.testTag("tool_save_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.BookmarkAdd,
                                contentDescription = "Save to History",
                                tint = ext.accentPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.testTag("tool_copy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy result",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.testTag("tool_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share result",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = primaryValue,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (extraContent != null) {
                Spacer(modifier = Modifier.height(14.dp))
                extraContent()
            }
        }
    }
}

@Composable
fun GlassNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    prefixText: String? = null,
    suffixText: String? = null,
    placeholder: String = "0",
    testTag: String = "number_input"
) {
    val ext = LocalSmartCalcColors.current
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            val filtered = raw.filter { it.isDigit() || it == '.' || it == '-' }
            onValueChange(filtered)
        },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        prefix = if (prefixText != null) {
            { Text(prefixText, color = ext.accentCyan, fontWeight = FontWeight.Bold) }
        } else null,
        suffix = if (suffixText != null) {
            { Text(suffixText, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        textStyle = MaterialTheme.typography.titleMedium.copy(
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.SemiBold
        ),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ext.accentCyan,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
            focusedContainerColor = ext.glassSurface,
            unfocusedContainerColor = ext.glassSurface,
            focusedLabelColor = ext.accentCyan
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

@Composable
fun MetricBreakdownRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.SemiBold
            ),
            color = valueColor
        )
    }
}
