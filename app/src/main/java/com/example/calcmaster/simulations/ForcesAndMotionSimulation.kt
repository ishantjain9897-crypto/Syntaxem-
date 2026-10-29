package com.example.calcmaster.simulations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun ForcesAndMotionSimulation(
    onSendToCalculator: (String) -> Unit
) {
    var massKg by remember { mutableFloatStateOf(5.0f) }
    var appliedForceN by remember { mutableFloatStateOf(30.0f) }
    var frictionCoeff by remember { mutableFloatStateOf(0.25f) }
    var inclineAngleDeg by remember { mutableFloatStateOf(0.0f) }

    val g = 9.8f
    // Physics calculations
    val thetaRad = inclineAngleDeg * PI.toFloat() / 180f
    val normalForce = massKg * g * cos(thetaRad)
    val gravityParallel = massKg * g * sin(thetaRad)
    val maxFriction = frictionCoeff * normalForce

    val netForce = max(0f, appliedForceN - gravityParallel - maxFriction)
    val acceleration = netForce / massKg

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forces_motion_sim_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Newton's Second Law & Friction",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "CBSE Class 11 Physics: Laws of Motion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        onSendToCalculator("($appliedForceN - $massKg * 9.8 * sin($inclineAngleDeg * pi / 180) - $frictionCoeff * $normalForce) / $massKg")
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("send_force_to_calc_btn")
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Solve a", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas Free-Body Diagram
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Ground or Inclined Plane
                    val groundY = h * 0.75f
                    drawLine(
                        color = Color(0xFF64748B),
                        start = Offset(20f, groundY),
                        end = Offset(w - 20f, groundY - tan(thetaRad) * (w - 40f) * 0.3f),
                        strokeWidth = 3f
                    )

                    // Block
                    val boxW = 70f
                    val boxH = 50f
                    val boxX = w * 0.45f
                    val boxY = groundY - boxH - 2f

                    drawRect(
                        color = Color(0xFF0D3B39),
                        topLeft = Offset(boxX, boxY),
                        size = Size(boxW, boxH)
                    )

                    val centerX = boxX + boxW / 2f
                    val centerY = boxY + boxH / 2f

                    // Applied Force Arrow (Right)
                    val appliedArrowLen = (appliedForceN / 50f) * 60f
                    drawLine(
                        color = Color(0xFF0284C7),
                        start = Offset(centerX, centerY),
                        end = Offset(centerX + appliedArrowLen, centerY),
                        strokeWidth = 4f
                    )

                    // Friction Arrow (Left)
                    val frictionArrowLen = (maxFriction / 50f) * 60f
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(centerX, centerY),
                        end = Offset(centerX - frictionArrowLen, centerY),
                        strokeWidth = 3f
                    )

                    // Gravity Arrow (Down)
                    drawLine(
                        color = Color(0xFFF59E0B),
                        start = Offset(centerX, centerY),
                        end = Offset(centerX, centerY + 45f),
                        strokeWidth = 3f
                    )

                    // Normal Force Arrow (Up)
                    drawLine(
                        color = Color(0xFF10B981),
                        start = Offset(centerX, centerY),
                        end = Offset(centerX, centerY - 45f),
                        strokeWidth = 3f
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time Telemetry Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Net Force F_net", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "%.2f N".format(netForce),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Acceleration a", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "%.2f m/s²".format(acceleration),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Parameter Scrubbers
            Text("Applied Force: ${appliedForceN.toInt()} N", style = MaterialTheme.typography.bodySmall)
            Slider(
                value = appliedForceN,
                onValueChange = { appliedForceN = it },
                valueRange = 0f..100f
            )

            Text("Mass: %.1f kg".format(massKg), style = MaterialTheme.typography.bodySmall)
            Slider(
                value = massKg,
                onValueChange = { massKg = it },
                valueRange = 1f..20f
            )

            Text("Friction Coefficient μ: %.2f".format(frictionCoeff), style = MaterialTheme.typography.bodySmall)
            Slider(
                value = frictionCoeff,
                onValueChange = { frictionCoeff = it },
                valueRange = 0f..0.8f
            )

            Text("Incline Angle: ${inclineAngleDeg.toInt()}°", style = MaterialTheme.typography.bodySmall)
            Slider(
                value = inclineAngleDeg,
                onValueChange = { inclineAngleDeg = it },
                valueRange = 0f..45f
            )
        }
    }
}
