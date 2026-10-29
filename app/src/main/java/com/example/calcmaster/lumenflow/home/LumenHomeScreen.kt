package com.example.calcmaster.lumenflow.home

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcmaster.lumenflow.model.LumenCurriculumData
import com.example.calcmaster.lumenflow.model.LumenLearningRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumenHomeScreen(
    onNavigateToCalculator: () -> Unit,
    onNavigateToLearnTopic: (String) -> Unit,
    onNavigateToLabSimulation: (String) -> Unit,
    onNavigateToProgress: () -> Unit,
    onOpenTutor: (contextTitle: String, formula: String) -> Unit,
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean
) {
    var isDailyRecDismissed by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AllInclusive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "CALCMASTER 2026",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                )
                            )
                            Text(
                                text = "LUMEN FLOW • Scientific Learning",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onOpenTutor("General Scientific Inquiry", "") },
                        modifier = Modifier.testTag("btn_home_tutor")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "AI Tutor",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier.testTag("btn_home_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
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
            // 1. Welcome Greeting & Offline Availability Indicator
            item {
                Spacer(modifier = Modifier.height(4.dp))
                OfflineAvailabilityBanner()
            }

            // 2. Continue Learning Action Card
            item {
                ContinueLearningCard(
                    onResume = { onNavigateToLearnTopic("physics_kinematics_2d") }
                )
            }

            // 3. Quick Scientific Calculator Launcher
            item {
                QuickCalculatorEntryCard(onOpenCalculator = onNavigateToCalculator)
            }

            // 4. Daily Practice Recommendation with Reason
            if (!isDailyRecDismissed) {
                item {
                    DailyPracticeRecommendationCard(
                        onStartPractice = { onNavigateToLearnTopic("trig_unit_circle") },
                        onDismiss = { isDailyRecDismissed = true }
                    )
                }
            }

            // 5. Recently Used Tools Carousel
            item {
                RecentlyUsedToolsSection(
                    onOpenCalculator = onNavigateToCalculator,
                    onOpenSimulation = { onNavigateToLabSimulation("projectile") },
                    onOpenTopic = onNavigateToLearnTopic
                )
            }

            // 6. One Suggested Topic with Reason
            item {
                SuggestedTopicCard(
                    onExploreTopic = { onNavigateToLearnTopic("physics_newton_laws") }
                )
            }

            // 7. Saved Mistakes to Review (Mistake Journal Preview)
            item {
                SavedMistakesReviewCard(onOpenMistakes = onNavigateToProgress)
            }

            // 8. Recently Explored Interactive Simulations
            item {
                RecentlyExploredSimulationsCard(
                    onOpenSimulation = onNavigateToLabSimulation
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Clean offline indicator showing standalone functionality without internet dependency.
 */
@Composable
fun OfflineAvailabilityBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth().testTag("offline_availability_banner"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF356B59))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "GUEST MODE • OFFLINE READY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "100% on-device calculations, derivations & simulations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "STANDALONE",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

/**
 * Continue Learning Card that resumes the student's active topic.
 */
@Composable
fun ContinueLearningCard(onResume: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("continue_learning_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CONTINUE LEARNING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "PHYSICS • CLASS 11",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Motion in a Plane: Projectile Trajectories",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Resume orthogonal velocity decomposition and the parabolic range formula R = (v₀² sin 2θ) / g.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onResume,
                modifier = Modifier.fillMaxWidth().testTag("btn_resume_learning"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Resume Concept & Exercises")
            }
        }
    }
}

/**
 * One-tap entry to full scientific calculator.
 */
@Composable
fun QuickCalculatorEntryCard(onOpenCalculator: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenCalculator() }
            .testTag("quick_calc_entry_card"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Quick Scientific Calculator",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Exact fractions, radicals, trigonometry & step derivations",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Daily Practice Recommendation Card with explicit reason.
 */
@Composable
fun DailyPracticeRecommendationCard(
    onStartPractice: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("daily_practice_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY PRACTICE RECOMMENDATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Unit Circle Symmetry & Quadrant Signs",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Reason: You recently calculated projectile angles. Practicing unit circle coordinates (cos θ, sin θ) reinforces vector resolution intuition.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStartPractice,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start 5-Minute Practice (Optional)")
            }
        }
    }
}

/**
 * Recently Used Tools Carousel.
 */
@Composable
fun RecentlyUsedToolsSection(
    onOpenCalculator: () -> Unit,
    onOpenSimulation: () -> Unit,
    onOpenTopic: (String) -> Unit
) {
    Column {
        Text(
            text = "RECENTLY USED TOOLS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val tools = listOf(
            Triple("Scientific Calculator", Icons.Default.Calculate) { onOpenCalculator() },
            Triple("Flight Simulator", Icons.Default.NearMe) { onOpenSimulation() },
            Triple("Unit Circle Quest", Icons.Default.Explore) { onOpenTopic("trig_unit_circle") },
            Triple("Quadratic Derivations", Icons.Default.Functions) { onOpenTopic("math_quadratic") }
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(tools) { (label, icon, action) ->
                Surface(
                    modifier = Modifier.clickable { action() },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium))
                    }
                }
            }
        }
    }
}

/**
 * Suggested Topic Card with explicit educational rationale.
 */
@Composable
fun SuggestedTopicCard(onExploreTopic: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SUGGESTED TOPIC FOR DEEPENING INTUITION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Laws of Motion: Static & Kinetic Friction Thresholds",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Why this topic? Connecting orthogonal gravity vectors to normal force N and maximum static friction f_s ≤ μ_s N bridges Kinematics with Dynamics.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onExploreTopic,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Inspect Topic & Worked Examples")
            }
        }
    }
}

/**
 * Saved Mistakes Review Card (preview of Mistake Journal).
 */
@Composable
fun SavedMistakesReviewCard(onOpenMistakes: () -> Unit) {
    val mistakes = LumenLearningRepository.mistakes

    Card(
        modifier = Modifier.fillMaxWidth().testTag("saved_mistakes_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SAVED MISTAKES TO REVIEW (${mistakes.size})",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                TextButton(onClick = onOpenMistakes) {
                    Text("View Journal", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Reviewing prior misconceptions strengthens retrieval pathways without penalty or streaks.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (mistakes.isEmpty()) {
                        Text(
                            text = "• All concepts verified. Keep solving to log new insights.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                    } else {
                        mistakes.take(2).forEach { item ->
                            Text(
                                text = "• ${item.errorConcept}: ${item.question.take(50)}...",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Recently Explored Interactive Simulations.
 */
@Composable
fun RecentlyExploredSimulationsCard(onOpenSimulation: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "INTERACTIVE LABORATORIES",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            val sims = listOf(
                Pair("Variable Flight Simulator", "Idealized 2D projectile kinematics with live vector telemetry") to "projectile",
                Pair("Harmonic Oscillator & Pendulum", "Deterministic simple harmonic motion T = 2π√(m/k)") to "shm",
                Pair("Vector Addition & Orthogonality", "Decompose components and compute dot/cross products") to "vectors"
            )

            sims.forEachIndexed { index, (info, simId) ->
                val (title, desc) = info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSimulation(simId) }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (index < sims.size - 1) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
