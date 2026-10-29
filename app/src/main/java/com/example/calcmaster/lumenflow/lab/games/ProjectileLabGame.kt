package com.example.calcmaster.lumenflow.lab.games

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun ProjectileLabGame(
    onSendToCalculator: (String) -> Unit,
    onOpenTutor: (String, String) -> Unit,
    onBackToDirectory: () -> Unit
) {
    var velocity by remember { mutableDoubleStateOf(20.0) }
    var angleDegrees by remember { mutableDoubleStateOf(45.0) }
    var targetDistance by remember { mutableDoubleStateOf(40.8) }
    var isLaunched by remember { mutableStateOf(false) }
    var hitsCount by remember { mutableIntStateOf(0) }
    var totalAttempts by remember { mutableIntStateOf(0) }

    val g = 9.80665
    val rad = Math.toRadians(angleDegrees)
    val timeOfFlight = (2 * velocity * sin(rad)) / g
    val maxHeight = (velocity * sin(rad)).pow(2) / (2 * g)
    val range = (velocity.pow(2) * sin(2 * rad)) / g

    val distanceDiff = range - targetDistance
    val isHit = isLaunched && abs(distanceDiff) <= 2.0

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
                    modifier = Modifier.testTag("btn_proj_back")
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
                        text = "Target Hits: $hitsCount / $totalAttempts",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Title and description
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PROJECTILE LAB: ARTILLERY TARGET CHALLENGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Target is located at distance R = ${"%.1f".format(targetDistance)} m. Adjust initial velocity v₀ and angle θ to hit within the ±2.0 m zone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Trajectory Canvas
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
                            .testTag("canvas_projectile_sim")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val groundY = size.height - 30f
                            val startX = 40f
                            val maxDisplayDistance = 80f // 80 meters scale
                            val scaleX = (size.width - 80f) / maxDisplayDistance
                            val scaleY = (groundY - 20f) / 40f // 40m max height scale

                            // Ground plane
                            drawLine(
                                color = Color(0xFF356B59),
                                start = Offset(10f, groundY),
                                end = Offset(size.width - 10f, groundY),
                                strokeWidth = 3f
                            )

                            // Target Flagpole & Zone
                            val targetCanvasX = startX + (targetDistance.toFloat() * scaleX)
                            drawLine(
                                color = Color(0xFFC86F58),
                                start = Offset(targetCanvasX, groundY),
                                end = Offset(targetCanvasX, groundY - 50f),
                                strokeWidth = 2.5f
                            )
                            drawCircle(
                                color = Color(0xFFC86F58),
                                radius = 8f,
                                center = Offset(targetCanvasX, groundY - 50f)
                            )

                            // Target landing tolerance zone
                            val tolerancePixels = 2.0f * scaleX
                            drawLine(
                                color = Color(0xFFC86F58).copy(alpha = 0.4f),
                                start = Offset(targetCanvasX - tolerancePixels, groundY),
                                end = Offset(targetCanvasX + tolerancePixels, groundY),
                                strokeWidth = 8f
                            )

                            // Cannon launcher
                            drawCircle(
                                color = Color(0xFF356B59),
                                radius = 10f,
                                center = Offset(startX, groundY)
                            )
                            val cannonLen = 22f
                            val cannonEndX = startX + cannonLen * cos(rad).toFloat()
                            val cannonEndY = groundY - cannonLen * sin(rad).toFloat()
                            drawLine(
                                color = Color(0xFF131A17),
                                start = Offset(startX, groundY),
                                end = Offset(cannonEndX, cannonEndY),
                                strokeWidth = 5f
                            )

                            // Parabolic Trajectory
                            if (isLaunched) {
                                val path = Path()
                                path.moveTo(startX, groundY)
                                val steps = 60
                                for (i in 1..steps) {
                                    val t = (timeOfFlight * i / steps).toDouble()
                                    val currentX = velocity * cos(rad) * t
                                    val currentY = (velocity * sin(rad) * t) - (0.5 * g * t * t)

                                    val canvasX = startX + (currentX.toFloat() * scaleX)
                                    val canvasY = groundY - (max(0.0, currentY).toFloat() * scaleY)
                                    path.lineTo(canvasX, canvasY)
                                }

                                drawPath(
                                    path = path,
                                    color = if (isHit) Color(0xFF356B59) else Color(0xFFC86F58),
                                    style = Stroke(width = 3f)
                                )

                                // Landing impact dot
                                val impactCanvasX = startX + (range.toFloat() * scaleX)
                                drawCircle(
                                    color = if (isHit) Color(0xFF356B59) else Color(0xFFC86F58),
                                    radius = 7f,
                                    center = Offset(impactCanvasX, groundY)
                                )
                            }
                        }
                    }

                    // Readout metrics
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Target Distance", style = MaterialTheme.typography.labelSmall)
                            Text("${"%.1f".format(targetDistance)} m", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Calculated Range R", style = MaterialTheme.typography.labelSmall)
                            Text(
                                if (isLaunched) "${"%.2f".format(range)} m" else "--",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isHit) Color(0xFF356B59) else if (isLaunched) Color(0xFFC86F58) else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Apex Height H", style = MaterialTheme.typography.labelSmall)
                            Text("${"%.1f".format(maxHeight)} m", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                        }
                    }
                }
            }
        }

        // Controls: Velocity & Angle sliders
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Launch Velocity v₀:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Text("${"%.1f".format(velocity)} m/s", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = velocity.toFloat(),
                        onValueChange = {
                            velocity = it.toDouble()
                            isLaunched = false
                        },
                        valueRange = 10f..35f,
                        modifier = Modifier.testTag("slider_proj_vel")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Launch Angle θ:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Text("${angleDegrees.toInt()}°", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = angleDegrees.toFloat(),
                        onValueChange = {
                            angleDegrees = it.toDouble()
                            isLaunched = false
                        },
                        valueRange = 15f..75f,
                        modifier = Modifier.testTag("slider_proj_ang")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                isLaunched = true
                                totalAttempts++
                                if (abs(range - targetDistance) <= 2.0) {
                                    hitsCount++
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("btn_proj_fire"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Launch Shot")
                        }

                        OutlinedButton(
                            onClick = {
                                // Generate a new random target between 25m and 65m
                                val targets = listOf(28.5, 34.2, 42.0, 50.5, 58.0, 64.5)
                                targetDistance = targets.filter { it != targetDistance }.random()
                                isLaunched = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_proj_new_target")
                        ) {
                            Text("New Target")
                        }
                    }
                }
            }
        }

        // Feedback / Result Card
        if (isLaunched) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isHit) Color(0xFFDDEFE5) else Color(0xFFFDE8E8)
                    ),
                    border = BorderStroke(1.5.dp, if (isHit) Color(0xFF356B59) else Color(0xFFC86F58))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isHit) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isHit) Color(0xFF356B59) else Color(0xFFC86F58)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHit) "TARGET HIT! DIRECT IMPACT" else "SHOT MISSED BY ${"%.1f".format(abs(distanceDiff))} m",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHit) Color(0xFF356B59) else Color(0xFFC86F58)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHit) {
                                "Excellent parameter tuning! Range R = (v₀² sin 2θ)/g matched $targetDistance m within the tolerance zone."
                            } else if (distanceDiff < 0) {
                                "Fell short by ${"%.1f".format(abs(distanceDiff))} m. Since R depends on v₀² sin(2θ), consider increasing initial velocity v₀ or adjusting θ towards 45°."
                            } else {
                                "Overshot by ${"%.1f".format(distanceDiff)} m. Consider decreasing initial velocity v₀ or moving θ away from 45°."
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Calculator hook
        item {
            OutlinedButton(
                onClick = {
                    onSendToCalculator("${"%.1f".format(velocity)}^2 * sin(2 * ${angleDegrees.toInt()}) / 9.80665")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compute Range in Scientific Calculator")
            }
        }
    }
}
