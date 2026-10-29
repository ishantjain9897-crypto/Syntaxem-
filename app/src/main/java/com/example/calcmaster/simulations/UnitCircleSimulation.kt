package com.example.calcmaster.simulations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun UnitCircleSimulation(
    onSendToCalculator: (String) -> Unit
) {
    var angleDeg by remember { mutableFloatStateOf(45f) }
    var isAutoPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isAutoPlaying) {
        if (isAutoPlaying) {
            while (isAutoPlaying) {
                withFrameNanos {
                    angleDeg = (angleDeg + 0.8f) % 360f
                }
            }
        }
    }

    val angleRad = Math.toRadians(angleDeg.toDouble())
    val cosVal = cos(angleRad)
    val sinVal = sin(angleRad)
    val tanVal = if (abs(cosVal) > 1e-4) tan(angleRad) else Double.NaN

    val quadrant = when {
        angleDeg in 0f..90f -> "Q1 (All +)"
        angleDeg in 90f..180f -> "Q2 (Sin +)"
        angleDeg in 180f..270f -> "Q3 (Tan +)"
        else -> "Q4 (Cos +)"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("unit_circle_sim_card"),
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
                        text = "Unit Circle & Wave Explorer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Interactive Class 11 Trigonometry Visualizer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalIconButton(
                    onClick = { isAutoPlaying = !isAutoPlaying },
                    modifier = Modifier.testTag("unit_circle_play_toggle")
                ) {
                    Icon(
                        imageVector = if (isAutoPlaying) Icons.Default.Refresh else Icons.Default.PlayArrow,
                        contentDescription = "Toggle animation"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Circle Canvas
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val touch = change.position
                            val dx = touch.x - center.x
                            val dy = center.y - touch.y // invert y for Cartesian
                            var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            if (deg < 0) deg += 360f
                            angleDeg = deg
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width * 0.40f

                    // Grid axes
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(0f, center.y),
                        end = Offset(size.width, center.y),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(center.x, 0f),
                        end = Offset(center.x, size.height),
                        strokeWidth = 1.5f
                    )

                    // Unit Circle
                    drawCircle(
                        color = Color(0xFF1E293B),
                        radius = radius,
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFF38BDF8),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 2.5f)
                    )

                    // Triangle point on circle
                    val px = center.x + (radius * cosVal).toFloat()
                    val py = center.y - (radius * sinVal).toFloat()

                    // Cosine component (horizontal on x-axis)
                    drawLine(
                        color = Color(0xFF10B981), // Emerald
                        start = center,
                        end = Offset(px, center.y),
                        strokeWidth = 4f
                    )

                    // Sine component (vertical)
                    drawLine(
                        color = Color(0xFFF59E0B), // Amber
                        start = Offset(px, center.y),
                        end = Offset(px, py),
                        strokeWidth = 4f
                    )

                    // Radius line
                    drawLine(
                        color = Color(0xFF38BDF8), // Cyan
                        start = center,
                        end = Offset(px, py),
                        strokeWidth = 3f
                    )

                    // Dashed projection to y-axis
                    drawLine(
                        color = Color(0x66F59E0B),
                        start = Offset(center.x, py),
                        end = Offset(px, py),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )

                    // Point marker
                    drawCircle(
                        color = Color(0xFFF8FAFC),
                        radius = 6.dp.toPx(),
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = Color(0xFF0284C7),
                        radius = 4.dp.toPx(),
                        center = Offset(px, py)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Angle Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "θ: ${angleDeg.toInt()}°",
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(60.dp)
                )
                Slider(
                    value = angleDeg,
                    onValueChange = { angleDeg = it },
                    valueRange = 0f..360f,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("unit_circle_slider")
                )
            }

            // Real-time measurements badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricChip(label = "sin(θ)", value = String.format("%.4f", sinVal), color = Color(0xFFF59E0B))
                MetricChip(label = "cos(θ)", value = String.format("%.4f", cosVal), color = Color(0xFF10B981))
                MetricChip(
                    label = "tan(θ)",
                    value = if (tanVal.isNaN() || abs(tanVal) > 99) "±∞" else String.format("%.3f", tanVal),
                    color = Color(0xFF818CF8)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Identity & Quadrant info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quadrant: $quadrant",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "sin²θ + cos²θ = 1.000",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onSendToCalculator("sin(${angleDeg.toInt()})") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("unit_circle_send_calc_btn")
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send sin(${angleDeg.toInt()}°) to Calculator")
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
    }
}
