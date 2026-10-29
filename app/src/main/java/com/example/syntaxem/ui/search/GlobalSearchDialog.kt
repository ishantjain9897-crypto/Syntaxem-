package com.example.syntaxem.ui.search

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

enum class SearchCategory(val title: String) {
    ALL("All"),
    CHAPTERS("Chapters"),
    FORMULAS("Formulas"),
    GAMES("Games & Sims"),
    MIND_MAPS("Mind Maps")
}

data class SearchResultItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: SearchCategory,
    val icon: ImageVector,
    val actionPayload: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchDialog(
    onDismiss: () -> Unit,
    onNavigateToChapter: (String) -> Unit,
    onNavigateToGame: (String) -> Unit,
    onSendToCalculator: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(SearchCategory.ALL) }

    val allSearchItems = remember {
        listOf(
            // Chapters
            SearchResultItem("ch_11_m_03", "Trigonometric Functions", "Class 11 Mathematics • Chapter 3", SearchCategory.CHAPTERS, Icons.Default.MenuBook, "ch_03_trigonometry"),
            SearchResultItem("ch_11_m_01", "Sets & Representations", "Class 11 Mathematics • Chapter 1", SearchCategory.CHAPTERS, Icons.Default.MenuBook, "ch_01_sets"),
            SearchResultItem("ch_11_m_02", "Relations and Functions", "Class 11 Mathematics • Chapter 2", SearchCategory.CHAPTERS, Icons.Default.MenuBook, "ch_02_relations"),
            SearchResultItem("ch_11_p_04", "Laws of Motion & Friction", "Class 11 Physics • Chapter 4", SearchCategory.CHAPTERS, Icons.Default.Science, "ch_11_p_04"),
            SearchResultItem("ch_11_p_03", "Motion in a Plane: Projectiles & Vectors", "Class 11 Physics • Chapter 3", SearchCategory.CHAPTERS, Icons.Default.Science, "ch_11_p_03"),
            SearchResultItem("ch_11_c_04", "Chemical Bonding & Molecular Structure", "Class 11 Chemistry • Chapter 4", SearchCategory.CHAPTERS, Icons.Default.Biotech, "ch_11_c_04"),

            // Formulas
            SearchResultItem("f_trig_double", "Double Angle Identity", "sin(2θ) = 2 sin(θ) cos(θ)", SearchCategory.FORMULAS, Icons.Default.Calculate, "2 * sin(x) * cos(x)"),
            SearchResultItem("f_quad", "Quadratic Formula", "x = (-b ± √(b² - 4ac)) / (2a)", SearchCategory.FORMULAS, Icons.Default.Calculate, "x^2 - 5*x + 6 = 0"),
            SearchResultItem("f_proj_range", "Projectile Range Equation", "R = (v₀² sin(2θ)) / g", SearchCategory.FORMULAS, Icons.Default.Calculate, "30^2 * sin(2 * 45) / 9.8"),
            SearchResultItem("f_euler", "Euler's Identity", "e^(iπ) + 1 = 0", SearchCategory.FORMULAS, Icons.Default.Calculate, "e^(pi * 0)"),
            SearchResultItem("f_shm", "Simple Harmonic Motion Period", "T = 2π √(m / k)", SearchCategory.FORMULAS, Icons.Default.Calculate, "2 * pi * sqrt(2 / 50)"),

            // Games & Simulations
            SearchResultItem("g_unit_circle", "Unit Circle Race", "Interactive Game • Match angles and trig values", SearchCategory.GAMES, Icons.Default.SportsEsports, "unit_circle_race"),
            SearchResultItem("g_formula_match", "Formula Match Arena", "Speed matching of Class 11 identities", SearchCategory.GAMES, Icons.Default.SportsEsports, "formula_match"),
            SearchResultItem("g_projectile_sim", "Variable Flight Simulator", "Interactive Trajectory Physics Simulation", SearchCategory.GAMES, Icons.Default.RocketLaunch, "sim_projectile"),
            SearchResultItem("g_graph_spotter", "Graph Spotter Challenge", "Visual curve identification", SearchCategory.GAMES, Icons.Default.SportsEsports, "graph_spotter"),
            SearchResultItem("g_harmonic_sim", "Harmonic Oscillator Simulator", "Spring-mass & pendulum phase simulation", SearchCategory.GAMES, Icons.Default.ShowChart, "sim_shm"),

            // Mind Maps
            SearchResultItem("mm_trig_mindmap", "Trigonometric Functions Mind Map", "Angles, Ratios, Unit Circle, Identities, Equations, Graphs", SearchCategory.MIND_MAPS, Icons.Default.Hub, "ch_03_trigonometry"),
            SearchResultItem("mm_vectors_flow", "Vector Cross & Dot Product Flowchart", "Directional derivation and right-hand rule workflow", SearchCategory.MIND_MAPS, Icons.Default.AccountTree, "flowchart_vectors"),
            SearchResultItem("mm_bonding_tree", "VSEPR & Hybridization Classification Tree", "Molecular geometry pathways", SearchCategory.MIND_MAPS, Icons.Default.Hub, "ch_11_c_04")
        )
    }

    val filteredItems = remember(query, selectedCategory) {
        allSearchItems.filter { item ->
            val matchesCat = selectedCategory == SearchCategory.ALL || item.category == selectedCategory
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.subtitle.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .testTag("global_search_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F1526),
            border = BorderStroke(1.dp, Color(0xFF25304C)),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header & Search Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXPLORE SYNTAXEM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF2ED8A7),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8E9BB5))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field"),
                    placeholder = { Text("Search chapters, formulas, games, simulations...", color = Color(0xFF6B7A99), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF2ED8A7)) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF8E9BB5))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF2ED8A7),
                        unfocusedBorderColor = Color(0xFF25304C),
                        focusedContainerColor = Color(0xFF151C2E),
                        unfocusedContainerColor = Color(0xFF151C2E)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Filter Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SearchCategory.values().forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            modifier = Modifier.clickable { selectedCategory = cat },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFF2ED8A7) else Color(0xFF151C2E),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF2ED8A7) else Color(0xFF25304C))
                        ) {
                            Text(
                                text = cat.title,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color(0xFF0B101E) else Color(0xFF8E9BB5),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Results list
                if (filteredItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No curriculum records found for '$query'", color = Color(0xFF8E9BB5), style = MaterialTheme.typography.bodyMedium)
                            Text("Try searching 'trig', 'projectile', 'formula', or 'sets'", color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredItems) { item ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        when (item.category) {
                                            SearchCategory.CHAPTERS, SearchCategory.MIND_MAPS -> {
                                                onNavigateToChapter(item.actionPayload)
                                                onDismiss()
                                            }
                                            SearchCategory.GAMES -> {
                                                onNavigateToGame(item.actionPayload)
                                                onDismiss()
                                            }
                                            SearchCategory.FORMULAS -> {
                                                onSendToCalculator(item.actionPayload)
                                                onDismiss()
                                            }
                                            else -> {
                                                onNavigateToChapter(item.actionPayload)
                                                onDismiss()
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF151C2E),
                                border = BorderStroke(1.dp, Color(0xFF25304C))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = when (item.category) {
                                            SearchCategory.CHAPTERS -> Color(0xFF1B3B36)
                                            SearchCategory.FORMULAS -> Color(0xFF382B14)
                                            SearchCategory.GAMES -> Color(0xFF221F45)
                                            SearchCategory.MIND_MAPS -> Color(0xFF1F2847)
                                            else -> Color(0xFF1F2847)
                                        },
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = null,
                                                tint = when (item.category) {
                                                    SearchCategory.CHAPTERS -> Color(0xFF2ED8A7)
                                                    SearchCategory.FORMULAS -> Color(0xFFFDB043)
                                                    SearchCategory.GAMES -> Color(0xFFFF5376)
                                                    SearchCategory.MIND_MAPS -> Color(0xFF6C6CF6)
                                                    else -> Color(0xFF6C6CF6)
                                                },
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = item.subtitle,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF8E9BB5)
                                            )
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFF6B7A99),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
