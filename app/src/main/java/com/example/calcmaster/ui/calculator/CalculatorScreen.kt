package com.example.calcmaster.ui.calculator

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcmaster.engine.MathEvaluator
import com.example.calcmaster.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    initialExpression: String = "",
    onNavigateToSimulation: (String) -> Unit,
    onNavigateToGame: (String) -> Unit,
    onNavigateToFeynman: (String) -> Unit = {},
    onOpenInWorkspace: (String) -> Unit = {}
) {
    var expression by remember { mutableStateOf(if (initialExpression.isNotEmpty()) initialExpression else "2 * sin(30) + 5") }
    var angleMode by remember { mutableStateOf(AngleMode.DEG) }
    var memoryValue by remember { mutableDoubleStateOf(0.0) }
    var calculationResult by remember { mutableStateOf<CalculationResult?>(null) }
    var history by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }
    var showStepsSheet by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showWhyNotDialog by remember { mutableStateOf(false) }
    var explanationMode by remember { mutableStateOf(ExplanationMode.FULL_SOLUTION) }
    var isHinglishMode by remember { mutableStateOf(false) }
    var isScientificExpanded by remember { mutableStateOf(true) }

    val evaluator = remember { MathEvaluator() }

    val appendToken = { token: String ->
        expression = if (expression == "0" && token != ".") token else expression + token
    }

    LaunchedEffect(expression, angleMode) {
        if (expression.isNotEmpty() && !expression.endsWith("=") && !expression.endsWith("+ ") && !expression.endsWith("- ") && !expression.endsWith("* ") && !expression.endsWith("/ ")) {
            calculationResult = evaluator.evaluate(expression, angleMode)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CalcMaster 2026",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "INSTRUMENT V2.0",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Language Switcher
                    IconButton(
                        onClick = { isHinglishMode = !isHinglishMode },
                        modifier = Modifier.testTag("hinglish_toggle_btn")
                    ) {
                        Surface(
                            color = if (isHinglishMode) Color(0xFFF59E0B).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isHinglishMode) Color(0xFFF59E0B) else Color.Transparent)
                        ) {
                            Text(
                                text = if (isHinglishMode) "हिं" else "EN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isHinglishMode) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Angle Mode Switcher (DEG / RAD / GRAD)
                    Surface(
                        onClick = {
                            angleMode = when (angleMode) {
                                AngleMode.DEG -> AngleMode.RAD
                                AngleMode.RAD -> AngleMode.GRAD
                                AngleMode.GRAD -> AngleMode.DEG
                            }
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f)),
                        modifier = Modifier.testTag("angle_mode_toggle_btn")
                    ) {
                        Text(
                            text = angleMode.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Why Not? Explainer
                    IconButton(
                        onClick = { showWhyNotDialog = true },
                        modifier = Modifier.testTag("why_not_btn")
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Why Not Explainer", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Open in Equation Workspace
                    IconButton(
                        onClick = { onOpenInWorkspace(expression) },
                        modifier = Modifier.testTag("open_in_workspace_btn")
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "Open in Equation Workspace",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // History Tape
                    IconButton(
                        onClick = { showHistorySheet = true },
                        modifier = Modifier.testTag("calc_history_btn")
                    ) {
                        Icon(Icons.Default.History, contentDescription = "History Tape")
                    }
                }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0B0F19), Color(0xFF111827))
                    )
                )
        ) {
            val isTabletOrWide = maxWidth > 600.dp

            if (isTabletOrWide) {
                // Wide / Tablet Layout: Side-by-side Display and Extended Keypad
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        InstrumentDisplayArea(
                            expression = expression,
                            angleMode = angleMode,
                            memoryValue = memoryValue,
                            calculationResult = calculationResult,
                            onShowSteps = { showStepsSheet = true }
                        )
                    }
                    Box(modifier = Modifier.weight(1.2f)) {
                        InstrumentKeypadGrid(
                            isScientificExpanded = isScientificExpanded,
                            onToggleScientific = { isScientificExpanded = !isScientificExpanded },
                            onAppendToken = appendToken,
                            onClear = {
                                expression = ""
                                calculationResult = null
                            },
                            onDelete = {
                                if (expression.isNotEmpty()) expression = expression.dropLast(1)
                            },
                            onToggleSign = {
                                expression = if (expression.startsWith("-")) expression.drop(1) else "-$expression"
                            },
                            onMemoryClear = { memoryValue = 0.0 },
                            onMemoryRecall = { appendToken(memoryValue.toString()) },
                            onMemoryAdd = {
                                val current = calculationResult?.decimalResult?.toDoubleOrNull() ?: 0.0
                                memoryValue += current
                            },
                            onMemorySub = {
                                val current = calculationResult?.decimalResult?.toDoubleOrNull() ?: 0.0
                                memoryValue -= current
                            },
                            onEvaluate = {
                                if (expression.isNotEmpty()) {
                                    val res = evaluator.evaluate(expression, angleMode)
                                    calculationResult = res
                                    if (res.isSuccess) {
                                        history = listOf(HistoryItem(expression = expression, result = res.decimalResult, conceptTag = res.conceptTag)) + history
                                    }
                                }
                            }
                        )
                    }
                }
            } else {
                // Standard Phone Layout: Precision Vertical Tier
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Display Area
                    Box(modifier = Modifier.weight(1.05f)) {
                        InstrumentDisplayArea(
                            expression = expression,
                            angleMode = angleMode,
                            memoryValue = memoryValue,
                            calculationResult = calculationResult,
                            onShowSteps = { showStepsSheet = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Textbook Shortcut Formulas
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        InstrumentQuickChip(label = "Quad: x²-5x+6=0") {
                            expression = "x^2 - 5x + 6 = 0"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        InstrumentQuickChip(label = "Proj: u=30 θ=45°") {
                            expression = "projectile u=30 theta=45"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        InstrumentQuickChip(label = "Vec Dot: [1,2,3]·[4,5,6]") {
                            expression = "dot([1, 2, 3], [4, 5, 6])"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        InstrumentQuickChip(label = "Vec Cross: [1,0,0]×[0,1,0]") {
                            expression = "cross([1, 0, 0], [0, 1, 0])"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        InstrumentQuickChip(label = "Matrix Det: [[2,3],[1,4]]") {
                            expression = "det([[2, 3], [1, 4]])"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        InstrumentQuickChip(label = "Matrix Inv: [[1,2],[3,4]]") {
                            expression = "inv([[1, 2], [3, 4]])"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        InstrumentQuickChip(label = "Trig: sin²+cos²") {
                            expression = "sin(45)^2 + cos(45)^2"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        InstrumentQuickChip(label = "Pendulum: T=2π√(L/g)") {
                            expression = "2 * pi * sqrt(1.0 / g)"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Responsive Keypad Grid
                    Box(modifier = Modifier.weight(2.05f)) {
                        InstrumentKeypadGrid(
                            isScientificExpanded = isScientificExpanded,
                            onToggleScientific = { isScientificExpanded = !isScientificExpanded },
                            onAppendToken = appendToken,
                            onClear = {
                                expression = ""
                                calculationResult = null
                            },
                            onDelete = {
                                if (expression.isNotEmpty()) expression = expression.dropLast(1)
                            },
                            onToggleSign = {
                                expression = if (expression.startsWith("-")) expression.drop(1) else "-$expression"
                            },
                            onMemoryClear = { memoryValue = 0.0 },
                            onMemoryRecall = { appendToken(memoryValue.toString()) },
                            onMemoryAdd = {
                                val current = calculationResult?.decimalResult?.toDoubleOrNull() ?: 0.0
                                memoryValue += current
                            },
                            onMemorySub = {
                                val current = calculationResult?.decimalResult?.toDoubleOrNull() ?: 0.0
                                memoryValue -= current
                            },
                            onEvaluate = {
                                if (expression.isNotEmpty()) {
                                    val res = evaluator.evaluate(expression, angleMode)
                                    calculationResult = res
                                    if (res.isSuccess) {
                                        history = listOf(HistoryItem(expression = expression, result = res.decimalResult, conceptTag = res.conceptTag)) + history
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // =========================================================================
    // STEP-BY-STEP DERIVATION MODAL SHEET
    // =========================================================================
    if (showStepsSheet && calculationResult != null) {
        ModalBottomSheet(
            onDismissRequest = { showStepsSheet = false },
            containerColor = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Verified Derivation",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                        Text(
                            text = "Deterministic Engine Step-by-Step Solution",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Row {
                        ExplanationMode.values().forEach { mode ->
                            FilterChip(
                                selected = explanationMode == mode,
                                onClick = { explanationMode = mode },
                                label = { Text(mode.name.take(4), style = MaterialTheme.typography.labelSmall) }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                calculationResult?.steps?.forEach { step ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Step ${step.stepNumber}: ${step.ruleOrIdentity}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )

                            if (explanationMode != ExplanationMode.HINT) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Applicability: ${step.applicabilityReason}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Text(
                                        text = step.substitution,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFF8FAFC),
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            if (explanationMode == ExplanationMode.FULL_SOLUTION) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Result: ${step.intermediateValue}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }

                            step.commonMistakeNote?.let { mistake ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = Color(0xFFF59E0B).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Common Pitfall: $mistake", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF59E0B))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Concept Replay Buttons
                Text(
                    text = "Concept Replay: Deepen Understanding",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    calculationResult?.linkedSimulation?.let { sim ->
                        Button(
                            onClick = {
                                showStepsSheet = false
                                onNavigateToSimulation(sim)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open in Lab", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    calculationResult?.linkedGame?.let { game ->
                        FilledTonalButton(
                            onClick = {
                                showStepsSheet = false
                                onNavigateToGame(game)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play Challenge", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FilledTonalButton(
                    onClick = {
                        showStepsSheet = false
                        onNavigateToFeynman(calculationResult?.conceptTag ?: "Class 11 Concept")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feynman_teachback_trigger_btn"),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Icon(
                        Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Teach It Back: Ab Mujhe Samjhao (Feynman Mode)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Verification Badge
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Verified: [Deterministic Engine v2.0] • [Exact Surds Checked]",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Why Not Dialog
    if (showWhyNotDialog) {
        AlertDialog(
            onDismissRequest = { showWhyNotDialog = false },
            icon = { Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Why Not? — Mathematical Boundaries", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "CalcMaster 2026 operates on a strict zero-hallucination deterministic policy.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Division by zero (x / 0): Undefined because no real number multiplied by zero equals the dividend.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Negative logarithms ln(x) for x ≤ 0: Complex branch cut outside real ℝ domain.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Fractional power of negative base (-2)^(1/2): Evaluated as complex number i√2.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showWhyNotDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }

    // History Bottom Sheet
    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            containerColor = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calculation History Tape",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC)
                    )
                    if (history.isNotEmpty()) {
                        TextButton(onClick = { history = emptyList() }) {
                            Text("Clear", color = Color(0xFFF43F5E))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No recent calculations yet", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF64748B))
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        history.forEach { item ->
                            Surface(
                                onClick = {
                                    expression = item.expression
                                    calculationResult = evaluator.evaluate(expression, angleMode)
                                    showHistorySheet = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = item.expression, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFF8FAFC))
                                        item.conceptTag?.let {
                                            Text(text = it, style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8))
                                        }
                                    }
                                    Text(
                                        text = "= ${item.result}",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
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

// =========================================================================
// 1. PREMIUM SCIENTIFIC INSTRUMENT DISPLAY COMPOSABLE
// =========================================================================
@Composable
private fun InstrumentDisplayArea(
    expression: String,
    angleMode: AngleMode,
    memoryValue: Double,
    calculationResult: CalculationResult?,
    onShowSteps: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .testTag("calc_display_card")
            .drawBehind {
                // Subtle scientific millimeter coordinate grid
                val step = 20.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = Color(0x0638BDF8),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color(0x0638BDF8),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += step
                }
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF080C14)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Bezel Status Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = angleMode.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (memoryValue != 0.0) {
                        Surface(
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "M",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Precision indicator
                Text(
                    text = "PRECISION 1e-12",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF64748B)
                )
            }

            // Natural Mathematical Expression View
            val exprScroll = rememberScrollState()
            LaunchedEffect(expression) {
                exprScroll.scrollTo(exprScroll.maxValue)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScroll),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = if (expression.isEmpty()) Color(0xFF64748B) else Color(0xFFF8FAFC),
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("calc_expression_text")
                )
            }

            // Digital LCD Result Output
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                calculationResult?.let { res ->
                    if (res.isSuccess) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (res.exactResult.isNotEmpty() && res.exactResult != res.decimalResult) {
                                Text(
                                    text = "= ${res.exactResult} ≈ ",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text(
                                text = res.decimalResult,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.testTag("calc_result_text")
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Concept Tag & Step Derivation Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            res.conceptTag?.let { tag ->
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(tag, style = MaterialTheme.typography.labelSmall, color = Color(0xFFF8FAFC))
                                    }
                                }
                            } ?: Spacer(modifier = Modifier.width(1.dp))

                            if (res.steps.isNotEmpty()) {
                                FilledTonalButton(
                                    onClick = onShowSteps,
                                    modifier = Modifier.testTag("view_steps_btn"),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF0369A1).copy(alpha = 0.4f))
                                ) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verified Steps (${res.steps.size})", style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8))
                                }
                            }
                        }
                    } else {
                        Text(
                            text = res.errorMessage ?: "Calculation Error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFF43F5E),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 2. RESPONSIVE KEYPAD GRID FOR NUMERIC & SCIENTIFIC KEYS
// =========================================================================
@Composable
private fun InstrumentKeypadGrid(
    isScientificExpanded: Boolean,
    onToggleScientific: () -> Unit,
    onAppendToken: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onToggleSign: () -> Unit,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySub: () -> Unit,
    onEvaluate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("calc_keypad"),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Scientific Row 1: sin, cos, tan, ln, log
        InstrumentKeypadRow {
            InstrumentKey(label = "sin", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF818CF8)) { onAppendToken("sin(") }
            InstrumentKey(label = "cos", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF818CF8)) { onAppendToken("cos(") }
            InstrumentKey(label = "tan", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF818CF8)) { onAppendToken("tan(") }
            InstrumentKey(label = "ln", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF818CF8)) { onAppendToken("ln(") }
            InstrumentKey(label = "log", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF818CF8)) { onAppendToken("log(") }
        }

        // Scientific Row 2: √, x^y, π, e, g
        InstrumentKeypadRow {
            InstrumentKey(label = "√", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF38BDF8)) { onAppendToken("sqrt(") }
            InstrumentKey(label = "^", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF38BDF8)) { onAppendToken("^") }
            InstrumentKey(label = "π", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF38BDF8)) { onAppendToken("π") }
            InstrumentKey(label = "e", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF38BDF8)) { onAppendToken("e") }
            InstrumentKey(label = "g", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFFF59E0B)) { onAppendToken("g") }
        }

        // Memory & Functions Strip: MC, MR, M+, M-, %
        InstrumentKeypadRow {
            InstrumentKey(label = "MC", bgGradient = listOf(Color(0xFF182234), Color(0xFF111927)), textColor = Color(0xFF94A3B8)) { onMemoryClear() }
            InstrumentKey(label = "MR", bgGradient = listOf(Color(0xFF182234), Color(0xFF111927)), textColor = Color(0xFF94A3B8)) { onMemoryRecall() }
            InstrumentKey(label = "M+", bgGradient = listOf(Color(0xFF182234), Color(0xFF111927)), textColor = Color(0xFF94A3B8)) { onMemoryAdd() }
            InstrumentKey(label = "M-", bgGradient = listOf(Color(0xFF182234), Color(0xFF111927)), textColor = Color(0xFF94A3B8)) { onMemorySub() }
            InstrumentKey(label = "%", bgGradient = listOf(Color(0xFF182234), Color(0xFF111927)), textColor = Color(0xFF38BDF8)) { onAppendToken("%") }
        }

        // Main Keypad:
        // Row 1: AC, DEL, (, ), ÷
        InstrumentKeypadRow {
            InstrumentKey(label = "AC", bgGradient = listOf(Color(0xFFE11D48), Color(0xFFBE123C)), textColor = Color.White) { onClear() }
            InstrumentKey(label = "DEL", bgGradient = listOf(Color(0xFF334155), Color(0xFF1E293B)), textColor = Color(0xFFF8FAFC)) { onDelete() }
            InstrumentKey(label = "(", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF162030)), textColor = Color(0xFF94A3B8)) { onAppendToken("(") }
            InstrumentKey(label = ")", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF162030)), textColor = Color(0xFF94A3B8)) { onAppendToken(")") }
            InstrumentKey(label = "÷", bgGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)), textColor = Color.White) { onAppendToken(" / ") }
        }

        // Row 2: 7, 8, 9, !, ×
        InstrumentKeypadRow {
            InstrumentKey(label = "7", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("7") }
            InstrumentKey(label = "8", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("8") }
            InstrumentKey(label = "9", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("9") }
            InstrumentKey(label = "!", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF162030)), textColor = Color(0xFF38BDF8)) { onAppendToken("!") }
            InstrumentKey(label = "×", bgGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)), textColor = Color.White) { onAppendToken(" * ") }
        }

        // Row 3: 4, 5, 6, x², -
        InstrumentKeypadRow {
            InstrumentKey(label = "4", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("4") }
            InstrumentKey(label = "5", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("5") }
            InstrumentKey(label = "6", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("6") }
            InstrumentKey(label = "x²", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF162030)), textColor = Color(0xFF38BDF8)) { onAppendToken("^2") }
            InstrumentKey(label = "-", bgGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)), textColor = Color.White) { onAppendToken(" - ") }
        }

        // Row 4: 1, 2, 3, ±, +
        InstrumentKeypadRow {
            InstrumentKey(label = "1", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("1") }
            InstrumentKey(label = "2", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("2") }
            InstrumentKey(label = "3", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("3") }
            InstrumentKey(label = "±", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF162030)), textColor = Color(0xFF94A3B8)) { onToggleSign() }
            InstrumentKey(label = "+", bgGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)), textColor = Color.White) { onAppendToken(" + ") }
        }

        // Row 5: 0, ., = (Prominent Execution Jewel Key)
        InstrumentKeypadRow {
            InstrumentKey(label = "0", modifier = Modifier.weight(2f), bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("0") }
            InstrumentKey(label = ".", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken(".") }
            InstrumentKey(
                label = "=",
                modifier = Modifier.weight(2f),
                bgGradient = listOf(Color(0xFF0EA5E9), Color(0xFF0284C7)),
                textColor = Color.White,
                onClick = onEvaluate
            )
        }
    }
}

// =========================================================================
// 3. TACTILE INSTRUMENT KEY COMPOSABLE
// =========================================================================
@Composable
private fun InstrumentKeypadRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        content = content
    )
}

@Composable
private fun RowScope.InstrumentKey(
    label: String,
    modifier: Modifier = Modifier.weight(1f),
    bgGradient: List<Color>,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(48.dp) // Minimum 48dp touch target
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(bgGradient))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.10f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .testTag("key_$label"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = if (label.all { it.isDigit() || it == '.' }) FontFamily.Monospace else FontFamily.Default,
            color = textColor
        )
    }
}

@Composable
private fun InstrumentQuickChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
