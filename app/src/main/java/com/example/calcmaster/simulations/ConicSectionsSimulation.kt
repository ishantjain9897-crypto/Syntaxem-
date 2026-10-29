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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun ConicSectionsSimulation(
    onSendToCalculator: (String) -> Unit
) {
    var eccentricity by remember { mutableFloatStateOf(0.7f) }
    var semiMajorA by remember { mutableFloatStateOf(4.0f) }

    val conicType = when {
        eccentricity == 0f -> "Circle (e = 0)"
        eccentricity < 1f -> "Ellipse (0 < e < 1)"
        eccentricity == 1f -> "Parabola (e = 1)"
        else -> "Hyperbola (e > 1)"
    }

    val standardEquation = when {
        eccentricity == 0f -> "x² + y² = ${semiMajorA.pow(2).toInt()}"
        eccentricity < 1f -> {
            val b = semiMajorA * sqrt(1f - eccentricity * eccentricity)
            "x² / %.1f + y² / %.1f = 1".format(semiMajorA.pow(2), b.pow(2))
        }
        eccentricity == 1f -> "y² = 4 · %.1f · x".format(semiMajorA)
        else -> {
            val b = semiMajorA * sqrt(eccentricity * eccentricity - 1f)
            "x² / %.1f - y² / %.1f = 1".format(semiMajorA.pow(2), b.pow(2))
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("conics_sim_card"),
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
                        text = "Conic Sections & Locus",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "CBSE Class 11 Maths: Chapter 11 Conics",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { onSendToCalculator(standardEquation) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("send_conic_to_calc_btn")
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Evaluate", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Conic Type & Equation Badge
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conicType,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = standardEquation,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas Plotting of the Conic Curve
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val originX = w / 2f
                    val originY = h / 2f
                    val scale = w / 16f

                    // Coordinate Axes
                    drawLine(Color(0xFF94A3B8), Offset(0f, originY), Offset(w, originY), 1.5f)
                    drawLine(Color(0xFF94A3B8), Offset(originX, 0f), Offset(originX, h), 1.5f)

                    val curveColor = Color(0xFF0D3B39)

                    if (eccentricity == 0f) {
                        // Circle
                        drawCircle(
                            color = curveColor,
                            radius = semiMajorA * scale,
                            center = Offset(originX, originY),
                            style = Stroke(width = 2.5.dp.toPx())
                        )
                    } else if (eccentricity < 1f) {
                        // Ellipse
                        val b = semiMajorA * sqrt(1f - eccentricity * eccentricity)
                        val path = Path()
                        for (i in 0..360 step 5) {
                            val rad = i * PI.toFloat() / 180f
                            val px = originX + semiMajorA * cos(rad) * scale
                            val py = originY - b * sin(rad) * scale
                            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                        }
                        path.close()
                        drawPath(path, curveColor, style = Stroke(width = 2.5.dp.toPx()))

                        // Foci
                        val c = semiMajorA * eccentricity
                        drawCircle(Color(0xFFE11D48), radius = 4.dp.toPx(), center = Offset(originX - c * scale, originY))
                        drawCircle(Color(0xFFE11D48), radius = 4.dp.toPx(), center = Offset(originX + c * scale, originY))
                    } else if (abs(eccentricity - 1f) < 0.05f) {
                        // Parabola y^2 = 4ax
                        val path = Path()
                        var first = true
                        for (y in -60..60) {
                            val yVal = y / 10f
                            val xVal = (yVal * yVal) / (4f * semiMajorA)
                            val px = originX + xVal * scale
                            val py = originY - yVal * scale
                            if (px < w) {
                                if (first) { path.moveTo(px, py); first = false } else path.lineTo(px, py)
                            }
                        }
                        drawPath(path, curveColor, style = Stroke(width = 2.5.dp.toPx()))
                    } else {
                        // Hyperbola x^2/a^2 - y^2/b^2 = 1
                        val b = semiMajorA * sqrt(eccentricity * eccentricity - 1f)
                        val pathRight = Path()
                        var first = true
                        for (y in -60..60) {
                            val yVal = y / 10f
                            val xVal = semiMajorA * sqrt(1f + (yVal * yVal) / (b * b))
                            val px = originX + xVal * scale
                            val py = originY - yVal * scale
                            if (px < w) {
                                if (first) { pathRight.moveTo(px, py); first = false } else pathRight.lineTo(px, py)
                            }
                        }
                        drawPath(pathRight, curveColor, style = Stroke(width = 2.5.dp.toPx()))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Parameter Scrubbers
            Text("Eccentricity e: %.2f".format(eccentricity), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Slider(
                value = eccentricity,
                onValueChange = { eccentricity = it },
                valueRange = 0f..2.0f,
                steps = 19
            )

            Text("Semi-Major Axis a: %.1f".format(semiMajorA), style = MaterialTheme.typography.bodySmall)
            Slider(
                value = semiMajorA,
                onValueChange = { semiMajorA = it },
                valueRange = 1f..6f
            )
        }
    }
}
