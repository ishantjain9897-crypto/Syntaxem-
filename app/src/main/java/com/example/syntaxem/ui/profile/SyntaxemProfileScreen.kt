package com.example.syntaxem.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import com.example.calcmaster.lumenflow.model.LumenLearningRepository
import com.example.calcmaster.lumenflow.model.SavedMistake
import com.example.core.designsystem.SyntaxemColorSet
import com.example.core.designsystem.SyntaxemPalette
import com.example.core.designsystem.SyntaxemTheme
import com.example.core.designsystem.SyntaxemThemeManager
import com.example.syntaxem.model.SyntaxemCurriculumData
import com.example.syntaxem.ui.appearance.AppearanceDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyntaxemProfileScreen(
    onNavigateToChapter: (String) -> Unit = {}
) {
    val palette = SyntaxemTheme.palette
    val currentColorSet by SyntaxemThemeManager.currentColorSet.collectAsState()
    val mistakes = LumenLearningRepository.mistakes
    var selectedClassLevel by remember { mutableIntStateOf(11) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAppearanceDialog by remember { mutableStateOf(false) }

    val chapters = remember(selectedClassLevel) {
        SyntaxemCurriculumData.chapters.filter { it.classLevel == selectedClassLevel }
    }

    Scaffold(
        containerColor = palette.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Profile & Mastery",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track conceptual understanding across NCERT Class 11 & 12",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF8E9BB5))
                )
            }

            // Student Badge Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF6C6CF6),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mathematics Scholar",
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "27 Chapters · 108 Games · 27 Simulations",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2ED8A7), fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }

            // Class 11 / Class 12 Switcher
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(11, 12).forEach { level ->
                        val isSelected = selectedClassLevel == level
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedClassLevel = level }
                                .testTag("btn_class_$level"),
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFF6C6CF6) else Color(0xFF151C2E),
                            border = if (!isSelected) BorderStroke(1.dp, Color(0xFF25304C)) else null
                        ) {
                            Box(modifier = Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Class $level Curriculum",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) Color.White else Color(0xFF8E9BB5),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Chapter Progress List
            item {
                Text(
                    text = "Class $selectedClassLevel Chapters",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }

            items(chapters) { ch ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToChapter(ch.id) },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF151C2E),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Chapter ${ch.number}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6C6CF6), fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = ch.title,
                                style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${ch.mindMapNodes.size} Nodes · 4 Games · 1 Simulation",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF8E9BB5)
                        )
                    }
                }
            }

            // Concept Replay History Card
            item {
                val sessions = com.example.syntaxem.model.ConceptReplayHistoryRepository.sessions
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("profile_concept_replay_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CONCEPT REPLAY TRAIL (${sessions.size})",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2ED8A7), fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "SOLVE → UNDERSTAND → APPLY",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9BB5), fontSize = 10.sp)
                            )
                        }

                        if (sessions.isEmpty()) {
                            Text(
                                text = "No calculations recorded yet. Use the scientific workspace to start your learning loop.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF8E9BB5)
                            )
                        } else {
                            sessions.take(4).forEach { session ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E263D)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = session.expression,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF151C2E)) {
                                                Text(
                                                    text = session.currentPhase.label,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Color(0xFF2ED8A7))
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "= ${session.exactValue} (~${session.decimalFormatted}) • ${session.topicName}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Mistake Journal Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("profile_mistakes_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MISTAKE JOURNAL (${mistakes.size})",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFDB043), fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Spaced Repetition",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9BB5))
                            )
                        }

                        if (mistakes.isEmpty()) {
                            Text(
                                text = "No misconceptions logged. Practice and play games to capture insights.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF8E9BB5)
                            )
                        } else {
                            mistakes.forEach { item ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E263D)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.errorConcept,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF2ED8A7),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            IconButton(
                                                onClick = { LumenLearningRepository.deleteMistake(item.id) },
                                                modifier = Modifier.size(18.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Dismiss",
                                                    tint = Color(0xFF8E9BB5),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.explanation,
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Appearance & Color Sets Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("profile_appearance_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = palette.surface),
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = palette.primary.copy(alpha = 0.2f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Palette,
                                            contentDescription = null,
                                            tint = palette.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Appearance & UI Theme",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = palette.textPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Current: ${palette.name} (${if (palette.isDark) "Dark" else "Light"})",
                                        style = MaterialTheme.typography.bodySmall.copy(color = palette.textSecondary)
                                    )
                                }
                            }

                            TextButton(
                                onClick = { showAppearanceDialog = true },
                                modifier = Modifier.testTag("btn_profile_open_appearance")
                            ) {
                                Text("Customize", color = palette.primary, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Quick switcher chips
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (rowPalettes in SyntaxemPalette.ALL_PALETTES.chunked(2)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (p in rowPalettes) {
                                        val isSelected = currentColorSet == p.id
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { SyntaxemThemeManager.setColorSet(p.id) }
                                                .testTag("chip_palette_${p.id.name.lowercase()}"),
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) palette.primary.copy(alpha = 0.18f) else palette.surfaceVariant,
                                            border = BorderStroke(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) palette.primary else palette.border
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = p.name,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = palette.textPrimary,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                        ),
                                                        maxLines = 1
                                                    )
                                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                                        p.previewColors.take(3).forEach { c ->
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(8.dp)
                                                                    .clip(CircleShape)
                                                                    .background(c)
                                                            )
                                                        }
                                                    }
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Active",
                                                        tint = palette.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Data Privacy Button
            item {
                OutlinedButton(
                    onClick = { showPrivacyDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = palette.secondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Privacy & Offline Policy", color = palette.textPrimary)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showAppearanceDialog) {
        AppearanceDialog(
            onDismiss = { showAppearanceDialog = false }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = Color(0xFF151C2E),
            title = {
                Text("Syntaxem Privacy Guarantee", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• 100% On-Device: All chapters, mind maps, game algorithms, and mistake journals function entirely offline.", color = Color(0xFFC0CADB))
                    Text("• Zero Advertisements: No intrusive ads, paywalls, or third-party behavioral trackers.", color = Color(0xFFC0CADB))
                    Text("• Non-Punitive: Zero anxiety streaks or loss-aversion tactics.", color = Color(0xFFC0CADB))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ED8A7), contentColor = Color(0xFF0B101E))
                ) {
                    Text("Understood")
                }
            }
        )
    }
}
