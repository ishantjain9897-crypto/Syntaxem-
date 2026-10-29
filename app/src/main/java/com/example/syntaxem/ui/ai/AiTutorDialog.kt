package com.example.syntaxem.ui.ai

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

enum class TutorDifficulty(val label: String, val badgeColor: Color) {
    INTUITIVE("1. Intuitive / Beginner", Color(0xFF2ED8A7)),
    CLASS_11_NCERT("2. Class 11 Rigorous", Color(0xFF6C6CF6)),
    JEE_ADVANCED("3. JEE Competitive", Color(0xFFFDB043))
}

enum class TutorMode(val label: String) {
    EXPLAIN("Concept Explainer"),
    SOLVER_STEPS("Step-by-Step Logic"),
    SUMMARY("Chapter Summary"),
    QUIZ("Practice Questions"),
    MISCONCEPTION("Mistake Diagnostic")
}

data class QuizQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val pedagogicalExplanation: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTutorDialog(
    initialTopic: String = "Trigonometric Functions",
    onDismiss: () -> Unit,
    onSendToCalculator: (String) -> Unit = {}
) {
    var selectedTopic by remember { mutableStateOf(initialTopic) }
    var difficulty by remember { mutableStateOf(TutorDifficulty.CLASS_11_NCERT) }
    var mode by remember { mutableStateOf(TutorMode.EXPLAIN) }
    var selectedQuizOption by remember { mutableStateOf<Int?>(null) }
    var quizSubmitted by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("ai_tutor_dialog"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF202A44),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF2ED8A7), modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SYNTAXEM SOCRATIC TUTOR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF2ED8A7),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                            )
                            Text(
                                text = "Concept: $selectedTopic",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8E9BB5))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TutorMode.values().forEach { m ->
                        val isSel = mode == m
                        Surface(
                            modifier = Modifier.clickable {
                                mode = m
                                selectedQuizOption = null
                                quizSubmitted = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) Color(0xFF2ED8A7) else Color(0xFF151C2E),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF2ED8A7) else Color(0xFF25304C))
                        ) {
                            Text(
                                text = m.label,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) Color(0xFF0B101E) else Color(0xFF8E9BB5),
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Difficulty Selector (For Explain mode)
                if (mode == TutorMode.EXPLAIN) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Depth Level:",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9BB5))
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TutorDifficulty.values().forEach { diff ->
                                val isSelected = difficulty == diff
                                Surface(
                                    modifier = Modifier.clickable { difficulty = diff },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) diff.badgeColor.copy(alpha = 0.2f) else Color(0xFF151C2E),
                                    border = BorderStroke(1.dp, if (isSelected) diff.badgeColor else Color(0xFF25304C))
                                ) {
                                    Text(
                                        text = diff.label,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) diff.badgeColor else Color(0xFF8E9BB5),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Verified Source vs Generated banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF121B30),
                    border = BorderStroke(1.dp, Color(0xFF223052)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2ED8A7), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Verified Curriculum Source: NCERT Class 11 Chapter 3 • Offline Knowledge Base Ready",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2ED8A7), fontSize = 10.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content Area
                Box(modifier = Modifier.weight(1f)) {
                    when (mode) {
                        TutorMode.EXPLAIN -> ExplainContent(selectedTopic, difficulty)
                        TutorMode.SOLVER_STEPS -> SolverStepsContent(onSendToCalculator)
                        TutorMode.SUMMARY -> SummaryContent(selectedTopic)
                        TutorMode.QUIZ -> QuizContent(
                            selectedOption = selectedQuizOption,
                            isSubmitted = quizSubmitted,
                            onSelectOption = { if (!quizSubmitted) selectedQuizOption = it },
                            onSubmit = { quizSubmitted = true }
                        )
                        TutorMode.MISCONCEPTION -> MisconceptionContent()
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF25304C))
                    ) {
                        Text("Close Tutor", color = Color(0xFF8E9BB5))
                    }

                    Button(
                        onClick = {
                            onSendToCalculator("sin(2*x) = 2*sin(x)*cos(x)")
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDB043))
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFF0B101E), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Solve in Workspace", color = Color(0xFF0B101E), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExplainContent(topic: String, difficulty: TutorDifficulty) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF151C2E),
                border = BorderStroke(1.dp, Color(0xFF25304C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = when (difficulty) {
                            TutorDifficulty.INTUITIVE -> "Intuitive Concept: Think of circular rotation"
                            TutorDifficulty.CLASS_11_NCERT -> "NCERT Axiomatic Definition: Real-valued function on unit circle"
                            TutorDifficulty.JEE_ADVANCED -> "JEE Framework: Periodicity, symmetries and Euler's form"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when (difficulty) {
                            TutorDifficulty.INTUITIVE ->
                                "Instead of just right-angled triangles where angles must be < 90°, the unit circle allows angle θ to be any real number: negative angles (clockwise), angles > 360° (multiple revolutions). On a unit circle (radius = 1), the x-coordinate of the terminal ray is cos(θ) and the y-coordinate is sin(θ)."
                            TutorDifficulty.CLASS_11_NCERT ->
                                "Let a point P(x, y) move along a unit circle centered at origin with arc length 'x' radians. The functions are defined as cos(x) = x-coordinate and sin(x) = y-coordinate. Because one complete revolution is 2π radians, sin(2nπ + x) = sin(x) and cos(2nπ + x) = cos(x) for any integer n. Fundamental identity: cos²(x) + sin²(x) = 1 follows directly from Pythagoras' theorem x² + y² = 1."
                            TutorDifficulty.JEE_ADVANCED ->
                                "Trigonometric functions map the real line ℝ to [-1, 1]. In Euler's representation, e^(ix) = cos(x) + i sin(x). This linearizes power products like cos^n(x) into multiple angles. In domain analysis, tan(x) and sec(x) are discontinuous at (2n+1)π/2, while cot(x) and csc(x) are discontinuous at nπ."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 19.sp)
                    )
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF151C2E),
                border = BorderStroke(1.dp, Color(0xFF25304C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Core Formulas to Remember",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF2ED8A7), fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• sin(x ± y) = sin(x)cos(y) ± cos(x)sin(y)\n• cos(x ± y) = cos(x)cos(y) ∓ sin(x)sin(y)\n• cos(2x) = cos²(x) - sin²(x) = 2cos²(x) - 1 = 1 - 2sin²(x)",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = Color(0xFFFDB043))
                    )
                }
            }
        }
    }
}

@Composable
private fun SolverStepsContent(onSendToCalculator: (String) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Example Problem: Solve 2 cos²(x) + 3 sin(x) = 0 for x ∈ [0, 2π)",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
        }

        val steps = listOf(
            Triple("Step 1: Unify trigonometric terms", "Use identity cos²(x) = 1 - sin²(x) to express equation entirely in sin(x).", "Axiom: Pythagorean Identity"),
            Triple("Step 2: Form quadratic equation", "2(1 - sin²(x)) + 3 sin(x) = 0  ⇒  -2 sin²(x) + 3 sin(x) + 2 = 0  ⇒  2 sin²(x) - 3 sin(x) - 2 = 0", "Algebraic Simplification"),
            Triple("Step 3: Factorize quadratic", "(2 sin(x) + 1)(sin(x) - 2) = 0", "Roots: sin(x) = -1/2 or sin(x) = 2"),
            Triple("Step 4: Check domain constraints", "Since range of sin(x) is [-1, 1], sin(x) = 2 is rejected as an extraneous root.", "Domain Validation"),
            Triple("Step 5: Solve in specified interval", "sin(x) = -1/2 is negative in Quadrants III and IV. Angles: π + π/6 = 7π/6 and 2π - π/6 = 11π/6.", "Final Solution Set: {7π/6, 11π/6}")
        )

        items(steps) { (title, detail, note) ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF151C2E),
                border = BorderStroke(1.dp, Color(0xFF25304C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2ED8A7), fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = detail, style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontFamily = FontFamily.Monospace))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "💡 $note", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFDB043)))
                }
            }
        }
    }
}

@Composable
private fun SummaryContent(topic: String) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF151C2E),
                border = BorderStroke(1.dp, Color(0xFF25304C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Executive Chapter Summary: $topic", style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Angles can be measured in degrees or radians. 1 radian = 180°/π ≈ 57° 16'. Relation: l = rθ where θ is in radians.\n\n" +
                                "2. Signs of ratios in quadrants (ASTC Rule):\n" +
                                "   • Quad I (0 to π/2): All positive\n" +
                                "   • Quad II (π/2 to π): Sin and Csc positive\n" +
                                "   • Quad III (π to 3π/2): Tan and Cot positive\n" +
                                "   • Quad IV (3π/2 to 2π): Cos and Sec positive\n\n" +
                                "3. Graph characteristics:\n" +
                                "   • sin(x) & cos(x) have period 2π and amplitude 1\n" +
                                "   • tan(x) has period π and vertical asymptotes at x = (2n+1)π/2",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 18.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizContent(
    selectedOption: Int?,
    isSubmitted: Boolean,
    onSelectOption: (Int) -> Unit,
    onSubmit: () -> Unit
) {
    val question = QuizQuestion(
        prompt = "What is the exact principal value of tan(15°)?",
        options = listOf(
            "2 - √3",
            "√3 - 1",
            "2 + √3",
            "1 / √3"
        ),
        correctIndex = 0,
        pedagogicalExplanation = "tan(15°) = tan(45° - 30°) = (tan 45° - tan 30°) / (1 + tan 45° tan 30°) = (1 - 1/√3) / (1 + 1/√3) = (√3 - 1) / (√3 + 1). Rationalizing denominator by multiplying (√3 - 1) gives (3 - 2√3 + 1) / 2 = 2 - √3."
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF151C2E),
            border = BorderStroke(1.dp, Color(0xFF25304C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Diagnostic Question", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2ED8A7), fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = question.prompt, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        question.options.forEachIndexed { index, option ->
            val isSelected = selectedOption == index
            val isCorrect = index == question.correctIndex
            val optionColor = when {
                !isSubmitted && isSelected -> Color(0xFF2ED8A7)
                isSubmitted && isCorrect -> Color(0xFF2ED8A7)
                isSubmitted && isSelected && !isCorrect -> Color(0xFFFF5376)
                else -> Color(0xFF151C2E)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectOption(index) },
                shape = RoundedCornerShape(12.dp),
                color = optionColor.copy(alpha = if (isSelected || (isSubmitted && isCorrect)) 0.2f else 1f),
                border = BorderStroke(1.dp, if (isSelected || (isSubmitted && isCorrect)) optionColor else Color(0xFF25304C))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${('A' + index)}. $option",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isSelected || (isSubmitted && isCorrect)) optionColor else Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (!isSubmitted) {
            Button(
                onClick = onSubmit,
                enabled = selectedOption != null,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ED8A7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Check Answer", color = Color(0xFF0B101E), fontWeight = FontWeight.Bold)
            }
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF121B30),
                border = BorderStroke(1.dp, Color(0xFF223052)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (selectedOption == question.correctIndex) "✓ Correct!" else "✗ Incorrect",
                        color = if (selectedOption == question.correctIndex) Color(0xFF2ED8A7) else Color(0xFFFF5376),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.pedagogicalExplanation,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 18.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MisconceptionContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val misconceptions = listOf(
            Pair("Confusing sin(A + B) with sin(A) + sin(B)", "Trigonometric functions are non-linear operators! sin(30° + 60°) = sin(90°) = 1, while sin(30°) + sin(60°) = 0.5 + 0.866 = 1.366 ≠ 1."),
            Pair("Misapplying the ASTC rule for Quadrant II", "Students often write cos(120°) = cos(180° - 60°) = cos(60°) = 1/2. But cosine is NEGATIVE in Quad II, so it must be -1/2!"),
            Pair("Squaring both sides without checking extraneous roots", "Squaring introduce fake solutions (e.g., if x = 2, x² = 4 also introduces x = -2). Always back-substitute into original equation.")
        )

        items(misconceptions) { (title, explanation) ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF151C2E),
                border = BorderStroke(1.dp, Color(0xFF25304C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF5376), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFF5376), fontWeight = FontWeight.Bold))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = explanation, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 18.sp))
                }
            }
        }
    }
}
