package com.example.calcmaster.lumenflow.lab

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.calcmaster.lumenflow.lab.games.AlgebraArenaGame
import com.example.calcmaster.lumenflow.lab.games.ForceBuilderGame
import com.example.calcmaster.lumenflow.lab.games.GraphEscapeGame
import com.example.calcmaster.lumenflow.lab.games.ProjectileLabGame
import com.example.calcmaster.lumenflow.lab.games.UnitCircleQuestGame
import com.example.orbitlab.model.CelestialBody
import com.example.orbitlab.model.FlightParameters
import com.example.orbitlab.ui.simulator.FlightSimulatorScreen

enum class LabTab(val label: String) {
    SIMULATIONS("Simulations"),
    EDUCATIONAL_GAMES("Concept Games")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumenLabScreen(
    initialSimId: String = "projectile",
    initialGameId: String? = null,
    onSendToCalculator: (String) -> Unit = {},
    onOpenTutor: (contextTitle: String, formula: String) -> Unit = { _, _ -> }
) {
    var activeTab by remember { mutableStateOf(if (initialGameId != null) LabTab.EDUCATIONAL_GAMES else LabTab.SIMULATIONS) }
    var selectedSimId by remember { mutableStateOf(initialSimId) }
    var selectedGameId by remember { mutableStateOf(initialGameId ?: "unit_circle_quest") }
    var activeGameSessionId by remember { mutableStateOf(initialGameId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "INTERACTIVE LAB & GAMES",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            )
                        )
                        Text(
                            text = "Deterministic Physical Models • Class 11",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onOpenTutor("Laboratory: $selectedSimId", "") },
                        modifier = Modifier.testTag("btn_lab_tutor")
                    ) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = "Tutor Assistance", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab row: Simulations vs Educational Games
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LabTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = tab },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            if (activeTab == LabTab.SIMULATIONS) {
                // Simulations Horizontal Selector
                val simList = listOf(
                    "projectile" to "Projectile Motion",
                    "vectors" to "Vector Addition & Resolution",
                    "newton" to "Newton's Second Law & Friction",
                    "energy" to "Work-Energy Conservation",
                    "shm" to "Harmonic Oscillations"
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(simList) { (id, title) ->
                        val isSelected = selectedSimId == id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSimId = id },
                            label = { Text(title, fontSize = 11.sp) },
                            modifier = Modifier.testTag("sim_chip_$id")
                        )
                    }
                }

                // Render selected simulation
                Box(modifier = Modifier.fillMaxSize()) {
                    when (selectedSimId) {
                        "projectile" -> {
                            FlightSimulatorScreen(
                                initialParams = FlightParameters(
                                    initialVelocity = 25.0,
                                    launchAngleDegrees = 45.0,
                                    gravity = 9.80665,
                                    celestialBody = CelestialBody.EARTH
                                ),
                                onOpenCalculatorWithExpression = onSendToCalculator
                            )
                        }
                        "vectors" -> {
                            VectorLabView(onSendToCalc = onSendToCalculator)
                        }
                        "newton" -> {
                            NewtonDynamicsLabView(onSendToCalc = onSendToCalculator)
                        }
                        "energy" -> {
                            WorkEnergyLabView(onSendToCalc = onSendToCalculator)
                        }
                        "shm" -> {
                            HarmonicOscillatorLabView(onSendToCalc = onSendToCalculator)
                        }
                        else -> {
                            FlightSimulatorScreen(
                                onOpenCalculatorWithExpression = onSendToCalculator
                            )
                        }
                    }
                }
            } else {
                // Educational Games Tab
                if (activeGameSessionId != null) {
                    when (activeGameSessionId) {
                        "unit_circle_quest" -> {
                            UnitCircleQuestGame(
                                onSendToCalculator = onSendToCalculator,
                                onOpenTutor = onOpenTutor,
                                onBackToDirectory = { activeGameSessionId = null }
                            )
                        }
                        "projectile_lab" -> {
                            ProjectileLabGame(
                                onSendToCalculator = onSendToCalculator,
                                onOpenTutor = onOpenTutor,
                                onBackToDirectory = { activeGameSessionId = null }
                            )
                        }
                        "algebra_arena" -> {
                            AlgebraArenaGame(
                                onSendToCalculator = onSendToCalculator,
                                onOpenTutor = onOpenTutor,
                                onBackToDirectory = { activeGameSessionId = null }
                            )
                        }
                        "force_builder" -> {
                            ForceBuilderGame(
                                onSendToCalculator = onSendToCalculator,
                                onOpenTutor = onOpenTutor,
                                onBackToDirectory = { activeGameSessionId = null }
                            )
                        }
                        "graph_escape" -> {
                            GraphEscapeGame(
                                onSendToCalculator = onSendToCalculator,
                                onOpenTutor = onOpenTutor,
                                onBackToDirectory = { activeGameSessionId = null }
                            )
                        }
                        else -> {
                            UnitCircleQuestGame(
                                onSendToCalculator = onSendToCalculator,
                                onOpenTutor = onOpenTutor,
                                onBackToDirectory = { activeGameSessionId = null }
                            )
                        }
                    }
                } else {
                    LumenGamesDirectoryView(
                        selectedGameId = selectedGameId,
                        onSelectGame = {
                            selectedGameId = it
                            activeGameSessionId = it
                        },
                        onSendToCalculator = onSendToCalculator
                    )
                }
            }
        }
    }
}

/**
 * Interactive Vector Resolution & Addition Lab View.
 */
@Composable
fun VectorLabView(onSendToCalc: (String) -> Unit) {
    var vectorAMag by remember { mutableDoubleStateOf(10.0) }
    var vectorAAngle by remember { mutableDoubleStateOf(30.0) }
    var vectorBMag by remember { mutableDoubleStateOf(8.0) }
    var vectorBAngle by remember { mutableDoubleStateOf(60.0) }

    val aRad = Math.toRadians(vectorAAngle)
    val bRad = Math.toRadians(vectorBAngle)
    val ax = vectorAMag * kotlin.math.cos(aRad)
    val ay = vectorAMag * kotlin.math.sin(aRad)
    val bx = vectorBMag * kotlin.math.cos(bRad)
    val by = vectorBMag * kotlin.math.sin(bRad)

    val rx = ax + bx
    val ry = ay + by
    val rMag = kotlin.math.sqrt(rx * rx + ry * ry)
    val rAngle = Math.toDegrees(kotlin.math.atan2(ry, rx))
    val dotProduct = ax * bx + ay * by

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "VECTOR ADDITION & RESOLUTION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "Assumptions: Flat Euclidean 2D coordinate space. Vectors decompose into orthogonal Cartesian components A = A_x î + A_y ĵ.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text("Vector A: |A| = ${"%.1f".format(vectorAMag)}, θ_A = ${vectorAAngle.toInt()}°")
                    Slider(
                        value = vectorAMag.toFloat(),
                        onValueChange = { vectorAMag = it.toDouble() },
                        valueRange = 1f..25f
                    )

                    Text("Vector B: |B| = ${"%.1f".format(vectorBMag)}, θ_B = ${vectorBAngle.toInt()}°")
                    Slider(
                        value = vectorBMag.toFloat(),
                        onValueChange = { vectorBMag = it.toDouble() },
                        valueRange = 1f..25f
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text("Resultant R = A + B:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("• R_x = A_x + B_x = ${"%.2f".format(ax)} + ${"%.2f".format(bx)} = ${"%.2f".format(rx)}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                    Text("• R_y = A_y + B_y = ${"%.2f".format(ay)} + ${"%.2f".format(by)} = ${"%.2f".format(ry)}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                    Text("• |R| = √(${rx}² + ${ry}²) = ${"%.2f".format(rMag)}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text("• θ_R = ${"%.1f".format(rAngle)}°", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                    Text("• Dot Product A · B = ${"%.2f".format(dotProduct)}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))

                    Button(
                        onClick = { onSendToCalc("sqrt(${rx}^2 + ${ry}^2)") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Magnitude Calculation to Calculator")
                    }
                }
            }
        }
    }
}

/**
 * Newton's Dynamics & Friction Lab View.
 */
@Composable
fun NewtonDynamicsLabView(onSendToCalc: (String) -> Unit) {
    var mass by remember { mutableDoubleStateOf(5.0) }
    var muStatic by remember { mutableDoubleStateOf(0.4) }
    var appliedForce by remember { mutableDoubleStateOf(25.0) }

    val normalForce = mass * 9.80665
    val maxStaticFriction = muStatic * normalForce
    val isMoving = appliedForce > maxStaticFriction
    val netForce = if (isMoving) appliedForce - (0.3 * normalForce) else 0.0
    val acceleration = if (isMoving) netForce / mass else 0.0

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "NEWTON'S SECOND LAW & FRICTION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text("Assumptions: Level horizontal surface. Dry Coulomb friction model f_s ≤ μ_s N and f_k = μ_k N. Standard gravity g = 9.81 m/s².", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text("Mass: ${"%.1f".format(mass)} kg")
                    Slider(value = mass.toFloat(), onValueChange = { mass = it.toDouble() }, valueRange = 1f..20f)

                    Text("Coefficient of Static Friction (μ_s): ${"%.2f".format(muStatic)}")
                    Slider(value = muStatic.toFloat(), onValueChange = { muStatic = it.toDouble() }, valueRange = 0.1f..1.0f)

                    Text("Applied Horizontal Force: ${"%.1f".format(appliedForce)} N")
                    Slider(value = appliedForce.toFloat(), onValueChange = { appliedForce = it.toDouble() }, valueRange = 0f..100f)

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text("• Normal Force N = mg = ${"%.2f".format(normalForce)} N", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                    Text("• Static Threshold f_s,max = μ_s N = ${"%.2f".format(maxStaticFriction)} N", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                    Text("• State: ${if (isMoving) "Accelerating (Motion Initiated)" else "Equilibrium (Static)"}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = if (isMoving) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary))
                    Text("• Acceleration a = F_net / m = ${"%.2f".format(acceleration)} m/s²", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))

                    Button(
                        onClick = { onSendToCalc("($appliedForce - 0.3 * $normalForce) / $mass") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calculate Acceleration in Calculator")
                    }
                }
            }
        }
    }
}

/**
 * Work-Energy Conservation Lab View.
 */
@Composable
fun WorkEnergyLabView(onSendToCalc: (String) -> Unit) {
    var height by remember { mutableDoubleStateOf(10.0) }
    var mass by remember { mutableDoubleStateOf(2.0) }
    val g = 9.80665

    val potentialEnergy = mass * g * height
    val impactVelocity = kotlin.math.sqrt(2 * g * height)
    val kineticEnergyAtBottom = 0.5 * mass * impactVelocity * impactVelocity

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "CONSERVATION OF MECHANICAL ENERGY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text("Assumptions: Closed isolated system. Only conservative forces (gravity) act. Zero aerodynamic dissipation.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text("Drop Height: ${"%.1f".format(height)} m")
                    Slider(value = height.toFloat(), onValueChange = { height = it.toDouble() }, valueRange = 1f..50f)

                    Text("Mass: ${"%.1f".format(mass)} kg")
                    Slider(value = mass.toFloat(), onValueChange = { mass = it.toDouble() }, valueRange = 0.5f..10f)

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text("• Initial Potential Energy U_i = mgh = ${"%.2f".format(potentialEnergy)} J", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                    Text("• Terminal Velocity v = √(2gh) = ${"%.2f".format(impactVelocity)} m/s", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text("• Final Kinetic Energy K_f = ½ m v² = ${"%.2f".format(kineticEnergyAtBottom)} J", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))

                    Button(
                        onClick = { onSendToCalc("sqrt(2 * 9.80665 * $height)") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calculate Velocity in Calculator")
                    }
                }
            }
        }
    }
}

/**
 * Simple Harmonic Motion Lab View.
 */
@Composable
fun HarmonicOscillatorLabView(onSendToCalc: (String) -> Unit) {
    var springConstant by remember { mutableDoubleStateOf(50.0) }
    var mass by remember { mutableDoubleStateOf(0.5) }

    val omega = kotlin.math.sqrt(springConstant / mass)
    val timePeriod = 2 * Math.PI * kotlin.math.sqrt(mass / springConstant)
    val frequency = 1.0 / timePeriod

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "SIMPLE HARMONIC MOTION (SHM)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text("Assumptions: Ideal Hooke's law spring (F = -kx). Massless spring. Zero damping resistance.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text("Spring Constant k: ${"%.1f".format(springConstant)} N/m")
                    Slider(value = springConstant.toFloat(), onValueChange = { springConstant = it.toDouble() }, valueRange = 10f..200f)

                    Text("Oscillating Mass m: ${"%.2f".format(mass)} kg")
                    Slider(value = mass.toFloat(), onValueChange = { mass = it.toDouble() }, valueRange = 0.1f..5f)

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text("• Angular Frequency ω = √(k/m) = ${"%.2f".format(omega)} rad/s", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                    Text("• Time Period T = 2π√(m/k) = ${"%.3f".format(timePeriod)} s", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text("• Frequency f = 1/T = ${"%.2f".format(frequency)} Hz", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))

                    Button(
                        onClick = { onSendToCalc("2 * pi * sqrt($mass / $springConstant)") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calculate Period in Calculator")
                    }
                }
            }
        }
    }
}

/**
 * Educational Games Directory View for the 5 Core Games.
 */
@Composable
fun LumenGamesDirectoryView(
    selectedGameId: String,
    onSelectGame: (String) -> Unit,
    onSendToCalculator: (String) -> Unit
) {
    val games = listOf(
        Triple("unit_circle_quest", "Unit Circle Quest", "Trigonometric quadrant identification, angle coordinate placement & sine-cosine relations."),
        Triple("projectile_lab", "Projectile Lab", "Physics trajectory prediction puzzle: adjust velocity and angle to strike targets."),
        Triple("algebra_arena", "Algebra Arena", "Turn-based algebraic transformation and equation factoring battle."),
        Triple("force_builder", "Force & Motion Builder", "Engineering balance challenge: place friction blocks and tension cables."),
        Triple("graph_escape", "Graph Escape", "Coordinate geometry escape room: identify conic intercepts to unlock barriers.")
    )

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "CONCEPT-DRIVEN EDUCATIONAL GAMES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text(text = "No purchasable currency, pay-to-progress mechanics, or forced ads. Every game is calibrated to build Class 11 conceptual intuition.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        items(games) { (id, title, desc) ->
            val isSelected = selectedGameId == id
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelectGame(id) }.testTag("game_card_$id"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Text(text = "OFFLINE READY", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = { onSelectGame(id) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                        Text("Launch Game Session")
                    }
                }
            }
        }
    }
}
