package com.example.calcmaster.lumenflow

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
import com.example.calcmaster.lumenflow.calculator.LumenCalculatorScreen
import com.example.calcmaster.lumenflow.home.LumenHomeScreen
import com.example.calcmaster.lumenflow.lab.LumenLabScreen
import com.example.calcmaster.lumenflow.learn.LumenLearnScreen
import com.example.calcmaster.lumenflow.progress.LumenProgressScreen
import com.example.calcmaster.lumenflow.tutor.LumenContextualTutorSheet

enum class LumenNavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_lumen_home"),
    CALCULATOR("Calculator", Icons.Filled.Calculate, Icons.Outlined.Calculate, "nav_lumen_calc"),
    LEARN("Learn", Icons.Filled.School, Icons.Outlined.School, "nav_lumen_learn"),
    LAB("Lab", Icons.Filled.Science, Icons.Outlined.Science, "nav_lumen_lab"),
    PROGRESS("Progress", Icons.Filled.Insights, Icons.Outlined.Insights, "nav_lumen_progress")
}

@Composable
fun LumenFlowMainScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var currentDestination by remember { mutableStateOf(LumenNavDestination.HOME) }
    var calculatorPresetExpression by remember { mutableStateOf("") }
    var selectedLearnTopicId by remember { mutableStateOf<String?>(null) }
    var selectedLabSimId by remember { mutableStateOf("projectile") }
    var selectedLabGameId by remember { mutableStateOf<String?>(null) }

    // Contextual AI Tutor sheet state
    var showTutorSheet by remember { mutableStateOf(false) }
    var tutorContextTitle by remember { mutableStateOf("General Scientific Inquiry") }
    var tutorContextFormula by remember { mutableStateOf("") }

    // Android back handler returns to Home screen
    BackHandler(enabled = currentDestination != LumenNavDestination.HOME) {
        currentDestination = LumenNavDestination.HOME
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("lumen_bottom_nav"),
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                LumenNavDestination.values().forEach { destination ->
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
                LumenNavDestination.HOME -> {
                    LumenHomeScreen(
                        onNavigateToCalculator = {
                            currentDestination = LumenNavDestination.CALCULATOR
                        },
                        onNavigateToLearnTopic = { topicId ->
                            selectedLearnTopicId = topicId
                            currentDestination = LumenNavDestination.LEARN
                        },
                        onNavigateToLabSimulation = { simId ->
                            selectedLabSimId = simId
                            selectedLabGameId = null
                            currentDestination = LumenNavDestination.LAB
                        },
                        onNavigateToProgress = {
                            currentDestination = LumenNavDestination.PROGRESS
                        },
                        onOpenTutor = { title, formula ->
                            tutorContextTitle = title
                            tutorContextFormula = formula
                            showTutorSheet = true
                        },
                        onToggleTheme = onToggleTheme,
                        isDarkTheme = isDarkTheme
                    )
                }

                LumenNavDestination.CALCULATOR -> {
                    LumenCalculatorScreen(
                        initialExpression = calculatorPresetExpression,
                        onNavigateToLabSim = { simId ->
                            selectedLabSimId = simId
                            selectedLabGameId = null
                            currentDestination = LumenNavDestination.LAB
                        },
                        onNavigateToLabGame = { gameId ->
                            selectedLabGameId = gameId
                            currentDestination = LumenNavDestination.LAB
                        },
                        onNavigateToLabWithRange = { _ ->
                            selectedLabSimId = "projectile"
                            selectedLabGameId = null
                            currentDestination = LumenNavDestination.LAB
                        },
                        onOpenTutor = { title, formula ->
                            tutorContextTitle = title
                            tutorContextFormula = formula
                            showTutorSheet = true
                        }
                    )
                }

                LumenNavDestination.LEARN -> {
                    LumenLearnScreen(
                        initialTopicId = selectedLearnTopicId,
                        onSendToCalculator = { expr ->
                            calculatorPresetExpression = expr
                            currentDestination = LumenNavDestination.CALCULATOR
                        },
                        onLaunchSimulation = { simId ->
                            selectedLabSimId = simId
                            selectedLabGameId = null
                            currentDestination = LumenNavDestination.LAB
                        },
                        onOpenTutor = { title, formula ->
                            tutorContextTitle = title
                            tutorContextFormula = formula
                            showTutorSheet = true
                        }
                    )
                }

                LumenNavDestination.LAB -> {
                    LumenLabScreen(
                        initialSimId = selectedLabSimId,
                        initialGameId = selectedLabGameId,
                        onSendToCalculator = { expr ->
                            calculatorPresetExpression = expr
                            currentDestination = LumenNavDestination.CALCULATOR
                        },
                        onOpenTutor = { title, formula ->
                            tutorContextTitle = title
                            tutorContextFormula = formula
                            showTutorSheet = true
                        }
                    )
                }

                LumenNavDestination.PROGRESS -> {
                    LumenProgressScreen(
                        onNavigateToTopic = { topicId ->
                            selectedLearnTopicId = topicId
                            currentDestination = LumenNavDestination.LEARN
                        },
                        onOpenTutor = { title, formula ->
                            tutorContextTitle = title
                            tutorContextFormula = formula
                            showTutorSheet = true
                        }
                    )
                }
            }

            // Contextual AI Tutor Bottom Sheet
            if (showTutorSheet) {
                LumenContextualTutorSheet(
                    activeContextTitle = tutorContextTitle,
                    activeExpressionOrFormula = tutorContextFormula,
                    onDismiss = { showTutorSheet = false },
                    onSendToCalculator = { expr ->
                        calculatorPresetExpression = expr
                        currentDestination = LumenNavDestination.CALCULATOR
                    }
                )
            }
        }
    }
}
