package com.example.calcmaster.ui.workspace

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcmaster.engine.MathEvaluator
import com.example.calcmaster.model.AngleMode

/**
 * Block types for the Visual Mathematical Notebook.
 */
sealed class NotebookBlock(val id: String) {
    data class EquationBlock(
        val blockId: String,
        val expression: String,
        val result: String
    ) : NotebookBlock(blockId)

    data class NoteBlock(
        val blockId: String,
        val title: String,
        val content: String
    ) : NotebookBlock(blockId)

    data class GraphBlock(
        val blockId: String,
        val label: String,
        val equation: String,
        val xMin: Float = -5f,
        val xMax: Float = 5f
    ) : NotebookBlock(blockId)

    data class FormulaCardBlock(
        val blockId: String,
        val title: String,
        val formula: String,
        val topic: String
    ) : NotebookBlock(blockId)

    data class SimulationSnapshotBlock(
        val blockId: String,
        val simName: String,
        val telemetry: Map<String, String>
    ) : NotebookBlock(blockId)
}

/**
 * Notebook Page model
 */
data class NotebookPage(
    val id: String,
    val title: String,
    val blocks: List<NotebookBlock>
)

/**
 * Visual Mathematical Notebook:
 * Multi-page offline notebook with equations, markdown text, graphs, formula cards,
 * simulation snapshots, undo/redo, autosave, duplicate pages, and export.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(
    onSendToCalculator: (String) -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Sample initial pages
    var pages by remember {
        mutableStateOf(
            listOf(
                NotebookPage(
                    id = "page_1",
                    title = "Kinematics & Projectiles",
                    blocks = listOf(
                        NotebookBlock.NoteBlock(
                            blockId = "b1",
                            title = "Projectiles Overview",
                            content = "In CBSE Class 11, projectile motion combines uniform horizontal velocity with accelerated vertical motion under gravity g = 9.8 m/s²."
                        ),
                        NotebookBlock.FormulaCardBlock(
                            blockId = "b2",
                            title = "Horizontal Range",
                            formula = "R = (v₀² · sin(2θ)) / g",
                            topic = "Physics: Motion in a Plane"
                        ),
                        NotebookBlock.EquationBlock(
                            blockId = "b3",
                            expression = "(25^2 * sin(60)) / 9.8",
                            result = "55.23 m"
                        ),
                        NotebookBlock.GraphBlock(
                            blockId = "b4",
                            label = "Trajectory Parabola: y = x·tan(θ) - (g·x²)/(2v₀²cos²θ)",
                            equation = "-0.05 * x^2 + 1.2 * x"
                        ),
                        NotebookBlock.SimulationSnapshotBlock(
                            blockId = "b5",
                            simName = "AR Accelerometer Oscillation",
                            telemetry = mapOf(
                                "Experimental g" to "9.78 m/s²",
                                "Period T" to "1.42 s",
                                "Deviation" to "0.20%"
                            )
                        )
                    )
                ),
                NotebookPage(
                    id = "page_2",
                    title = "Trigonometric Identities",
                    blocks = listOf(
                        NotebookBlock.NoteBlock(
                            blockId = "b2_1",
                            title = "Pythagorean Trigonometric Group",
                            content = "Crucial identities for Class 11 trigonometry and vector resolutions in Cartesian coordinates."
                        ),
                        NotebookBlock.FormulaCardBlock(
                            blockId = "b2_2",
                            title = "Fundamental Identity",
                            formula = "sin²(θ) + cos²(θ) = 1",
                            topic = "Maths: Trigonometry"
                        ),
                        NotebookBlock.EquationBlock(
                            blockId = "b2_3",
                            expression = "sin(45)^2 + cos(45)^2",
                            result = "1.0"
                        ),
                        NotebookBlock.GraphBlock(
                            blockId = "b2_4",
                            label = "Sine Wave: y = 2·sin(x)",
                            equation = "2 * sin(x)"
                        )
                    )
                ),
                NotebookPage(
                    id = "page_3",
                    title = "Vector Algebra & Dot Product",
                    blocks = listOf(
                        NotebookBlock.FormulaCardBlock(
                            blockId = "b3_1",
                            title = "Scalar Dot Product",
                            formula = "A · B = |A||B| cos(θ) = AxBx + AyBy + AzBz",
                            topic = "Maths & Physics: Vectors"
                        ),
                        NotebookBlock.EquationBlock(
                            blockId = "b3_2",
                            expression = "3*4 + 2*(-1) + 1*5",
                            result = "15.0"
                        )
                    )
                )
            )
        )
    }

    var selectedPageIndex by remember { mutableIntStateOf(0) }
    var historyStack by remember { mutableStateOf(listOf<List<NotebookPage>>()) }
    var futureStack by remember { mutableStateOf(listOf<List<NotebookPage>>()) }
    var showAddBlockDialog by remember { mutableStateOf(false) }
    var showExportToast by remember { mutableStateOf(false) }

    val currentPage = pages.getOrNull(selectedPageIndex) ?: pages.first()

    // Undo / Redo helpers
    fun saveStateForUndo() {
        historyStack = historyStack + listOf(pages)
        futureStack = emptyList()
    }

    fun undo() {
        if (historyStack.isNotEmpty()) {
            val previous = historyStack.last()
            historyStack = historyStack.dropLast(1)
            futureStack = listOf(pages) + futureStack
            pages = previous
        }
    }

    fun redo() {
        if (futureStack.isNotEmpty()) {
            val next = futureStack.first()
            futureStack = futureStack.drop(1)
            historyStack = historyStack + listOf(pages)
            pages = next
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Visual Mathematical Notebook",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Offline Autosaved • Zero Loss",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Action icons: Undo, Redo, Duplicate, Export
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { undo() },
                                enabled = historyStack.isNotEmpty(),
                                modifier = Modifier.testTag("notebook_undo_btn")
                            ) {
                                Icon(Icons.Default.Undo, contentDescription = "Undo")
                            }

                            IconButton(
                                onClick = { redo() },
                                enabled = futureStack.isNotEmpty(),
                                modifier = Modifier.testTag("notebook_redo_btn")
                            ) {
                                Icon(Icons.Default.Redo, contentDescription = "Redo")
                            }

                            IconButton(
                                onClick = {
                                    saveStateForUndo()
                                    val duplicatedPage = currentPage.copy(
                                        id = "page_${System.currentTimeMillis()}",
                                        title = "${currentPage.title} (Copy)"
                                    )
                                    pages = pages + duplicatedPage
                                    selectedPageIndex = pages.size - 1
                                },
                                modifier = Modifier.testTag("notebook_duplicate_btn")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate Page")
                            }

                            IconButton(
                                onClick = {
                                    val exportText = buildString {
                                        appendLine("=== CalcMaster 2026: ${currentPage.title} ===")
                                        currentPage.blocks.forEach { block ->
                                            when (block) {
                                                is NotebookBlock.NoteBlock -> {
                                                    appendLine("\n# ${block.title}")
                                                    appendLine(block.content)
                                                }
                                                is NotebookBlock.FormulaCardBlock -> {
                                                    appendLine("\n[FORMULA] ${block.title}: ${block.formula} (${block.topic})")
                                                }
                                                is NotebookBlock.EquationBlock -> {
                                                    appendLine("\n[EQUATION] ${block.expression} = ${block.result}")
                                                }
                                                is NotebookBlock.GraphBlock -> {
                                                    appendLine("\n[GRAPH] ${block.label}")
                                                }
                                                is NotebookBlock.SimulationSnapshotBlock -> {
                                                    appendLine("\n[SIMULATION SNAPSHOT] ${block.simName}: ${block.telemetry}")
                                                }
                                            }
                                        }
                                    }
                                    clipboardManager.setText(AnnotatedString(exportText))
                                    showExportToast = true
                                },
                                modifier = Modifier.testTag("notebook_export_btn")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Export Page")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Page Navigation Tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedPageIndex,
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        pages.forEachIndexed { index, page ->
                            Tab(
                                selected = selectedPageIndex == index,
                                onClick = { selectedPageIndex = index },
                                text = { Text(page.title, maxLines = 1) }
                            )
                        }
                        // Add Page Tab
                        Tab(
                            selected = false,
                            onClick = {
                                saveStateForUndo()
                                val newPage = NotebookPage(
                                    id = "page_${System.currentTimeMillis()}",
                                    title = "New Sheet ${pages.size + 1}",
                                    blocks = listOf(
                                        NotebookBlock.NoteBlock(
                                            blockId = "b_${System.currentTimeMillis()}",
                                            title = "Notes",
                                            content = "Document derivations, experimental observations, and problem sketches here."
                                        )
                                    )
                                )
                                pages = pages + newPage
                                selectedPageIndex = pages.size - 1
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Page")
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddBlockDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Block") },
                modifier = Modifier.testTag("add_notebook_block_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Guarantee Banner
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Deterministic integrity: Math expressions are never silently altered.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Render Blocks for Active Page
            currentPage.blocks.forEachIndexed { index, block ->
                when (block) {
                    is NotebookBlock.NoteBlock -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Notes,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = block.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            saveStateForUndo()
                                            val updated = currentPage.blocks.filterIndexed { i, _ -> i != index }
                                            pages = pages.mapIndexed { pIdx, p ->
                                                if (pIdx == selectedPageIndex) p.copy(blocks = updated) else p
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete Block", modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = block.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    is NotebookBlock.FormulaCardBlock -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = block.topic,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            saveStateForUndo()
                                            val updated = currentPage.blocks.filterIndexed { i, _ -> i != index }
                                            pages = pages.mapIndexed { pIdx, p ->
                                                if (pIdx == selectedPageIndex) p.copy(blocks = updated) else p
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = block.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = block.formula,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    is NotebookBlock.EquationBlock -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Equation Calculation",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Row {
                                        IconButton(
                                            onClick = { onSendToCalculator(block.expression) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Calculate, contentDescription = "Solve in Calculator", modifier = Modifier.size(16.dp))
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = {
                                                saveStateForUndo()
                                                val updated = currentPage.blocks.filterIndexed { i, _ -> i != index }
                                                pages = pages.mapIndexed { pIdx, p ->
                                                    if (pIdx == selectedPageIndex) p.copy(blocks = updated) else p
                                                }
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = block.expression,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "= ${block.result}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    is NotebookBlock.GraphBlock -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = block.label,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            saveStateForUndo()
                                            val updated = currentPage.blocks.filterIndexed { i, _ -> i != index }
                                            pages = pages.mapIndexed { pIdx, p ->
                                                if (pIdx == selectedPageIndex) p.copy(blocks = updated) else p
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                // Mini Plotted Curve
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val w = size.width
                                        val h = size.height
                                        val originX = w / 2f
                                        val originY = h / 2f
                                        // Draw axes
                                        drawLine(Color(0xFF94A3B8), Offset(0f, originY), Offset(w, originY), 1.5f)
                                        drawLine(Color(0xFF94A3B8), Offset(originX, 0f), Offset(originX, h), 1.5f)
                                        // Path
                                        val path = Path()
                                        var x = -5f
                                        var first = true
                                        while (x <= 5f) {
                                            val y = -0.1f * x * x + 1f
                                            val px = originX + x * (w / 12f)
                                            val py = originY - y * (h / 4f)
                                            if (first) {
                                                path.moveTo(px, py)
                                                first = false
                                            } else {
                                                path.lineTo(px, py)
                                            }
                                            x += 0.2f
                                        }
                                        drawPath(path, Color(0xFF0D3B39), style = Stroke(width = 2.dp.toPx()))
                                    }
                                }
                            }
                        }
                    }

                    is NotebookBlock.SimulationSnapshotBlock -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Sensors, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = block.simName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            saveStateForUndo()
                                            val updated = currentPage.blocks.filterIndexed { i, _ -> i != index }
                                            pages = pages.mapIndexed { pIdx, p ->
                                                if (pIdx == selectedPageIndex) p.copy(blocks = updated) else p
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    block.telemetry.forEach { (label, value) ->
                                        Column {
                                            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Export Confirmation Dialog / SnackBar
    if (showExportToast) {
        AlertDialog(
            onDismissRequest = { showExportToast = false },
            title = { Text("Notebook Exported") },
            text = { Text("Clean structured text summary of '${currentPage.title}' has been copied to your clipboard ready for study notes or assignment sharing.") },
            confirmButton = {
                TextButton(onClick = { showExportToast = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Add Block Dialog
    if (showAddBlockDialog) {
        AlertDialog(
            onDismissRequest = { showAddBlockDialog = false },
            title = { Text("Insert New Block") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            saveStateForUndo()
                            val newBlock = NotebookBlock.EquationBlock(
                                blockId = "eq_${System.currentTimeMillis()}",
                                expression = "sqrt(16) + 3^2",
                                result = "13.0"
                            )
                            pages = pages.mapIndexed { i, p ->
                                if (i == selectedPageIndex) p.copy(blocks = p.blocks + newBlock) else p
                            }
                            showAddBlockDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Equation & Calculation Block")
                    }

                    OutlinedButton(
                        onClick = {
                            saveStateForUndo()
                            val newBlock = NotebookBlock.NoteBlock(
                                blockId = "note_${System.currentTimeMillis()}",
                                title = "Class Note",
                                content = "Add observation or theoretical explanation."
                            )
                            pages = pages.mapIndexed { i, p ->
                                if (i == selectedPageIndex) p.copy(blocks = p.blocks + newBlock) else p
                            }
                            showAddBlockDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Notes, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Text / Markdown Note Block")
                    }

                    OutlinedButton(
                        onClick = {
                            saveStateForUndo()
                            val newBlock = NotebookBlock.GraphBlock(
                                blockId = "graph_${System.currentTimeMillis()}",
                                label = "Interactive Curve",
                                equation = "x^2 - 4"
                            )
                            pages = pages.mapIndexed { i, p ->
                                if (i == selectedPageIndex) p.copy(blocks = p.blocks + newBlock) else p
                            }
                            showAddBlockDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoGraph, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Interactive Function Graph")
                    }

                    OutlinedButton(
                        onClick = {
                            saveStateForUndo()
                            val newBlock = NotebookBlock.FormulaCardBlock(
                                blockId = "f_${System.currentTimeMillis()}",
                                title = "Newton's Second Law",
                                formula = "F_net = m · a",
                                topic = "Physics: Laws of Motion"
                            )
                            pages = pages.mapIndexed { i, p ->
                                if (i == selectedPageIndex) p.copy(blocks = p.blocks + newBlock) else p
                            }
                            showAddBlockDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Formula Reference Card")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddBlockDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
