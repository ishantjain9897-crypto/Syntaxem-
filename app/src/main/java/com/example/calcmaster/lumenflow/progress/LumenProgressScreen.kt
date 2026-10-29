package com.example.calcmaster.lumenflow.progress

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcmaster.lumenflow.model.LumenCurriculumData
import com.example.calcmaster.lumenflow.model.LumenLearningRepository
import com.example.calcmaster.lumenflow.model.SavedMistake

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumenProgressScreen(
    onNavigateToTopic: (String) -> Unit = {},
    onOpenTutor: (contextTitle: String, formula: String) -> Unit = { _, _ -> }
) {
    val topics = remember { LumenCurriculumData.topics }
    val mistakes = LumenLearningRepository.mistakes
    val goals = remember { LumenCurriculumData.learningGoals }

    var showExportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "EDUCATIONAL PROGRESS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            )
                        )
                        Text(
                            text = "No Vanity Metrics • Concept Mastery Tracking",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.testTag("btn_export_privacy")
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = "Privacy & Export", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Mastery Overview Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                MasteryOverviewCard(topics = topics)
            }

            // 2. Personal Learning Goals
            item {
                LearningGoalsCard(goals = goals)
            }

            // 3. Saved Mistakes Journal
            item {
                MistakesJournalCard(
                    mistakes = mistakes,
                    onRemoveMistake = { id ->
                        LumenLearningRepository.deleteMistake(id)
                    },
                    onAskTutor = { mistake ->
                        onOpenTutor("Mistake Review: ${mistake.errorConcept}", mistake.verifiedAnswer)
                    }
                )
            }

            // 4. Spaced Repetition Revision Queue
            item {
                RevisionQueueCard(
                    onReviewTopic = { topicId ->
                        onNavigateToTopic(topicId)
                    }
                )
            }

            // 5. Privacy & Data Control Notice
            item {
                PrivacyNoticeCard(onOpenPrivacy = { showExportDialog = true })
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Privacy Centre & Data Export") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Storage Policy: 100% of your calculations, mistakes, and progress records remain on-device.")
                    Text("• Zero Tracking: No behavioural advertisements, no telemetry tracking, and no student profiling.")
                    Text("• Full Control: You can export your data in JSON format or wipe local records at any time.")
                }
            },
            confirmButton = {
                Button(onClick = { showExportDialog = false }) {
                    Text("Export Local JSON")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun MasteryOverviewCard(topics: List<com.example.calcmaster.lumenflow.model.CurriculumTopic>) {
    val masteredCount = topics.count { it.masteryLevel >= 0.8f }
    val inProgressCount = topics.count { it.masteryLevel in 0.4f..0.79f }
    val needsReviewCount = topics.count { it.needsReview }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("mastery_overview_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "CURRICULUM TOPIC MASTERY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Class 11 CBSE Mathematics & Physics",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricProgressPill("MASTERED", "$masteredCount Topics", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                MetricProgressPill("IN PROGRESS", "$inProgressCount Topics", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                MetricProgressPill("NEEDS REVIEW", "$needsReviewCount Topics", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MetricProgressPill(label: String, count: String, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp, color = color))
            Text(text = count, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = color))
        }
    }
}

@Composable
fun LearningGoalsCard(goals: List<com.example.calcmaster.lumenflow.model.LearningGoal>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "PERSONAL LEARNING GOALS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )

            goals.forEach { goal ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = goal.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text(text = "${(goal.progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    LinearProgressIndicator(
                        progress = { goal.progress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MistakesJournalCard(
    mistakes: List<SavedMistake>,
    onRemoveMistake: (String) -> Unit,
    onAskTutor: (SavedMistake) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("mistake_journal_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MISTAKE JOURNAL (${mistakes.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
                Text("Non-punitive reflection", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (mistakes.isEmpty()) {
                Text(
                    text = "No saved mistakes currently in journal. When you attempt exercises, misconceptions are preserved here for spaced review.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                mistakes.forEach { item ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = item.errorConcept,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                )
                                IconButton(onClick = { onRemoveMistake(item.id) }, modifier = Modifier.size(20.dp)) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(14.dp))
                                }
                            }
                            Text(text = "Q: ${item.question}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                            Text(text = "Your response: ${item.studentAnswer}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            Text(text = "Verified: ${item.verifiedAnswer}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Why? ${item.explanation}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            OutlinedButton(
                                onClick = { onAskTutor(item) },
                                modifier = Modifier.align(Alignment.End),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Ask Tutor", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RevisionQueueCard(onReviewTopic: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "SPACED REPETITION REVISION QUEUE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "Concepts scheduled for interval review to consolidate long-term memory:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Trigonometric Quadrant Invariance", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = "Scheduled: Today • 3rd Review", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                Button(onClick = { onReviewTopic("trig_unit_circle") }, shape = RoundedCornerShape(8.dp)) {
                    Text("Review Now")
                }
            }
        }
    }
}

@Composable
fun PrivacyNoticeCard(onOpenPrivacy: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onOpenPrivacy() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "Privacy & Safety Guarantee", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(text = "Zero ads, zero data selling, full local offline control", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
