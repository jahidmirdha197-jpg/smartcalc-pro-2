package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.components.GlassCard
import com.example.ui.theme.LocalSmartCalcColors

enum class SmartToolType(
    val route: String,
    val title: String,
    val subtitle: String,
    val categoryTag: String,
    val icon: ImageVector
) {
    PERCENTAGE("tool_percentage", "Percentage", "Find % of values & changes", "Math", Icons.Rounded.Percent),
    DISCOUNT("tool_discount", "Discount", "Final price & net savings", "Shopping", Icons.Rounded.LocalOffer),
    GST_VAT("tool_gst", "GST & VAT", "Tax inclusive & exclusive", "Finance", Icons.Rounded.ReceiptLong),
    CURRENCY("tool_currency", "Currency", "24+ global currencies", "Convert", Icons.Rounded.CurrencyExchange),
    UNIT("tool_unit", "Unit Converter", "Length, mass, temp, data", "Convert", Icons.Rounded.Straighten),
    AGE("tool_age", "Age Calculator", "Exact age & birthday timer", "Life", Icons.Rounded.Cake),
    LOAN_EMI("tool_loan", "Loan & EMI", "Monthly EMI & interest split", "Finance", Icons.Rounded.AccountBalance),
    TIP("tool_tip", "Tip & Split", "Bill tip & per-person share", "Daily", Icons.Rounded.Restaurant),
    BMI("tool_bmi", "BMI Health", "Body mass index & range", "Health", Icons.Rounded.MonitorHeart)
}

@Composable
fun ToolsScreen(
    onToolSelected: (SmartToolType) -> Unit,
    modifier: Modifier = Modifier
) {
    val ext = LocalSmartCalcColors.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredTools = remember(searchQuery) {
        val q = searchQuery.trim()
        if (q.isEmpty()) SmartToolType.entries
        else SmartToolType.entries.filter {
            it.title.contains(q, ignoreCase = true) ||
                it.subtitle.contains(q, ignoreCase = true) ||
                it.categoryTag.contains(q, ignoreCase = true)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 156.dp),
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Smart Tools",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Precision converters, financial planners & daily calculators",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search tools (EMI, GST, Currency...)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search tools",
                                tint = ext.accentCyan
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ext.accentCyan,
                            unfocusedBorderColor = ext.glassBorderStart.copy(alpha = 0.6f),
                            focusedContainerColor = ext.glassSurface,
                            unfocusedContainerColor = ext.glassSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tools_search_input")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            items(filteredTools, key = { it.route }) { tool ->
                ToolGridCard(
                    tool = tool,
                    accentIndex = tool.ordinal,
                    onClick = { onToolSelected(tool) }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ToolGridCard(
    tool: SmartToolType,
    accentIndex: Int,
    onClick: () -> Unit
) {
    val ext = LocalSmartCalcColors.current
    val accentColor = when (accentIndex % 3) {
        0 -> ext.accentCyan
        1 -> ext.accentBlue
        else -> ext.accentPurple
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("tool_card_${tool.route}"),
        cornerRadius = 22.dp,
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.24f),
                                    ext.accentPurple.copy(alpha = 0.14f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = accentColor.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(15.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.title,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = 0.14f),
                    border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = tool.categoryTag,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = tool.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
