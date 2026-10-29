package com.example.orbitlab.ui.simulator

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.orbitlab.model.CelestialBody
import com.example.orbitlab.model.FlightParameters
import com.example.orbitlab.model.SavedRun
import kotlinx.coroutines.delay
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSimulatorScreen(
    initialParams: FlightParameters = FlightParameters(),
    onOpenCalculatorWithExpression: (String) -> Unit = {}
) {
    // Current active simulation parameters
    var params by remember { mutableStateOf(initialParams) }

    // Playback state
    var isPlaying by remember { mutableStateOf(false) }
    var currentTime by remember { mutableDoubleStateOf(0.0) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    // Comparison runs
    var runA by remember {
        mutableStateOf(
            SavedRun(
                id = "run_a",
                label = "Run A (Earth 45°)",
                params = FlightParameters(25.0, 45.0, 9.80665, CelestialBody.EARTH)
            )
        )
    }
    var runB by remember {
        mutableStateOf<SavedRun?>(null)
    }
    var compareMode by remember { mutableStateOf(false) }

    // Animation ticker loop
    LaunchedEffect(isPlaying, playbackSpeed, params.timeOfFlight) {
        if (isPlaying) {
            val dt = 0.025
            val stepDelayMs = (dt * 1000 / playbackSpeed).toLong().coerceAtLeast(10L)
            while (isPlaying && currentTime < params.timeOfFlight) {
                delay(stepDelayMs)
                currentTime = (currentTime + dt).coerceAtMost(params.timeOfFlight)
                if (currentTime >= params.timeOfFlight) {
                    isPlaying = false
                }
            }
        }
    }

    // Reset current time when physical parameters change
    LaunchedEffect(params.initialVelocity, params.launchAngleDegrees, params.gravity) {
        if (currentTime > params.timeOfFlight) {
            currentTime = params.timeOfFlight
        }
    }

    val currentState = remember(params, currentTime) {
        params.computeState(currentTime)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "VARIABLE FLIGHT SIMULATOR",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "Deterministic 2D Kinematics • CBSE Class 11",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    FilterChip(
                        selected = compareMode,
                        onClick = {
                            compareMode = !compareMode
                            if (compareMode && runB == null) {
                                runB = SavedRun(
                                    id = "run_b",
                                    label = "Run B (Current)",
                                    params = params
                                )
                            }
                        },
                        label = { Text(if (compareMode) "Comparing A vs B" else "Compare Mode") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("btn_compare_mode")
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Simulation Display Canvas with Live Vector Readouts
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SimulationCanvasCard(
                    params = params,
                    currentTime = currentTime,
                    currentState = currentState,
                    runB = if (compareMode) runB else null
                )
            }

            // 2. Playback & Time Scrubbing Controls
            item {
                PlaybackControlsCard(
                    isPlaying = isPlaying,
                    currentTime = currentTime,
                    totalTime = params.timeOfFlight,
                    playbackSpeed = playbackSpeed,
                    onTogglePlay = {
                        if (currentTime >= params.timeOfFlight) {
                            currentTime = 0.0
                        }
                        isPlaying = !isPlaying
                    },
                    onReset = {
                        isPlaying = false
                        currentTime = 0.0
                    },
                    onStepForward = {
                        isPlaying = false
                        currentTime = (currentTime + 0.1).coerceAtMost(params.timeOfFlight)
                    },
                    onTimeChange = { newTime ->
                        currentTime = newTime
                    },
                    onSpeedChange = { newSpeed ->
                        playbackSpeed = newSpeed
                    }
                )
            }

            // 3. Side-by-Side Comparison Matrix (When active)
            if (compareMode && runB != null) {
                item {
                    ExperimentComparisonCard(
                        runA = runA,
                        runB = runB!!,
                        onUpdateRunA = { runA = SavedRun("run_a", "Run A (${params.celestialBody.displayName})", params) },
                        onUpdateRunB = { runB = SavedRun("run_b", "Run B (${params.celestialBody.displayName})", params) }
                    )
                }
            }

            // 4. Physical Parameter Scrubbers
            item {
                ParameterControlsCard(
                    params = params,
                    onParamsChange = { newParams ->
                        params = newParams
                    }
                )
            }

            // 5. Numerical Telemetry Readouts
            item {
                TelemetryMetricsCard(
                    params = params,
                    currentState = currentState,
                    onSendToCalculator = onOpenCalculatorWithExpression
                )
            }

            // 6. Governing Equations & Idealised Model Boundary
            item {
                GoverningEquationsCard(params = params)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * High-precision canvas for 2D projectile trajectory, coordinate grid, and velocity vectors.
 */
@Composable
fun SimulationCanvasCard(
    params: FlightParameters,
    currentTime: Double,
    currentState: com.example.orbitlab.model.TrajectoryPoint,
    runB: SavedRun? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .testTag("flight_sim_canvas_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121417) // Deep graphite background
        ),
        border = BorderStroke(1.dp, Color(0xFF222C3A))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Calculate bounds with 15% margin
                val maxHoriz = max(params.horizontalRange, runB?.params?.horizontalRange ?: 0.0).coerceAtLeast(10.0) * 1.15
                val maxVert = max(params.maxHeight, runB?.params?.maxHeight ?: 0.0).coerceAtLeast(5.0) * 1.25

                fun worldToScreen(x: Double, y: Double): Offset {
                    val sx = (x / maxHoriz).toFloat() * canvasWidth
                    val sy = canvasHeight - (y / maxVert).toFloat() * canvasHeight
                    return Offset(sx, sy)
                }

                // 1. Draw coordinate grid lines (cyan tint)
                val gridLinesX = 5
                val gridLinesY = 4
                for (i in 0..gridLinesX) {
                    val gx = (i.toFloat() / gridLinesX) * canvasWidth
                    drawLine(
                        color = Color(0x1F06B6D4),
                        start = Offset(gx, 0f),
                        end = Offset(gx, canvasHeight),
                        strokeWidth = 1f
                    )
                }
                for (j in 0..gridLinesY) {
                    val gy = (j.toFloat() / gridLinesY) * canvasHeight
                    drawLine(
                        color = Color(0x1F06B6D4),
                        start = Offset(0f, gy),
                        end = Offset(canvasWidth, gy),
                        strokeWidth = 1f
                    )
                }

                // 2. Draw ground baseline
                drawLine(
                    color = Color(0xFF334155),
                    start = Offset(0f, canvasHeight),
                    end = Offset(canvasWidth, canvasHeight),
                    strokeWidth = 2.5f
                )

                // 3. Draw Run B comparison trajectory (Amber line) if active
                if (runB != null) {
                    val curveB = runB.params.generateTrajectoryCurve()
                    val pathB = Path()
                    curveB.forEachIndexed { idx, pt ->
                        val screenPt = worldToScreen(pt.x, pt.y)
                        if (idx == 0) pathB.moveTo(screenPt.x, screenPt.y)
                        else pathB.lineTo(screenPt.x, screenPt.y)
                    }
                    drawPath(
                        path = pathB,
                        color = Color(0xFFF59E0B).copy(alpha = 0.7f),
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )
                }

                // 4. Draw Primary Run trajectory (Technical Cyan line)
                val curveA = params.generateTrajectoryCurve()
                val pathA = Path()
                curveA.forEachIndexed { idx, pt ->
                    val screenPt = worldToScreen(pt.x, pt.y)
                    if (idx == 0) pathA.moveTo(screenPt.x, screenPt.y)
                    else pathA.lineTo(screenPt.x, screenPt.y)
                }
                drawPath(
                    path = pathA,
                    color = Color(0xFF22D3EE),
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )

                // 5. Draw Apex Point marker
                val apexState = params.computeState(params.timeToApex)
                val apexScreen = worldToScreen(apexState.x, apexState.y)
                drawCircle(
                    color = Color(0xFF22D3EE).copy(alpha = 0.4f),
                    radius = 9f,
                    center = apexScreen
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.5f,
                    center = apexScreen
                )

                // 6. Draw Current Projectile Position & Velocity Vectors
                val currScreen = worldToScreen(currentState.x, currentState.y)
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = 8f,
                    center = currScreen
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = currScreen
                )

                // Draw vector velocity arrow v(t)
                val vectorScale = 1.2f
                val arrowEnd = Offset(
                    currScreen.x + (currentState.vx.toFloat() * vectorScale),
                    currScreen.y - (currentState.vy.toFloat() * vectorScale)
                )
                drawLine(
                    color = Color(0xFFFBBF24),
                    start = currScreen,
                    end = arrowEnd,
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )

                // Horizontal vx component (green)
                drawLine(
                    color = Color(0xFF10B981).copy(alpha = 0.6f),
                    start = currScreen,
                    end = Offset(currScreen.x + (currentState.vx.toFloat() * vectorScale), currScreen.y),
                    strokeWidth = 2f
                )

                // Vertical vy component (crimson/cyan)
                drawLine(
                    color = if (currentState.vy >= 0) Color(0xFF06B6D4).copy(alpha = 0.6f) else Color(0xFFF43F5E).copy(alpha = 0.6f),
                    start = currScreen,
                    end = Offset(currScreen.x, currScreen.y - (currentState.vy.toFloat() * vectorScale)),
                    strokeWidth = 2f
                )
            }

            // Coordinate indicators overlay
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color(0xFF1A222D).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Text(
                        text = "x: ${"%.1f".format(currentState.x)}m  y: ${"%.1f".format(currentState.y)}m",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF22D3EE),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Surface(
                    color = Color(0xFF1A222D).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Text(
                        text = "|v|: ${"%.1f".format(currentState.speed)}m/s",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Legend overlay (Bottom Right)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp, 3.dp).background(Color(0xFF22D3EE)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Primary Trajectory",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
                if (runB != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp, 3.dp).background(Color(0xFFF59E0B)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Comparison Run B",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Playback control bar: Play, Pause, Reset, Step Forward, and Slow-Motion speeds.
 */
@Composable
fun PlaybackControlsCard(
    isPlaying: Boolean,
    currentTime: Double,
    totalTime: Double,
    playbackSpeed: Float,
    onTogglePlay: () -> Unit,
    onReset: () -> Unit,
    onStepForward: () -> Unit,
    onTimeChange: (Double) -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("playback_controls_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Slider row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${"%.2f".format(currentTime)}s",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.width(52.dp)
                )

                Slider(
                    value = currentTime.toFloat(),
                    onValueChange = { onTimeChange(it.toDouble()) },
                    valueRange = 0f..max(totalTime.toFloat(), 0.01f),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("flight_time_slider")
                )

                Text(
                    text = "${"%.2f".format(totalTime)}s",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.width(52.dp)
                )
            }

            // Buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledIconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier.testTag("btn_toggle_play"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play"
                        )
                    }

                    IconButton(
                        onClick = onReset,
                        modifier = Modifier.testTag("btn_reset_flight")
                    ) {
                        Icon(imageVector = Icons.Default.Replay, contentDescription = "Reset")
                    }

                    IconButton(
                        onClick = onStepForward,
                        modifier = Modifier.testTag("btn_step_flight")
                    ) {
                        Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Step Forward")
                    }
                }

                // Speed selector
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    listOf(0.25f, 0.5f, 1.0f, 2.0f).forEach { speed ->
                        val isSelected = playbackSpeed == speed
                        Box(
                            modifier = Modifier
                                .clickable { onSpeedChange(speed) }
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Side-by-side experiment comparison matrix showing Run A vs Run B and deltas.
 */
@Composable
fun ExperimentComparisonCard(
    runA: SavedRun,
    runB: SavedRun,
    onUpdateRunA: () -> Unit,
    onUpdateRunB: () -> Unit
) {
    val deltaR = runB.params.horizontalRange - runA.params.horizontalRange
    val deltaH = runB.params.maxHeight - runA.params.maxHeight
    val deltaT = runB.params.timeOfFlight - runA.params.timeOfFlight

    Card(
        modifier = Modifier.fillMaxWidth().testTag("experiment_comparison_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SIDE-BY-SIDE EXPERIMENT COMPARISON",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onUpdateRunA,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Set Run A", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = onUpdateRunB,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Set Run B", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Comparison table header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Metric", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.3f))
                Text("Run A (Cyan)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0891B2), modifier = Modifier.weight(1f))
                Text("Run B (Amber)", style = MaterialTheme.typography.labelSmall, color = Color(0xFFD97706), modifier = Modifier.weight(1f))
                Text("Delta (B - A)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            }

            Divider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant)

            ComparisonRow("Range (R)", "${"%.1f".format(runA.params.horizontalRange)}m", "${"%.1f".format(runB.params.horizontalRange)}m", "${if (deltaR >= 0) "+" else ""}${"%.1f".format(deltaR)}m")
            ComparisonRow("Max Height (H)", "${"%.1f".format(runA.params.maxHeight)}m", "${"%.1f".format(runB.params.maxHeight)}m", "${if (deltaH >= 0) "+" else ""}${"%.1f".format(deltaH)}m")
            ComparisonRow("Time of Flight (T)", "${"%.2f".format(runA.params.timeOfFlight)}s", "${"%.2f".format(runB.params.timeOfFlight)}s", "${if (deltaT >= 0) "+" else ""}${"%.2f".format(deltaT)}s")
            ComparisonRow("Gravity (g)", "${"%.2f".format(runA.params.gravity)}m/s²", "${"%.2f".format(runB.params.gravity)}m/s²", "${"%.2f".format(runB.params.gravity - runA.params.gravity)}")
        }
    }
}

@Composable
fun ComparisonRow(metric: String, a: String, b: String, delta: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(metric, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.3f))
        Text(a, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.weight(1f))
        Text(b, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.weight(1f))
        Text(delta, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
    }
}

/**
 * Real-time parameter controls for Initial Velocity, Launch Angle, and Gravity.
 */
@Composable
fun ParameterControlsCard(
    params: FlightParameters,
    onParamsChange: (FlightParameters) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("parameter_controls_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "PHYSICAL PARAMETER CALIBRATION",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            // 1. Initial Velocity Scrubber
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Initial Velocity (v₀)", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${"%.1f".format(params.initialVelocity)} m/s",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Slider(
                    value = params.initialVelocity.toFloat(),
                    onValueChange = { onParamsChange(params.copy(initialVelocity = it.toDouble())) },
                    valueRange = 1f..100f,
                    modifier = Modifier.testTag("slider_velocity")
                )
            }

            // 2. Launch Angle Scrubber
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Launch Angle (θ)", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${params.launchAngleDegrees.roundToInt()}°",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Slider(
                    value = params.launchAngleDegrees.toFloat(),
                    onValueChange = { onParamsChange(params.copy(launchAngleDegrees = it.toDouble())) },
                    valueRange = 0f..90f,
                    steps = 89,
                    modifier = Modifier.testTag("slider_angle")
                )
            }

            // 3. Celestial Gravity Presets & Field Selection
            Column {
                Text("Gravitational Field (g)", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CelestialBody.values().forEach { body ->
                        val isSelected = params.celestialBody == body
                        InputChip(
                            selected = isSelected,
                            onClick = {
                                if (body == CelestialBody.CUSTOM) {
                                    onParamsChange(params.copy(celestialBody = body))
                                } else {
                                    onParamsChange(params.copy(gravity = body.gravity, celestialBody = body))
                                }
                            },
                            label = { Text(body.displayName, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (params.celestialBody == CelestialBody.CUSTOM) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Custom g", style = MaterialTheme.typography.labelSmall)
                        Text("${"%.2f".format(params.gravity)} m/s²", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace))
                    }
                    Slider(
                        value = params.gravity.toFloat(),
                        onValueChange = { onParamsChange(params.copy(gravity = it.toDouble())) },
                        valueRange = 0.5f..30f
                    )
                }
            }
        }
    }
}

/**
 * Metric readout cards: Range, Max Height, Time of flight, and Velocity components.
 */
@Composable
fun TelemetryMetricsCard(
    params: FlightParameters,
    currentState: com.example.orbitlab.model.TrajectoryPoint,
    onSendToCalculator: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("telemetry_metrics_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "DETERMINISTIC TELEMETRY",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricTile(
                    label = "MAX RANGE (R)",
                    value = "${"%.2f".format(params.horizontalRange)} m",
                    formula = "R = v₀² sin(2θ) / g",
                    modifier = Modifier.weight(1f),
                    onClick = { onSendToCalculator("(${params.initialVelocity}^2 * sin(2 * ${params.launchAngleDegrees})) / ${params.gravity}") }
                )
                MetricTile(
                    label = "APEX HEIGHT (H)",
                    value = "${"%.2f".format(params.maxHeight)} m",
                    formula = "H = (v₀ sin θ)² / (2g)",
                    modifier = Modifier.weight(1f),
                    onClick = { onSendToCalculator("(${params.initialVelocity} * sin(${params.launchAngleDegrees}))^2 / (2 * ${params.gravity})") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricTile(
                    label = "FLIGHT TIME (T)",
                    value = "${"%.2f".format(params.timeOfFlight)} s",
                    formula = "T = 2 v₀ sin(θ) / g",
                    modifier = Modifier.weight(1f),
                    onClick = { onSendToCalculator("(2 * ${params.initialVelocity} * sin(${params.launchAngleDegrees})) / ${params.gravity}") }
                )
                MetricTile(
                    label = "APEX TIME",
                    value = "${"%.2f".format(params.timeToApex)} s",
                    formula = "t_apex = v₀ sin(θ) / g",
                    modifier = Modifier.weight(1f),
                    onClick = { onSendToCalculator("(${params.initialVelocity} * sin(${params.launchAngleDegrees})) / ${params.gravity}") }
                )
            }
        }
    }
}

@Composable
fun MetricTile(
    label: String,
    value: String,
    formula: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
            Text(formula, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
        }
    }
}

/**
 * Explanatory card showing governing formulas and stating explicit model boundaries.
 */
@Composable
fun GoverningEquationsCard(params: FlightParameters) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "IDEALISED MODEL BOUNDARY & EQUATIONS",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Text(
                text = "Model assumptions: Air resistance (atmospheric drag) is omitted. Gravitational field is assumed uniform (g = const) and curvature of the planetary surface is neglected. Calculations are deterministic solutions to Newton's equations of motion.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = "• x(t) = v₀ cos(θ) · t\n• y(t) = v₀ sin(θ) · t - ½ g t²\n• v_x(t) = v₀ cos(θ) = constant\n• v_y(t) = v₀ sin(θ) - g · t",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
            )
        }
    }
}
