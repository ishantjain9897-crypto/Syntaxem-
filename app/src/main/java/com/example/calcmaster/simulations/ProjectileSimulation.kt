package com.example.calcmaster.simulations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import kotlin.math.*

@Composable
fun ProjectileSimulation(
    onSendToCalculator: (String) -> Unit
) {
    var velocity by remember { mutableFloatStateOf(30f) } // m/s
    var angleDeg by remember { mutableFloatStateOf(45f) } // deg
    var selectedPlanet by remember { mutableStateOf("Earth (9.8 m/s²)") }

    val g = when (selectedPlanet) {
        "Moon (1.6 m/s²)" -> 1.62
        "Mars (3.7 m/s²)" -> 3.71
        else -> 9.80665
    }

    val angleRad = Math.toRadians(angleDeg.toDouble())
    val range = (velocity * velocity * sin(2 * angleRad)) / g
    val maxHeight = (velocity * velocity * sin(angleRad).pow(2)) / (2 * g)
    val timeOfFlight = (2 * velocity * sin(angleRad)) / g

    // Animation progress (0f to 1f)
    var isSimulating by remember { mutableStateOf(false) }
    var animProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isSimulating) {
        if (isSimulating) {
            animProgress = 0f
            val startTime = System.currentTimeMillis()
            val totalDurationMs = (timeOfFlight * 1000).coerceIn(800.0, 4000.0).toLong()

            while (isSimulating && animProgress < 1f) {
                withFrameNanos {
                    val elapsed = System.currentTimeMillis() - startTime
                    animProgress = (elapsed.toFloat() / totalDurationMs).coerceIn(0f, 1f)
                    if (animProgress >= 1f) isSimulating = false
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("projectile_sim_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Projectile Motion Lab",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "CBSE Class 11 Kinematics in a Plane",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalButton(
                    onClick = {
                        isSimulating = true
                    },
                    modifier = Modifier.testTag("projectile_launch_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isSimulating) "Flight..." else "Launch")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Trajectory Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF0B132B), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.BottomStart
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val padBottom = 24.dp.toPx()
                    val padLeft = 24.dp.toPx()
                    val padRight = 16.dp.toPx()
                    val padTop = 16.dp.toPx()

                    val graphWidth = size.width - padLeft - padRight
                    val graphHeight = size.height - padBottom - padTop

                    // Ground line
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(0f, size.height - padBottom),
                        end = Offset(size.width, size.height - padBottom),
                        strokeWidth = 3f
                    )

                    // Axes ticks
                    drawLine(
                        color = Color(0xFF38BDF8),
                        start = Offset(padLeft, size.height - padBottom),
                        end = Offset(padLeft + graphWidth, size.height - padBottom),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0xFF38BDF8),
                        start = Offset(padLeft, size.height - padBottom),
                        end = Offset(padLeft, padTop),
                        strokeWidth = 2f
                    )

                    // Draw full parabolic curve
                    val path = Path()
                    val numPoints = 80
                    val maxT = timeOfFlight

                    // Scale factors: map range -> graphWidth, maxHeight -> graphHeight * 0.8
                    val scaleX = if (range > 0) graphWidth / range.toFloat() else 1f
                    val scaleY = if (maxHeight > 0) (graphHeight * 0.75f) / maxHeight.toFloat() else 1f

                    path.moveTo(padLeft, size.height - padBottom)
                    for (i in 1..numPoints) {
                        val t = (i.toFloat() / numPoints) * maxT
                        val x = (velocity * cos(angleRad) * t).toFloat()
                        val y = (velocity * sin(angleRad) * t - 0.5 * g * t * t).toFloat()

                        val cx = padLeft + x * scaleX
                        val cy = (size.height - padBottom) - (y * scaleY)
                        path.lineTo(cx, cy)
                    }

                    drawPath(
                        path = path,
                        color = Color(0x6638BDF8),
                        style = Stroke(width = 2.5f)
                    )

                    // Apex Marker
                    val apexX = padLeft + (range.toFloat() / 2f) * scaleX
                    val apexY = (size.height - padBottom) - (maxHeight.toFloat() * scaleY)
                    drawCircle(color = Color(0xFFF59E0B), radius = 4.dp.toPx(), center = Offset(apexX, apexY))

                    // Animated projectile position
                    val currentT = animProgress * maxT
                    val curX = (velocity * cos(angleRad) * currentT).toFloat()
                    val curY = (velocity * sin(angleRad) * currentT - 0.5 * g * currentT * currentT).coerceAtLeast(0.0).toFloat()

                    val ballX = padLeft + curX * scaleX
                    val ballY = (size.height - padBottom) - (curY * scaleY)

                    // Draw glow & ball
                    drawCircle(color = Color(0x44F43F5E), radius = 10.dp.toPx(), center = Offset(ballX, ballY))
                    drawCircle(color = Color(0xFFF43F5E), radius = 6.dp.toPx(), center = Offset(ballX, ballY))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("v₀: ${velocity.toInt()} m/s", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(80.dp))
                Slider(
                    value = velocity,
                    onValueChange = { velocity = it },
                    valueRange = 10f..60f,
                    modifier = Modifier.weight(1f).testTag("projectile_v0_slider")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("θ: ${angleDeg.toInt()}°", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(80.dp))
                Slider(
                    value = angleDeg,
                    onValueChange = { angleDeg = it },
                    valueRange = 15f..80f,
                    modifier = Modifier.weight(1f).testTag("projectile_angle_slider")
                )
            }

            // Planet selector chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Earth (9.8 m/s²)", "Moon (1.6 m/s²)", "Mars (3.7 m/s²)").forEach { planet ->
                    FilterChip(
                        selected = selectedPlanet == planet,
                        onClick = { selectedPlanet = planet },
                        label = { Text(planet.split(" ")[0], style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Results summary cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ResultBox(title = "Range (R)", value = String.format("%.1f m", range), color = Color(0xFF38BDF8))
                ResultBox(title = "Max Height (H)", value = String.format("%.1f m", maxHeight), color = Color(0xFFF59E0B))
                ResultBox(title = "Flight Time (T)", value = String.format("%.2f s", timeOfFlight), color = Color(0xFF10B981))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { onSendToCalculator("projectile u=${velocity.toInt()} theta=${angleDeg.toInt()}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("projectile_send_calc_btn")
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Parameters to Kinematics Solver")
            }
        }
    }
}

@Composable
private fun ResultBox(title: String, value: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = color)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
    }
}
