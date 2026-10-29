package com.example.calcmaster.feynman

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

data class FeynmanRubricItem(
    val title: String,
    val matched: Boolean,
    val feedback: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeynmanModeScreen(
    currentTopic: String = "Projectile Motion Kinematics",
    onReturnToCalc: () -> Unit
) {
    var studentExplanation by remember {
        mutableStateOf("We launch at angle theta with speed u. The horizontal velocity is u*cos(theta) and stays constant because no air resistance. At the peak, vertical velocity becomes zero, so time to reach apex is u*sin(theta)/g, and total flight time is double.")
    }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var isEvaluated by remember { mutableStateOf(false) }

    val rubrics = remember(studentExplanation, isEvaluated) {
        val lower = studentExplanation.lowercase()
        listOf(
            FeynmanRubricItem(
                title = "Identified Core Governing Formula",
                matched = lower.contains("u*cos") || lower.contains("sin") || lower.contains("velocity") || lower.contains("formula"),
                feedback = if (lower.contains("cos") || lower.contains("sin")) "Great job stating velocity resolution into horizontal and vertical components." else "Mention the velocity component breakdown u·cos(θ) and u·sin(θ)."
            ),
            FeynmanRubricItem(
                title = "Key Assumption Declared (Ideal Model)",
                matched = lower.contains("air") || lower.contains("resistance") || lower.contains("constant"),
                feedback = if (lower.contains("air") || lower.contains("resistance")) "Good job mentioning negligible air drag for ideal parabolic path." else "Remember to note that air resistance is neglected in the textbook model."
            ),
            FeynmanRubricItem(
                title = "Boundary / Apex Condition Explained",
                matched = lower.contains("peak") || lower.contains("apex") || lower.contains("zero"),
                feedback = if (lower.contains("zero") || lower.contains("peak")) "Accurately noted that v_y = 0 at apex." else "Explain what happens to vertical velocity at maximum height."
            ),
            FeynmanRubricItem(
                title = "Clarity & Simplicity (No Jargon Overload)",
                matched = true,
                feedback = "Explanation is clear enough for a 9th grader to grasp the intuition."
            )
        )
    }

    val score = rubrics.count { it.matched }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Feynman Teach-Back Mode",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "\"Ab Mujhe Samjhao\" • Teach it to master it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
            // Concept Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Topic: $currentTopic",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Explain this verified result in your own words (or in Hinglish). The AI tutor will listen for key physical assumptions and highlight any hidden conceptual gaps.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Student Speech / Text Input Area
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Explanation (Voice or Text)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            FilledTonalIconButton(
                                onClick = {
                                    isRecordingVoice = !isRecordingVoice
                                    if (!isRecordingVoice) {
                                        studentExplanation = "At the peak, vertical velocity is zero. Range is max at 45 degrees because sin(2 theta) is 1. Air resistance is ignored."
                                    }
                                },
                                modifier = Modifier.testTag("feynman_record_btn")
                            ) {
                                Icon(
                                    imageVector = if (isRecordingVoice) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (isRecordingVoice) Color(0xFFF43F5E) else MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = studentExplanation,
                            onValueChange = { studentExplanation = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("feynman_text_field"),
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("Record or type your 30-sec explanation here...") }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { isEvaluated = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("feynman_evaluate_btn")
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Analyze My Explanation (Find Concept Gaps)")
                        }
                    }
                }
            }

            // Evaluation Report
            if (isEvaluated) {
                item {
                    Text(
                        text = "Feynman Gap Analysis (${score}/${rubrics.size} Mastered)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(rubrics.size) { index ->
                    val item = rubrics[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (item.matched) Icons.Default.CheckCircle else Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = if (item.matched) Color(0xFF10B981) else Color(0xFFF59E0B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.matched) Color(0xFF10B981) else Color(0xFFF59E0B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.feedback,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Doubt Chain Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Forum, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Doubt Chain (Peer & Teacher Hints)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Stuck on an intermediate step? Post only the diverging step anonymously to receive conceptual hints from verified teachers—never full answer leaks.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
