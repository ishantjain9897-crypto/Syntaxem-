package com.example.calcmaster.lumenflow.lab.games

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

data class GatePuzzle(
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double,
    val expectedSlope: Double,
    val expectedIntercept: Double,
    val description: String
)

@Composable
fun GraphEscapeGame(
    onSendToCalculator: (String) -> Unit,
    onOpenTutor: (String, String) -> Unit,
    onBackToDirectory: () -> Unit
) {
    val puzzles = remember {
        listOf(
            GatePuzzle(1.0, 3.0, 4.0, 9.0, 2.0, 1.0, "Pass through Alpha Gate at (1, 3) and (4, 9)"),
            GatePuzzle(0.0, 2.0, 3.0, 8.0, 2.0, 2.0, "Pass through Beta Gate at (0, 2) and (3, 8)"),
            GatePuzzle(2.0, 4.0, 5.0, 1.0, -1.0, 6.0, "Pass through Gamma Gate at (2, 4) and (5, 1)")
        )
    }

    var currentPuzzleIndex by remember { mutableIntStateOf(0) }
    val puzzle = puzzles[currentPuzzleIndex]

    var inputSlope by remember { mutableDoubleStateOf(1.0) }
    var inputIntercept by remember { mutableDoubleStateOf(0.0) }
    var isAttempted by remember { mutableStateOf(false) }

    val slopeError = abs(inputSlope - puzzle.expectedSlope)
    val interceptError = abs(inputIntercept - puzzle.expectedIntercept)
    val isUnlocked = isAttempted && slopeError < 0.15 && interceptError < 0.25

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

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isUnlocked) Color(0xFFDDEFE5) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (isUnlocked) "BARRIER UNLOCKED!" else "BARRIER LOCKED",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color(0xFF356B59) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                        text = "GRAPH ESCAPE • COORDINATE GEOMETRY BARRIER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Align a line beam y = mx + c through target coordinates (${puzzle.x1.toInt()}, ${puzzle.y1.toInt()}) and (${puzzle.x2.toInt()}, ${puzzle.y2.toInt()}) to bridge the gap.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Canvas Coordinate Plane
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .testTag("canvas_graph_escape")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val originX = 50f
                            val originY = size.height - 30f
                            val gridScale = 22f

                            // Axes
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.5f),
                                start = Offset(originX, 10f),
                                end = Offset(originX, size.height - 10f),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.5f),
                                start = Offset(10f, originY),
                                end = Offset(size.width - 10f, originY),
                                strokeWidth = 2f
                            )

                            // Checkpoint 1
                            val cp1X = originX + (puzzle.x1.toFloat() * gridScale)
                            val cp1Y = originY - (puzzle.y1.toFloat() * gridScale)
                            drawCircle(color = Color(0xFFC86F58), radius = 8f, center = Offset(cp1X, cp1Y))

                            // Checkpoint 2
                            val cp2X = originX + (puzzle.x2.toFloat() * gridScale)
                            val cp2Y = originY - (puzzle.y2.toFloat() * gridScale)
                            drawCircle(color = Color(0xFFC86F58), radius = 8f, center = Offset(cp2X, cp2Y))

                            // Student's line y = mx + c
                            val xStart = 0f
                            val yStart = (inputSlope * xStart + inputIntercept).toFloat()
                            val xEnd = 8f
                            val yEnd = (inputSlope * xEnd + inputIntercept).toFloat()

                            val canvasStart = Offset(originX + xStart * gridScale, originY - yStart * gridScale)
                            val canvasEnd = Offset(originX + xEnd * gridScale, originY - yEnd * gridScale)

                            drawLine(
                                color = if (isUnlocked) Color(0xFF356B59) else Color(0xFF131A17),
                                start = canvasStart,
                                end = canvasEnd,
                                strokeWidth = if (isUnlocked) 4.5f else 3f
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text("Point A: (${puzzle.x1.toInt()}, ${puzzle.y1.toInt()})", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                        Text("Point B: (${puzzle.x2.toInt()}, ${puzzle.y2.toInt()})", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Slope & Intercept Sliders
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Slope m = Δy/Δx:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Text("${"%.2f".format(inputSlope)}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = inputSlope.toFloat(),
                        onValueChange = {
                            inputSlope = it.toDouble()
                            isAttempted = false
                        },
                        valueRange = -3f..4f,
                        modifier = Modifier.testTag("slider_slope")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Y-Intercept c:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Text("${"%.2f".format(inputIntercept)}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = inputIntercept.toFloat(),
                        onValueChange = {
                            inputIntercept = it.toDouble()
                            isAttempted = false
                        },
                        valueRange = -2f..8f,
                        modifier = Modifier.testTag("slider_intercept")
                    )

                    Button(
                        onClick = { isAttempted = true },
                        modifier = Modifier.fillMaxWidth().testTag("btn_test_beam"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isUnlocked) "Bridge Active!" else "Test Beam Alignment")
                    }
                }
            }
        }

        // Result & Next Gate
        if (isAttempted) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) Color(0xFFDDEFE5) else Color(0xFFFDE8E8)
                    ),
                    border = BorderStroke(1.5.dp, if (isUnlocked) Color(0xFF356B59) else Color(0xFFC86F58))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isUnlocked) "GATE UNLOCKED!" else "BEAM MISALIGNED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color(0xFF356B59) else Color(0xFFC86F58)
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isUnlocked) {
                                "Perfect derivation! Slope m = (${puzzle.y2} - ${puzzle.y1}) / (${puzzle.x2} - ${puzzle.x1}) = ${puzzle.expectedSlope}, and intercept c = ${puzzle.expectedIntercept}."
                            } else {
                                "Hint: Slope m = (y₂ - y₁) / (x₂ - x₁) = (${puzzle.y2.toInt()} - ${puzzle.y1.toInt()}) / (${puzzle.x2.toInt()} - ${puzzle.x1.toInt()}). Check your intercept c by substituting (x₁, y₁)."
                            },
                            style = MaterialTheme.typography.bodySmall
                        )

                        if (isUnlocked && currentPuzzleIndex < puzzles.size - 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    currentPuzzleIndex++
                                    isAttempted = false
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Advance to Next Barrier")
                            }
                        }
                    }
                }
            }
        }

        // Calculator Hook
        item {
            OutlinedButton(
                onClick = {
                    onSendToCalculator("(${puzzle.y2} - ${puzzle.y1}) / (${puzzle.x2} - ${puzzle.x1})")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compute Slope in Scientific Calculator")
            }
        }
    }
}
