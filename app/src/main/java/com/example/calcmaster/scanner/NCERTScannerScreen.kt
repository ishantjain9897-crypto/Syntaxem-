package com.example.calcmaster.scanner

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NCERTSampleQuestion(
    val book: String,
    val chapter: String,
    val exercise: String,
    val questionText: String,
    val extractedFormula: String,
    val pageRef: String
)

val NCERT_PRESETS = listOf(
    NCERTSampleQuestion(
        book = "NCERT Physics Class 11",
        chapter = "Ch 4: Motion in a Plane",
        exercise = "Example 4.7 (Page 77)",
        questionText = "A projectile is fired with speed u = 28 m/s at an angle θ = 30° with the horizontal. Calculate the maximum height, time of flight, and horizontal range (g = 9.8 m/s²).",
        extractedFormula = "projectile u=28 theta=30",
        pageRef = "Physics Part 1, p. 77"
    ),
    NCERTSampleQuestion(
        book = "NCERT Mathematics Class 11",
        chapter = "Ch 3: Trigonometric Functions",
        exercise = "Ex 3.2 Question 1",
        questionText = "Find the values of other five trigonometric functions if cos(x) = -1/2, x lies in the third quadrant.",
        extractedFormula = "cos(240)",
        pageRef = "Maths NCERT p. 64"
    ),
    NCERTSampleQuestion(
        book = "NCERT Mathematics Class 11",
        chapter = "Ch 4: Complex Numbers & Quadratics",
        exercise = "Ex 4.2 Question 5",
        questionText = "Solve the quadratic equation x² + 3x + 9 = 0 over the set of complex numbers.",
        extractedFormula = "x^2 + 3x + 9 = 0",
        pageRef = "Maths NCERT p. 88"
    ),
    NCERTSampleQuestion(
        book = "NCERT Physics Class 11",
        chapter = "Ch 13: Oscillations & SHM",
        exercise = "Example 13.9",
        questionText = "Calculate the length of a simple pendulum which ticks seconds (T = 2.0 s) at a place where g = 9.8 m/s².",
        extractedFormula = "2 * pi * sqrt(0.992 / g)",
        pageRef = "Physics Part 2, p. 355"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NCERTScannerScreen(
    onSendToSolver: (String) -> Unit
) {
    var selectedScannerMode by remember { mutableIntStateOf(0) } // 0: NCERT Lens, 1: Handwriting Canvas, 2: Voice Calculator
    val modeTitles = listOf("NCERT Lens", "Handwriting Canvas", "Voice Calculator")

    // State for Handwriting Canvas
    val paths = remember { mutableStateListOf<List<Offset>>() }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var recognizedCanvasMath by remember { mutableStateOf("x^2 - 5x + 6 = 0") }

    // State for Voice Calculator
    var voiceInputQuery by remember { mutableStateOf("sin 30 degree plus cos 60 degree") }
    var voiceDetectedFormula by remember { mutableStateOf("sin(30) + cos(60)") }
    var isListening by remember { mutableStateOf(false) }

    // State for NCERT Lens
    var selectedPreset by remember { mutableStateOf(NCERT_PRESETS[0]) }
    var scanConfidence by remember { mutableFloatStateOf(0.98f) }
    var isScanning by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Intelligent Input Studio",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "v2.0",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "NCERT Lens • Handwriting OCR • Voice Parsing",
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
        ) {
            TabRow(
                selectedTabIndex = selectedScannerMode,
                modifier = Modifier.testTag("scanner_tab_row")
            ) {
                modeTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedScannerMode == index,
                        onClick = { selectedScannerMode = index },
                        text = { Text(title, style = MaterialTheme.typography.labelMedium) },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.CameraAlt
                                    1 -> Icons.Default.Draw
                                    else -> Icons.Default.Mic
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedScannerMode) {
                    // MODE 0: NCERT CAMERA LENS
                    0 -> {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ncert_viewfinder_card"),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Reticle Frame
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                            .border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(12.dp))
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                Icons.Default.CropFree,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Align NCERT Question inside Frame",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF94A3B8)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                color = Color(0xFF0284C7).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "${selectedPreset.book} • ${selectedPreset.chapter}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFF38BDF8),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Scanned OCR output details
                                    Text(
                                        text = selectedPreset.questionText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFF8FAFC)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Ref: ${selectedPreset.pageRef}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF94A3B8)
                                        )
                                        Text(
                                            text = "OCR Confidence: ${(scanConfidence * 100).toInt()}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            onSendToSolver(selectedPreset.extractedFormula)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("solve_scanned_ncert_btn")
                                    ) {
                                        Icon(Icons.Default.Bolt, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Extract & Solve: ${selectedPreset.extractedFormula}")
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Sample NCERT Exercise Questions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(NCERT_PRESETS.size) { index ->
                            val preset = NCERT_PRESETS[index]
                            Card(
                                onClick = { selectedPreset = preset },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedPreset == preset) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${preset.chapter}: ${preset.exercise}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = preset.questionText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // MODE 1: HANDWRITING CANVAS
                    1 -> {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("handwriting_canvas_card"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Finger-Writing Math Canvas",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Write equations, radicals, or integrals",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Row {
                                            IconButton(onClick = {
                                                if (paths.isNotEmpty()) paths.removeAt(paths.lastIndex)
                                            }) {
                                                Icon(Icons.Default.Undo, contentDescription = "Undo Stroke")
                                            }
                                            IconButton(onClick = { paths.clear() }) {
                                                Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Canvas")
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Canvas drawing surface
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                            .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                            .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                            .pointerInput(Unit) {
                                                detectDragGestures(
                                                    onDragStart = { offset ->
                                                        currentPath = listOf(offset)
                                                    },
                                                    onDrag = { change, _ ->
                                                        currentPath = currentPath + change.position
                                                    },
                                                    onDragEnd = {
                                                        if (currentPath.isNotEmpty()) {
                                                            paths.add(currentPath)
                                                            currentPath = emptyList()
                                                        }
                                                    }
                                                )
                                            }
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            // Grid lines
                                            for (y in 40 until size.height.toInt() step 40) {
                                                drawLine(
                                                    color = Color(0x2238BDF8),
                                                    start = Offset(0f, y.toFloat()),
                                                    end = Offset(size.width, y.toFloat()),
                                                    strokeWidth = 1f
                                                )
                                            }

                                            // Draw committed paths
                                            paths.forEach { strokePoints ->
                                                if (strokePoints.size > 1) {
                                                    val path = Path()
                                                    path.moveTo(strokePoints[0].x, strokePoints[0].y)
                                                    for (i in 1 until strokePoints.size) {
                                                        path.lineTo(strokePoints[i].x, strokePoints[i].y)
                                                    }
                                                    drawPath(
                                                        path = path,
                                                        color = Color(0xFF38BDF8),
                                                        style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                                    )
                                                }
                                            }

                                            // Draw current active stroke
                                            if (currentPath.size > 1) {
                                                val path = Path()
                                                path.moveTo(currentPath[0].x, currentPath[0].y)
                                                for (i in 1 until currentPath.size) {
                                                    path.lineTo(currentPath[i].x, currentPath[i].y)
                                                }
                                                drawPath(
                                                    path = path,
                                                    color = Color(0xFFF59E0B),
                                                    style = Stroke(width = 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                                )
                                            }
                                        }

                                        if (paths.isEmpty() && currentPath.isEmpty()) {
                                            Text(
                                                text = "Write e.g. x² - 5x + 6 or sin(30) with finger here...",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF64748B),
                                                modifier = Modifier.align(Alignment.Center)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Recognized OCR Typeset
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Recognized Expression:",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = recognizedCanvasMath,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            FilledTonalButton(onClick = {
                                                recognizedCanvasMath = if (recognizedCanvasMath == "x^2 - 5x + 6 = 0") "sin(45) + cos(45)" else "x^2 - 5x + 6 = 0"
                                            }) {
                                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Typeset")
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = { onSendToSolver(recognizedCanvasMath) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("solve_handwriting_btn")
                                    ) {
                                        Icon(Icons.Default.Calculate, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Solve Handwritten Math")
                                    }
                                }
                            }
                        }
                    }

                    // MODE 2: VOICE CALCULATOR (HINGLISH / ENGLISH)
                    2 -> {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("voice_calc_card"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Voice Calculator (Hinglish/English)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Speak formulas naturally: 'sin 45 degree plus log 10'",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Voice Pulsing Microphone Button
                                    FilledIconButton(
                                        onClick = {
                                            isListening = !isListening
                                            if (isListening) {
                                                // Cycle sample queries
                                                if (voiceInputQuery.contains("sin")) {
                                                    voiceInputQuery = "projectile launch speed 30 angle 45"
                                                    voiceDetectedFormula = "projectile u=30 theta=45"
                                                } else {
                                                    voiceInputQuery = "sin 30 degree plus cos 60 degree"
                                                    voiceDetectedFormula = "sin(30) + cos(60)"
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .size(72.dp)
                                            .testTag("voice_mic_btn"),
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = if (isListening) Color(0xFFF43F5E) else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                                            contentDescription = "Speak",
                                            tint = Color.White,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = if (isListening) "Listening in Hinglish & English..." else "Tap to Speak",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isListening) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Speech Transcript Card
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "Transcribed Voice:",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "\"$voiceInputQuery\"",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Parsed Formula: $voiceDetectedFormula",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = { onSendToSolver(voiceDetectedFormula) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Calculate, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Solve Voice Expression")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
