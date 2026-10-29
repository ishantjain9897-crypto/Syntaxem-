package com.example.syntaxem.ui.chapter

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syntaxem.model.ChapterData
import com.example.syntaxem.model.MindMapNode
import com.example.syntaxem.model.NodeColorType
import com.example.syntaxem.model.SyntaxemCurriculumData

enum class ChapterTab {
    NOTES,
    MIND_MAP,
    FLOWCHART
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterMindMapScreen(
    initialChapterId: String = "ch_03_trigonometry",
    onPlayGame: (String) -> Unit = {},
    onSendToCalculator: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var selectedChapterId by remember { mutableStateOf(initialChapterId) }
    var activeTab by remember { mutableStateOf(ChapterTab.MIND_MAP) }
    var selectedNode by remember { mutableStateOf<MindMapNode?>(null) }
    var showFormulaSheet by remember { mutableStateOf(false) }
    var showChapterPicker by remember { mutableStateOf(false) }

    val chapter = remember(selectedChapterId) {
        SyntaxemCurriculumData.getChapter(selectedChapterId) ?: SyntaxemCurriculumData.chapters.first()
    }

    Scaffold(
        containerColor = Color(0xFF0B101E),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mathematics · Class ${chapter.classLevel}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF8E9BB5),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )

                    IconButton(
                        onClick = { showChapterPicker = true },
                        modifier = Modifier.size(28.dp).testTag("btn_switch_chapter")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Chapter",
                            tint = Color(0xFF6C6CF6)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { onSendToCalculator("sin(45)") },
                        modifier = Modifier.size(36.dp).testTag("btn_chapter_calc")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Quick Calculator",
                            tint = Color(0xFF2ED8A7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Segmented 3-Pill Control: [Notes] [Mind map] [Flowchart] (Matching Image 1)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ChapterTab.values().forEach { tab ->
                        val isSelected = activeTab == tab
                        val tabTitle = when (tab) {
                            ChapterTab.NOTES -> "Notes"
                            ChapterTab.MIND_MAP -> "Mind map"
                            ChapterTab.FLOWCHART -> "Flowchart"
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activeTab = tab }
                                .testTag("tab_${tab.name.lowercase()}"),
                            shape = RoundedCornerShape(24.dp),
                            color = if (isSelected) Color(0xFF6C6CF6) else Color(0xFF151C2E),
                            border = if (!isSelected) BorderStroke(1.dp, Color(0xFF25304C)) else null
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tabTitle,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) Color.White else Color(0xFF8E9BB5),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    ChapterTab.MIND_MAP -> {
                        MindMapView(
                            chapter = chapter,
                            onNodeClick = { selectedNode = it }
                        )
                    }
                    ChapterTab.FLOWCHART -> {
                        FlowchartView(
                            steps = chapter.flowchartSteps,
                            onSendToCalculator = onSendToCalculator
                        )
                    }
                    ChapterTab.NOTES -> {
                        NotesView(
                            conceptCards = chapter.conceptCards,
                            formulaSheet = chapter.formulaSheet,
                            onSendToCalculator = onSendToCalculator
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons matching Image 1: [Play a game] [Formula sheet]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val firstGame = chapter.games.firstOrNull()?.id ?: "unit_circle_race"
                        onPlayGame(firstGame)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_play_a_game"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2ED8A7),
                        contentColor = Color(0xFF0B101E)
                    )
                ) {
                    Text(
                        text = "Play a game",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }

                Button(
                    onClick = { showFormulaSheet = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_formula_sheet"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF151C2E),
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color(0xFF25304C))
                ) {
                    Text(
                        text = "Formula sheet",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }

    // Node Detail Bottom Sheet / Dialog
    if (selectedNode != null) {
        val node = selectedNode!!
        AlertDialog(
            onDismissRequest = { selectedNode = null },
            containerColor = Color(0xFF151C2E),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = node.colorType.color,
                        modifier = Modifier.size(14.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = node.label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = node.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFC0CADB))
                    )

                    if (node.keyFormulas.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E263D),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Governing Formulas:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF2ED8A7),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                node.keyFormulas.forEach { f ->
                                    Text(
                                        text = "• $f",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val f = node.keyFormulas.firstOrNull() ?: "sin(30)"
                        val cleanExpr = f.substringBefore("=").trim().ifBlank { "sin(30)" }
                        onSendToCalculator(cleanExpr)
                        selectedNode = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C6CF6)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Calculate in Workspace", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedNode = null }) {
                    Text("Close", color = Color(0xFF8E9BB5))
                }
            }
        )
    }

    // Formula Sheet Full Dialog
    if (showFormulaSheet) {
        AlertDialog(
            onDismissRequest = { showFormulaSheet = false },
            containerColor = Color(0xFF151C2E),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF2ED8A7))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${chapter.title} • Formula Sheet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chapter.formulaSheet) { (name, formula) ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E263D),
                            border = BorderStroke(1.dp, Color(0xFF25304C))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = name, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9BB5)))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formula,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2ED8A7)
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFormulaSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ED8A7), contentColor = Color(0xFF0B101E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close")
                }
            }
        )
    }

    // Chapter Switcher Dialog
    if (showChapterPicker) {
        AlertDialog(
            onDismissRequest = { showChapterPicker = false },
            containerColor = Color(0xFF151C2E),
            title = {
                Text("Select NCERT Chapter (Class 11 & 12)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(SyntaxemCurriculumData.chapters) { ch ->
                        val isCurrent = ch.id == selectedChapterId
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedChapterId = ch.id
                                    showChapterPicker = false
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrent) Color(0xFF252952) else Color(0xFF1E263D),
                            border = BorderStroke(1.dp, if (isCurrent) Color(0xFF6C6CF6) else Color(0xFF25304C))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${ch.number}. ${ch.title} (Class ${ch.classLevel})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isCurrent) Color(0xFF6C6CF6) else Color.White,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                if (isCurrent) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF6C6CF6), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChapterPicker = false }) {
                    Text("Cancel", color = Color(0xFF8E9BB5))
                }
            }
        )
    }
}

/**
 * Visual Mind Map matching Image 1 pixel-for-pixel:
 * Dark rounded container card (#151C2E) with central circular node and 6 radiating branches.
 */
@Composable
fun MindMapView(
    chapter: ChapterData,
    onNodeClick: (MindMapNode) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mind_map_canvas_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
        border = BorderStroke(1.dp, Color(0xFF25304C))
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            val width = maxWidth.value
            val height = maxHeight.value
            val centerX = width / 2f
            val centerY = height / 2f

            val nodes = chapter.mindMapNodes
            val node0 = nodes.getOrNull(0) // Top-Left (Angles)
            val node1 = nodes.getOrNull(1) // Top-Right (Ratios)
            val node2 = nodes.getOrNull(2) // Left (Unit circle)
            val node3 = nodes.getOrNull(3) // Right (Identities)
            val node4 = nodes.getOrNull(4) // Bottom-Left (Equations)
            val node5 = nodes.getOrNull(5) // Bottom-Right (Graphs)

            // Connector Lines Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val lineColor = Color(0xFF3B4868)
                val strokeWidth = 3.5f

                // Top-Left line
                drawLine(
                    color = lineColor,
                    start = centerOffset,
                    end = Offset(size.width * 0.28f, size.height * 0.18f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                // Top-Right line
                drawLine(
                    color = lineColor,
                    start = centerOffset,
                    end = Offset(size.width * 0.72f, size.height * 0.18f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                // Left line
                drawLine(
                    color = lineColor,
                    start = centerOffset,
                    end = Offset(size.width * 0.22f, size.height * 0.50f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                // Right line
                drawLine(
                    color = lineColor,
                    start = centerOffset,
                    end = Offset(size.width * 0.78f, size.height * 0.50f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                // Bottom-Left line
                drawLine(
                    color = lineColor,
                    start = centerOffset,
                    end = Offset(size.width * 0.28f, size.height * 0.82f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                // Bottom-Right line
                drawLine(
                    color = lineColor,
                    start = centerOffset,
                    end = Offset(size.width * 0.72f, size.height * 0.82f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            // Central Circular Node (Trigono-metry)
            Surface(
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.Center)
                    .clickable { /* noop or root info */ }
                    .testTag("node_center"),
                shape = CircleShape,
                color = Color(0xFF6C6CF6),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = chapter.centralNodeLabel,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 18.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Top-Left Node (Angles)
            if (node0 != null) {
                MindMapNodePill(
                    node = node0,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = (width * 0.08f).dp, top = (height * 0.12f).dp),
                    onClick = { onNodeClick(node0) }
                )
            }

            // Top-Right Node (Ratios)
            if (node1 != null) {
                MindMapNodePill(
                    node = node1,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = (width * 0.08f).dp, top = (height * 0.12f).dp),
                    onClick = { onNodeClick(node1) }
                )
            }

            // Left Node (Unit circle)
            if (node2 != null) {
                MindMapNodePill(
                    node = node2,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = (width * 0.02f).dp),
                    onClick = { onNodeClick(node2) }
                )
            }

            // Right Node (Identities)
            if (node3 != null) {
                MindMapNodePill(
                    node = node3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = (width * 0.02f).dp),
                    onClick = { onNodeClick(node3) }
                )
            }

            // Bottom-Left Node (Equations)
            if (node4 != null) {
                MindMapNodePill(
                    node = node4,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = (width * 0.08f).dp, bottom = (height * 0.12f).dp),
                    onClick = { onNodeClick(node4) }
                )
            }

            // Bottom-Right Node (Graphs)
            if (node5 != null) {
                MindMapNodePill(
                    node = node5,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = (width * 0.08f).dp, bottom = (height * 0.12f).dp),
                    onClick = { onNodeClick(node5) }
                )
            }
        }
    }
}

@Composable
fun MindMapNodePill(
    node: MindMapNode,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("node_${node.id}"),
        shape = RoundedCornerShape(16.dp),
        color = node.colorType.color,
        shadowElevation = 4.dp
    ) {
        Text(
            text = node.label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                color = when (node.colorType) {
                    NodeColorType.MINT -> Color(0xFF0B101E)
                    NodeColorType.AMBER -> Color(0xFF0B101E)
                    NodeColorType.CYAN -> Color(0xFF0B101E)
                    else -> Color.White
                },
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        )
    }
}

/**
 * Flowchart problem-solving view for the chapter.
 */
@Composable
fun FlowchartView(
    steps: List<com.example.syntaxem.model.FlowchartStep>,
    onSendToCalculator: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flowchart_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                border = BorderStroke(1.dp, Color(0xFF25304C))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PROBLEM-SOLVING FLOWCHART",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2ED8A7))
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Algorithmic decision tree to systematically decompose and solve typical board problems.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8E9BB5)
                    )
                }
            }
        }

        items(steps) { step ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                border = BorderStroke(1.dp, Color(0xFF25304C))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF6C6CF6),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${step.stepNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = step.action,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC0CADB))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tip: ${step.tip}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFDB043), fontSize = 11.sp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Notes & Concept Cards view for the chapter.
 */
@Composable
fun NotesView(
    conceptCards: List<com.example.syntaxem.model.ConceptCard>,
    formulaSheet: List<Pair<String, String>>,
    onSendToCalculator: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notes_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(conceptCards) { card ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2E)),
                border = BorderStroke(1.dp, Color(0xFF25304C))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = card.content,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E9BB5))
                    )
                    if (card.formula != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E263D),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = card.formula,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2ED8A7)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
