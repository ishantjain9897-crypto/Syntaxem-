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

@Composable
fun ForceBuilderGame(
    onSendToCalculator: (String) -> Unit,
    onOpenTutor: (String, String) -> Unit,
    onBackToDirectory: () -> Unit
) {
    var mass by remember { mutableDoubleStateOf(5.0) } // kg
    var muStatic by remember { mutableDoubleStateOf(0.4) }
    var muKinetic by remember { mutableDoubleStateOf(0.28) }
    var appliedForce by remember { mutableDoubleStateOf(15.0) } // N

    val g = 9.80665
    val normalForce = mass * g
    val maxStaticFriction = muStatic * normalForce
    val kineticFriction = muKinetic * normalForce

    val isMoving = appliedForce > maxStaticFriction
    val actualFriction = if (isMoving) kineticFriction else min(appliedForce, maxStaticFriction)
    val netForce = if (isMoving) (appliedForce - kineticFriction) else 0.0
    val acceleration = if (isMoving) netForce / mass else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Navigation
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
                    color = if (isMoving) Color(0xFFDDEFE5) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (isMoving) "STATUS: IN MOTION (ACCELERATING)" else "STATUS: STATIC EQUILIBRIUM",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isMoving) Color(0xFF356B59) else MaterialTheme.colorScheme.onSurfaceVariant
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
                        text = "FORCE & MOTION BUILDER • STATIC VS KINETIC FRICTION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Find the exact critical force required to overcome static friction f_s,max = μ_s · N and initiate block motion.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Free Body Diagram Canvas
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
                            .height(180.dp)
                            .testTag("canvas_fbd_builder")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val centerX = size.width / 2f
                            val floorY = size.height - 40f
                            val blockWidth = 90f
                            val blockHeight = 60f
                            val blockX = centerX - (blockWidth / 2f)
                            val blockY = floorY - blockHeight

                            // Floor
                            drawLine(
                                color = Color(0xFF356B59),
                                start = Offset(20f, floorY),
                                end = Offset(size.width - 20f, floorY),
                                strokeWidth = 3f
                            )

                            // Block
                            drawRect(
                                color = if (isMoving) Color(0xFF356B59).copy(alpha = 0.85f) else Color(0xFF131A17).copy(alpha = 0.75f),
                                topLeft = Offset(blockX, blockY),
                                size = androidx.compose.ui.geometry.Size(blockWidth, blockHeight)
                            )

                            // Applied Force Vector (Right arrow)
                            val appliedVectorLen = (appliedForce.toFloat() * 2.8f).coerceIn(10f, 120f)
                            drawLine(
                                color = Color(0xFF356B59),
                                start = Offset(blockX + blockWidth, blockY + blockHeight / 2f),
                                end = Offset(blockX + blockWidth + appliedVectorLen, blockY + blockHeight / 2f),
                                strokeWidth = 4f
                            )

                            // Friction Force Vector (Left arrow)
                            val frictionVectorLen = (actualFriction.toFloat() * 2.8f).coerceIn(10f, 120f)
                            drawLine(
                                color = Color(0xFFC86F58),
                                start = Offset(blockX, floorY - 5f),
                                end = Offset(blockX - frictionVectorLen, floorY - 5f),
                                strokeWidth = 4f
                            )

                            // Normal Force (Up arrow)
                            drawLine(
                                color = Color(0xFF4A6572),
                                start = Offset(centerX, blockY),
                                end = Offset(centerX, blockY - 45f),
                                strokeWidth = 3f
                            )

                            // Gravity mg (Down arrow)
                            drawLine(
                                color = Color(0xFF4A6572),
                                start = Offset(centerX, blockY + blockHeight),
                                end = Offset(centerX, blockY + blockHeight + 35f),
                                strokeWidth = 3f
                            )
                        }
                    }

                    // Vector Readouts
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Applied F", style = MaterialTheme.typography.labelSmall)
                            Text("${"%.1f".format(appliedForce)} N", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF356B59)))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Friction f", style = MaterialTheme.typography.labelSmall)
                            Text("${"%.1f".format(actualFriction)} N", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFFC86F58)))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Max Static f_s", style = MaterialTheme.typography.labelSmall)
                            Text("${"%.1f".format(maxStaticFriction)} N", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Acceleration a", style = MaterialTheme.typography.labelSmall)
                            Text("${"%.2f".format(acceleration)} m/s²", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                        }
                    }
                }
            }
        }

        // Interactive Sliders
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Applied Force F_applied:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Text("${"%.1f".format(appliedForce)} N", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = appliedForce.toFloat(),
                        onValueChange = { appliedForce = it.toDouble() },
                        valueRange = 0f..40f,
                        modifier = Modifier.testTag("slider_applied_force")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Static Friction Coeff μ_s:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Text("${"%.2f".format(muStatic)}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = muStatic.toFloat(),
                        onValueChange = {
                            muStatic = it.toDouble()
                            if (muKinetic > muStatic) muKinetic = muStatic * 0.75
                        },
                        valueRange = 0.1f..0.8f
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Block Mass m:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Text("${"%.1f".format(mass)} kg", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = mass.toFloat(),
                        onValueChange = { mass = it.toDouble() },
                        valueRange = 1f..10f
                    )
                }
            }
        }

        // Conceptual Insight Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "CRITICAL INSIGHT • CBSE CLASS 11 LAWS OF MOTION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = if (!isMoving) {
                            "Static friction is self-adjusting! Until applied force exceeds f_s,max (${"%.1f".format(maxStaticFriction)} N), friction matches applied force exactly, keeping net acceleration at a = 0."
                        } else {
                            "Motion initiated! Since F_applied (${"%.1f".format(appliedForce)} N) > f_s,max (${"%.1f".format(maxStaticFriction)} N), friction drops from static threshold to kinetic friction f_k (${"%.1f".format(kineticFriction)} N). Acceleration is a = (${"%.1f".format(appliedForce)} - ${"%.1f".format(kineticFriction)}) / $mass = ${"%.2f".format(acceleration)} m/s²."
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Calculator Hook
        item {
            OutlinedButton(
                onClick = {
                    onSendToCalculator("($appliedForce - $muKinetic * $mass * 9.80665) / $mass")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compute Acceleration in Scientific Calculator")
            }
        }
    }
}
