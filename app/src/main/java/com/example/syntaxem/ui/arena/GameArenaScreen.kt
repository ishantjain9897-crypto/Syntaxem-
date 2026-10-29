package com.example.syntaxem.ui.arena

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcmaster.lumenflow.lab.games.AlgebraArenaGame
import com.example.calcmaster.lumenflow.lab.games.ForceBuilderGame
import com.example.calcmaster.lumenflow.lab.games.GraphEscapeGame
import com.example.calcmaster.lumenflow.lab.games.ProjectileLabGame
import com.example.calcmaster.lumenflow.lab.games.UnitCircleQuestGame
import com.example.syntaxem.model.NodeColorType

data class ArenaGameItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val colorType: NodeColorType,
    val isFeatured: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameArenaScreen(
    initialGameId: String? = null,
    onSendToCalculator: (String) -> Unit = {},
    onOpenTutor: (String, String) -> Unit = { _, _ -> }
) {
    var activeGameId by remember { mutableStateOf<String?>(initialGameId) }
    var selectedCategory by remember { mutableStateOf("Trigonometry") }

    val categories = listOf("Trigonometry", "Algebra", "Functions", "Calculus", "Vectors", "All")

    val gamesList = remember {
        listOf(
            ArenaGameItem(
                id = "unit_circle_race",
                title = "Unit Circle Race",
                subtitle = "Beat the clock: place angles and read sin, cos, tan.",
                category = "Trigonometry",
                colorType = NodeColorType.MINT,
                isFeatured = true
            ),
            ArenaGameItem(
                id = "formula_match",
                title = "Formula Match",
                subtitle = "Pair identities fast",
                category = "Trigonometry",
                colorType = NodeColorType.PERIWINKLE
            ),
            ArenaGameItem(
                id = "graph_spotter",
                title = "Graph Spotter",
                subtitle = "Name the curve",
                category = "Trigonometry",
                colorType = NodeColorType.AMBER
            ),
            ArenaGameItem(
                id = "equation_puzzle",
                title = "Equation Puzzle",
                subtitle = "Solve fast",
                category = "Algebra",
                colorType = NodeColorType.PINK
            ),
            ArenaGameItem(
                id = "map_builder",
                title = "Map Builder",
                subtitle = "Fill the mind map",
                category = "Functions",
                colorType = NodeColorType.MINT
            ),
            ArenaGameItem(
                id = "projectile_lab",
                title = "Projectile Lab",
                subtitle = "Trajectory artillery challenge",
                category = "Vectors",
                colorType = NodeColorType.AMBER
            ),
            ArenaGameItem(
                id = "force_builder",
                title = "Force Builder",
                subtitle = "Static vs kinetic friction",
                category = "Calculus",
                colorType = NodeColorType.CYAN
            ),
            ArenaGameItem(
                id = "graph_escape",
                title = "Graph Escape",
                subtitle = "Linear coordinate barrier",
                category = "Algebra",
                colorType = NodeColorType.PERIWINKLE
            )
        )
    }

    val filteredGames = remember(selectedCategory) {
        if (selectedCategory == "All") gamesList.filter { !it.isFeatured }
        else gamesList.filter { it.category == selectedCategory && !it.isFeatured }
    }

    var arenaMode by remember { mutableStateOf("Games") } // "Games" or "Simulations"

    // If active game or simulation is launched, render the screen directly with clean exit
    if (activeGameId != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B101E))
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF151C2E),
                border = BorderStroke(1.dp, Color(0xFF25304C))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { activeGameId = null }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back to Arena", tint = Color.White)
                    }
                    Text(
                        text = "Back to Arena",
                        style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (activeGameId) {
                    "sim_projectile" -> {
                        com.example.calcmaster.simulations.ProjectileSimulation(onSendToCalculator = onSendToCalculator)
                    }
                    "sim_shm" -> {
                        com.example.calcmaster.simulations.SimpleHarmonicMotionSimulation(onSendToCalculator = onSendToCalculator)
                    }
                    "sim_forces" -> {
                        com.example.calcmaster.simulations.ForcesAndMotionSimulation(onSendToCalculator = onSendToCalculator)
                    }
                    "sim_unit_circle" -> {
                        com.example.calcmaster.simulations.UnitCircleSimulation(onSendToCalculator = onSendToCalculator)
                    }
                    "sim_conics" -> {
                        com.example.calcmaster.simulations.ConicSectionsSimulation(onSendToCalculator = onSendToCalculator)
                    }
                    "unit_circle_race", "unit_circle_quest", "formula_match" -> {
                        UnitCircleQuestGame(
                            onSendToCalculator = onSendToCalculator,
                            onOpenTutor = onOpenTutor,
                            onBackToDirectory = { activeGameId = null }
                        )
                    }
                    "projectile_lab" -> {
                        ProjectileLabGame(
                            onSendToCalculator = onSendToCalculator,
                            onOpenTutor = onOpenTutor,
                            onBackToDirectory = { activeGameId = null }
                        )
                    }
                    "equation_puzzle", "map_builder" -> {
                        AlgebraArenaGame(
                            onSendToCalculator = onSendToCalculator,
                            onOpenTutor = onOpenTutor,
                            onBackToDirectory = { activeGameId = null }
                        )
                    }
                    "force_builder" -> {
                        ForceBuilderGame(
                            onSendToCalculator = onSendToCalculator,
                            onOpenTutor = onOpenTutor,
                            onBackToDirectory = { activeGameId = null }
                        )
                    }
                    "graph_spotter", "graph_escape" -> {
                        GraphEscapeGame(
                            onSendToCalculator = onSendToCalculator,
                            onOpenTutor = onOpenTutor,
                            onBackToDirectory = { activeGameId = null }
                        )
                    }
                    else -> {
                        UnitCircleQuestGame(
                            onSendToCalculator = onSendToCalculator,
                            onOpenTutor = onOpenTutor,
                            onBackToDirectory = { activeGameId = null }
                        )
                    }
                }
            }
        }
        return
    }

    // Main Game Arena View matching Image 2 pixel-for-pixel
    Scaffold(
        containerColor = Color(0xFF0B101E)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Title & Subtitle from Image 2
                Text(
                    text = "Game Arena",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    modifier = Modifier.testTag("txt_arena_title")
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Learn every concept by playing",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF8E9BB5),
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Top Arena Mode Switcher: [Concept Games] [Interactive Simulations]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Concept Games (108)" to "Games", "Interactive Simulations (Lab)" to "Simulations").forEach { (label, modeKey) ->
                        val isSel = arenaMode == modeKey
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { arenaMode = modeKey }
                                .testTag("arena_tab_$modeKey"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) Color(0xFF2ED8A7) else Color(0xFF151C2E),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF2ED8A7) else Color(0xFF25304C))
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) Color(0xFF0B101E) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            if (arenaMode == "Simulations") {
                // Interactive Simulations List
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val simList = listOf(
                            Triple("sim_projectile", "Variable Flight Projectile Simulator", "Adjust launch angle, v₀ and planetary gravity (Earth/Moon/Mars)"),
                            Triple("sim_shm", "Harmonic Oscillator (Spring-Mass)", "Simulate amplitude, spring constant k, mass, period T and velocity"),
                            Triple("sim_forces", "Forces & Newton's Laws Lab", "Static vs kinetic friction and inclined plane acceleration"),
                            Triple("sim_unit_circle", "Unit Circle Trigonometric Projections", "Interactive real-time sin, cos, and tan projections on unit circle"),
                            Triple("sim_conics", "Conic Sections & Quadratic Curves", "Eccentricity, focus, directrix for ellipse, parabola, hyperbola")
                        )

                        simList.forEach { (simId, title, desc) ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { activeGameId = simId }
                                    .testTag("sim_tile_$simId"),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF151C2E),
                                border = BorderStroke(1.dp, Color(0xFF25304C))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF1A3833),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Science,
                                                contentDescription = null,
                                                tint = Color(0xFF2ED8A7),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = title, style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 12.sp))
                                    }
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF6B7A99))
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            modifier = Modifier
                                .clickable { selectedCategory = cat }
                                .testTag("cat_chip_$cat"),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color(0xFF6C6CF6) else Color(0xFF151C2E),
                            border = if (!isSelected) BorderStroke(1.dp, Color(0xFF25304C)) else null
                        ) {
                            Text(
                                text = cat,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) Color.White else Color(0xFF8E9BB5),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }

            // FEATURED HERO CARD (Vibrant Mint #2ED8A7 matching Image 2)
            item {
                val featuredGame = gamesList.first { it.isFeatured }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_featured_game"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2ED8A7))
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {
                        Text(
                            text = "FEATURED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF0B101E),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = featuredGame.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color(0xFF0B101E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = featuredGame.subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF0B101E).copy(alpha = 0.85f),
                                fontSize = 15.sp,
                                lineHeight = 20.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { activeGameId = featuredGame.id },
                            modifier = Modifier
                                .width(96.dp)
                                .height(44.dp)
                                .testTag("btn_featured_play"),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B101E),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Play",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }
            }

            // 2-Column Game Grid from Image 2
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val rows = filteredGames.chunked(2)
                    rows.forEach { rowGames ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowGames.forEach { game ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(130.dp)
                                        .clickable { activeGameId = game.id }
                                        .testTag("game_tile_${game.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF151C2E),
                                    border = BorderStroke(1.dp, Color(0xFF25304C))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Colored square icon matching Image 2
                                        Surface(
                                            modifier = Modifier.size(24.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            color = game.colorType.color
                                        ) {}

                                        Column {
                                            Text(
                                                text = game.title,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = game.subtitle,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF8E9BB5),
                                                    fontSize = 12.sp
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowGames.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
