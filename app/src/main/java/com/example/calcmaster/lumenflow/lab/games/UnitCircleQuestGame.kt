package com.example.calcmaster.lumenflow.lab.games

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

data class UnitCircleChallenge(
    val prompt: String,
    val targetAngle: Double,
    val questionType: String, // "QUADRANT", "SIGN", "VALUE"
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@Composable
fun UnitCircleQuestGame(
    onSendToCalculator: (String) -> Unit,
    onOpenTutor: (String, String) -> Unit,
    onBackToDirectory: () -> Unit
) {
    var selectedAngleDegrees by remember { mutableDoubleStateOf(30.0) }
    var currentChallengeIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    val challenges = remember {
        listOf(
            UnitCircleChallenge(
                prompt = "What is the sign of cos(150°)?",
                targetAngle = 150.0,
                questionType = "SIGN",
                options = listOf("Positive (+)", "Negative (-)", "Zero (0)"),
                correctIndex = 1,
                explanation = "150° is in Quadrant II (90° < θ < 180°). In Quadrant II, x-coordinates are negative, so cos(θ) < 0 (ASTC: Only Sine is positive)."
            ),
            UnitCircleChallenge(
                prompt = "Which quadrant does an angle of 240° lie in?",
                targetAngle = 240.0,
                questionType = "QUADRANT",
                options = listOf("Quadrant I", "Quadrant II", "Quadrant III", "Quadrant IV"),
                correctIndex = 2,
                explanation = "Since 180° < 240° < 270°, it is located in Quadrant III, where both x (cos) and y (sin) are negative and tan is positive."
            ),
            UnitCircleChallenge(
                prompt = "What is the exact value of sin(30°)?",
                targetAngle = 30.0,
                questionType = "VALUE",
                options = listOf("1/2 (0.5)", "√3/2 (≈0.866)", "√2/2 (≈0.707)", "1"),
                correctIndex = 0,
                explanation = "In the standard 30°-60°-90° reference triangle, sin(30°) = opposite/hypotenuse = 1/2 = 0.5."
            ),
            UnitCircleChallenge(
                prompt = "What is the coordinate (cos θ, sin θ) at θ = 90°?",
                targetAngle = 90.0,
                questionType = "VALUE",
                options = listOf("(1, 0)", "(0, 1)", "(-1, 0)", "(0, -1)"),
                correctIndex = 1,
                explanation = "At 90° (π/2 rad), the terminal arm lies vertically along the positive y-axis, yielding (x, y) = (0, 1)."
            ),
            UnitCircleChallenge(
                prompt = "What is the sign of tan(315°)?",
                targetAngle = 315.0,
                questionType = "SIGN",
                options = listOf("Positive (+)", "Negative (-)", "Undefined"),
                correctIndex = 1,
                explanation = "315° is in Quadrant IV (270° < θ < 360°), where sin is negative and cos is positive. Thus tan(θ) = sin/cos is negative."
            )
        )
    }

    val activeChallenge = challenges[currentChallengeIndex]
    val rad = Math.toRadians(selectedAngleDegrees)
    val cosVal = cos(rad)
    val sinVal = sin(rad)
    val tanVal = if (abs(cosVal) < 1e-6) Double.POSITIVE_INFINITY else tan(rad)

    val standardAngles = listOf(0.0, 30.0, 45.0, 60.0, 90.0, 120.0, 135.0, 150.0, 180.0, 210.0, 225.0, 240.0, 270.0, 300.0, 315.0, 330.0, 360.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Navigation header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBackToDirectory,
                    modifier = Modifier.testTag("btn_quest_back")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("All Games")
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Score: $score / ${challenges.size}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Title and learning goal
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "UNIT CIRCLE QUEST • CBSE CLASS 11",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Master trigonometric signs, radian conversions, and (cos θ, sin θ) coordinate mapping without rote memorization.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Interactive Unit Circle Canvas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .testTag("canvas_unit_circle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val radius = size.width * 0.40f

                            // Axes
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.5f),
                                start = Offset(10f, center.y),
                                end = Offset(size.width - 10f, center.y),
                                strokeWidth = 1.5f
                            )
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.5f),
                                start = Offset(center.x, 10f),
                                end = Offset(center.x, size.height - 10f),
                                strokeWidth = 1.5f
                            )

                            // Unit Circle boundary
                            drawCircle(
                                color = Color(0xFF356B59),
                                radius = radius,
                                center = center,
                                style = Stroke(width = 2.5f)
                            )

                            // Quadrant Guidelines
                            drawCircle(
                                color = Color.Gray.copy(alpha = 0.15f),
                                radius = radius * 0.5f,
                                center = center,
                                style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f)))
                            )

                            // Angle Arm
                            val armX = center.x + (radius * cosVal).toFloat()
                            val armY = center.y - (radius * sinVal).toFloat()

                            // Projection lines (cos on x-axis, sin on y-axis)
                            drawLine(
                                color = Color(0xFFC86F58).copy(alpha = 0.7f),
                                start = Offset(armX, center.y),
                                end = Offset(armX, armY),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                            )
                            drawLine(
                                color = Color(0xFF356B59).copy(alpha = 0.7f),
                                start = Offset(center.x, armY),
                                end = Offset(armX, armY),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                            )

                            // Arm vector
                            drawLine(
                                color = Color(0xFF356B59),
                                start = center,
                                end = Offset(armX, armY),
                                strokeWidth = 3.5f
                            )

                            // Coordinate point marker
                            drawCircle(
                                color = Color(0xFFC86F58),
                                radius = 7f,
                                center = Offset(armX, armY)
                            )
                        }
                    }

                    // Live numerical readouts
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("θ Angle", style = MaterialTheme.typography.labelSmall)
                            Text(
                                "${selectedAngleDegrees.toInt()}° (${"%.2f".format(rad)} rad)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("cos θ (x)", style = MaterialTheme.typography.labelSmall)
                            Text(
                                "%.3f".format(cosVal),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF356B59))
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("sin θ (y)", style = MaterialTheme.typography.labelSmall)
                            Text(
                                "%.3f".format(sinVal),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFFC86F58))
                            )
                        }
                    }
                }
            }
        }

        // Angle quick selector chips
        item {
            Column {
                Text(
                    text = "Select / Test Angle θ:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(standardAngles) { ang ->
                        val isSel = selectedAngleDegrees.toInt() == ang.toInt()
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedAngleDegrees = ang },
                            label = { Text("${ang.toInt()}°", fontSize = 11.sp) },
                            modifier = Modifier.testTag("quest_ang_${ang.toInt()}")
                        )
                    }
                }
            }
        }

        // Active Challenge Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("quest_challenge_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHALLENGE #${currentChallengeIndex + 1} OF ${challenges.size}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        IconButton(
                            onClick = {
                                onOpenTutor("Unit Circle: ${activeChallenge.prompt}", "sin(θ), cos(θ)")
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Psychology, contentDescription = "Hint", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Text(
                        text = activeChallenge.prompt,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    // Challenge Options
                    activeChallenge.options.forEachIndexed { idx, optionText ->
                        val isSelected = selectedOptionIndex == idx
                        val isCorrect = activeChallenge.correctIndex == idx

                        val bgColor = when {
                            !isSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                            isSubmitted && isCorrect -> Color(0xFFDDEFE5)
                            isSubmitted && isSelected && !isCorrect -> Color(0xFFFDE8E8)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        val borderColor = when {
                            isSubmitted && isCorrect -> Color(0xFF356B59)
                            isSubmitted && isSelected && !isCorrect -> Color(0xFFC86F58)
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isSubmitted) {
                                    selectedOptionIndex = idx
                                    selectedAngleDegrees = activeChallenge.targetAngle
                                }
                                .testTag("quest_opt_$idx"),
                            shape = RoundedCornerShape(8.dp),
                            color = bgColor,
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                if (isSubmitted) {
                                    if (isCorrect) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF356B59))
                                    } else if (isSelected) {
                                        Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFC86F58))
                                    }
                                }
                            }
                        }
                    }

                    // Explanation & Next action
                    if (isSubmitted) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Explanation:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = activeChallenge.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (currentChallengeIndex < challenges.size - 1) {
                                    currentChallengeIndex++
                                    selectedOptionIndex = null
                                    isSubmitted = false
                                    selectedAngleDegrees = challenges[currentChallengeIndex].targetAngle
                                } else {
                                    // Loop or reset
                                    currentChallengeIndex = 0
                                    selectedOptionIndex = null
                                    isSubmitted = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("btn_quest_next"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (currentChallengeIndex < challenges.size - 1) "Next Challenge" else "Restart Quest")
                        }
                    } else {
                        Button(
                            onClick = {
                                if (selectedOptionIndex != null) {
                                    isSubmitted = true
                                    if (selectedOptionIndex == activeChallenge.correctIndex) {
                                        score++
                                    }
                                }
                            },
                            enabled = selectedOptionIndex != null,
                            modifier = Modifier.fillMaxWidth().testTag("btn_quest_submit"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Verify Answer")
                        }
                    }
                }
            }
        }

        // Quick Calculator integration hook
        item {
            OutlinedButton(
                onClick = {
                    onSendToCalculator("sin(${selectedAngleDegrees.toInt()})")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compute sin(${selectedAngleDegrees.toInt()}°) in Scientific Calculator")
            }
        }
    }
}
