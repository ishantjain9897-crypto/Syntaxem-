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
fun SimpleHarmonicMotionSimulation(
    onSendToCalculator: (String) -> Unit
) {
    var massKg by remember { mutableFloatStateOf(1.5f) }
    var springConstantK by remember { mutableFloatStateOf(60.0f) }
    var amplitudeM by remember { mutableFloatStateOf(0.20f) }

    val omega = sqrt(springConstantK / massKg)
    val periodT = 2 * PI.toFloat() / omega
    val frequencyHz = 1f / periodT

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shm_sim_card"),
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
                        text = "Simple Harmonic Motion (SHM)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "CBSE Class 11 Physics: Chapter 14 Oscillations",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        onSendToCalculator("2 * pi * sqrt($massKg / $springConstantK)")
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("send_shm_to_calc_btn")
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Solve T", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-Time Telemetry Row
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
                        Text("Period T", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "%.3f s".format(periodT),
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
                        Text("Angular Freq ω", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "%.2f rad/s".format(omega),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas Phase Waveform
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val originY = h / 2f

                    // Zero displacement line
                    drawLine(Color(0xFF94A3B8), Offset(0f, originY), Offset(w, originY), 1.5f)

                    // Waveform: x(t) = A * cos(omega * t)
                    val wavePath = Path()
                    var first = true
                    for (i in 0..100) {
                        val t = (i / 100f) * periodT * 2f
                        val disp = amplitudeM * cos(omega * t)
                        val px = (i / 100f) * w
                        val py = originY - (disp / 0.35f) * (h * 0.4f)
                        if (first) {
                            wavePath.moveTo(px, py)
                            first = false
                        } else {
                            wavePath.lineTo(px, py)
                        }
                    }
                    drawPath(wavePath, Color(0xFF0D3B39), style = Stroke(width = 2.5.dp.toPx()))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Parameter Scrubbers
            Text("Mass m: %.2f kg".format(massKg), style = MaterialTheme.typography.bodySmall)
            Slider(
                value = massKg,
                onValueChange = { massKg = it },
                valueRange = 0.2f..5.0f
            )

            Text("Spring Constant k: %.1f N/m".format(springConstantK), style = MaterialTheme.typography.bodySmall)
            Slider(
                value = springConstantK,
                onValueChange = { springConstantK = it },
                valueRange = 10f..200f
            )

            Text("Amplitude A: %.2f m".format(amplitudeM), style = MaterialTheme.typography.bodySmall)
            Slider(
                value = amplitudeM,
                onValueChange = { amplitudeM = it },
                valueRange = 0.05f..0.35f
            )
        }
    }
}
