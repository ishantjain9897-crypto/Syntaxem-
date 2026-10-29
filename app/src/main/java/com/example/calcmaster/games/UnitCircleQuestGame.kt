package com.example.calcmaster.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Question(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val misconceptionNote: String? = null
)

val UNIT_CIRCLE_STAGES = listOf(
    Pair(
        "Stage 1: Quadrant Signs",
        listOf(
            Question(
                prompt = "In which quadrant does an angle of 215° lie, and which ratio is positive?",
                options = listOf("Q2 (Sine is positive)", "Q3 (Tangent is positive)", "Q4 (Cosine is positive)", "Q1 (All are positive)"),
                correctIndex = 1,
                explanation = "180° < 215° < 270°, so it lies in Quadrant 3. Under ASTC, Tangent (and Cotangent) is positive.",
                misconceptionNote = "Remember: Q1=All, Q2=Silver(Sin), Q3=Tea(Tan), Q4=Cups(Cos)."
            ),
            Question(
                prompt = "What is the sign of cos(310°)?",
                options = listOf("Positive (+)", "Negative (-)", "Zero (0)", "Undefined"),
                correctIndex = 0,
                explanation = "310° is in Quadrant 4 (270° to 360°), where cosine is positive (x-coordinate > 0).",
                misconceptionNote = "Cosine corresponds to the horizontal x-axis projection."
            )
        )
    ),
    Pair(
        "Stage 2: Radians & Special Angles",
        listOf(
            Question(
                prompt = "What is 150° in radians?",
                options = listOf("2π/3 rad", "5π/6 rad", "3π/4 rad", "7π/6 rad"),
                correctIndex = 1,
                explanation = "Multiply by π/180: 150 × (π/180) = (150/180)π = 5π/6 radians.",
                misconceptionNote = "Simplify 15/18 by dividing numerator and denominator by 3."
            ),
            Question(
                prompt = "Convert 4π/3 radians to degrees:",
                options = listOf("210°", "225°", "240°", "270°"),
                correctIndex = 2,
                explanation = "Replace π with 180°: 4 × (180°/3) = 4 × 60° = 240°.",
                misconceptionNote = "4/3 of a semicircle (180°) is 60° into the third quadrant."
            )
        )
    ),
    Pair(
        "Stage 3: Exact Values",
        listOf(
            Question(
                prompt = "What is the exact value of sin(120°)?",
                options = listOf("1/2", "√3/2", "-√3/2", "-1/2"),
                correctIndex = 1,
                explanation = "sin(120°) = sin(180° - 60°) = +sin(60°) = √3/2. Sine is positive in Q2.",
                misconceptionNote = "Do not confuse sin(60°) = √3/2 with cos(60°) = 1/2."
            ),
            Question(
                prompt = "Evaluate cos(240°):",
                options = listOf("-1/2", "-√3/2", "1/2", "0"),
                correctIndex = 0,
                explanation = "cos(240°) = cos(180° + 60°) = -cos(60°) = -1/2 in Quadrant 3.",
                misconceptionNote = "In Q3, both x and y are negative, so cosine is negative."
            )
        )
    )
)

@Composable
fun UnitCircleQuestGame(
    onProgressUpdated: (score: Int) -> Unit
) {
    var currentStageIndex by remember { mutableIntStateOf(0) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var totalScore by remember { mutableIntStateOf(0) }

    val stage = UNIT_CIRCLE_STAGES.getOrNull(currentStageIndex) ?: UNIT_CIRCLE_STAGES[0]
    val questions = stage.second
    val question = questions.getOrNull(currentQuestionIndex) ?: questions[0]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("unit_circle_quest_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Unit Circle Quest",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${stage.first} • Question ${currentQuestionIndex + 1}/${questions.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Score: $totalScore",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Question prompt
            Text(
                text = question.prompt,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Options
            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                val isCorrect = index == question.correctIndex

                val buttonColor = when {
                    !isAnswerSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isAnswerSubmitted && isCorrect -> Color(0xFF10B981).copy(alpha = 0.2f)
                    isAnswerSubmitted && isSelected && !isCorrect -> Color(0xFFF43F5E).copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                }

                val borderColor = when {
                    !isAnswerSubmitted && isSelected -> MaterialTheme.colorScheme.primary
                    isAnswerSubmitted && isCorrect -> Color(0xFF10B981)
                    isAnswerSubmitted && isSelected && !isCorrect -> Color(0xFFF43F5E)
                    else -> Color.Transparent
                }

                Surface(
                    onClick = {
                        if (!isAnswerSubmitted) {
                            selectedOption = index
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("quest_opt_$index"),
                    shape = RoundedCornerShape(10.dp),
                    color = buttonColor,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${('A' + index)}. ",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        if (isAnswerSubmitted) {
                            if (isCorrect) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = Color(0xFF10B981))
                            } else if (isSelected) {
                                Icon(Icons.Default.Close, contentDescription = "Incorrect", tint = Color(0xFFF43F5E))
                            }
                        }
                    }
                }
            }

            // Explanation Card after submission
            AnimatedVisibility(visible = isAnswerSubmitted) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    val correct = selectedOption == question.correctIndex
                    Text(
                        text = if (correct) "✓ Concept Mastered!" else "Notice the Rule:",
                        fontWeight = FontWeight.Bold,
                        color = if (correct) Color(0xFF10B981) else Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    question.misconceptionNote?.let { note ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 Tip: $note",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action button
            if (!isAnswerSubmitted) {
                Button(
                    onClick = {
                        if (selectedOption != null) {
                            isAnswerSubmitted = true
                            if (selectedOption == question.correctIndex) {
                                totalScore += 10
                                onProgressUpdated(totalScore)
                            }
                        }
                    },
                    enabled = selectedOption != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quest_submit_btn")
                ) {
                    Text("Check Answer")
                }
            } else {
                Button(
                    onClick = {
                        isAnswerSubmitted = false
                        selectedOption = null
                        if (currentQuestionIndex + 1 < questions.size) {
                            currentQuestionIndex++
                        } else if (currentStageIndex + 1 < UNIT_CIRCLE_STAGES.size) {
                            currentStageIndex++
                            currentQuestionIndex = 0
                        } else {
                            // Loop back or finish
                            currentStageIndex = 0
                            currentQuestionIndex = 0
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quest_next_btn")
                ) {
                    Text(if (currentQuestionIndex + 1 < questions.size || currentStageIndex + 1 < UNIT_CIRCLE_STAGES.size) "Next Challenge" else "Restart Quest")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.NavigateNext, contentDescription = null)
                }
            }
        }
    }
}
