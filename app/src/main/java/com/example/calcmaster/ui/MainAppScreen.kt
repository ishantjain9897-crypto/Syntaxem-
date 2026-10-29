package com.example.calcmaster.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.calcmaster.ui.calculator.CalculatorScreen
import com.example.calcmaster.ui.explore.ExploreScreen
import com.example.calcmaster.ui.practice.PracticeScreen
import com.example.calcmaster.ui.profile.ProfileScreen
import com.example.calcmaster.ui.workspace.WorkspaceContainerScreen

enum class AppDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    WORKSPACE("Workspace", Icons.Filled.Draw, Icons.Outlined.Draw, "nav_workspace"),
    CALCULATOR("Calculator", Icons.Filled.Calculate, Icons.Outlined.Calculate, "nav_calc"),
    EXPLORE("Explore", Icons.Filled.Science, Icons.Outlined.Science, "nav_explore"),
    PRACTICE("Practice", Icons.Filled.School, Icons.Outlined.School, "nav_practice"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    var currentDestination by remember { mutableStateOf(AppDestination.CALCULATOR) }
    var calculatorPresetExpression by remember { mutableStateOf("") }
    var workspacePresetExpression by remember { mutableStateOf("x^2 - 5*x + 6") }
    var exploreInitialTab by remember { mutableIntStateOf(0) }
    var exploreSimTarget by remember { mutableStateOf("unit_circle") }
    var exploreGameTarget by remember { mutableStateOf("unit_circle_quest") }

    // Android back handler returns to Calculator
    BackHandler(enabled = currentDestination != AppDestination.CALCULATOR) {
        currentDestination = AppDestination.CALCULATOR
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_bottom_nav"),
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                AppDestination.values().forEach { destination ->
                    val selected = currentDestination == destination
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title
                            )
                        },
                        label = { Text(destination.title) },
                        modifier = Modifier.testTag(destination.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppDestination.WORKSPACE -> {
                    WorkspaceContainerScreen(
                        initialExpression = workspacePresetExpression,
                        onSendToCalculator = { expr ->
                            calculatorPresetExpression = expr
                            currentDestination = AppDestination.CALCULATOR
                        }
                    )
                }

                AppDestination.CALCULATOR -> {
                    CalculatorScreen(
                        initialExpression = calculatorPresetExpression,
                        onNavigateToSimulation = { simId ->
                            exploreSimTarget = simId
                            exploreInitialTab = 0
                            currentDestination = AppDestination.EXPLORE
                        },
                        onNavigateToGame = { gameId ->
                            exploreGameTarget = gameId
                            exploreInitialTab = 2
                            currentDestination = AppDestination.EXPLORE
                        },
                        onNavigateToFeynman = { _ ->
                            currentDestination = AppDestination.PRACTICE
                        },
                        onOpenInWorkspace = { expr ->
                            workspacePresetExpression = expr
                            currentDestination = AppDestination.WORKSPACE
                        }
                    )
                }

                AppDestination.EXPLORE -> {
                    ExploreScreen(
                        initialSubTab = exploreInitialTab,
                        initialSimTarget = exploreSimTarget,
                        initialGameTarget = exploreGameTarget,
                        onSendToCalculator = { expr ->
                            calculatorPresetExpression = expr
                            currentDestination = AppDestination.CALCULATOR
                        }
                    )
                }

                AppDestination.PRACTICE -> {
                    PracticeScreen(
                        onNavigateToGame = { gameId ->
                            exploreGameTarget = gameId
                            exploreInitialTab = 2
                            currentDestination = AppDestination.EXPLORE
                        },
                        onSendToCalculator = { expr ->
                            calculatorPresetExpression = expr
                            currentDestination = AppDestination.CALCULATOR
                        }
                    )
                }

                AppDestination.PROFILE -> {
                    ProfileScreen(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme
                    )
                }
            }
        }
    }
}
