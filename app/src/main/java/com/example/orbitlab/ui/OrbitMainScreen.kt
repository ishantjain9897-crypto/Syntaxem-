package com.example.orbitlab.ui

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
import com.example.orbitlab.model.FlightParameters
import com.example.orbitlab.ui.assistant.AIMissionAssistantScreen
import com.example.orbitlab.ui.calculator.OrbitCalcScreen
import com.example.orbitlab.ui.missioncontrol.MissionControlScreen
import com.example.orbitlab.ui.missions.MissionsScreen
import com.example.orbitlab.ui.simulator.FlightSimulatorScreen

enum class OrbitNavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    MISSION_CONTROL("Control", Icons.Filled.RocketLaunch, Icons.Outlined.RocketLaunch, "nav_control"),
    SIMULATOR("Flight Sim", Icons.Filled.NearMe, Icons.Outlined.NearMe, "nav_simulator"),
    CALCULATOR("Calculator", Icons.Filled.Calculate, Icons.Outlined.Calculate, "nav_calc"),
    MISSIONS("Missions", Icons.Filled.Flag, Icons.Outlined.Flag, "nav_missions"),
    ASSISTANT("Assistant", Icons.Filled.Psychology, Icons.Outlined.Psychology, "nav_assistant")
}

@Composable
fun OrbitMainScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var currentDestination by remember { mutableStateOf(OrbitNavDestination.MISSION_CONTROL) }
    var activeFlightParameters by remember { mutableStateOf(FlightParameters()) }
    var calcInitialExpression by remember { mutableStateOf("") }

    // Back handler returns to Mission Control
    BackHandler(enabled = currentDestination != OrbitNavDestination.MISSION_CONTROL) {
        currentDestination = OrbitNavDestination.MISSION_CONTROL
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("orbit_bottom_nav"),
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                OrbitNavDestination.values().forEach { destination ->
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
                OrbitNavDestination.MISSION_CONTROL -> {
                    MissionControlScreen(
                        onNavigateToSimulator = { params ->
                            activeFlightParameters = params
                            currentDestination = OrbitNavDestination.SIMULATOR
                        },
                        onNavigateToCalculator = {
                            currentDestination = OrbitNavDestination.CALCULATOR
                        },
                        onNavigateToMissions = {
                            currentDestination = OrbitNavDestination.MISSIONS
                        },
                        onNavigateToAssistant = {
                            currentDestination = OrbitNavDestination.ASSISTANT
                        },
                        onToggleTheme = onToggleTheme,
                        isDarkTheme = isDarkTheme
                    )
                }

                OrbitNavDestination.SIMULATOR -> {
                    FlightSimulatorScreen(
                        initialParams = activeFlightParameters,
                        onOpenCalculatorWithExpression = { expr ->
                            calcInitialExpression = expr
                            currentDestination = OrbitNavDestination.CALCULATOR
                        }
                    )
                }

                OrbitNavDestination.CALCULATOR -> {
                    OrbitCalcScreen(
                        initialExpression = calcInitialExpression,
                        onNavigateToSimulatorWithRange = { targetRange ->
                            activeFlightParameters = activeFlightParameters.copy(
                                initialVelocity = kotlin.math.sqrt(targetRange * 9.80665).coerceIn(1.0, 100.0)
                            )
                            currentDestination = OrbitNavDestination.SIMULATOR
                        }
                    )
                }

                OrbitNavDestination.MISSIONS -> {
                    MissionsScreen(
                        onLaunchMissionInSim = { params ->
                            activeFlightParameters = params
                            currentDestination = OrbitNavDestination.SIMULATOR
                        }
                    )
                }

                OrbitNavDestination.ASSISTANT -> {
                    AIMissionAssistantScreen(
                        onNavigateToSimulator = {
                            currentDestination = OrbitNavDestination.SIMULATOR
                        }
                    )
                }
            }
        }
    }
}
