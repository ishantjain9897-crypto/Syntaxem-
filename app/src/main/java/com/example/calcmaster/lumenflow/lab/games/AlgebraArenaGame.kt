package com.example.calcmaster.lumenflow.lab.games

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class QuadraticProblem(
    val a: Int,
    val b: Int,
    val c: Int,
    val equationStr: String,
    val discriminant: Int,
    val root1Str: String,
    val root2Str: String,
    val factorP: Int,
    val factorQ: Int
)

@Composable
fun AlgebraArenaGame(
    onSendToCalculator: (String) -> Unit,
    onOpenTutor: (String, String) -> Unit,
    onBackToDirectory: () -> Unit
) {
    val problems = remember {
        listOf(
            QuadraticProblem(1, -5, 6, "x² - 5x + 6 = 0", 1, "x = 2", "x = 3", -2, -3),
            QuadraticProblem(1, 7, 12, "x² + 7x + 12 = 0", 1, "x = -3", "x = -4", 3, 4),
            QuadraticProblem(1, -2, -15, "x² - 2x - 15 = 0", 64, "x = 5", "x = -3", -5, 3),
            QuadraticProblem(1, -6, 9, "x² - 6x + 9 = 0", 0, "x = 3", "x = 3", -3, -3),
            QuadraticProblem(1, 2, 5, "x² + 2x + 5 = 0", -16, "x = -1 + 2i", "x = -1 - 2i", 0, 0)
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedRootOption by remember { mutableStateOf<Int?>(null) }
    var isVerified by remember { mutableStateOf(false) }
    var streak by remember { mutableIntStateOf(0) }

    val currentProblem = problems[currentIndex]

    // Formulate 3 plausible root options
    val rootOptions = remember(currentIndex) {
        when (currentIndex) {
            0 -> listOf("x = 2 and x = 3", "x = -2 and x = -3", "x = 1 and x = 6")
            1 -> listOf("x = -3 and x = -4", "x = 3 and x = 4", "x = -2 and x = -6")
            2 -> listOf("x = 5 and x = -3", "x = -5 and x = 3", "x = 15 and x = -1")
            3 -> listOf("x = 3 (repeated root)", "x = -3 (repeated root)", "x = 9 and x = 1")
            else -> listOf("x = -1 ± 2i (complex conjugate)", "x = 1 ± 2i", "x = -2 ± 4i")
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Navigation Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBackToDirectory) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("All Games")
                }

                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Text(
                        text = "Arena Progress: ${currentIndex + 1}/${problems.size}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Title
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ALGEBRA ARENA • FACTORING & ROOTS DUEL",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Solve quadratic equations using discriminant D = b² - 4ac and factoring rules.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Problem Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("arena_equation_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "GIVEN EQUATION:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = currentProblem.equationStr,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    // Coefficients
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("a = ${currentProblem.a}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                        Text("b = ${currentProblem.b}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                        Text("c = ${currentProblem.c}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                    }
                }
            }
        }

        // Step 1: Discriminant Inspection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "1. DISCRIMINANT (D = b² - 4ac)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "D = (${currentProblem.b})² - 4(${currentProblem.a})(${currentProblem.c}) = ${currentProblem.discriminant}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = when {
                            currentProblem.discriminant > 0 -> "• Since D > 0, the equation has two distinct real roots."
                            currentProblem.discriminant == 0 -> "• Since D = 0, the equation has a single repeated real root."
                            else -> "• Since D < 0, the equation has complex conjugate roots x = -b/(2a) ± i√|D|/(2a)."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Step 2: Select the Correct Roots
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "2. Select the verified root solution:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )

                rootOptions.forEachIndexed { index, option ->
                    val isSelected = selectedRootOption == index
                    val isCorrect = index == 0 // First option is always configured as the correct one

                    val bgColor = when {
                        !isVerified && isSelected -> MaterialTheme.colorScheme.primaryContainer
                        isVerified && isCorrect -> Color(0xFFDDEFE5)
                        isVerified && isSelected && !isCorrect -> Color(0xFFFDE8E8)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        isVerified && isCorrect -> Color(0xFF356B59)
                        isVerified && isSelected && !isCorrect -> Color(0xFFC86F58)
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isVerified) { selectedRootOption = index },
                        shape = RoundedCornerShape(8.dp),
                        color = bgColor,
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            if (isVerified) {
                                if (isCorrect) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF356B59))
                                } else if (isSelected) {
                                    Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFC86F58))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            if (!isVerified) {
                Button(
                    onClick = {
                        if (selectedRootOption != null) {
                            isVerified = true
                            if (selectedRootOption == 0) streak++
                        }
                    },
                    enabled = selectedRootOption != null,
                    modifier = Modifier.fillMaxWidth().testTag("btn_arena_verify"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Verify Roots")
                }
            } else {
                Button(
                    onClick = {
                        currentIndex = (currentIndex + 1) % problems.size
                        selectedRootOption = null
                        isVerified = false
                    },
                    modifier = Modifier.fillMaxWidth().testTag("btn_arena_next"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (currentIndex < problems.size - 1) "Next Challenge" else "Loop Arena Challenges")
                }
            }
        }

        // Calculator Hook
        item {
            OutlinedButton(
                onClick = {
                    onSendToCalculator("(-(${currentProblem.b}) + sqrt(${currentProblem.b}^2 - 4 * ${currentProblem.a} * ${currentProblem.c})) / (2 * ${currentProblem.a})")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compute Quadratic Formula in Calculator")
            }
        }
    }
}
