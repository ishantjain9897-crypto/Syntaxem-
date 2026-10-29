package com.example.syntaxem.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.designsystem.SyntaxemColorSet
import com.example.core.designsystem.SyntaxemThemeManager

data class StudentProfilePreferences(
    val academicSession: String = "2026–27",
    val board: String = "CBSE / NCERT (Verified)",
    val classLevel: String = "Class 11",
    val stream: String = "Science (PCM)",
    val languageMedium: String = "English",
    val dailyGoalMinutes: Int = 45,
    val themeMode: String = "Dark (Midnight Navy)",
    val weatherEnabled: Boolean = true
)

@Composable
fun OnboardingDialog(
    initialPreferences: StudentProfilePreferences = StudentProfilePreferences(),
    onDismiss: () -> Unit,
    onSavePreferences: (StudentProfilePreferences) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1: Curriculum, 2: Class & Stream, 3: Goals & Theme
    var session by remember { mutableStateOf(initialPreferences.academicSession) }
    var board by remember { mutableStateOf(initialPreferences.board) }
    var classLevel by remember { mutableStateOf(initialPreferences.classLevel) }
    var stream by remember { mutableStateOf(initialPreferences.stream) }
    var language by remember { mutableStateOf(initialPreferences.languageMedium) }
    var dailyGoal by remember { mutableIntStateOf(initialPreferences.dailyGoalMinutes) }
    var themeMode by remember { mutableStateOf(initialPreferences.themeMode) }
    var weatherEnabled by remember { mutableStateOf(initialPreferences.weatherEnabled) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .testTag("onboarding_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F1526),
            border = BorderStroke(1.dp, Color(0xFF25304C)),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SYNTAXEM SETUP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF2ED8A7),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "Student Profile & Curriculum",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "Step $step / 3",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF8E9BB5),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { step / 3f },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = Color(0xFF2ED8A7),
                    trackColor = Color(0xFF25304C)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step content
                Box(modifier = Modifier.weight(1f)) {
                    when (step) {
                        1 -> StepOneCurriculum(
                            session = session,
                            onSessionSelect = { session = it },
                            board = board,
                            onBoardSelect = { board = it },
                            language = language,
                            onLanguageSelect = { language = it }
                        )
                        2 -> StepTwoClassAndStream(
                            classLevel = classLevel,
                            onClassSelect = { classLevel = it },
                            stream = stream,
                            onStreamSelect = { stream = it }
                        )
                        3 -> StepThreeGoalsAndTheme(
                            dailyGoal = dailyGoal,
                            onGoalSelect = { dailyGoal = it },
                            themeMode = themeMode,
                            onThemeSelect = { themeMode = it },
                            weatherEnabled = weatherEnabled,
                            onWeatherToggle = { weatherEnabled = it }
                        )
                    }
                }

                // Bottom navigation controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF25304C))
                        ) {
                            Text("Back", color = Color(0xFF8E9BB5))
                        }
                    } else {
                        TextButton(onClick = onDismiss) {
                            Text("Skip for Now", color = Color(0xFF8E9BB5))
                        }
                    }

                    Button(
                        onClick = {
                            if (step < 3) {
                                step++
                            } else {
                                onSavePreferences(
                                    StudentProfilePreferences(
                                        academicSession = session,
                                        board = board,
                                        classLevel = classLevel,
                                        stream = stream,
                                        languageMedium = language,
                                        dailyGoalMinutes = dailyGoal,
                                        themeMode = themeMode,
                                        weatherEnabled = weatherEnabled
                                    )
                                )
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ED8A7))
                    ) {
                        Text(
                            text = if (step < 3) "Next Step" else "Complete Setup",
                            color = Color(0xFF0B101E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepOneCurriculum(
    session: String,
    onSessionSelect: (String) -> Unit,
    board: String,
    onBoardSelect: (String) -> Unit,
    language: String,
    onLanguageSelect: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text(
                text = "1. Academic Session",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("2026–27 (Current)", "2025–26").forEach { sess ->
                    SelectableChip(
                        text = sess,
                        isSelected = session.startsWith(sess.substring(0, 7)),
                        onClick = { onSessionSelect(sess) }
                    )
                }
            }
        }

        item {
            Text(
                text = "2. Curriculum Authority / Board",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "CBSE / NCERT (Verified Official)" to "Fully verified chapters for Class 11 & 12 Science.",
                    "Vidya Bharati (Provisional)" to "School curriculum variant aligned with Sanskriti Bodh.",
                    "State Board / ISC (Provisional)" to "Provisional aligned framework."
                ).forEach { (title, subtitle) ->
                    SelectableCard(
                        title = title,
                        subtitle = subtitle,
                        isSelected = board.startsWith(title.substring(0, 4)),
                        onClick = { onBoardSelect(title) }
                    )
                }
            }
        }

        item {
            Text(
                text = "3. Language Medium",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("English", "Hindi (हिन्दी)").forEach { lang ->
                    SelectableChip(
                        text = lang,
                        isSelected = language.startsWith(lang.substring(0, 3)),
                        onClick = { onLanguageSelect(lang) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepTwoClassAndStream(
    classLevel: String,
    onClassSelect: (String) -> Unit,
    stream: String,
    onStreamSelect: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text(
                text = "1. Academic Class",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Initial focus is Class 11, with full Class 12 support.",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9BB5))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Class 11", "Class 12", "Class 10 (Preview)").forEach { cls ->
                    SelectableChip(
                        text = cls,
                        isSelected = classLevel == cls,
                        onClick = { onClassSelect(cls) }
                    )
                }
            }
        }

        item {
            Text(
                text = "2. Stream Selection",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Science (PCM)" to "Physics, Chemistry, Mathematics (Full 108 games & simulations)",
                    "Science (PCB)" to "Physics, Chemistry, Biology focus",
                    "Commerce & Mathematics" to "Applied Mathematics & Economics track"
                ).forEach { (title, subtitle) ->
                    SelectableCard(
                        title = title,
                        subtitle = subtitle,
                        isSelected = stream.startsWith(title.substring(0, 7)),
                        onClick = { onStreamSelect(title) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepThreeGoalsAndTheme(
    dailyGoal: Int,
    onGoalSelect: (Int) -> Unit,
    themeMode: String,
    onThemeSelect: (String) -> Unit,
    weatherEnabled: Boolean,
    onWeatherToggle: (Boolean) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text(
                text = "1. Daily Learning Target",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(30 to "30 Mins", 45 to "45 Mins", 60 to "60 Mins").forEach { (mins, label) ->
                    SelectableChip(
                        text = label,
                        isSelected = dailyGoal == mins,
                        onClick = { onGoalSelect(mins) }
                    )
                }
            }
        }

        item {
            Text(
                text = "2. Visual Theme & Color Set",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (rowSets in SyntaxemColorSet.values().toList().chunked(2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (set in rowSets) {
                            val isSelected = themeMode.contains(set.title) || themeMode == set.name
                            Box(modifier = Modifier.weight(1f)) {
                                SelectableChip(
                                    text = set.title,
                                    isSelected = isSelected,
                                    onClick = {
                                        onThemeSelect(set.title)
                                        SyntaxemThemeManager.setColorSet(set)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "3. Study Environment Telemetry",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF151C2E),
                border = BorderStroke(1.dp, Color(0xFF25304C))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Display Local Weather on Dashboard",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Uses approximate location or manual city. Zero ads, zero tracking.",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9BB5))
                        )
                    }
                    Switch(
                        checked = weatherEnabled,
                        onCheckedChange = onWeatherToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2ED8A7))
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectableChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF2ED8A7) else Color(0xFF151C2E),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF2ED8A7) else Color(0xFF25304C))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (isSelected) Color(0xFF0B101E) else Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@Composable
private fun SelectableCard(title: String, subtitle: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF1C2844) else Color(0xFF151C2E),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF2ED8A7) else Color(0xFF25304C))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isSelected) Color(0xFF2ED8A7) else Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9BB5))
            )
        }
    }
}
