package com.example.calcmaster.ui.simulations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calcmaster.simulations.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulationLabScreen(
    initialTab: String = "unit_circle",
    onSendToCalculator: (String) -> Unit
) {
    var selectedTab by remember {
        mutableIntStateOf(
            when (initialTab) {
                "projectile" -> 1
                "forces" -> 2
                "conics" -> 3
                "shm" -> 4
                else -> 0
            }
        )
    }

    val tabTitles = listOf(
        "Unit Circle" to Icons.Default.Circle,
        "Projectiles" to Icons.Default.RocketLaunch,
        "Forces & Motion" to Icons.Default.Speed,
        "Conics" to Icons.Default.Timeline,
        "Oscillations" to Icons.Default.GraphicEq
    )

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 8.dp,
            modifier = Modifier.testTag("sim_tab_row")
        ) {
            tabTitles.forEachIndexed { index, (title, icon) ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, style = MaterialTheme.typography.labelMedium) },
                    icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            when (selectedTab) {
                0 -> UnitCircleSimulation(onSendToCalculator = onSendToCalculator)
                1 -> ProjectileSimulation(onSendToCalculator = onSendToCalculator)
                2 -> ForcesAndMotionSimulation(onSendToCalculator = onSendToCalculator)
                3 -> ConicSectionsSimulation(onSendToCalculator = onSendToCalculator)
                4 -> SimpleHarmonicMotionSimulation(onSendToCalculator = onSendToCalculator)
            }
        }
    }
}
