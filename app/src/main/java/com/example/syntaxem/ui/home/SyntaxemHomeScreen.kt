package com.example.syntaxem.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcmaster.lumenflow.model.LumenLearningRepository
import com.example.syntaxem.model.SyntaxemCurriculumData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyntaxemHomeScreen(
    onNavigateToChapter: (String) -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    onOpenTutor: (String, String) -> Unit = { _, _ -> }
) {
    val activeChapter = remember { SyntaxemCurriculumData.chapters.first() }
    val mistakes = LumenLearningRepository.mistakes

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
            // App Branding Header
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF6C6CF6),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AllInclusive,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Syntaxem",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                            )
                            Text(
                                text = "Mathematics · Class 11 & 12",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF8E9BB5),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF151C2E),
                        border = BorderStroke(1.dp, Color(0xFF25304C))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = CircleShape, color = Color(0xFF2ED8A7), modifier = Modifier.size(8.dp)) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "100% Offline",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2ED8A7), fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Quick Calculator Shortcut Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCalculator() }
                        .testTag("card_quick_calc_home"),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF151C2E),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF2ED8A7).copy(alpha = 0.15f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        tint = Color(0xFF2ED8A7),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Scientific Calculator",
                                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Exact surds, AST derivation & memory",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5))
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF8E9BB5)
                        )
                    }
                }
            }

            // Continue Learning / Active Chapter Card (Mind Map Preview)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToChapter(activeChapter.id) }
                        .testTag("card_continue_learning"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                    border = BorderStroke(1.dp, Color(0xFF6C6CF6).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE CHAPTER • CLASS ${activeChapter.classLevel}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF6C6CF6),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF6C6CF6).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Open Mind Map",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6C6CF6), fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = activeChapter.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Explore 6 radiating concept nodes: Angles, Ratios, Unit circle, Identities, Equations, and Graphs.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF8E9BB5))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeChapter.mindMapNodes.take(3).forEach { node ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = node.colorType.color
                                ) {
                                    Text(
                                        text = node.label,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF0B101E),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Featured Game Arena Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToGames() }
                        .testTag("card_home_game_arena"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2ED8A7))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "108 CONCEPT GAMES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF0B101E),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Game Arena",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = Color(0xFF0B101E),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Unit Circle Race, Formula Match & Graph Spotter.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF0B101E).copy(alpha = 0.8f)
                                )
                            )
                        }

                        Button(
                            onClick = onNavigateToGames,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B101E),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Enter Arena")
                        }
                    }
                }
            }

            // All 27 Chapters Carousel
            item {
                Text(
                    text = "All Chapters (NCERT 11 & 12)",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(SyntaxemCurriculumData.chapters) { ch ->
                        Surface(
                            modifier = Modifier
                                .width(200.dp)
                                .clickable { onNavigateToChapter(ch.id) },
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF151C2E),
                            border = BorderStroke(1.dp, Color(0xFF25304C))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Class ${ch.classLevel} · Ch ${ch.number}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6C6CF6), fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ch.title,
                                    style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${ch.mindMapNodes.size} Mind Map Nodes",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }
            }

            // Mistake Journal Review Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF151C2E),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MISTAKE JOURNAL (${mistakes.size})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFFDB043))
                            )
                            Text("Zero-streak anxiety", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Preserves concept misconceptions privately for low-pressure spaced review.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8E9BB5)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
