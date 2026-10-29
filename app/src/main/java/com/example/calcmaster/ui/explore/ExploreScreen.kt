package com.example.calcmaster.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calcmaster.arlab.ARPhysicsSensorLabScreen
import com.example.calcmaster.ui.games.GamesHubScreen
import com.example.calcmaster.ui.simulations.SimulationLabScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    initialSubTab: Int = 0,
    initialSimTarget: String = "unit_circle",
    initialGameTarget: String = "unit_circle_quest",
    onSendToCalculator: (String) -> Unit = {}
) {
    var selectedSubTab by remember { mutableIntStateOf(initialSubTab) }
    var globalScore by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedSubTab,
            modifier = Modifier.testTag("explore_sub_tabs")
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Simulations") },
                icon = { Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_simulations")
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Sensor Lab") },
                icon = { Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_sensor_lab")
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Math Games") },
                icon = { Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_math_games")
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedSubTab) {
                0 -> SimulationLabScreen(
                    initialTab = initialSimTarget,
                    onSendToCalculator = onSendToCalculator
                )
                1 -> ARPhysicsSensorLabScreen(
                    onSendGToCalc = onSendToCalculator
                )
                2 -> GamesHubScreen(
                    initialGame = initialGameTarget,
                    onUpdateGlobalScore = { newScore ->
                        globalScore = newScore
                    }
                )
            }
        }
    }
}
