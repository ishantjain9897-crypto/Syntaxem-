package com.example.calcmaster.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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

data class GardenPlant(
    val id: String,
    val name: String,
    val concept: String,
    val iconEmoji: String,
    val stage: Int, // 1: Sprout, 2: Seedling, 3: Budding, 4: Blooming
    val maxStage: Int = 4,
    val healthy: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConceptGardenScreen(
    onReviewConcept: (String) -> Unit
) {
    var plants by remember {
        mutableStateOf(
            listOf(
                GardenPlant("p1", "Trigo Lotus", "Trigonometric Identities", "🪷", 4),
                GardenPlant("p2", "Kinematics Fern", "Projectile Motion in 2D", "🌿", 3),
                GardenPlant("p3", "Quadratic Bonsai", "Complex & Real Roots", "🪴", 4),
                GardenPlant("p4", "Newton's Oak", "Laws of Motion & Friction", "🌳", 2),
                GardenPlant("p5", "Thermo Sunflower", "Heat Engines & Carnot Cycle", "🌻", 2),
                GardenPlant("p6", "SHM Willow", "Oscillations & Resonance", "🌾", 1)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Concept Garden",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Growth without Pressure • Zero Streaks • 100% Private",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Calm Ethical Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("garden_banner_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B).copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFF10B981).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Spa, contentDescription = null, tint = Color(0xFF10B981))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Your Knowledge Sanctuary",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Every concept you understand blossoms a plant. Taking a break pauses growth—your plants never wither or die.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Living Concepts (${plants.count { it.stage == it.maxStage }}/${plants.size} Blooming)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(plants, key = { it.id }) { plant ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = plant.iconEmoji,
                                style = MaterialTheme.typography.displaySmall
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = plant.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = plant.concept,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { plant.stage.toFloat() / plant.maxStage.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = Color(0xFF10B981),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (plant.stage == plant.maxStage) "Blooming ★" else "Stage ${plant.stage}/${plant.maxStage}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (plant.stage == plant.maxStage) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            FilledTonalButton(
                                onClick = { onReviewConcept(plant.concept) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Nurture", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
