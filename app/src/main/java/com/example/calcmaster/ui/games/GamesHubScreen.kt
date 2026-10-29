package com.example.calcmaster.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calcmaster.games.AlgebraArenaGame
import com.example.calcmaster.games.UnitCircleQuestGame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesHubScreen(
    initialGame: String = "unit_circle_quest",
    onUpdateGlobalScore: (Int) -> Unit
) {
    var selectedTab by remember {
        mutableStateOf(if (initialGame == "algebra_arena") 1 else 0) }
    val tabTitles = listOf("Unit Circle Quest", "Algebra Arena")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Class 11 Game Studio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Playable Mastery Challenges • Zero Ads • Free Forever",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.testTag("games_tab_row")
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, style = MaterialTheme.typography.labelMedium) },
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.SportsEsports else Icons.Default.Functions,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                when (selectedTab) {
                    0 -> UnitCircleQuestGame(onProgressUpdated = onUpdateGlobalScore)
                    1 -> AlgebraArenaGame(onScoreUpdated = onUpdateGlobalScore)
                }
            }
        }
    }
}
