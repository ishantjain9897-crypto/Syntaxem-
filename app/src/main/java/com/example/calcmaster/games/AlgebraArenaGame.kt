package com.example.calcmaster.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val ALGEBRA_QUESTIONS = listOf(
    Question(
        prompt = "Find the roots of x² - 5x + 6 = 0:",
        options = listOf("x = 2 and x = 3", "x = -2 and x = -3", "x = 1 and x = 6", "x = -1 and x = 5"),
        correctIndex = 0,
        explanation = "Factoring (x - 2)(x - 3) = 0 gives x = 2 or x = 3. Check: 2² - 5(2) + 6 = 4 - 10 + 6 = 0.",
        misconceptionNote = "When (x - a)(x - b) = 0, roots are positive a and b."
    ),
    Question(
        prompt = "What is the discriminant Δ of 2x² - 4x + 2 = 0?",
        options = listOf("Δ = 16", "Δ = 0 (One repeated root)", "Δ = -8 (Complex roots)", "Δ = 4"),
        correctIndex = 1,
        explanation = "Δ = b² - 4ac = (-4)² - 4(2)(2) = 16 - 16 = 0. Therefore, the equation has exactly one real repeated root at x = -b/2a = 1.",
        misconceptionNote = "When Δ = 0, the parabola touches the x-axis at exactly one point."
    ),
    Question(
        prompt = "Simplify the complex number (2 + 3i) + (4 - 5i):",
        options = listOf("6 - 2i", "6 + 8i", "8 - 15i", "6 - 8i"),
        correctIndex = 0,
        explanation = "Combine real and imaginary parts: (2 + 4) + (3 - 5)i = 6 - 2i.",
        misconceptionNote = "Treat 'i' like a variable, with the key property that i² = -1."
    ),
    Question(
        prompt = "In an Arithmetic Progression with first term a = 3 and common difference d = 4, what is the 10th term?",
        options = listOf("37", "39", "40", "43"),
        correctIndex = 1,
        explanation = "a_n = a + (n - 1)d -> a_10 = 3 + (10 - 1)·4 = 3 + 9·4 = 3 + 36 = 39.",
        misconceptionNote = "Remember that the coefficient of d is (n - 1), not n."
    )
)

@Composable
fun AlgebraArenaGame(
    onScoreUpdated: (score: Int) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    val question = ALGEBRA_QUESTIONS[currentIndex]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("algebra_arena_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Algebra Arena",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Class 11 Equations & Series • ${currentIndex + 1}/${ALGEBRA_QUESTIONS.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Arena Pts: $score",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = question.prompt,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                val isCorrect = index == question.correctIndex

                val buttonColor = when {
                    !isSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isSubmitted && isCorrect -> Color(0xFF10B981).copy(alpha = 0.2f)
                    isSubmitted && isSelected && !isCorrect -> Color(0xFFF43F5E).copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                }

                Surface(
                    onClick = { if (!isSubmitted) selectedOption = index },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = buttonColor
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
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
                            modifier = Modifier.weight(1f)
                        )
                        if (isSubmitted) {
                            if (isCorrect) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                            else if (isSelected) Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFF43F5E))
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isSubmitted) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (selectedOption == question.correctIndex) "✓ Correct Transformation!" else "Method Verification:",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedOption == question.correctIndex) Color(0xFF10B981) else Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = question.explanation, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isSubmitted) {
                Button(
                    onClick = {
                        if (selectedOption != null) {
                            isSubmitted = true
                            if (selectedOption == question.correctIndex) {
                                score += 10
                                onScoreUpdated(score)
                            }
                        }
                    },
                    enabled = selectedOption != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Validate Answer")
                }
            } else {
                Button(
                    onClick = {
                        isSubmitted = false
                        selectedOption = null
                        currentIndex = (currentIndex + 1) % ALGEBRA_QUESTIONS.size
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next Duel")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.NavigateNext, contentDescription = null)
                }
            }
        }
    }
}
