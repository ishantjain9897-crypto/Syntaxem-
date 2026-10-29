package com.example.calcmaster.worksheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class WorksheetQuestionItem(
    val qNum: Int,
    val question: String,
    val marks: Int,
    val solution: String,
    val pyqFrequency: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorksheetForgeScreen(
    onSendQuestionToCalc: (String) -> Unit
) {
    var selectedChapter by remember { mutableStateOf("Trigonometric Functions & Kinematics") }
    var showAnswerKey by remember { mutableStateOf(false) }

    val worksheetQuestions = remember(selectedChapter) {
        listOf(
            WorksheetQuestionItem(
                qNum = 1,
                question = "A stone is thrown with an initial velocity of 20 m/s at an angle of 30° above the horizontal. Find its maximum height and horizontal range (g = 9.8 m/s²).",
                marks = 3,
                solution = "H = (u² sin²θ)/(2g) = (400 × 0.25)/(19.6) = 5.10 m. R = (u² sin 2θ)/g = (400 × √3/2)/9.8 = 35.35 m.",
                pyqFrequency = "Appeared in 4 of last 5 CBSE papers"
            ),
            WorksheetQuestionItem(
                qNum = 2,
                question = "Find the real and complex roots of the quadratic equation 2x² - 4x + 5 = 0.",
                marks = 2,
                solution = "Δ = b² - 4ac = 16 - 40 = -24 < 0. x = (4 ± i√24)/4 = 1 ± (√6 / 2)i.",
                pyqFrequency = "NCERT Exemplar Standard Problem"
            ),
            WorksheetQuestionItem(
                qNum = 3,
                question = "Prove that cos(6x) = 32 cos⁶(x) - 48 cos⁴(x) + 18 cos²(x) - 1.",
                marks = 4,
                solution = "Express cos(6x) = cos(2(3x)) = 2 cos²(3x) - 1. Substitute cos(3x) = 4 cos³(x) - 3 cos(x) and expand.",
                pyqFrequency = "CBSE High-Weightage Long Answer"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Worksheet Forge & Blueprint",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "NCERT Exemplar + PYQ Prediction Engine",
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
            // Blueprint Predictor Insight Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exam Blueprint Predictor",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Based on CBSE Class 11 weightage trends: Kinematics 2D and Trigonometric Transformations carry 14 marks combined in mid-term and annual assessments.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Controls: Chapter and Answer Key Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Practice Sheet (3 Questions)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    FilterChip(
                        selected = showAnswerKey,
                        onClick = { showAnswerKey = !showAnswerKey },
                        label = { Text(if (showAnswerKey) "Hide Solutions" else "Show Answer Key") },
                        leadingIcon = {
                            Icon(
                                if (showAnswerKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                }
            }

            // Questions list
            items(worksheetQuestions, key = { it.qNum }) { q ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "Q${q.qNum}. [${q.marks} Marks]",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = q.pyqFrequency,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = q.question,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Answer Key section
                        AnimatedVisibility(visible = showAnswerKey) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Verified Solution & Marking Scheme:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = q.solution,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    val formula = if (q.qNum == 1) "projectile u=20 theta=30" else "2x^2 - 4x + 5 = 0"
                                    onSendQuestionToCalc(formula)
                                }
                            ) {
                                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open in Calculator")
                            }
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* Export Sheet */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export PDF Worksheet")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
