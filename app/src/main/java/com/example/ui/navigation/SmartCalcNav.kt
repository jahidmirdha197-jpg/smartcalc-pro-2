package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.FuturisticBackdrop
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartToolType
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.tools.AgeToolScreen
import com.example.ui.screens.tools.BmiToolScreen
import com.example.ui.screens.tools.CurrencyToolScreen
import com.example.ui.screens.tools.DiscountToolScreen
import com.example.ui.screens.tools.GstVatToolScreen
import com.example.ui.screens.tools.LoanEmiToolScreen
import com.example.ui.screens.tools.PercentageToolScreen
import com.example.ui.screens.tools.TipToolScreen
import com.example.ui.screens.tools.UnitToolScreen
import com.example.ui.theme.LocalSmartCalcColors
import com.example.ui.viewmodel.CalculatorViewModel
import com.example.ui.viewmodel.CurrencyViewModel
import com.example.ui.viewmodel.HistoryViewModel
import com.example.ui.viewmodel.SettingsViewModel

sealed class TopLevelDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : TopLevelDestination("home", "Home", Icons.Rounded.Calculate, Icons.Outlined.Calculate)
    data object Tools : TopLevelDestination("tools", "Tools", Icons.Rounded.GridView, Icons.Outlined.GridView)
    data object History : TopLevelDestination("history", "History", Icons.Rounded.History, Icons.Outlined.History)
    data object Settings : TopLevelDestination("settings", "Settings", Icons.Rounded.Settings, Icons.Outlined.Settings)
}

private val topLevelItems = listOf(
    TopLevelDestination.Home,
    TopLevelDestination.Tools,
    TopLevelDestination.History,
    TopLevelDestination.Settings
)

@Composable
fun SmartCalcRoot(
    calculatorViewModel: CalculatorViewModel,
    historyViewModel: HistoryViewModel,
    settingsViewModel: SettingsViewModel,
    currencyViewModel: CurrencyViewModel
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showBottomBar = topLevelItems.any { it.route == currentRoute }

    val calcState by calculatorViewModel.uiState.collectAsStateWithLifecycle()
    val prefs by settingsViewModel.preferences.collectAsStateWithLifecycle()
    val historyState by historyViewModel.uiState.collectAsStateWithLifecycle()
    val ext = LocalSmartCalcColors.current

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    FuturisticBackdrop {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showBottomBar) {
                    Column(
                        modifier = Modifier.background(ext.navBarSurface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            ext.glassBorderStart.copy(alpha = 0.6f),
                                            ext.glassBorderEnd.copy(alpha = 0.6f)
                                        )
                                    )
                                )
                        )
                        Box(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                tonalElevation = 0.dp,
                                windowInsets = WindowInsets(0, 0, 0, 0)
                            ) {
                                topLevelItems.forEach { dest ->
                                    val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { navigateTopLevel(dest.route) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                                contentDescription = dest.label
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = dest.label,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = ext.accentCyan,
                                            selectedTextColor = ext.accentCyan,
                                            indicatorColor = ext.accentCyan.copy(alpha = 0.16f),
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("nav_${dest.route}")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            val topInsetModifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .let { mod ->
                    if (!showBottomBar) mod.windowInsetsPadding(WindowInsets.navigationBars) else mod
                }

            NavHost(
                navController = navController,
                startDestination = TopLevelDestination.Home.route,
                modifier = topInsetModifier
            ) {
                composable(TopLevelDestination.Home.route) {
                    HomeScreen(
                        uiState = calcState,
                        preferences = prefs,
                        viewModel = calculatorViewModel,
                        onOpenHistory = { navigateTopLevel(TopLevelDestination.History.route) },
                        onOpenSettings = { navigateTopLevel(TopLevelDestination.Settings.route) }
                    )
                }

                composable(TopLevelDestination.Tools.route) {
                    ToolsScreen(
                        onToolSelected = { tool ->
                            navController.navigate(tool.route)
                        }
                    )
                }

                composable(TopLevelDestination.History.route) {
                    HistoryScreen(
                        uiState = historyState,
                        viewModel = historyViewModel,
                        onReuseCalculation = { expr, result ->
                            calculatorViewModel.loadFromHistory(expr, result)
                            navigateTopLevel(TopLevelDestination.Home.route)
                        }
                    )
                }

                composable(TopLevelDestination.Settings.route) {
                    SettingsScreen(
                        preferences = prefs,
                        historyCount = historyState.totalCount,
                        viewModel = settingsViewModel
                    )
                }

                // Smart Tool Sub-Screens
                composable(SmartToolType.PERCENTAGE.route) {
                    PercentageToolScreen(
                        onBack = { navController.popBackStack() },
                        onSaveHistory = { expr, res, cat ->
                            historyViewModel.saveToolResult(expr, res, cat)
                        }
                    )
                }
                composable(SmartToolType.DISCOUNT.route) {
                    DiscountToolScreen(
                        onBack = { navController.popBackStack() },
                        onSaveHistory = { expr, res, cat ->
                            historyViewModel.saveToolResult(expr, res, cat)
                        }
                    )
                }
                composable(SmartToolType.GST_VAT.route) {
                    GstVatToolScreen(
                        onBack = { navController.popBackStack() },
                        onSaveHistory = { expr, res, cat ->
                            historyViewModel.saveToolResult(expr, res, cat)
                        }
                    )
                }
                composable(SmartToolType.CURRENCY.route) {
                    CurrencyToolScreen(
                        currencyViewModel = currencyViewModel,
                        onBack = { navController.popBackStack() },
                        onSaveHistory = { expr, res, cat ->
                            historyViewModel.saveToolResult(expr, res, cat)
                        }
                    )
                }
                composable(SmartToolType.UNIT.route) {
                    UnitToolScreen(
                        onBack = { navController.popBackStack() },
                        onSaveHistory = { expr, res, cat ->
                            historyViewModel.saveToolResult(expr, res, cat)
                        }
                    )
                }
                composable(SmartToolType.AGE.route) {
                    AgeToolScreen(
                        onBack = { navController.popBackStack() },
                        onSaveHistory = { expr, res, cat ->
                            historyViewModel.saveToolResult(expr, res, cat)
                        }
                    )
                }
                composable(SmartToolType.LOAN_EMI.route) {
                    LoanEmiToolScreen(
                        onBack = { navController.popBackStack() },
                        onSaveHistory = { expr, res, cat ->
                            historyViewModel.saveToolResult(expr, res, cat)
                        }
                    )
                }
                composable(SmartToolType.TIP.route) {
                    TipToolScreen(
                        onBack = { navController.popBackStack() },
                        onSaveHistory = { expr, res, cat ->
                            historyViewModel.saveToolResult(expr, res, cat)
                        }
                    )
                }
                composable(SmartToolType.BMI.route) {
                    BmiToolScreen(
                        onBack = { navController.popBackStack() },
                        onSaveHistory = { expr, res, cat ->
                            historyViewModel.saveToolResult(expr, res, cat)
                        }
                    )
                }
            }
        }
    }
}
