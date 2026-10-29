package com.example.syntaxem.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcmaster.lumenflow.calculator.LumenCalculatorScreen
import com.example.core.designsystem.SyntaxemTheme
import com.example.syntaxem.data.weather.CityLocation
import com.example.syntaxem.model.VisualLibraryPipeline
import com.example.syntaxem.ui.arena.GameArenaScreen
import com.example.syntaxem.ui.chapter.ChapterMindMapScreen
import com.example.syntaxem.ui.dashboard.AcademicDashboardScreen
import com.example.syntaxem.ui.profile.SyntaxemProfileScreen
import com.example.syntaxem.ui.syllabus.SyllabusTrackerScreen

enum class SyntaxemNavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_syntaxem_home"),
    CALCULATOR("Calculator", Icons.Filled.Calculate, Icons.Outlined.Calculate, "nav_syntaxem_calc"),
    LEARN("Learn", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_syntaxem_learn"),
    GAMES("Games", Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports, "nav_syntaxem_games"),
    PROGRESS("Progress", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, "nav_syntaxem_progress")
}

@Composable
fun SyntaxemMainScreen() {
    val palette = SyntaxemTheme.palette
    var currentTab by remember { mutableStateOf(SyntaxemNavTab.HOME) }
    var activeChapterId by remember { mutableStateOf("ch_03_trigonometry") }
    var activeGameId by remember { mutableStateOf<String?>(null) }
    var calculatorPresetExpression by remember { mutableStateOf("") }
    var showProfileModal by remember { mutableStateOf(false) }

    // Back handling
    BackHandler(enabled = showProfileModal || currentTab != SyntaxemNavTab.HOME) {
        if (showProfileModal) {
            showProfileModal = false
        } else {
            currentTab = SyntaxemNavTab.HOME
        }
    }

    Scaffold(
        containerColor = palette.background,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("syntaxem_bottom_nav"),
                shape = RoundedCornerShape(32.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SyntaxemNavTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        val activeColor = when (tab) {
                            SyntaxemNavTab.GAMES -> palette.secondary
                            SyntaxemNavTab.PROGRESS -> palette.secondary
                            SyntaxemNavTab.CALCULATOR -> palette.tertiary
                            else -> palette.primary
                        }

                        Column(
                            modifier = Modifier
                                .clickable {
                                    currentTab = tab
                                    showProfileModal = false
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag(tab.tag),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                tint = if (isSelected) activeColor else palette.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) palette.textPrimary else palette.textSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showProfileModal) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showProfileModal = false }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = palette.textPrimary)
                        }
                        Text(text = "Scholar Profile & Mistake Journal", style = MaterialTheme.typography.titleMedium.copy(color = palette.textPrimary, fontWeight = FontWeight.Bold))
                    }
                    SyntaxemProfileScreen(
                        onNavigateToChapter = { chId ->
                            activeChapterId = chId
                            showProfileModal = false
                            currentTab = SyntaxemNavTab.LEARN
                        }
                    )
                }
            } else {
                when (currentTab) {
                    SyntaxemNavTab.HOME -> {
                        AcademicDashboardScreen(
                            onNavigateToSyllabus = {
                                currentTab = SyntaxemNavTab.PROGRESS
                            },
                            onNavigateToCalculator = {
                                calculatorPresetExpression = ""
                                currentTab = SyntaxemNavTab.CALCULATOR
                            },
                            onNavigateToLearn = { chId ->
                                if (chId != null) activeChapterId = chId
                                currentTab = SyntaxemNavTab.LEARN
                            },
                            onNavigateToGames = {
                                currentTab = SyntaxemNavTab.GAMES
                            }
                        )
                    }

                    SyntaxemNavTab.CALCULATOR -> {
                        LumenCalculatorScreen(
                            initialExpression = calculatorPresetExpression,
                            onNavigateToLabSim = { _ ->
                                currentTab = SyntaxemNavTab.GAMES
                            },
                            onNavigateToLabGame = { gameId ->
                                activeGameId = gameId
                                currentTab = SyntaxemNavTab.GAMES
                            }
                        )
                    }

                    SyntaxemNavTab.LEARN -> {
                        ChapterMindMapScreen(
                            initialChapterId = activeChapterId,
                            onPlayGame = { gameId ->
                                activeGameId = gameId
                                currentTab = SyntaxemNavTab.GAMES
                            },
                            onSendToCalculator = { expr ->
                                calculatorPresetExpression = expr
                                currentTab = SyntaxemNavTab.CALCULATOR
                            },
                            onNavigateBack = {
                                currentTab = SyntaxemNavTab.HOME
                            }
                        )
                    }

                    SyntaxemNavTab.GAMES -> {
                        GameArenaScreen(
                            initialGameId = activeGameId,
                            onSendToCalculator = { expr ->
                                calculatorPresetExpression = expr
                                currentTab = SyntaxemNavTab.CALCULATOR
                            }
                        )
                    }

                    SyntaxemNavTab.PROGRESS -> {
                        SyllabusTrackerScreen(
                            onNavigateBack = {
                                currentTab = SyntaxemNavTab.HOME
                            },
                            onStudyChapter = { chId ->
                                activeChapterId = chId
                                currentTab = SyntaxemNavTab.LEARN
                            }
                        )
                    }
                }
            }
        }
    }
}
