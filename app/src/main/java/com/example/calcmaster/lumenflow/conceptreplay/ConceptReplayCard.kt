package com.example.calcmaster.lumenflow.conceptreplay

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.core.calculation.model.Result

/**
 * Intelligent detector that identifies the Class 11 curriculum topic from an expression.
 */
fun detectConceptTopic(expression: String): ConceptTopicInfo {
    val lower = expression.lowercase()
    return when {
        lower.contains("9.8") || lower.contains("20^2") || lower.contains("sin(2") || lower.contains("v_0") || lower.contains("projectile") -> {
            ConceptTopicInfo(
                topicName = "Motion in a Plane: Projectiles",
                simId = "projectile",
                gameId = "projectile_lab",
                gameTitle = "Projectile Lab Target Challenge",
                simTitle = "Variable Flight Simulator",
                keyFormula = "R = (v₀² sin 2θ) / g"
            )
        }
        lower.contains("x^2") || lower.contains("x²") || lower.contains("- 5*x") || lower.contains("quadratic") -> {
            ConceptTopicInfo(
                topicName = "Quadratic Equations & Roots",
                simId = "projectile",
                gameId = "algebra_arena",
                gameTitle = "Algebra Arena: Factoring Duel",
                simTitle = "Parabolic Trajectory Plot",
                keyFormula = "x = (-b ± √(b² - 4ac)) / (2a)"
            )
        }
        lower.contains("0.3") || lower.contains("0.4") || lower.contains("friction") || lower.contains("force") -> {
            ConceptTopicInfo(
                topicName = "Newton's Laws & Friction",
                simId = "newton",
                gameId = "force_builder",
                gameTitle = "Force & Motion Builder",
                simTitle = "Friction Dynamics Simulation",
                keyFormula = "f_s,max = μ_s N, F_net = ma"
            )
        }
        lower.contains("sin") || lower.contains("cos") || lower.contains("tan") || lower.contains("pi") -> {
            ConceptTopicInfo(
                topicName = "Trigonometry & Unit Circle",
                simId = "vectors",
                gameId = "unit_circle_quest",
                gameTitle = "Unit Circle Quest",
                simTitle = "Vector & Angle Visualizer",
                keyFormula = "cos² θ + sin² θ = 1, ASTC Quadrant Rules"
            )
        }
        else -> {
            ConceptTopicInfo(
                topicName = "Class 11 Scientific Computation",
                simId = "projectile",
                gameId = "graph_escape",
                gameTitle = "Graph Escape: Coordinate Barrier",
                simTitle = "Trajectory Simulation",
                keyFormula = "Exact Symbolic & Decimal Derivation"
            )
        }
    }
}

data class ConceptTopicInfo(
    val topicName: String,
    val simId: String,
    val gameId: String,
    val gameTitle: String,
    val simTitle: String,
    val keyFormula: String
)

@Composable
fun ConceptReplayCard(
    expression: String,
    evaluationResult: Result?,
    onSeeSimulation: (String) -> Unit,
    onPlayGame: (String) -> Unit,
    onRemember: (String) -> Unit,
    onOpenTutor: (String, String) -> Unit,
    onSeeDerivation: () -> Unit = {}
) {
    val concept = remember(expression) { detectConceptTopic(expression) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_concept_replay"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CONCEPT REPLAY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = concept.topicName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Text(
                text = "“SOLVE → UNDERSTAND → EXPERIMENT → APPLY → REMEMBER” loop: Move smoothly between verified calculations, step-by-step logic, visual experiments, and private memory revision.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            // The 5 Connected Action Steps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 1. SOLVE
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("replay_step_solve"),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF192A24),
                    border = BorderStroke(1.dp, Color(0xFF2ED8A7))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2ED8A7), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "SOLVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2ED8A7),
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = "Exact",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp, color = Color(0xFF2ED8A7))
                        )
                    }
                }

                // 2. UNDERSTAND (Steps / Derivations)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSeeDerivation() }
                        .testTag("replay_step_understand"),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "STEPS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = "Why Rule",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp)
                        )
                    }
                }

                // 3. EXPERIMENT (Simulation)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSeeSimulation(concept.simId) }
                        .testTag("replay_step_see"),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = Color(0xFFFDB043), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "EXPERIMENT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDB043),
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = "Sim Lab",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp)
                        )
                    }
                }

                // 4. APPLY (Game Challenge)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onPlayGame(concept.gameId) }
                        .testTag("replay_step_play"),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = Color(0xFF6C6CF6), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "APPLY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6C6CF6),
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = "Arena",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp)
                        )
                    }
                }

                // 5. REMEMBER (Private Notes & Journal)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onRemember(concept.topicName) }
                        .testTag("replay_step_remember"),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, tint = Color(0xFFFF6B8B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "REMEMBER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6B8B),
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = "Review",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp)
                        )
                    }
                }
            }

            // Quick launch buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onPlayGame(concept.gameId) },
                    modifier = Modifier.weight(1f).testTag("btn_replay_play"),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play: ${concept.gameTitle}", fontSize = 11.sp, maxLines = 1)
                }

                OutlinedButton(
                    onClick = { onSeeSimulation(concept.simId) },
                    modifier = Modifier.weight(1f).testTag("btn_replay_see"),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("See in Lab", fontSize = 11.sp, maxLines = 1)
                }
            }
        }
    }
}
