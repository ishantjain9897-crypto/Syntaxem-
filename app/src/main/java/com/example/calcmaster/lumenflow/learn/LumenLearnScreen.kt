package com.example.calcmaster.lumenflow.learn

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.calcmaster.lumenflow.model.CurriculumTopic
import com.example.calcmaster.lumenflow.model.LumenCurriculumData
import com.example.calcmaster.lumenflow.model.LumenSubject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumenLearnScreen(
    initialTopicId: String? = null,
    onSendToCalculator: (String) -> Unit = {},
    onLaunchSimulation: (String) -> Unit = {},
    onOpenTutor: (contextTitle: String, formula: String) -> Unit = { _, _ -> }
) {
    var selectedSubject by remember { mutableStateOf(LumenSubject.MATHEMATICS) }
    val allTopics = remember { LumenCurriculumData.topics }
    var selectedTopicId by remember { mutableStateOf(initialTopicId ?: allTopics.first().id) }

    val currentTopic = allTopics.find { it.id == selectedTopicId } ?: allTopics.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CURRICULUM LEARNING",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            )
                        )
                        Text(
                            text = "CBSE & NCERT Class 11 • Concept Mastery",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onOpenTutor("Topic: ${currentTopic.title}", currentTopic.governingFormulae.firstOrNull() ?: "") },
                        modifier = Modifier.testTag("btn_learn_tutor")
                    ) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = "Tutor", tint = MaterialTheme.colorScheme.primary)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Subject Category Filter Tabs
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LumenSubject.values().forEach { subject ->
                        val isSelected = selectedSubject == subject
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedSubject = subject
                                val firstInSubject = allTopics.firstOrNull { it.subject == subject }
                                if (firstInSubject != null) {
                                    selectedTopicId = firstInSubject.id
                                }
                            },
                            label = { Text(subject.displayName, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 2. Topic Selector Carousel for Chosen Subject
            item {
                val subjectTopics = allTopics.filter { it.subject == selectedSubject }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjectTopics) { topic ->
                        val isSelected = topic.id == selectedTopicId
                        Surface(
                            modifier = Modifier
                                .clickable { selectedTopicId = topic.id }
                                .testTag("topic_tab_${topic.id}"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(
                                    text = topic.title,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Mastery: ${(topic.masteryLevel * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. Learning Objective Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("topic_objective_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LEARNING OBJECTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentTopic.learningObjective,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Prerequisites
                        Text(
                            text = "Prerequisites: ${currentTopic.prerequisites.joinToString(" • ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 4. Concept Explanation Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "CONCEPT EXPLORATION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = currentTopic.conceptExplanation,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )

                        Divider(color = MaterialTheme.colorScheme.outlineVariant)

                        Text(
                            text = "Governing Formulae:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        currentTopic.governingFormulae.forEach { formula ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = formula,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    IconButton(
                                        onClick = { onSendToCalculator(formula) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Calculate, contentDescription = "Calc", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Worked Examples Section
            item {
                Text(
                    text = "WORKED EXAMPLES",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(currentTopic.workedExamples) { example ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Problem: ${example.problem}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        example.steps.forEach { step ->
                            Text(
                                text = "• $step",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Final: ${example.finalAnswer}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = example.verifiedRule,
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.tertiary)
                            )
                        }
                    }
                }
            }

            // 6. Action: Launch Practice / Simulation
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onLaunchSimulation("projectile") },
                        modifier = Modifier.weight(1f).testTag("btn_launch_lab"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Open in Lab Sim")
                    }
                    OutlinedButton(
                        onClick = { onOpenTutor("Practice: ${currentTopic.title}", currentTopic.governingFormulae.firstOrNull() ?: "") },
                        modifier = Modifier.weight(1f).testTag("btn_request_hint"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Tutor Hints")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
