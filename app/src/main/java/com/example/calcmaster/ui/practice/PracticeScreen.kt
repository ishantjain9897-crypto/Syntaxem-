package com.example.calcmaster.ui.practice

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import com.example.calcmaster.model.ConceptMastery

data class PracticeQuestion(
    val id: String,
    val topic: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class ErrorNotebookEntry(
    val id: String,
    val topic: String,
    val failedQuestion: String,
    val studentMistake: String,
    val correctiveInsight: String,
    var isResolved: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    onNavigateToGame: (String) -> Unit = {},
    onSendToCalculator: (String) -> Unit = {}
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Daily Practice, 1: Topic Map, 2: Error Notebook, 3: Spaced Repetition

    // CBSE/NCERT Class 11 Topics Map
    val mathTopics = remember {
        listOf(
            ConceptMastery("m_01", "Sets & Functions", "Mathematics", 0.95f, 20, 19),
            ConceptMastery("m_02", "Trigonometric Functions", "Mathematics", 0.85f, 30, 25),
            ConceptMastery("m_03", "Complex Numbers & Quadratics", "Mathematics", 0.90f, 25, 23),
            ConceptMastery("m_04", "Linear Inequalities", "Mathematics", 0.80f, 15, 12),
            ConceptMastery("m_05", "Permutations & Combinations", "Mathematics", 0.65f, 18, 12),
            ConceptMastery("m_06", "Binomial Theorem", "Mathematics", 0.70f, 14, 10),
            ConceptMastery("m_07", "Sequences & Series", "Mathematics", 0.85f, 22, 19),
            ConceptMastery("m_08", "Straight Lines & Conic Sections", "Mathematics", 0.75f, 28, 21),
            ConceptMastery("m_09", "Limits & Derivatives", "Mathematics", 0.60f, 16, 10)
        )
    }

    val physicsTopics = remember {
        listOf(
            ConceptMastery("p_01", "Units & Measurements", "Physics", 0.95f, 20, 19),
            ConceptMastery("p_02", "Motion in a Straight Line", "Physics", 0.90f, 24, 22),
            ConceptMastery("p_03", "Motion in a Plane (Projectiles)", "Physics", 0.70f, 20, 14),
            ConceptMastery("p_04", "Laws of Motion & Friction", "Physics", 0.65f, 18, 12),
            ConceptMastery("p_05", "Work, Energy & Power", "Physics", 0.80f, 22, 18),
            ConceptMastery("p_06", "Rotational Motion", "Physics", 0.55f, 12, 7),
            ConceptMastery("p_07", "Gravitation", "Physics", 0.85f, 15, 13),
            ConceptMastery("p_08", "Oscillations & Waves (SHM)", "Physics", 0.75f, 16, 12)
        )
    }

    // Adaptive Question State
    val dailyQuestions = remember {
        listOf(
            PracticeQuestion(
                id = "q1",
                topic = "Trigonometric Identities",
                question = "What is the exact value of sin(15°) using the difference formula sin(45° - 30°)?",
                options = listOf("(√6 - √2) / 4", "(√6 + √2) / 4", "(√3 - 1) / 2", "1 / 4"),
                correctIndex = 0,
                explanation = "sin(45° - 30°) = sin(45°)cos(30°) - cos(45°)sin(30°) = (√2/2)(√3/2) - (√2/2)(1/2) = (√6 - √2)/4."
            ),
            PracticeQuestion(
                id = "q2",
                topic = "Quadratic Equations",
                question = "For what discriminant value Δ does 2x² - kx + 8 = 0 have equal real roots?",
                options = listOf("k = ±8", "k = ±4", "k = ±16", "k = 0"),
                correctIndex = 0,
                explanation = "Equal roots imply discriminant Δ = b² - 4ac = 0. Here k² - 4(2)(8) = k² - 64 = 0 → k = ±8."
            ),
            PracticeQuestion(
                id = "q3",
                topic = "Projectile Motion",
                question = "At what launch angle θ is the horizontal range of a projectile maximum on level ground?",
                options = listOf("45°", "30°", "60°", "90°"),
                correctIndex = 0,
                explanation = "Range R = (v₀² sin(2θ)) / g. The maximum value of sin(2θ) is 1, which occurs when 2θ = 90° → θ = 45°."
            )
        )
    }

    var activeQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var answerSubmitted by remember { mutableStateOf(false) }

    // Error Notebook Entries
    var errorEntries by remember {
        mutableStateOf(
            listOf(
                ErrorNotebookEntry(
                    id = "err_1",
                    topic = "Quadratic Discriminant",
                    failedQuestion = "Find nature of roots of 3x² - 2x + 1 = 0",
                    studentMistake = "Calculated Δ = (-2)² - 4(3)(1) = -4 - 12 = -16. Forgot that (-2)² = +4.",
                    correctiveInsight = "Squaring any real number yields a positive result. (-2)² = +4, so Δ = 4 - 12 = -8 (Complex roots).",
                    isResolved = false
                ),
                ErrorNotebookEntry(
                    id = "err_2",
                    topic = "Trigonometric Angle Modes",
                    failedQuestion = "Evaluate sin(60) in kinematics calculation",
                    studentMistake = "Calculator was in RAD mode instead of DEG mode, giving -0.304 instead of √3/2 ≈ 0.866.",
                    correctiveInsight = "Always check the top-bar angle mode badge (DEG / RAD) before computing trigonometric values in physics.",
                    isResolved = true
                ),
                ErrorNotebookEntry(
                    id = "err_3",
                    topic = "Projectile Max Height vs Range",
                    failedQuestion = "Formula for maximum projectile height",
                    studentMistake = "Confused H_max = (v₀² sin²θ)/(2g) with R = (v₀² sin 2θ)/g.",
                    correctiveInsight = "Max height depends on vertical velocity component squared divided by 2g. Range uses the double-angle identity sin(2θ).",
                    isResolved = false
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Section Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            edgePadding = 8.dp,
            modifier = Modifier.testTag("practice_section_tabs")
        ) {
            val tabs = listOf(
                "Daily Practice" to Icons.Default.Quiz,
                "Class 11 Topic Map" to Icons.Default.AccountTree,
                "Error Notebook" to Icons.Default.BookmarkBorder,
                "Spaced Repetition" to Icons.Default.Update
            )
            tabs.forEachIndexed { index, (title, icon) ->
                Tab(
                    selected = selectedSection == index,
                    onClick = { selectedSection = index },
                    text = { Text(title, style = MaterialTheme.typography.labelMedium) },
                    icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedSection) {
                // SECTION 0: DAILY ADAPTIVE PRACTICE
                0 -> {
                    val currentQ = dailyQuestions[activeQuestionIndex]
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = currentQ.topic,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Text(
                                            text = "Question ${activeQuestionIndex + 1} of ${dailyQuestions.size}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = currentQ.question,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 22.sp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Options
                                    currentQ.options.forEachIndexed { optIndex, optionText ->
                                        val isSelected = selectedOptionIndex == optIndex
                                        val isCorrect = currentQ.correctIndex == optIndex
                                        val buttonColor = if (answerSubmitted) {
                                            if (isCorrect) Color(0xFF10B981) else if (isSelected) Color(0xFFEF4444) else MaterialTheme.colorScheme.surfaceVariant
                                        } else {
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        }

                                        Surface(
                                            onClick = {
                                                if (!answerSubmitted) {
                                                    selectedOptionIndex = optIndex
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            color = buttonColor,
                                            border = BorderStroke(
                                                1.dp,
                                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${('A' + optIndex)}. ",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    text = optionText,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontFamily = FontFamily.Monospace,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                if (answerSubmitted && isCorrect) {
                                                    Icon(Icons.Default.Check, contentDescription = "Correct", tint = Color.White)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Action Button: Check Answer or Next Question
                                    if (!answerSubmitted) {
                                        Button(
                                            onClick = { answerSubmitted = true },
                                            enabled = selectedOptionIndex != null,
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Verify Answer")
                                        }
                                    } else {
                                        Column {
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Text(
                                                        text = if (selectedOptionIndex == currentQ.correctIndex) "✓ Correct Solution" else "✗ Review Required",
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (selectedOptionIndex == currentQ.correctIndex) Color(0xFF10B981) else Color(0xFFEF4444)
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = currentQ.explanation,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Button(
                                                onClick = {
                                                    activeQuestionIndex = (activeQuestionIndex + 1) % dailyQuestions.size
                                                    selectedOptionIndex = null
                                                    answerSubmitted = false
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("Next Challenge")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // SECTION 1: CLASS 11 TOPIC MAP
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Mathematics Syllabus",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        items(mathTopics, key = { it.topicId }) { topic ->
                            TopicCard(topic = topic)
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Physics Syllabus",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        items(physicsTopics, key = { it.topicId }) { topic ->
                            TopicCard(topic = topic)
                        }
                    }
                }

                // SECTION 2: ERROR NOTEBOOK
                2 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Private Misconception Vault",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Captures personal blindspots, sign errors, and formula confusions for targeted revision without judgement.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }

                        items(errorEntries, key = { it.id }) { entry ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(
                                    1.dp,
                                    if (entry.isResolved) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFEF4444).copy(alpha = 0.4f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = entry.topic,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (entry.isResolved) "Resolved" else "Needs Review",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (entry.isResolved) Color(0xFF10B981) else Color(0xFFEF4444),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Checkbox(
                                                checked = entry.isResolved,
                                                onCheckedChange = { checked ->
                                                    errorEntries = errorEntries.map {
                                                        if (it.id == entry.id) it.copy(isResolved = checked) else it
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = entry.failedQuestion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Mistake: ${entry.studentMistake}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFEF4444)
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Insight: ${entry.correctiveInsight}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // SECTION 3: SPACED REPETITION
                3 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "SuperMemo 2 / Leitner Spaced Intervals",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Optimal revision schedule calculated from your past accuracy. Revisit high-yield Class 11 formulas before memory decay sets in.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))

                                    val intervals = listOf(
                                        "Due Today (1-Day Interval)" to "Trigonometric Compound Angles (sin(A+B), cos(A+B))",
                                        "Due in 3 Days (3-Day Interval)" to "Horizontal Projectile Velocity Components",
                                        "Due in 7 Days (7-Day Interval)" to "Conic Sections Focus-Directrix Eccentricity",
                                        "Mastered (14-Day Interval)" to "Newton's Second Law & Friction Limits"
                                    )

                                    intervals.forEachIndexed { idx, (header, topic) ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Schedule,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = header,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text(
                                                        text = topic,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Medium
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
        }
    }
}

@Composable
fun TopicCard(topic: ConceptMastery) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topic.topicName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${topic.category} • ${topic.correctAttempts}/${topic.totalAttempts} correct",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${(topic.masteryScore * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (topic.masteryScore >= 0.8f) Color(0xFF10B981) else Color(0xFFF59E0B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { topic.masteryScore },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (topic.masteryScore >= 0.8f) Color(0xFF10B981) else Color(0xFFF59E0B),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
