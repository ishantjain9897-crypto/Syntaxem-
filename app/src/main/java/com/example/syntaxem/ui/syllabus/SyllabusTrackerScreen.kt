package com.example.syntaxem.ui.syllabus

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.syntaxem.data.local.dao.ChapterWithProgress
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllabusTrackerScreen(
    viewModel: SyllabusViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onStudyChapter: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var showCurriculumDialog by remember { mutableStateOf(false) }
    var showExportImportDialog by remember { mutableStateOf(false) }
    var exportJsonText by remember { mutableStateOf("") }
    var importJsonInput by remember { mutableStateOf("") }
    var importError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color(0xFF0B101E),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MY SYLLABUS TRACKER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "${state.activeCurriculum?.authority ?: "CBSE / NCERT"} • Session ${state.activeCurriculum?.academicSession ?: "2026–27"}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2ED8A7), fontSize = 11.sp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Curriculum switcher
                    IconButton(onClick = { showCurriculumDialog = true }, modifier = Modifier.testTag("btn_curriculum_settings")) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = "Curriculum Settings", tint = Color.White)
                    }
                    // JSON Export / Import
                    IconButton(
                        onClick = {
                            scope.launch {
                                exportJsonText = viewModel.exportProgress()
                                importJsonInput = ""
                                importError = null
                                showExportImportDialog = true
                            }
                        },
                        modifier = Modifier.testTag("btn_syllabus_export_import")
                    ) {
                        Icon(imageVector = Icons.Default.ImportExport, contentDescription = "Export / Import Progress", tint = Color(0xFF2ED8A7))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF151C2E))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Undo Banner
            AnimatedVisibility(visible = state.canUndo) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E263D),
                    border = BorderStroke(1.dp, Color(0xFF2ED8A7))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.snackbarMessage ?: "Status updated",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                        TextButton(onClick = { viewModel.undoLastStatusChange() }, modifier = Modifier.testTag("btn_undo_status")) {
                            Icon(imageVector = Icons.Default.Undo, contentDescription = null, tint = Color(0xFF2ED8A7), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Undo", color = Color(0xFF2ED8A7), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Summary Progress Header Card
            Card(
                modifier = Modifier.fillMaxWidth().testTag("syllabus_progress_header"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                border = BorderStroke(1.dp, Color(0xFF25304C))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "OVERALL PROGRESS",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6C6CF6), fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = state.progress.displayFraction,
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                        }

                        Text(
                            text = state.progress.displayPercentage,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color(0xFF2ED8A7),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    LinearProgressIndicator(
                        progress = { state.progress.percentage / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFF2ED8A7),
                        trackColor = Color(0xFF25304C)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Completed: ${state.progress.completedCount}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2ED8A7), fontSize = 11.sp))
                        Text(text = "In Progress: ${state.progress.inProgressCount}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6C6CF6), fontSize = 11.sp))
                        Text(text = "Needs Revision: ${state.progress.needsRevisionCount}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFDB043), fontSize = 11.sp))
                        Text(text = "Not Started: ${state.progress.notStartedCount}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.fillMaxWidth().testTag("syllabus_search_bar"),
                placeholder = { Text("Search chapters by name or number...", color = Color(0xFF8E9BB5), fontSize = 13.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF8E9BB5)) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF8E9BB5))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C6CF6),
                    unfocusedBorderColor = Color(0xFF25304C),
                    focusedContainerColor = Color(0xFF151C2E),
                    unfocusedContainerColor = Color(0xFF151C2E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subject Selector Tabs (All, Math, Physics, Chemistry)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    val isAllSelected = state.selectedSubjectId == null
                    FilterChip(
                        selected = isAllSelected,
                        onClick = { viewModel.selectSubject(null) },
                        label = { Text("All Subjects", fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_sub_all"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6C6CF6),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF151C2E),
                            labelColor = Color(0xFF8E9BB5)
                        )
                    )
                }

                items(state.subjects) { sub ->
                    val isSelected = state.selectedSubjectId == sub.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectSubject(sub.id) },
                        label = { Text(sub.name, fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_sub_${sub.name.lowercase()}"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6C6CF6),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF151C2E),
                            labelColor = Color(0xFF8E9BB5)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Status Filter Pills (All, Not Started, In Progress, Completed, Needs Revision)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(StatusFilter.values()) { filter ->
                    val isSelected = state.statusFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setStatusFilter(filter) },
                        label = { Text(filter.label, fontSize = 10.sp) },
                        modifier = Modifier.testTag("filter_status_${filter.name.lowercase()}"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2ED8A7),
                            selectedLabelColor = Color(0xFF0B101E),
                            containerColor = Color(0xFF151C2E),
                            labelColor = Color(0xFF8E9BB5)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chapter List
            if (state.filteredChapters.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = null, tint = Color(0xFF8E9BB5), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No chapters match the selected filters.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF8E9BB5))
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.filteredChapters) { item ->
                        ChapterProgressCard(
                            item = item,
                            onStatusChange = { newStatus ->
                                viewModel.updateChapterStatus(item.chapter.id, newStatus)
                            },
                            onOpenDetail = {
                                viewModel.openChapterDetail(item)
                            },
                            onStudy = {
                                onStudyChapter(item.chapter.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Chapter Detail Sheet / Modal
    if (state.selectedChapterForDetail != null) {
        val detail = state.selectedChapterForDetail!!
        var notesInput by remember { mutableStateOf(detail.progress?.personalNotes ?: "") }
        var selectedStatus by remember { mutableStateOf(detail.progress?.status ?: "NOT_STARTED") }
        var targetDateEpoch by remember { mutableStateOf(detail.progress?.targetDate) }

        AlertDialog(
            onDismissRequest = { viewModel.openChapterDetail(null) },
            containerColor = Color(0xFF151C2E),
            title = {
                Column {
                    Text(
                        text = "Chapter ${detail.chapter.chapterNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6C6CF6), fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = detail.chapter.title,
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = detail.chapter.description,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC0CADB))
                    )

                    Divider(color = Color(0xFF25304C))

                    // Status Selector
                    Text(
                        text = "Completion Status",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "NOT_STARTED" to "Not Started",
                            "IN_PROGRESS" to "Progress",
                            "COMPLETED" to "Done",
                            "NEEDS_REVISION" to "Revision"
                        ).forEach { (code, label) ->
                            val isSel = selectedStatus == code
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedStatus = code }
                                    .testTag("detail_status_$code"),
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) Color(0xFF2ED8A7) else Color(0xFF1E263D),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFF2ED8A7) else Color(0xFF25304C))
                            ) {
                                Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSel) Color(0xFF0B101E) else Color(0xFF8E9BB5),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Quick Target Date Selector
                    Text(
                        text = "Target Date",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "3 Days" to (86400000L * 3),
                            "1 Week" to (86400000L * 7),
                            "2 Weeks" to (86400000L * 14)
                        ).forEach { (lbl, millis) ->
                            OutlinedButton(
                                onClick = { targetDateEpoch = System.currentTimeMillis() + millis },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                border = BorderStroke(1.dp, Color(0xFF25304C))
                            ) {
                                Text(lbl, fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }

                    if (targetDateEpoch != null) {
                        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        Text(
                            text = "Target: ${sdf.format(Date(targetDateEpoch!!))}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2ED8A7), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        )
                    }

                    // Personal Notes Field
                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("detail_notes_input"),
                        label = { Text("Personal Notes & Reminders", fontSize = 11.sp) },
                        placeholder = { Text("E.g., formulas, teacher tips, derivation cues", color = Color(0xFF8E9BB5), fontSize = 11.sp) },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6C6CF6),
                            unfocusedBorderColor = Color(0xFF25304C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateChapterStatus(detail.chapter.id, selectedStatus)
                        viewModel.updateChapterNotesAndTarget(detail.chapter.id, notesInput, targetDateEpoch)
                        viewModel.openChapterDetail(null)
                    },
                    modifier = Modifier.testTag("btn_save_chapter_detail"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ED8A7), contentColor = Color(0xFF0B101E))
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.openChapterDetail(null) }) {
                    Text("Cancel", color = Color(0xFF8E9BB5))
                }
            }
        )
    }

    // Curriculum Settings Dialog
    if (showCurriculumDialog) {
        AlertDialog(
            onDismissRequest = { showCurriculumDialog = false },
            containerColor = Color(0xFF151C2E),
            title = {
                Text(
                    text = "Select Academic Curriculum",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Choose your verified or school-specific syllabus variant. Demonstrations and provisional boards are clearly labeled.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5))
                    )

                    state.availableCurricula.forEach { curr ->
                        val isSelected = state.activeCurriculum?.id == curr.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectCurriculum(curr.id)
                                    showCurriculumDialog = false
                                }
                                .testTag("curr_option_${curr.id}"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF1E263D) else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF2ED8A7) else Color(0xFF25304C))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = curr.authority,
                                        style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (curr.verificationStatus == "VERIFIED") Color(0xFF192A24) else Color(0xFF2A2419)
                                    ) {
                                        Text(
                                            text = curr.verificationStatus,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                color = if (curr.verificationStatus == "VERIFIED") Color(0xFF2ED8A7) else Color(0xFFFDB043),
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                                Text(
                                    text = "Class ${curr.classLevel} • ${curr.stream} • Session ${curr.academicSession}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp)
                                )
                                Text(
                                    text = curr.sourceReference,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF5A6882), fontSize = 10.sp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurriculumDialog = false }) {
                    Text("Close", color = Color(0xFF2ED8A7))
                }
            }
        )
    }

    // JSON Export / Import Dialog
    if (showExportImportDialog) {
        AlertDialog(
            onDismissRequest = { showExportImportDialog = false },
            containerColor = Color(0xFF151C2E),
            title = {
                Text("Export / Import Syllabus Progress", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Export your progress as a documented offline JSON backup, or import teacher-provided syllabus updates.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5))
                    )

                    OutlinedTextField(
                        value = importJsonInput.ifEmpty { exportJsonText },
                        onValueChange = { importJsonInput = it },
                        modifier = Modifier.fillMaxWidth().height(160.dp).testTag("json_export_import_field"),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6C6CF6),
                            unfocusedBorderColor = Color(0xFF25304C),
                            focusedContainerColor = Color(0xFF0B101E),
                            unfocusedContainerColor = Color(0xFF0B101E)
                        )
                    )

                    if (importError != null) {
                        Text(
                            text = importError!!,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFF6B8B), fontSize = 11.sp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val input = importJsonInput.ifEmpty { exportJsonText }
                        viewModel.importProgress(input) { success, msg ->
                            if (success) {
                                showExportImportDialog = false
                            } else {
                                importError = msg
                            }
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_import_json"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ED8A7), contentColor = Color(0xFF0B101E))
                ) {
                    Text("Apply / Import JSON", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportImportDialog = false }) {
                    Text("Cancel", color = Color(0xFF8E9BB5))
                }
            }
        )
    }
}

@Composable
fun ChapterProgressCard(
    item: ChapterWithProgress,
    onStatusChange: (String) -> Unit,
    onOpenDetail: () -> Unit,
    onStudy: () -> Unit
) {
    val prog = item.progress
    val currentStatus = prog?.status ?: "NOT_STARTED"

    val (statusLabel, statusColor, statusBg) = when (currentStatus) {
        "COMPLETED" -> Triple("Completed", Color(0xFF2ED8A7), Color(0xFF192A24))
        "IN_PROGRESS" -> Triple("In Progress", Color(0xFF6C6CF6), Color(0xFF1E263D))
        "NEEDS_REVISION" -> Triple("Needs Revision", Color(0xFFFDB043), Color(0xFF2A2419))
        else -> Triple("Not Started", Color(0xFF8E9BB5), Color(0xFF151C2E))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chapter_card_${item.chapter.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
        border = BorderStroke(1.dp, if (currentStatus == "COMPLETED") Color(0xFF2ED8A7).copy(alpha = 0.4f) else Color(0xFF25304C))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1E263D)) {
                        Text(
                            text = "Ch ${item.chapter.chapterNumber}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6C6CF6), fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.chapter.title,
                        style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }

                // Status Badge (click toggles to next status: Not Started -> In Progress -> Completed -> Needs Revision)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg,
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clickable {
                            val next = when (currentStatus) {
                                "NOT_STARTED" -> "IN_PROGRESS"
                                "IN_PROGRESS" -> "COMPLETED"
                                "COMPLETED" -> "NEEDS_REVISION"
                                else -> "NOT_STARTED"
                            }
                            onStatusChange(next)
                        }
                        .testTag("status_toggle_${item.chapter.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = when (currentStatus) {
                            "COMPLETED" -> Icons.Default.CheckCircle
                            "IN_PROGRESS" -> Icons.Default.Timelapse
                            "NEEDS_REVISION" -> Icons.Default.WarningAmber
                            else -> Icons.Default.RadioButtonUnchecked
                        }
                        Icon(imageVector = icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Text(
                text = item.chapter.description,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5), fontSize = 11.sp),
                maxLines = 2
            )

            // Notes / Target preview if present
            if (!prog?.personalNotes.isNullOrBlank() || prog?.targetDate != null) {
                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1E263D)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!prog?.personalNotes.isNullOrBlank()) {
                            Text(
                                text = "Note: ${prog!!.personalNotes}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC0CADB), fontSize = 10.sp),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (prog?.targetDate != null) {
                            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                            Text(
                                text = "Target: ${sdf.format(Date(prog.targetDate))}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2ED8A7), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Bottom Actions (Details / Notes, Study Now)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onOpenDetail,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, tint = Color(0xFF8E9BB5), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Notes & Target", color = Color(0xFF8E9BB5), fontSize = 11.sp)
                }

                TextButton(
                    onClick = onStudy,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF6C6CF6), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Study Mind Map", color = Color(0xFF6C6CF6), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
