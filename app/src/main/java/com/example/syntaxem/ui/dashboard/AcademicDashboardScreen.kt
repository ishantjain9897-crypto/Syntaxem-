package com.example.syntaxem.ui.dashboard

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.draw.clip
import com.example.core.designsystem.SyntaxemTheme
import com.example.syntaxem.data.weather.CityLocation
import com.example.syntaxem.model.VisualLibraryPipeline
import com.example.syntaxem.ui.ai.AiTutorDialog
import com.example.syntaxem.ui.appearance.AppearanceDialog
import com.example.syntaxem.ui.onboarding.OnboardingDialog
import com.example.syntaxem.ui.search.GlobalSearchDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicDashboardScreen(
    viewModel: AcademicDashboardViewModel = viewModel(),
    onNavigateToSyllabus: () -> Unit = {},
    onNavigateToCalculator: () -> Unit = {},
    onNavigateToLearn: (String?) -> Unit = {},
    onNavigateToGames: () -> Unit = {}
) {
    val palette = SyntaxemTheme.palette
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCityDialog by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showAiTutorDialog by remember { mutableStateOf(false) }
    var showOnboardingDialog by remember { mutableStateOf(false) }
    var showAppearanceDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.checkLocationPermissionAndRefresh()
    }

    Scaffold(
        containerColor = palette.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Academic Command Centre Greeting
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "COMMAND CENTRE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = palette.secondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "Academic Session ${state.academicSession}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = palette.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { showAppearanceDialog = true },
                            modifier = Modifier.size(36.dp).testTag("btn_dashboard_appearance")
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = "Appearance & Color Sets", tint = palette.primary)
                        }
                        IconButton(
                            onClick = { showSearchDialog = true },
                            modifier = Modifier.size(36.dp).testTag("btn_dashboard_search")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = palette.textPrimary)
                        }
                        IconButton(
                            onClick = { showAiTutorDialog = true },
                            modifier = Modifier.size(36.dp).testTag("btn_dashboard_ai_tutor")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Tutor", tint = palette.secondary)
                        }
                        IconButton(
                            onClick = { showOnboardingDialog = true },
                            modifier = Modifier.size(36.dp).testTag("btn_dashboard_profile_setup")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Setup", tint = palette.textSecondary)
                        }
                    }
                }
            }

            // Live Clock Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("dashboard_live_clock_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF6C6CF6),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DEVICE LOCAL TIME",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF8E9BB5),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.currentTimeString.ifEmpty { "12:00:00" },
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${state.currentDateString} • ${state.timeZoneString}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF8E9BB5),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.toggleTimeFormat() },
                            modifier = Modifier.testTag("btn_toggle_time_format"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            border = BorderStroke(1.dp, Color(0xFF25304C))
                        ) {
                            Text(
                                text = if (state.is24HourFormat) "24h" else "12h",
                                color = Color(0xFF2ED8A7),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Weather & Live Environment Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("dashboard_weather_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Cloud,
                                    contentDescription = null,
                                    tint = Color(0xFFFDB043),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "STUDY ENVIRONMENT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF8E9BB5),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextButton(
                                    onClick = { showCityDialog = true },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2ED8A7), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = state.selectedCity.name,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2ED8A7), fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        val weather = state.weather
                        if (weather != null && weather.isAvailable) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "%.1f°C".format(weather.temperature),
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "${weather.condition} • Feels like %.1f°C".format(weather.feelsLikeTemperature),
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC0CADB))
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Humidity: ${weather.humidity}%",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp)
                                    )
                                    Text(
                                        text = "Wind: %.1f km/h".format(weather.windSpeed),
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1E263D)) {
                                        Text(
                                            text = weather.freshnessLabel,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                color = if (weather.isStale) Color(0xFFFDB043) else Color(0xFF2ED8A7)
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            // Offline or Unavailable State
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E263D)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Weather Unavailable (Offline Mode)",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Using ${state.selectedCity.displayName}. Connect to internet or select another city.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 10.sp)
                                        )
                                    }
                                    IconButton(onClick = { viewModel.checkLocationPermissionAndRefresh() }) {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", tint = Color(0xFF2ED8A7))
                                    }
                                }
                            }
                        }

                        // Permission Rationale Banner (if location not granted)
                        if (!state.hasLocationPermission) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1A2234),
                                border = BorderStroke(1.dp, Color(0xFF25304C))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Enable approximate GPS for local weather",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 10.sp)
                                    )
                                    TextButton(
                                        onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Enable", color = Color(0xFF2ED8A7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Provider Attribution
                        Text(
                            text = "Attribution: ${weather?.sourceProvider ?: "Open-Meteo Open Meteorological Data"}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF5A6882), fontSize = 9.sp)
                        )
                    }
                }
            }

            // AI Socratic Tutor Quick Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAiTutorDialog = true }
                        .testTag("dashboard_ai_tutor_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                    border = BorderStroke(1.dp, Color(0xFF2ED8A7).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1A3833),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF2ED8A7), modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "SYNTAXEM SOCRATIC TUTOR",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF2ED8A7),
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                    Text(
                                        text = "Learn. Visualize. Solve.",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF2ED8A7))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Axiomatic derivations, 3 difficulty tiers (Intuitive → NCERT → JEE), and common misconception diagnoses.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showAiTutorDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                border = BorderStroke(1.dp, Color(0xFF25304C))
                            ) {
                                Text("Ask Tutor", color = Color(0xFF2ED8A7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { showAiTutorDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                border = BorderStroke(1.dp, Color(0xFF25304C))
                            ) {
                                Text("Explain Steps", color = Color(0xFFFDB043), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { showAiTutorDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                border = BorderStroke(1.dp, Color(0xFF25304C))
                            ) {
                                Text("Quick Quiz", color = Color(0xFF6C6CF6), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Visual Library & Schema Coverage Pipeline Card
            item {
                val report = remember { VisualLibraryPipeline.getContentCoverageReport() }
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("dashboard_visual_library_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "VISUAL LIBRARY & SCHEMA PIPELINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF6C6CF6),
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "${report.totalCreated} Validated Diagrams Bundled",
                                    style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF222B42)
                            ) {
                                Text(
                                    text = "20,000+ Target",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9BB5), fontSize = 10.sp)
                                )
                            }
                        }

                        Text(
                            text = "Real content report: ${report.verifiedNcertCount} Verified NCERT • ${report.provisionalCount} Provisional Vidya Bharati • ${report.pendingReviewCount} In Review. 100% schema integrity, zero duplicates.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BreakdownPill("Verified", report.verifiedNcertCount, Color(0xFF2ED8A7))
                            BreakdownPill("Provisional", report.provisionalCount, Color(0xFFFDB043))
                            BreakdownPill("In Review", report.pendingReviewCount, Color(0xFF6C6CF6))
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { onNavigateToLearn(null) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C6CF6)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("View Mind Maps", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Overall Syllabus Completion Meter
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("dashboard_syllabus_meter_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SYLLABUS COMPLETION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF6C6CF6),
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = state.overallProgress.displayFraction,
                                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                            }

                            Text(
                                text = state.overallProgress.displayPercentage,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color(0xFF2ED8A7),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { state.overallProgress.percentage / 100f },
                            modifier = Modifier.fillMaxWidth().height(10.dp),
                            color = Color(0xFF2ED8A7),
                            trackColor = Color(0xFF25304C)
                        )

                        // Breakdown Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BreakdownPill(label = "Done", count = state.overallProgress.completedCount, color = Color(0xFF2ED8A7))
                            BreakdownPill(label = "In Progress", count = state.overallProgress.inProgressCount, color = Color(0xFF6C6CF6))
                            BreakdownPill(label = "Revision", count = state.overallProgress.needsRevisionCount, color = Color(0xFFFDB043))
                            BreakdownPill(label = "Remaining", count = state.overallProgress.notStartedCount, color = Color(0xFF8E9BB5))
                        }

                        Button(
                            onClick = onNavigateToSyllabus,
                            modifier = Modifier.fillMaxWidth().testTag("btn_open_syllabus_tracker"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C6CF6))
                        ) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Full Syllabus Tracker", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Subject Cards (Mathematics, Physics, Chemistry)
            item {
                Text(
                    text = "SUBJECT PROGRESS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.subjectSummaries) { summary ->
                        Card(
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { onNavigateToSyllabus() }
                                .testTag("card_subject_${summary.subject.name.lowercase()}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                            border = BorderStroke(1.dp, Color(0xFF25304C))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = summary.subject.name,
                                    style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Text(
                                    text = "${summary.completedChapters} / ${summary.totalChapters} Ch.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp)
                                )
                                LinearProgressIndicator(
                                    progress = { summary.progress.percentage / 100f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp),
                                    color = Color(0xFF2ED8A7),
                                    trackColor = Color(0xFF25304C)
                                )
                                Text(
                                    text = summary.progress.displayPercentage,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF2ED8A7),
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Continue Studying Card
            if (state.continueChapter != null) {
                val continueItem = state.continueChapter!!
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("dashboard_continue_study_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                        border = BorderStroke(1.dp, Color(0xFF25304C))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CONTINUE STUDYING",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF2ED8A7),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1E263D)) {
                                    Text(
                                        text = continueItem.progress?.status ?: "IN_PROGRESS",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF2ED8A7),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "Ch ${continueItem.chapter.chapterNumber}: ${continueItem.chapter.title}",
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = continueItem.chapter.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5)),
                                maxLines = 2
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onNavigateToLearn(continueItem.chapter.id) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ED8A7), contentColor = Color(0xFF0B101E))
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Study Chapter", fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = onNavigateToCalculator,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFF25304C))
                                ) {
                                    Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Scientific Calc", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Revision Queue Card
            if (state.revisionQueue.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("dashboard_revision_queue_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                        border = BorderStroke(1.dp, Color(0xFFFDB043).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFFDB043), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "REVISION QUEUE (${state.revisionQueue.size})",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFDB043), fontWeight = FontWeight.Bold)
                                    )
                                }
                                TextButton(onClick = onNavigateToSyllabus) {
                                    Text("View All", fontSize = 11.sp, color = Color(0xFFFDB043))
                                }
                            }

                            state.revisionQueue.take(2).forEach { item ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E263D),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.chapter.title,
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                            )
                                            if (!item.progress?.personalNotes.isNullOrBlank()) {
                                                Text(
                                                    text = item.progress!!.personalNotes!!,
                                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 10.sp),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                        IconButton(onClick = { onNavigateToLearn(item.chapter.id) }) {
                                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Revise", tint = Color(0xFFFDB043), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Daily Study Suggestion Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("dashboard_suggestion_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF6C6CF6).copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TipsAndUpdates,
                                    contentDescription = null,
                                    tint = Color(0xFF6C6CF6),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "DAILY STUDY SUGGESTION",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6C6CF6), fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = state.dailySuggestion,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC0CADB))
                            )
                        }
                    }
                }
            }

            // Appearance & Color Sets Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAppearanceDialog = true }
                        .testTag("card_dashboard_appearance"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = palette.surface),
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = palette.primary.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = palette.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "Appearance & Color Sets",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = palette.textPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Current: ${palette.name} • ${if (palette.isDark) "Dark" else "Light"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = palette.textSecondary
                                    )
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            palette.previewColors.take(4).forEach { col ->
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(col)
                                        .border(BorderStroke(1.dp, palette.border), CircleShape)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open Appearance",
                                tint = palette.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Manual City Selection Dialog
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            containerColor = Color(0xFF151C2E),
            title = {
                Text(
                    text = "Select City for Study Environment",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(CityLocation.DEFAULT_CITIES) { city ->
                        val isSelected = city.name == state.selectedCity.name
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectCity(city)
                                    showCityDialog = false
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF1E263D) else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, Color(0xFF2ED8A7)) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = city.name, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                    Text(text = city.state, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp))
                                }
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF2ED8A7), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("Close", color = Color(0xFF2ED8A7))
                }
            }
        )
    }

    if (showSearchDialog) {
        GlobalSearchDialog(
            onDismiss = { showSearchDialog = false },
            onNavigateToChapter = { chId ->
                onNavigateToLearn(chId)
            },
            onNavigateToGame = { _ ->
                onNavigateToGames()
            },
            onSendToCalculator = { _ ->
                onNavigateToCalculator()
            }
        )
    }

    if (showAiTutorDialog) {
        AiTutorDialog(
            initialTopic = state.continueChapter?.chapter?.title ?: "Trigonometric Functions",
            onDismiss = { showAiTutorDialog = false },
            onSendToCalculator = { _ ->
                onNavigateToCalculator()
            }
        )
    }

    if (showOnboardingDialog) {
        OnboardingDialog(
            onDismiss = { showOnboardingDialog = false },
            onSavePreferences = { _ -> }
        )
    }

    if (showAppearanceDialog) {
        AppearanceDialog(
            onDismiss = { showAppearanceDialog = false }
        )
    }
}

@Composable
fun BreakdownPill(label: String, count: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF1E263D)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, color = Color(0xFF8E9BB5))
            )
        }
    }
}
